package com.example.ecommerceapp.ui.activity.home.seller.product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.SellerProductRepository;
import com.example.ecommerceapp.data.model.response.seller.product.SellerAssistantResponse;
import com.example.ecommerceapp.ui.activity.home.seller.review.SellerReviewActivity;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerImagePagerAdapter;
import com.example.ecommerceapp.ui.view.FuzzyTriangleView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;
import android.app.ProgressDialog;
import android.widget.Button;

import java.math.RoundingMode;

public class SellerProductDetailActivity extends AppCompatActivity {

    private TextView txtProductCode, txtName, txtPrice, txtCategory, txtStock, txtSold, txtRating, txtStatus, txtDescription;
    private TextView tvViewReviews;
    private ViewPager2 viewPagerImages;
    private ImageView ivBack;
    private WormDotsIndicator dotsIndicator;
    private ScrollView svProductDetail;
    private ProgressBar progressBarProductDetail;
    private Button btnAiAnalysis;

    private SellerProductRepository repository;

    private boolean isIndicatorAttached = false;

    private int productId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_product_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initListeners();

        // init API
        TokenManager tokenManager = TokenManager.getInstance(this);

        repository = new SellerProductRepository(
                ApiClient.getProductService(tokenManager)
        );

        // lấy productId
        productId = getIntent().getIntExtra("productId", -1);

        if (productId != -1) {
            fetchProduct(productId);
        }

    }

    private void initViews() {
        txtProductCode = findViewById(R.id.txtProductCode);
        txtName = findViewById(R.id.txtName);
        txtPrice = findViewById(R.id.txtPrice);
        txtCategory = findViewById(R.id.txtCategory);
        txtStock = findViewById(R.id.txtStock);
        txtSold = findViewById(R.id.txtSold);
        txtRating = findViewById(R.id.txtRating);
        txtStatus = findViewById(R.id.txtStatus);
        txtDescription = findViewById(R.id.txtDescription);
        viewPagerImages = findViewById(R.id.viewPagerImages);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        ivBack = findViewById(R.id.ivBack);
        tvViewReviews = findViewById(R.id.tvViewReviews);
        svProductDetail = findViewById(R.id.svProductDetail);
        progressBarProductDetail = findViewById(R.id.progressBarProductDetail);
        btnAiAnalysis = findViewById(R.id.btnAiAnalysis);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());

        tvViewReviews.setOnClickListener(v -> {
            Intent it = new Intent(SellerProductDetailActivity.this, SellerReviewActivity.class);
            it.putExtra("productId", productId);
            startActivity(it);
        });

        if (btnAiAnalysis != null) {
            btnAiAnalysis.setOnClickListener(v -> fetchAiAnalysis());
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void bindData(SellerProductResponse product) {
        if (progressBarProductDetail != null) progressBarProductDetail.setVisibility(View.GONE);
        if (svProductDetail != null) svProductDetail.setVisibility(View.VISIBLE);

        txtProductCode.setText(product.getProductCode() != null ? product.getProductCode() : "N/A");
        txtName.setText(product.getName());
        txtPrice.setText("💰 " + String.format("%,.0f", product.getPrice()) + " đ");
        txtCategory.setText("🏷️ Danh mục: " + product.getCategoryName());
        txtStock.setText("📦 Kho: " + product.getStock());
        txtSold.setText("🔥 Đã bán: " + product.getSoldCount());
        if (product.getRatingAvg() != null) {
            txtRating.setText("⭐ " + product.getRatingAvg().setScale(1, RoundingMode.HALF_UP));
        } else {
            txtRating.setText("⭐ 0.0");
        }
        boolean isDeleted = Boolean.TRUE.equals(product.getIsDeleted());
        if (isDeleted) {
            txtStatus.setText("Đã xóa");
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
            txtStatus.setBackgroundResource(R.drawable.bg_product_status_rejected);
        } else {
            txtStatus.setText(product.getStatus().getLabel());
            switch (product.getStatus()) {
                case PENDING:
                    txtStatus.setTextColor(ContextCompat.getColor(this, R.color.orange));
                    txtStatus.setBackgroundResource(R.drawable.bg_product_status_pending);
                    break;
                case APPROVED:
                    txtStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
                    txtStatus.setBackgroundResource(R.drawable.bg_product_status_approved);
                    break;
                case REJECTED:
                    txtStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
                    txtStatus.setBackgroundResource(R.drawable.bg_product_status_rejected);
                    break;
                default:
                    txtStatus.setTextColor(ContextCompat.getColor(this, R.color.black));
                    txtStatus.setBackgroundResource(0);
                    break;
            }
        }
        txtDescription.setText(product.getDescription() != null
                ? "Mô tả: " +product.getDescription()
                : "Chưa có mô tả");

        // Load ảnh
        if (product.getImages() != null && !product.getImages().isEmpty()) {

            SellerImagePagerAdapter adapter =
                    new SellerImagePagerAdapter(this, product.getImages());

            viewPagerImages.setAdapter(adapter);

            dotsIndicator.setVisibility(View.VISIBLE);
            dotsIndicator.attachTo(viewPagerImages);

        } else {

            viewPagerImages.setAdapter(null);

            dotsIndicator.setVisibility(View.GONE);
        }

    }

    private void fetchProduct(int id) {
        repository.getProductById(id).enqueue(new retrofit2.Callback<SellerProductResponse>() {

            @Override
            public void onResponse(retrofit2.Call<SellerProductResponse> call,
                                   retrofit2.Response<SellerProductResponse> response) {

                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SellerProductResponse> call, Throwable t) {
                Toast.makeText(SellerProductDetailActivity.this,
                        "Lỗi tải dữ liệu",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAiAnalysis() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Trợ lý AI đang phân tích dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        repository.getProductAnalysis(productId).enqueue(new retrofit2.Callback<SellerAssistantResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SellerAssistantResponse> call, retrofit2.Response<SellerAssistantResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    showAiAnalysisBottomSheet(response.body());
                } else {
                    Toast.makeText(SellerProductDetailActivity.this, "Không thể phân tích sản phẩm lúc này.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SellerAssistantResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SellerProductDetailActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAiAnalysisBottomSheet(SellerAssistantResponse data) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.layout_seller_ai_analysis_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        TextView tvAnalysis = view.findViewById(R.id.tvAnalysis);
        TextView tvRecommendation = view.findViewById(R.id.tvRecommendation);
        FuzzyTriangleView fuzzyTriangleView = view.findViewById(R.id.fuzzyTriangleView);
        Button btnClose = view.findViewById(R.id.btnClose);

        tvAnalysis.setText(data.getAnalysis() != null ? data.getAnalysis() : "Không có phân tích.");
        tvRecommendation.setText(data.getRecommendation() != null ? data.getRecommendation() : "Không có lời khuyên.");

        double r = data.getRatingScore() != null ? data.getRatingScore() : 0.0;
        double s = data.getSoldScore() != null ? data.getSoldScore() : 0.0;
        double p = data.getPriceScore() != null ? data.getPriceScore() : 0.0;

        fuzzyTriangleView.setScores((float) r, (float) s, (float) p);

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

}