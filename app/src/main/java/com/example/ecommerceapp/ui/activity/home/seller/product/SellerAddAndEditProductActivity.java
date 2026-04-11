package com.example.ecommerceapp.ui.activity.home.seller.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ProductImageRequest;
import com.example.ecommerceapp.data.model.request.seller.product.SellerProductRequest;
import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.CategoryRepository;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductImageRepository;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerImageEditAdapter;
import com.example.ecommerceapp.utils.ImageUploadHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerAddAndEditProductActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextInputEditText etName, etPrice, etStock, etDescription;
    private AutoCompleteTextView etCategory;
    private MaterialButton btnSubmit, btnSelectImages;
    private RecyclerView rvImages;
    private SellerImageEditAdapter imageAdapter;

    private TokenManager tokenManager;
    private SellerProductRepository productRepository;
    private SellerProductImageRepository productImageRepository;

    private int productId = -1;
    private boolean isEdit = false;

    private List<CategoryResponse> categoryList = new ArrayList<>();
    private Integer selectedCategoryId = null;
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            List<Uri> list = new ArrayList<>();

                            if (result.getData().getClipData() != null) {
                                int count = result.getData().getClipData().getItemCount();

                                for (int i = 0; i < count; i++) {
                                    Uri uri = result.getData()
                                            .getClipData()
                                            .getItemAt(i)
                                            .getUri();
                                    list.add(uri);
                                }
                            } else {
                                list.add(result.getData().getData());
                            }

                            imageAdapter.addImages(list);
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_add_and_edit_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productId = getIntent().getIntExtra("productId", -1);
        isEdit = productId != -1;

        tokenManager = TokenManager.getInstance(this);

        initViews();
        setupActions();

        productImageRepository = new SellerProductImageRepository(
                ApiClient.getProductImageService(tokenManager)
        );

        productRepository = new SellerProductRepository(
                ApiClient.getProductService(tokenManager)
        );

        loadCategories(() -> {
            if (isEdit) loadProduct();
        });
    }

    private void initViews() {
        tvTitle = findViewById(R.id.tvTitle);

        etName = findViewById(R.id.etName);
        etPrice = findViewById(R.id.etPrice);
        etStock = findViewById(R.id.etStock);
        etDescription = findViewById(R.id.etDescription);

        etCategory = findViewById(R.id.etCategory);

        btnSubmit = findViewById(R.id.btnAddOrEditProduct);
        btnSelectImages = findViewById(R.id.btnSelectImages);

        ImageView ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());

        rvImages = findViewById(R.id.rvImages);

        imageAdapter = new SellerImageEditAdapter();

        rvImages.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );

        rvImages.setAdapter(imageAdapter);
    }

    private void loadProduct() {
        productRepository.getProductById(productId)
                .enqueue(new Callback<SellerProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SellerProductResponse> call,
                                           @NonNull Response<SellerProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            bindData(response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SellerProductResponse> call, @NonNull Throwable t) {
                        Toast.makeText(SellerAddAndEditProductActivity.this,
                                "Lỗi load sản phẩm",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadCategories(Runnable onDone) {
        CategoryRepository repo = new CategoryRepository(
                ApiClient.getCategoryService(tokenManager)
        );

        repo.getCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryResponse>> call,
                                   @NonNull Response<List<CategoryResponse>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    categoryList = response.body();

                    List<String> names = new ArrayList<>();
                    for (CategoryResponse c : categoryList) {
                        names.add(c.getName());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            SellerAddAndEditProductActivity.this,
                            android.R.layout.simple_dropdown_item_1line,
                            names
                    );

                    etCategory.setAdapter(adapter);

                    if (onDone != null) onDone.run(); // ✅ thêm dòng này
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryResponse>> call, @NonNull Throwable t) {
                Toast.makeText(SellerAddAndEditProductActivity.this,
                        "Load category lỗi",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImages(int productId) {

        List<Uri> localImages = imageAdapter.getLocalImages();

        if (localImages == null || localImages.isEmpty()) {
            runOnUiThread(() -> {
                Toast.makeText(this, "Thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
            return;
        }

        int total = localImages.size();
        final int[] done = {0};

        for (Uri uri : localImages) {

            ImageUploadHelper.uploadImage(
                    tokenManager,
                    uri,
                    this,
                    new ImageUploadHelper.Callback<String>() {

                        @Override
                        public void onSuccess(String url) {
                            addProductImage(productId, url);

                            done[0]++;
                            checkDone(total, done[0]);
                        }

                        @Override
                        public void onError(String error) {
                            done[0]++;
                            checkDone(total, done[0]);
                        }
                    }
            );
        }
    }

    private void checkDone(int total, int done) {
        if (done == total) {

            imageAdapter.getLocalImages().clear();

            runOnUiThread(() -> {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                finish();
            });
        }
    }

    private void addProductImage(int productId, String url) {

        ProductImageRequest req = new ProductImageRequest();
        req.setProductId(productId);
        req.setImageUrl(url);

        productImageRepository.addProductImage(req)
                .enqueue(new Callback<ProductImageResponse>() {
                    @Override
                    public void onResponse(Call<ProductImageResponse> call,
                                           Response<ProductImageResponse> response) {
                        if (response.isSuccessful()) {
                            System.out.println("Save image OK");
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductImageResponse> call, Throwable t) {
                        Toast.makeText(SellerAddAndEditProductActivity.this,
                                "Lưu ảnh DB lỗi", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    private void bindData(SellerProductResponse product) {

        tvTitle.setText("CẬP NHẬT SẢN PHẨM");

        etName.setText(product.getName());
        etPrice.setText(String.valueOf(product.getPrice()));
        etStock.setText(String.valueOf(product.getStock()));
        etDescription.setText(product.getDescription());

        selectedCategoryId = null;

        if (product.getCategoryName() != null) {
            etCategory.setText(
                    product.getCategoryName() != null ? product.getCategoryName() : "",
                    false
            );

            for (CategoryResponse c : categoryList) {
                if (product.getCategoryName().equals(c.getName())) {
                    selectedCategoryId = c.getId();
                    break;
                }
            }
        } else {
            etCategory.setText("", false);
        }

        // ===== FIX LOAD SERVER IMAGES =====
        if (product.getImages() != null) {
            imageAdapter.setServerImages(product.getImages());
        }

        btnSubmit.setText("Cập nhật");
    }

    private void setupActions() {

        etCategory.setOnItemClickListener((parent, view, position, id) -> {
            handleCategorySelect(parent, position);
        });

        btnSelectImages.setOnClickListener(v -> handleImageSelect());

        btnSubmit.setOnClickListener(v -> {

            if (!validateForm()) return;

            String name = Objects.requireNonNull(etName.getText()).toString().trim();
            String priceStr = Objects.requireNonNull(etPrice.getText()).toString().trim();
            String stockStr = Objects.requireNonNull(etStock.getText()).toString().trim();

            SellerProductRequest request = new SellerProductRequest();
            request.setName(name);
            request.setPrice(new BigDecimal(priceStr));
            request.setStock(Integer.parseInt(stockStr));
            request.setDescription(Objects.requireNonNull(etDescription.getText()).toString());
            request.setCategoryId(selectedCategoryId);

            showConfirmDialog(request);
        });

    }

    private void handleImageSelect() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        imagePickerLauncher.launch(intent);
    }

    private void handleCategorySelect(AdapterView<?> parent, int position) {

        String name = (String) parent.getItemAtPosition(position);

        for (CategoryResponse c : categoryList) {
            if (c.getName().equals(name)) {
                selectedCategoryId = c.getId();
                break;
            }
        }
    }

    private void createProduct(SellerProductRequest request) {

        productRepository.createProduct(request)
                .enqueue(new Callback<SellerProductResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SellerProductResponse> call,
                                           @NonNull Response<SellerProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            int newProductId = response.body().getId();

                            uploadImages(newProductId);

                            Toast.makeText(SellerAddAndEditProductActivity.this,
                                    "Tạo thành công", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SellerProductResponse> call, @NonNull Throwable t) {
                        Toast.makeText(SellerAddAndEditProductActivity.this,
                                "Lỗi tạo sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProduct(SellerProductRequest request) {

        productRepository.updateProduct(productId, request)
                .enqueue(new Callback<SellerProductResponse>() {

                    @Override
                    public void onResponse(@NonNull Call<SellerProductResponse> call,
                                           @NonNull Response<SellerProductResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            List<ProductImageResponse> deleteList = imageAdapter.getDeletedServerImages();

                            if (deleteList == null || deleteList.isEmpty()) {
                                uploadImages(productId);
                                return;
                            }

                            final int total = deleteList.size();
                            final int[] deletedCount = {0};

                            for (ProductImageResponse img : deleteList) {

                                productImageRepository.deleteProductImage(img.getId())
                                        .enqueue(new Callback<Void>() {

                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {

                                                deletedCount[0]++;

                                                if (deletedCount[0] == total) {
                                                    uploadImages(productId);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                                deletedCount[0]++;

                                                if (deletedCount[0] == total) {
                                                    uploadImages(productId);
                                                }
                                            }
                                        });
                            }

                        } else {
                            Toast.makeText(SellerAddAndEditProductActivity.this,
                                    "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SellerProductResponse> call, @NonNull Throwable t) {
                        Toast.makeText(SellerAddAndEditProductActivity.this,
                                "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean validateForm() {
        boolean isValid = true;

        etName.setError(null);
        etPrice.setError(null);
        etStock.setError(null);
        etCategory.setError(null);

        String name = Objects.requireNonNull(etName.getText()).toString().trim();
        String priceStr = Objects.requireNonNull(etPrice.getText()).toString().trim();
        String stockStr = Objects.requireNonNull(etStock.getText()).toString().trim();

        // NAME
        if (name.isEmpty()) {
            etName.setError("Tên không được để trống");
            isValid = false;
        } else {
            etName.setError(null);
        }

        // PRICE
        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(new BigDecimal("9999999999999999.99")) > 0) {
                etPrice.setError("Giá quá lớn");
                isValid = false;
            }
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                etPrice.setError("Giá phải >= 0");
                isValid = false;
            } else {
                etPrice.setError(null);
            }
        } catch (Exception e) {
            etPrice.setError("Giá không hợp lệ");
            isValid = false;
        }

        // STOCK
        try {
            int stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                etStock.setError("Tồn kho phải >= 0");
                isValid = false;
            } else {
                etStock.setError(null);
            }
        } catch (Exception e) {
            etStock.setError("Số lượng không hợp lệ");
            isValid = false;
        }

        return isValid;
    }

    private void showConfirmDialog(SellerProductRequest request) {

        String message = isEdit
                ? "Bạn có chắc muốn cập nhật sản phẩm này?"
                : "Bạn có chắc muốn tạo sản phẩm này?";

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    if (isEdit) {
                        updateProduct(request);
                    } else {
                        createProduct(request);
                    }
                })
                .setNegativeButton("Huỷ", (dialog, which) -> dialog.dismiss())
                .show();
    }

}