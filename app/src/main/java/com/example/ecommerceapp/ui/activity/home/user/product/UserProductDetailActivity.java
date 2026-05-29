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
import com.example.ecommerceapp.data.model.request.ConversationRequest;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.data.model.response.ReviewResponse;
import com.example.ecommerceapp.data.model.response.UserProductImageResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.example.ecommerceapp.ui.adapter.user.ReviewAdapter;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.utils.ImageLoader;
import com.example.ecommerceapp.ui.activity.home.user.chat.ChatActivity;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProductDetailActivity extends AppCompatActivity {

    private androidx.viewpager2.widget.ViewPager2 vpProductImages;
    private TextView tvImageIndex;
    private com.example.ecommerceapp.ui.adapter.user.ProductImageAdapter imageAdapter;
    
    private RecyclerView rvReviews;
    private TextView tvAverageRating, tvNoReviews;
    private RatingBar mainRatingBar;
    private TokenManager tokenManager;
    private UserProductResponse currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_detail);

        tokenManager = TokenManager.getInstance(this);

        // 1. Ánh xạ View Sản phẩm cơ bản
        ImageView btnBack = findViewById(R.id.btnBack);
        vpProductImages = findViewById(R.id.vpProductImages);
        tvImageIndex = findViewById(R.id.tvImageIndex);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);

        // --- ÁNH XẠ THÊM CÁC VIEW MỚI ---
        TextView tvDetailDesc = findViewById(R.id.tvDetailDesc);
        TextView tvDetailRatingAvg = findViewById(R.id.tvDetailRatingAvg);
        TextView tvDetailSoldCount = findViewById(R.id.tvDetailSoldCount);
        TextView tvDetailStock = findViewById(R.id.tvDetailStock);

        LinearLayout bottomActionbar = findViewById(R.id.bottomActionbar);
        View btnChatNow = bottomActionbar.getChildAt(0); // Lấy item đầu tiên là nút "Chat ngay"
        LinearLayout btnAddToCart = findViewById(R.id.btnViewCart);
        Button btnBuyNow = findViewById(R.id.btnBuyNow);

        // Ánh xạ View Review
        rvReviews = findViewById(R.id.rvReviews);
        tvAverageRating = findViewById(R.id.tvAverageRating);
        mainRatingBar = findViewById(R.id.mainRatingBar);
        tvNoReviews = findViewById(R.id.tvNoReviews);
        
        ImageView ivShopAvatar = findViewById(R.id.ivShopAvatar);
        TextView tvShopNameView = findViewById(R.id.tvShopName);
        TextView tvShopRatingView = findViewById(R.id.tvShopRating);
        Button btnViewShop = findViewById(R.id.btnViewShop);

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

        // (Đã thay thế bằng Slider ảnh trong loadProductDetail)

        // --- ĐỔ DỮ LIỆU MỚI LÊN MÀN HÌNH ---
        tvDetailRatingAvg.setText(String.format("%.1f", ratingAvg));
        tvDetailSoldCount.setText("Đã bán " + soldCount);
        tvDetailStock.setText("Kho: " + stock);

        if (description != null && !description.isEmpty()) {
            tvDetailDesc.setText(description);
        } else {
            tvDetailDesc.setText("Chưa có mô tả cho sản phẩm này.");
        }

        // --- GỌI API TẢI CHI TIẾT VÀ REVIEW ---
        if (productId != -1) {
            loadProductDetail(productId);
            loadReviews(productId);
        }

        // --- TẢI THÔNG TIN SHOP ---
        if (shopId != -1) {
            ApiClient.getPublicShopService(tokenManager).getShopById(shopId).enqueue(new Callback<com.example.ecommerceapp.data.model.response.ShopResponse>() {
                @Override
                public void onResponse(Call<com.example.ecommerceapp.data.model.response.ShopResponse> call, Response<com.example.ecommerceapp.data.model.response.ShopResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.ecommerceapp.data.model.response.ShopResponse shop = response.body();
                        tvShopNameView.setText(shop.getShopName() != null ? shop.getShopName() : "Shop");
                        if (shop.getRatingAvg() != null) {
                            tvShopRatingView.setText(String.format("%.1f", shop.getRatingAvg()));
                        }
                        if (shop.getAvatar() != null && !shop.getAvatar().isEmpty()) {
                            ImageLoader.load(UserProductDetailActivity.this, ivShopAvatar, shop.getAvatar());
                        }
                    }
                }

                @Override
                public void onFailure(Call<com.example.ecommerceapp.data.model.response.ShopResponse> call, Throwable t) {}
            });

            btnViewShop.setOnClickListener(v -> {
                Intent intent = new Intent(UserProductDetailActivity.this, com.example.ecommerceapp.ui.activity.home.user.shop.UserShopDetailActivity.class);
                intent.putExtra("SHOP_ID", shopId);
                startActivity(intent);
            });
        }

        // ==========================================
        // 4. XỬ LÝ LOGIC GIỎ HÀNG & CHAT
        // ==========================================

        // --- LOGIC CHAT NGAY ---
        btnChatNow.setOnClickListener(v -> {
            if (tokenManager.getUserId() == -1) {
                showLoginRequireDialog();
                return;
            }

            if (shopId != -1) {
                ConversationRequest req = new ConversationRequest(shopId, (int) tokenManager.getUserId());
                ApiClient.getChatApiService(tokenManager).createConversation(req).enqueue(new Callback<ConversationResponse>() {
                    @Override
                    public void onResponse(Call<ConversationResponse> call, Response<ConversationResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            int conversationId = response.body().getId();
                            String shopName = response.body().getShopName();
                            Intent intent = new Intent(UserProductDetailActivity.this, ChatActivity.class);
                            intent.putExtra("CONVERSATION_ID", conversationId);
                            intent.putExtra("SHOP_NAME", shopName != null ? shopName : "Shop");
                            intent.putExtra("SHOP_ID", shopId);
                            startActivity(intent);

                            Toast.makeText(UserProductDetailActivity.this, "Đã lấy thành công phòng Chat ID: " + conversationId, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                String errorDetail = response.errorBody() != null ? response.errorBody().string() : "Không rõ lỗi";
                                android.util.Log.e("API_CHAT_ERROR", "Mã lỗi: " + response.code() + " - Chi tiết: " + errorDetail);
                                Toast.makeText(UserProductDetailActivity.this, "Lỗi " + response.code() + ": " + errorDetail, Toast.LENGTH_LONG).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ConversationResponse> call, Throwable t) {
                        Toast.makeText(UserProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(UserProductDetailActivity.this, "Không tìm thấy thông tin Shop!", Toast.LENGTH_SHORT).show();
            }
        });

        // --- LOGIC GIỎ HÀNG ---
        // Khởi tạo đối tượng sản phẩm tạm thời từ Intent
        currentProduct = new UserProductResponse();
        if (productId != -1) currentProduct.setId(productId);
        if (shopId != -1) currentProduct.setShopId(shopId);
        if (name != null) currentProduct.setName(name);
        if (priceString != null) {
            try {
                currentProduct.setPrice(new java.math.BigDecimal(priceString));
            } catch (Exception e) {
                currentProduct.setPrice(java.math.BigDecimal.ZERO);
            }
        }
        if (imageUrl != null) {
            com.example.ecommerceapp.data.model.response.UserProductImageResponse imgObj = new com.example.ecommerceapp.data.model.response.UserProductImageResponse();
            imgObj.setImageUrl(imageUrl);
            currentProduct.setImages(java.util.Collections.singletonList(imgObj));
        }

        btnAddToCart.setOnClickListener(v -> {
            if (tokenManager.getUserId() == -1) {
                showLoginRequireDialog();
                return;
            }
            if (tokenManager.getShopId() == shopId) {
                Toast.makeText(UserProductDetailActivity.this, "Bạn không thể mua sản phẩm của chính mình!", Toast.LENGTH_SHORT).show();
                return;
            }
            com.example.ecommerceapp.ui.fragment.user.AddToCartBottomSheet bottomSheet = new com.example.ecommerceapp.ui.fragment.user.AddToCartBottomSheet(currentProduct, currentProduct.getStock() != null ? currentProduct.getStock() : stock, quantity -> {
                CartManager.getInstance().addToCart(currentProduct, quantity);
                Toast.makeText(UserProductDetailActivity.this, "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
            });
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        btnBuyNow.setOnClickListener(v -> {
            if (tokenManager.getUserId() == -1) {
                showLoginRequireDialog();
                return;
            }
            if (tokenManager.getShopId() == shopId) {
                Toast.makeText(UserProductDetailActivity.this, "Bạn không thể mua sản phẩm của chính mình!", Toast.LENGTH_SHORT).show();
                return;
            }
            com.example.ecommerceapp.ui.fragment.user.AddToCartBottomSheet bottomSheet = new com.example.ecommerceapp.ui.fragment.user.AddToCartBottomSheet(currentProduct, currentProduct.getStock() != null ? currentProduct.getStock() : stock, quantity -> {
                CartManager.getInstance().addToCart(currentProduct, quantity);
                Intent intent = new Intent(UserProductDetailActivity.this, UserCartActivity.class);
                startActivity(intent);
            });
            bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // ==========================================
    // 5. HÀM TẢI CHI TIẾT SẢN PHẨM (ĐỂ LẤY NHIỀU ẢNH)
    // ==========================================
    private void loadProductDetail(int productId) {
        ApiClient.getUserProductService().getProductById(productId).enqueue(new Callback<UserProductResponse>() {
            @Override
            public void onResponse(Call<UserProductResponse> call, Response<UserProductResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentProduct = response.body();
                    displayProductDetail(currentProduct);
                }
            }

            @Override
            public void onFailure(Call<UserProductResponse> call, Throwable t) {
                Toast.makeText(UserProductDetailActivity.this, "Không thể tải chi tiết sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayProductDetail(UserProductResponse product) {
        // Cập nhật các trường thông tin nếu có dữ liệu mới từ API
        if (product.getName() != null) ((TextView)findViewById(R.id.tvDetailName)).setText(product.getName());
        if (product.getPrice() != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            ((TextView)findViewById(R.id.tvDetailPrice)).setText(formatter.format(product.getPrice()) + "đ");
        }
        if (product.getDescription() != null) ((TextView)findViewById(R.id.tvDetailDesc)).setText(product.getDescription());
        ((TextView)findViewById(R.id.tvDetailSoldCount)).setText("Đã bán " + product.getSoldCount());
        ((TextView)findViewById(R.id.tvDetailStock)).setText("Kho: " + product.getStock());

        // Thiết lập Slider ảnh
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            java.util.List<String> urls = new java.util.ArrayList<>();
            for (com.example.ecommerceapp.data.model.response.UserProductImageResponse img : product.getImages()) {
                urls.add(img.getImageUrl());
            }

            imageAdapter = new com.example.ecommerceapp.ui.adapter.user.ProductImageAdapter(this, urls);
            vpProductImages.setAdapter(imageAdapter);

            if (urls.size() > 1) {
                tvImageIndex.setVisibility(View.VISIBLE);
                tvImageIndex.setText("1/" + urls.size());
                vpProductImages.registerOnPageChangeCallback(new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        super.onPageSelected(position);
                        tvImageIndex.setText((position + 1) + "/" + urls.size());
                    }
                });
            } else {
                tvImageIndex.setVisibility(View.GONE);
            }
        }
    }

    // ==========================================
    // 6. HÀM TẢI ĐÁNH GIÁ (REVIEW)
    // ==========================================
    private void loadReviews(int productId) {
        // ĐÃ SỬA LẠI THÀNH getUserService() / getUserApiService() ĐỂ LẤY ĐÁNH GIÁ
        ApiClient.getUserService(tokenManager).getReviewsByProduct(productId).enqueue(new Callback<List<ReviewResponse>>() {
            @Override
            public void onResponse(Call<List<ReviewResponse>> call, Response<List<ReviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewResponse> reviews = response.body();

                    if (reviews.isEmpty()) {
                        rvReviews.setVisibility(View.GONE);
                        tvNoReviews.setVisibility(View.VISIBLE);
                        tvAverageRating.setText("0/5 (0 đánh giá)");
                        mainRatingBar.setRating(0);
                    } else {
                        float averageRating = calculateAverage(reviews);
                        tvAverageRating.setText(String.format("%.1f/5 (%d đánh giá)", averageRating, reviews.size()));
                        mainRatingBar.setRating(averageRating);

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