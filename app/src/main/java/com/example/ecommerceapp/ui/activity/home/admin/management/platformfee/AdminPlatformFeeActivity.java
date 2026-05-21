package com.example.ecommerceapp.ui.activity.home.admin.management.platformfee;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.admin.platformfee.AdminPlatformFeeRequest;
import com.example.ecommerceapp.data.model.response.admin.platformfee.AdminPlatformFeeResponse;

import java.math.BigDecimal;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminPlatformFeeActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvCurrentFeeRate;
    private TextView tvStatus;
    private LinearLayout layoutStatus;
    private EditText edtNewRate;
    private Button btnUpdate;
    private FrameLayout layoutLoading;

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_platform_fee);

        tokenManager = TokenManager.getInstance(this);

        initViews();
        setListeners();
        loadCurrentFee();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        tvCurrentFeeRate = findViewById(R.id.tvCurrentFeeRate);
        tvStatus = findViewById(R.id.tvStatus);
        layoutStatus = findViewById(R.id.layoutStatus);
        edtNewRate = findViewById(R.id.edtNewRate);
        btnUpdate = findViewById(R.id.btnUpdate);
        layoutLoading = findViewById(R.id.layoutLoading);
    }

    private void setListeners() {
        ivBack.setOnClickListener(v -> finish());
        btnUpdate.setOnClickListener(v -> updatePlatformFee());
    }

    private void loadCurrentFee() {
        showLoading(true);
        ApiClient.getAdminPlatformFeeService(tokenManager).getCurrentFee().enqueue(new Callback<AdminPlatformFeeResponse>() {
            @Override
            public void onResponse(Call<AdminPlatformFeeResponse> call, Response<AdminPlatformFeeResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    AdminPlatformFeeResponse data = response.body();
                    displayFeeData(data);
                } else {
                    Toast.makeText(AdminPlatformFeeActivity.this, "Không thể tải thông tin phí hiện tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminPlatformFeeResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AdminPlatformFeeActivity.this, "Lỗi kết nối máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFeeData(AdminPlatformFeeResponse data) {
        BigDecimal rate = data.getRate();
        if (rate != null) {
            String rateText = rate.stripTrailingZeros().toPlainString() + "%";
            tvCurrentFeeRate.setText(rateText);
            // Pre-populate input for convenience
            edtNewRate.setText(rate.stripTrailingZeros().toPlainString());
        } else {
            tvCurrentFeeRate.setText("0%");
        }

        Boolean isActive = data.getIsActive();
        if (isActive != null && isActive) {
            tvStatus.setText("Đang hoạt động");
            tvStatus.setTextColor(getResources().getColor(R.color.green));
            layoutStatus.setBackgroundResource(R.drawable.bg_status_active);
        } else {
            tvStatus.setText("Không hoạt động");
            tvStatus.setTextColor(getResources().getColor(R.color.red));
            layoutStatus.setBackgroundResource(R.drawable.bg_status_blocked);
        }
    }

    private void updatePlatformFee() {
        String inputRateStr = edtNewRate.getText().toString().trim();
        if (inputRateStr.isEmpty()) {
            edtNewRate.setError("Vui lòng nhập tỷ lệ phí");
            edtNewRate.requestFocus();
            return;
        }

        BigDecimal newRate;
        try {
            newRate = new BigDecimal(inputRateStr);
        } catch (NumberFormatException e) {
            edtNewRate.setError("Tỷ lệ phí không hợp lệ");
            edtNewRate.requestFocus();
            return;
        }

        if (newRate.compareTo(BigDecimal.ZERO) < 0 || newRate.compareTo(new BigDecimal("100")) > 0) {
            edtNewRate.setError("Tỷ lệ phí phải nằm trong khoảng từ 0% đến 100%");
            edtNewRate.requestFocus();
            return;
        }

        final BigDecimal finalRate = newRate;
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận cập nhật")
                .setMessage("Bạn có chắc chắn muốn thay đổi phí nền tảng thành " + finalRate.stripTrailingZeros().toPlainString() + "% không? Mức phí mới này sẽ được áp dụng cho tất cả các đơn hàng tiếp theo.")
                .setPositiveButton("Đồng ý", (dialog, which) -> executeUpdate(finalRate))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeUpdate(BigDecimal newRate) {
        showLoading(true);
        AdminPlatformFeeRequest request = new AdminPlatformFeeRequest(newRate);
        ApiClient.getAdminPlatformFeeService(tokenManager).updateCurrentFee(request).enqueue(new Callback<AdminPlatformFeeResponse>() {
            @Override
            public void onResponse(Call<AdminPlatformFeeResponse> call, Response<AdminPlatformFeeResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(AdminPlatformFeeActivity.this, "Cập nhật phí nền tảng thành công", Toast.LENGTH_SHORT).show();
                    displayFeeData(response.body());
                    edtNewRate.clearFocus();
                } else {
                    Toast.makeText(AdminPlatformFeeActivity.this, "Cập nhật phí thất bại. Vui lòng kiểm tra lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminPlatformFeeResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(AdminPlatformFeeActivity.this, "Lỗi mạng khi cập nhật phí", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            layoutLoading.setVisibility(View.VISIBLE);
            btnUpdate.setEnabled(false);
        } else {
            layoutLoading.setVisibility(View.GONE);
            btnUpdate.setEnabled(true);
        }
    }
}
