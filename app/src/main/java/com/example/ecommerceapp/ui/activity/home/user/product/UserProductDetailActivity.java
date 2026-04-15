package com.example.ecommerceapp.ui.activity.home.user.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ReviewResponse;
import com.example.ecommerceapp.data.model.response.UserProductImageResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.example.ecommerceapp.ui.adapter.user.ReviewAdapter;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.utils.ImageLoader;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProductDetailActivity extends AppCompatActivity {

    // Khai báo biến cho phần Review
    private RecyclerView rvReviews;
    private TextView tvAverageRating, tvNoReviews;
    private RatingBar mainRatingBar;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_detail);

        tokenManager = TokenManager.getInstance(this);

        // 1. Ánh xạ View Sản phẩm cơ bản
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);

        // --- ÁNH XẠ THÊM CÁC VIEW MỚI ---
        TextView tvDetailDesc = findViewById(R.id.tvDetailDesc);
        TextView tvDetailRatingAvg = findViewById(R.id.tvDetailRatingAvg);
        TextView tvDetailSoldCount = findViewById(R.id.tvDetailSoldCount);
        TextView tvDetailStock = findViewById(R.id.tvDetailStock);

        LinearLayout btnAddToCart = findViewById(R.id.btnViewCart);
        Button btnBuyNow = findViewById(R.id.btnBuyNow);

        // Ánh xạ View Review
        rvReviews = findViewById(R.id.rvReviews);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        mainRatingBar = findViewById(R.id.mainRatingBar);
        tvNoReviews = findViewById(R.id.tvNoReviews);

        // Cài đặt RecyclerView cho Review
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        // 2. Nhận dữ liệu từ Intent gửi sang
        int productId = getIntent().getIntExtra("product_id", -1);
        int shopId = getIntent().getIntExtra("shop_id", -1);
        String name = getIntent().getStringExtra("product_name");
        String priceString = getIntent().getStringExtra("product_price");
        String imageUrl = getIntent().getStringExtra("product_image");

        // --- NHẬN THÊM DỮ LIỆU MỚI TỪ INTENT ---
        float ratingAvg = getIntent().getFloatExtra("product_rating_avg", 0f);
        int soldCount = getIntent().getIntExtra("product_sold_count", 0);
        int stock = getIntent().getIntExtra("product_stock", 0);
        String description = getIntent().getStringExtra("product_desc");

        // 3. Hiển thị dữ liệu lên màn hình
        if (name != null) tvDetailName.setText(name);

        if (priceString != null) {
            try {
                double price = Double.parseDouble(priceString);
                DecimalFormat formatter = new DecimalFormat("#,###");
                tvDetailPrice.setText(formatter.format(price) + "đ");
            } catch (Exception e) {
                tvDetailPrice.setText(priceString + "đ");
            }
        }

        if (imageUrl != null) {
            ImageLoader.load(this, ivDetailImage, imageUrl);
        }

        // --- ĐỔ DỮ LIỆU MỚI LÊN MÀN HÌNH ---
        tvDetailRatingAvg.setText(String.format("%.1f", ratingAvg));
        tvDetailSoldCount.setText("Đã bán " + soldCount);
        tvDetailStock.setText("Kho: " + stock);

        if (description != null && !description.isEmpty()) {
            tvDetailDesc.setText(description);
        } else {
            tvDetailDesc.setText("Chưa có mô tả cho sản phẩm này.");
        }

        // --- GỌI API TẢI REVIEW ---
        if (productId != -1) {
            loadReviews(productId);
        }

        // ==========================================
        // 4. XỬ LÝ LOGIC GIỎ HÀNG
        // ==========================================
        UserProductResponse currentProduct = new UserProductResponse();
        if (productId != -1) currentProduct.setId(productId);
        if (shopId != -1) currentProduct.setShopId(shopId);
        if (name != null) currentProduct.setName(name);
        if (priceString != null) {
            try {
                currentProduct.setPrice(new BigDecimal(priceString));
            } catch (Exception e) {
                currentProduct.setPrice(BigDecimal.ZERO);
            }
        }
        if (imageUrl != null) {
            UserProductImageResponse imgObj = new UserProductImageResponse();
            imgObj.setImageUrl(imageUrl);
            currentProduct.setImages(Collections.singletonList(imgObj));
        }

        btnAddToCart.setOnClickListener(v -> {
            if (tokenManager.getUserId() == -1) {
                showLoginRequireDialog();
                return;
            }
            CartManager.getInstance().addToCart(currentProduct);
            Toast.makeText(UserProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnBuyNow.setOnClickListener(v -> {
            if (tokenManager.getUserId() == -1) {
                showLoginRequireDialog();
                return;
            }
            CartManager.getInstance().addToCart(currentProduct);
            Intent intent = new Intent(UserProductDetailActivity.this, UserCartActivity.class);
            startActivity(intent);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // ==========================================
    // 5. HÀM TẢI ĐÁNH GIÁ (REVIEW)
    // ==========================================
    private void loadReviews(int productId) {
        // Lưu ý: Đảm bảo bạn đã khai báo API getReviewsByProduct trong file chứa API của bạn
        ApiClient.getUserService(tokenManager).getReviewsByProduct(productId).enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewResponse> reviews = response.body();

                    if (reviews.isEmpty()) {
                        // Không có đánh giá
                        rvReviews.setVisibility(View.GONE);
                        tvNoReviews.setVisibility(View.VISIBLE);
                        tvAverageRating.setText("0/5 (0 đánh giá)");
                        mainRatingBar.setRating(0);
                    } else {
                        // Tính trung bình sao
                        float averageRating = calculateAverage(reviews);
                        tvAverageRating.setText(String.format("%.1f/5 (%d đánh giá)", averageRating, reviews.size()));
                        mainRatingBar.setRating(averageRating);

                        // Hiển thị danh sách
                        tvNoReviews.setVisibility(View.GONE);
                        rvReviews.setVisibility(View.VISIBLE);
                        rvReviews.setAdapter(new ReviewAdapter(reviews));
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ReviewResponse>> call, Throwable t) {
                Toast.makeText(UserProductDetailActivity.this, "Không thể tải đánh giá", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private float calculateAverage(List<ReviewResponse> reviews) {
        if (reviews == null || reviews.isEmpty()) return 0;
        float sum = 0;
        for (ReviewResponse r : reviews) {
            sum += r.getRating();
        }
        return sum / reviews.size();
    }

    private void showLoginRequireDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng. Đăng nhập ngay?")
                .setCancelable(true)
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(this, LoginActivity.class));
                })
                .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
                .show();
    }
}