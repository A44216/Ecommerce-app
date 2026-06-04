package com.example.ecommerceapp.ui.activity.home.user.review;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ReviewRequest;
import com.example.ecommerceapp.data.model.response.ReviewResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private TextView tvRatingHint;
    private EditText edtReviewComment;
    private Button btnSubmitReview;

    private int productId = -1;
    private int orderItemId = -1;
    private int reviewId = -1;
    private String mode = "";
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // 1. Ánh xạ View
        ratingBar = findViewById(R.id.ratingBar);
        tvRatingHint = findViewById(R.id.tvRatingHint);
        edtReviewComment = findViewById(R.id.edtReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);
        findViewById(R.id.btnBackReview).setOnClickListener(v -> finish());

        tokenManager = TokenManager.getInstance(this);

        // 2. Nhận Product ID và Order Item ID từ Intent
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        orderItemId = getIntent().getIntExtra("ORDER_ITEM_ID", -1);
        reviewId = getIntent().getIntExtra("REVIEW_ID", -1);
        mode = getIntent().getStringExtra("MODE");
        
        if (productId == -1 || orderItemId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm hoặc đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if ("VIEW".equals(mode)) {
            // Chế độ xem: Tắt tương tác và fetch dữ liệu
            ratingBar.setIsIndicator(true);
            edtReviewComment.setEnabled(false);
            btnSubmitReview.setVisibility(android.view.View.GONE);
            tvRatingHint.setText("Đang tải đánh giá...");
            loadReviewData();
        } else {
            // 3. Sự kiện thay đổi chữ khi vuốt sao
            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                if (rating == 1) tvRatingHint.setText("Rất tệ");
                else if (rating == 2) tvRatingHint.setText("Tệ");
                else if (rating == 3) tvRatingHint.setText("Bình thường");
                else if (rating == 4) tvRatingHint.setText("Tốt");
                else if (rating == 5) tvRatingHint.setText("Tuyệt vời");
                else tvRatingHint.setText("Vui lòng chọn mức độ hài lòng");
            });

            // 4. Sự kiện bấm nút Gửi
            btnSubmitReview.setOnClickListener(v -> submitReview());
        }
    }

    private void loadReviewData() {
        if (reviewId == -1) {
            tvRatingHint.setText("Lỗi: Không tìm thấy ID đánh giá!");
            return;
        }

        ApiClient.getUserService(tokenManager).getReviewById(reviewId).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ReviewResponse review = response.body();
                    ratingBar.setRating(review.getRating());
                    edtReviewComment.setText(review.getComment());
                    tvRatingHint.setText("Đánh giá của bạn");
                } else {
                    tvRatingHint.setText("Lỗi khi tải đánh giá");
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                tvRatingHint.setText("Lỗi kết nối mạng");
            }
        });
    }

    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = edtReviewComment.getText().toString().trim();
        int userId = (int) tokenManager.getUserId();

        // Validate cơ bản
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.isEmpty()) {
            Toast.makeText(this, "Vui lòng viết vài lời nhận xét nhé!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo cục dữ liệu Request giống hệt BE yêu cầu
        ReviewRequest request = new ReviewRequest(userId, productId, orderItemId, rating, comment);

        // HIỂN THỊ LOADING Ở ĐÂY (NẾU CÓ)
        btnSubmitReview.setEnabled(false);
        btnSubmitReview.setText("Đang gửi...");

        // Gọi API (Giả sử bạn để API trong ProductApiService hoặc UserApiService)
        // LƯU Ý: Thay thế apiService bằng đúng tên file chứa API submitReview của bạn
        ApiClient.getUserService(tokenManager).submitReview(request).enqueue(new Callback<ReviewResponse>() {
            @Override
            public void onResponse(Call<ReviewResponse> call, Response<ReviewResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ReviewActivity.this, "Cảm ơn bạn đã đánh giá!", Toast.LENGTH_SHORT).show();
                    finish(); // Thành công -> Đóng màn hình
                } else {
                    btnSubmitReview.setEnabled(true);
                    btnSubmitReview.setText("Gửi đánh giá");
                    Toast.makeText(ReviewActivity.this, "Lỗi khi gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewResponse> call, Throwable t) {
                btnSubmitReview.setEnabled(true);
                btnSubmitReview.setText("Gửi đánh giá");
                Toast.makeText(ReviewActivity.this, "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}