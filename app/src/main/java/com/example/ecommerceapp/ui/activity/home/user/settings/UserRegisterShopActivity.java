package com.example.ecommerceapp.ui.activity.home.user.settings;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ShopRequest;
import com.example.ecommerceapp.data.model.response.ShopResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRegisterShopActivity extends AppCompatActivity {

    private EditText etShopName, etDescription;
    private Button btnSubmit;
    private ImageView ivBack;

    private TokenManager tokenManager;
    private ShopService shopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register_shop);

        etShopName = findViewById(R.id.etShopName);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());

        tokenManager = TokenManager.getInstance(this);
        shopService = ApiClient.getPublicShopService(tokenManager);

        btnSubmit.setOnClickListener(v -> submitShopRegistration());
    }

    private void submitShopRegistration() {
        String shopName = etShopName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (shopName.isEmpty()) {
            etShopName.setError("Tên shop không được để trống");
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang gửi...");

        ShopRequest request = new ShopRequest();
        request.shopName = shopName;
        request.description = description;
        request.userId = (int) tokenManager.getUserId();
        request.status = ShopStatus.PENDING;

        shopService.createShop(request).enqueue(new Callback<ShopResponse>() {
            @Override
            public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Gửi yêu cầu đăng ký");
                
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(UserRegisterShopActivity.this, "Đã gửi yêu cầu đăng ký! Vui lòng chờ Admin duyệt.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(UserRegisterShopActivity.this, "Đăng ký thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopResponse> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Gửi yêu cầu đăng ký");
                Toast.makeText(UserRegisterShopActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
