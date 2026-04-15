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

        // 2. Nhận Product ID từ Intent
        productId = getIntent().getIntExtra("PRODUCT_ID", -1);
        if (productId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

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
        ReviewRequest request = new ReviewRequest(userId, productId, rating, comment);

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