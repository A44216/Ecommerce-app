package com.example.ecommerceapp.ui.activity.home.seller.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.repository.ProductRepository;
import com.example.ecommerceapp.ui.adapter.seller.ImagePagerAdapter;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import java.math.RoundingMode;

public class ProductDetailActivity extends AppCompatActivity {

    private TextView txtName, txtPrice, txtStock, txtSold, txtRating, txtStatus, txtDescription;
    private ViewPager2 viewPagerImages;
    private ImageView ivBack;
    private WormDotsIndicator dotsIndicator;

    private ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        handleBack();

        // init API
        repository = new ProductRepository(ApiClient.getProductService());

        // lấy productId
        int productId = getIntent().getIntExtra("productId", -1);

        if (productId != -1) {
            fetchProduct(productId);
        }

    }

    private void initViews() {
        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtStock = findViewById(R.id.txtStock);
        txtSold = findViewById(R.id.txtSold);
        txtRating = findViewById(R.id.txtRating);
        txtStatus = findViewById(R.id.txtStatus);
        txtDescription = findViewById(R.id.txtDescription);
        viewPagerImages = findViewById(R.id.viewPagerImages);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        ivBack = findViewById(R.id.ivBack);
    }

    private void handleBack() {
        ivBack.setOnClickListener(v -> finish());
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(ProductResponse product) {

        txtName.setText(product.getName());

        txtPrice.setText("💰 " + String.format("%,.0f", product.getPrice()) + " đ");

        txtStock.setText("📦 Kho: " + product.getStock());

        txtSold.setText("🔥 Đã bán: " + product.getSoldCount());

        if (product.getRatingAvg() != null) {
            txtRating.setText("⭐ " + product.getRatingAvg().setScale(1, RoundingMode.HALF_UP));
        } else {
            txtRating.setText("⭐ 0.0");
        }

        txtStatus.setText("Trạng thái: " + product.getStatus());

        txtDescription.setText(product.getDescription() != null
                ? "Mô tả: " +product.getDescription()
                : "Chưa có mô tả");

        // Load ảnh
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            ImagePagerAdapter adapter = new ImagePagerAdapter(this, product.getImages());
            viewPagerImages.setAdapter(adapter);
        }

        // Kết nối dots indicator
        dotsIndicator.attachTo(viewPagerImages);
    }

    private void fetchProduct(int id) {
        repository.getProductById(id).enqueue(new retrofit2.Callback<ProductResponse>() {

            @Override
            public void onResponse(retrofit2.Call<ProductResponse> call,
                                   retrofit2.Response<ProductResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ProductResponse> call, Throwable t) {
                Toast.makeText(ProductDetailActivity.this,
                        "Lỗi tải dữ liệu",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

}