package com.example.ecommerceapp.ui.activity.home.seller.shop;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerShopService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.seller.shop.SellerShopRequest;
import com.example.ecommerceapp.data.repository.seller.SellerShopRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerShopViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerShopViewModelFactory;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.utils.ImageUploadHelper;

public class SellerShopInfoActivity extends AppCompatActivity {

    private ImageView imgAvatar, ivBack;
    private EditText edtShopName, edtAddress, edtPhone, edtEmail, edtDescription;
    private Button btnUpdate, btnChooseImage;

    private SellerShopViewModel viewModel;
    private TokenManager tokenManager;

    private String avatarUrl;

    private boolean isUpdating = false;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() != RESULT_OK || result.getData() == null) {
                            return;
                        }

                        Uri uri = result.getData().getData();
                        if (uri == null) return;

                        ImageUploadHelper.uploadImage(
                                tokenManager,
                                uri,
                                this,
                                new ImageUploadHelper.Callback<String>() {
                                    @Override
                                    public void onSuccess(String url) {
                                        avatarUrl = url;

                                        ImageLoader.load(
                                                SellerShopInfoActivity.this,
                                                imgAvatar,
                                                url
                                        );

                                    }

                                    @Override
                                    public void onError(String error) {

                                        Toast.makeText(
                                                SellerShopInfoActivity.this,
                                                error,
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                }
                        );
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_info);

        tokenManager = TokenManager.getInstance(this);

        initViews();
        setupViewModel();
        observeData();
        setListeners();
    }

    private void initViews() {

        ivBack = findViewById(R.id.ivBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        edtShopName = findViewById(R.id.edtShopName);
        edtAddress = findViewById(R.id.edtAddress);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtDescription = findViewById(R.id.edtDescription);

        btnUpdate = findViewById(R.id.btnUpdateShopInfo);
        btnChooseImage = findViewById(R.id.btnChooseImage);
    }

    private void setupViewModel() {
        SellerShopService api = ApiClient.getShopService(tokenManager);
        SellerShopRepository repository = new SellerShopRepository(api);

        viewModel = new ViewModelProvider(
                this,
                new SellerShopViewModelFactory(repository)
        ).get(SellerShopViewModel.class);

        int userId = (int) tokenManager.getUserId();
        viewModel.fetchMyShop();
    }

    private void observeData() {
        viewModel.getShop().observe(this, shop -> {
            if (shop == null) return;

            edtShopName.setText(shop.getShopName());
            edtAddress.setText(shop.getAddress());
            edtPhone.setText(shop.getPhone());
            edtEmail.setText(shop.getEmail());
            edtDescription.setText(shop.getDescription());

            avatarUrl = shop.getAvatar();

            if (avatarUrl == null || avatarUrl.trim().isEmpty()) {
                imgAvatar.setImageResource(R.drawable.ic_avatar);
            } else {
                ImageLoader.load(this, imgAvatar, avatarUrl);
            }

            // chỉ xử lý khi update xong
            if (isUpdating) {
                isUpdating = false;

                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void setListeners() {

        ivBack.setOnClickListener( v -> finish());

        btnChooseImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            imagePickerLauncher.launch(intent);
        });

        btnUpdate.setOnClickListener(v -> updateShop());
    }

    private void updateShop() {

        String name = edtShopName.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (name.isEmpty()) {
            edtShopName.setError("Vui lòng nhập tên shop");
            edtShopName.requestFocus();
            return;
        }

        if (!email.isEmpty() &&
                !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (!phone.isEmpty() && !phone.matches("^0\\d{9}$")) {
            edtPhone.setError("SĐT phải 10 số và bắt đầu bằng số 0");
            edtPhone.requestFocus();
            return;
        }

        showConfirmUpdateDialog(name, address, phone, email, description);
    }

    private void showConfirmUpdateDialog(String name, String address, String phone, String email, String description) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận cập nhật")
                .setMessage("Bạn có chắc chắn muốn cập nhật thông tin shop không?")
                .setPositiveButton("Cập nhật", (dialog, which) -> {

                    isUpdating = true;

                    int shopId = (int) tokenManager.getShopId();

                    SellerShopRequest request = new SellerShopRequest();
                    request.setShopName(name);
                    request.setAddress(address);
                    request.setPhone(phone);
                    request.setEmail(email);
                    request.setDescription(description);
                    request.setAvatar(avatarUrl);

                    viewModel.updateShop(request);
                })
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }

}