package com.example.ecommerceapp.ui.activity.home.admin.management.complaint;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminComplaintService;
import com.example.ecommerceapp.data.enums.ComplaintStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.admin.management.AdminReplyComplaintRequest;
import com.example.ecommerceapp.data.model.response.admin.management.complaint.AdminComplaintDetailResponse;
import com.example.ecommerceapp.ui.activity.home.admin.management.user.AdminUserDetailActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.format.DateTimeFormatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminComplaintDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvComplaintCode, tvComplaintStatus, tvUsername, tvFullName, tvCreatedAt, tvContent;
    
    private CardView cvResolvedInfo;
    private TextView tvResolvedBy, tvResolvedAt, tvAdminResponse;
    
    private LinearLayout llActionArea, llReplyContainer;
    private MaterialButton btnReject, btnResolve, btnCancelReply, btnSubmitReply;
    private TextInputLayout tilReplyContent;
    private TextInputEditText edtReplyContent;
    private TextView tvReplyTitle;
    private ProgressBar progressBar;

    private AdminComplaintService adminComplaintService;
    private int complaintId = -1;
    
    private ComplaintStatus pendingActionStatus = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_complaint_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adminComplaintService = ApiClient.getAdminComplaintService(TokenManager.getInstance(this));
        
        complaintId = getIntent().getIntExtra("complaintId", -1);
        
        initViews();
        setupListeners();
        
        if (complaintId != -1) {
            fetchComplaintDetail();
        } else {
            Toast.makeText(this, "ID khiếu nại không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvComplaintCode = findViewById(R.id.tvComplaintCode);
        tvComplaintStatus = findViewById(R.id.tvComplaintStatus);
        tvUsername = findViewById(R.id.tvUsername);
        tvFullName = findViewById(R.id.tvFullName);
        tvCreatedAt = findViewById(R.id.tvCreatedAt);
        tvContent = findViewById(R.id.tvContent);
        
        cvResolvedInfo = findViewById(R.id.cvResolvedInfo);
        tvResolvedBy = findViewById(R.id.tvResolvedBy);
        tvResolvedAt = findViewById(R.id.tvResolvedAt);
        tvAdminResponse = findViewById(R.id.tvAdminResponse);
        
        llActionArea = findViewById(R.id.llActionArea);
        llReplyContainer = findViewById(R.id.llReplyContainer);
        
        btnReject = findViewById(R.id.btnReject);
        btnResolve = findViewById(R.id.btnResolve);
        btnCancelReply = findViewById(R.id.btnCancelReply);
        btnSubmitReply = findViewById(R.id.btnSubmitReply);
        
        tilReplyContent = findViewById(R.id.tilReplyContent);
        edtReplyContent = findViewById(R.id.edtReplyContent);
        tvReplyTitle = findViewById(R.id.tvReplyTitle);
        
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        btnResolve.setOnClickListener(v -> {
            pendingActionStatus = ComplaintStatus.RESOLVED;
            showReplyContainer("Giải quyết khiếu nại");
        });

        btnReject.setOnClickListener(v -> {
            pendingActionStatus = ComplaintStatus.REJECTED;
            showReplyContainer("Từ chối khiếu nại");
        });

        btnCancelReply.setOnClickListener(v -> {
            hideReplyContainer();
        });

        btnSubmitReply.setOnClickListener(v -> submitReply());
    }

    private void showReplyContainer(String title) {
        llActionArea.setVisibility(View.GONE);
        llReplyContainer.setVisibility(View.VISIBLE);
        tvReplyTitle.setText(title);
        tilReplyContent.setError(null);
    }

    private void hideReplyContainer() {
        llReplyContainer.setVisibility(View.GONE);
        llActionArea.setVisibility(View.VISIBLE);
        edtReplyContent.setText("");
        pendingActionStatus = null;
        tilReplyContent.setError(null);
    }

    private void fetchComplaintDetail() {
        progressBar.setVisibility(View.VISIBLE);
        adminComplaintService.getComplaintById(complaintId).enqueue(new Callback<AdminComplaintDetailResponse>() {
            @Override
            public void onResponse(Call<AdminComplaintDetailResponse> call, Response<AdminComplaintDetailResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    Toast.makeText(AdminComplaintDetailActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminComplaintDetailResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminComplaintDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindData(AdminComplaintDetailResponse data) {
        tvComplaintCode.setText("Mã: " + (data.getComplaintCode() != null ? data.getComplaintCode() : "N/A"));

        if (data.getUser() != null) {

            tvUsername.setText("Người dùng: " + data.getUser().getUsername());
            tvFullName.setText("Họ và tên: " + data.getUser().getFullName());

            tvUsername.setOnClickListener(v -> {
                Integer userId = data.getUser().getId();
                if (userId != null) {
                    Intent intent = new Intent(AdminComplaintDetailActivity.this, AdminUserDetailActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
            });

            tvUsername.setClickable(true);

        } else {
            tvUsername.setText("Người dùng: N/A");
            tvFullName.setText("Họ và tên: N/A");

            tvUsername.setOnClickListener(null);
        }
        
        tvContent.setText(data.getContent());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (data.getCreatedAt() != null) {
            tvCreatedAt.setText("Ngày gửi: " + data.getCreatedAt().format(formatter));
        }

        // Set status
        if (data.getStatus() != null) {
            switch (data.getStatus()) {
                case "PENDING":
                    tvComplaintStatus.setText("Chờ xử lý");
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.orange));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                    llActionArea.setVisibility(View.VISIBLE);
                    cvResolvedInfo.setVisibility(View.GONE);
                    break;
                case "RESOLVED":
                    tvComplaintStatus.setText("Đã giải quyết");
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_approved);
                    llActionArea.setVisibility(View.GONE);
                    showResolvedInfo(data, formatter);
                    break;
                case "REJECTED":
                    tvComplaintStatus.setText("Đã từ chối");
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(this, R.color.red));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_rejected);
                    llActionArea.setVisibility(View.GONE);
                    showResolvedInfo(data, formatter);
                    break;
                default:
                    llActionArea.setVisibility(View.GONE);
                    cvResolvedInfo.setVisibility(View.GONE);
                    break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showResolvedInfo(AdminComplaintDetailResponse data, DateTimeFormatter formatter) {
        cvResolvedInfo.setVisibility(View.VISIBLE);
        if (data.getResolvedBy() != null) {
            tvResolvedBy.setText("Người xử lý: " + data.getResolvedBy().getUsername());
        } else {
            tvResolvedBy.setText("Người xử lý: Hệ thống");
        }
        
        if (data.getResolvedAt() != null) {
            tvResolvedAt.setText("Thời gian: " + data.getResolvedAt().format(formatter));
        } else {
            tvResolvedAt.setText("Thời gian: N/A");
        }
        
        tvAdminResponse.setText(data.getAdminResponse() != null ? data.getAdminResponse() : "Không có phản hồi");
    }

    private void submitReply() {
        String responseContent = edtReplyContent.getText() != null ? edtReplyContent.getText().toString().trim() : "";
        
        if (responseContent.isEmpty()) {
            tilReplyContent.setError("Vui lòng nhập nội dung phản hồi");
            return;
        }
        tilReplyContent.setError(null);
        
        if (pendingActionStatus == null) return;

        AdminReplyComplaintRequest request = new AdminReplyComplaintRequest(pendingActionStatus, responseContent);
        
        progressBar.setVisibility(View.VISIBLE);
        adminComplaintService.replyComplaint(complaintId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(AdminComplaintDetailActivity.this, "Xử lý khiếu nại thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK); // Notify parent activity to refresh
                    finish();
                } else {
                    Toast.makeText(AdminComplaintDetailActivity.this, "Xử lý thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AdminComplaintDetailActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
