package com.example.ecommerceapp.ui.activity.home.user.editprofile;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ChangeEmailRequest;
import com.example.ecommerceapp.data.model.request.SendOtpRequest;
import com.example.ecommerceapp.data.model.request.VerifyOtpRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeEmailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private LinearLayout llStep1, llStep2;
    private TextView tvCurrentEmail;
    private EditText etOldOtp, etNewEmail, etNewOtp;
    private Button btnSendOldOtp, btnVerifyOldEmail, btnSendNewOtp, btnConfirmChange;

    private AuthService authService;
    private UserService userService;
    private TokenManager tokenManager;

    private String currentEmail;
    private String verifiedOldOtp;

    private CountDownTimer oldOtpTimer, newOtpTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        currentEmail = getIntent().getStringExtra("CURRENT_EMAIL");

        authService = ApiClient.getAuthService();
        tokenManager = TokenManager.getInstance(this);
        userService = ApiClient.getUserService(tokenManager);

        initViews();
        initEvents();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);

        tvCurrentEmail = findViewById(R.id.tvCurrentEmail);
        if (currentEmail != null) tvCurrentEmail.setText(currentEmail);

        etOldOtp = findViewById(R.id.etOldOtp);
        etNewEmail = findViewById(R.id.etNewEmail);
        etNewOtp = findViewById(R.id.etNewOtp);

        btnSendOldOtp = findViewById(R.id.btnSendOldOtp);
        btnVerifyOldEmail = findViewById(R.id.btnVerifyOldEmail);
        btnSendNewOtp = findViewById(R.id.btnSendNewOtp);
        btnConfirmChange = findViewById(R.id.btnConfirmChange);
    }

    private void initEvents() {
        ivBack.setOnClickListener(v -> finish());

        // Step 1
        btnSendOldOtp.setOnClickListener(v -> handleSendOldOtp());
        btnVerifyOldEmail.setOnClickListener(v -> handleVerifyOldOtp());

        // Step 2
        btnSendNewOtp.setOnClickListener(v -> handleSendNewOtp());
        btnConfirmChange.setOnClickListener(v -> handleConfirmChange());
    }

    private void handleSendOldOtp() {
        btnSendOldOtp.setEnabled(false);
        btnSendOldOtp.setText("Đang gửi...");

        SendOtpRequest request = new SendOtpRequest(currentEmail);
        authService.sendUnlinkEmailOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangeEmailActivity.this, "Đã gửi mã OTP về email cũ", Toast.LENGTH_SHORT).show();
                    startOldOtpTimer();
                } else {
                    btnSendOldOtp.setEnabled(true);
                    btnSendOldOtp.setText("Gửi mã");
                    Toast.makeText(ChangeEmailActivity.this, "Lỗi gửi mã OTP", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSendOldOtp.setEnabled(true);
                btnSendOldOtp.setText("Gửi mã");
                Toast.makeText(ChangeEmailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startOldOtpTimer() {
        oldOtpTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                btnSendOldOtp.setText("Gửi lại (" + millisUntilFinished / 1000 + "s)");
            }
            public void onFinish() {
                btnSendOldOtp.setEnabled(true);
                btnSendOldOtp.setText("Gửi lại");
            }
        }.start();
    }

    private void handleVerifyOldOtp() {
        String code = etOldOtp.getText().toString().trim();
        if (code.length() != 6) {
            etOldOtp.setError("Mã OTP phải 6 số");
            return;
        }

        btnVerifyOldEmail.setEnabled(false);
        btnVerifyOldEmail.setText("Đang xác minh...");

        VerifyOtpRequest request = new VerifyOtpRequest(currentEmail, code);
        authService.verifyOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnVerifyOldEmail.setEnabled(true);
                btnVerifyOldEmail.setText("Tiếp tục");

                if (response.isSuccessful()) {
                    verifiedOldOtp = code; // Lưu lại để dùng ở bước cuối
                    llStep1.setVisibility(View.GONE);
                    llStep2.setVisibility(View.VISIBLE);
                } else {
                    etOldOtp.setError("Mã OTP không đúng hoặc đã hết hạn");
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnVerifyOldEmail.setEnabled(true);
                btnVerifyOldEmail.setText("Tiếp tục");
                Toast.makeText(ChangeEmailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSendNewOtp() {
        String newEmail = etNewEmail.getText().toString().trim();
        if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etNewEmail.setError("Email không hợp lệ");
            return;
        }

        btnSendNewOtp.setEnabled(false);
        btnSendNewOtp.setText("Đang gửi...");

        SendOtpRequest request = new SendOtpRequest(newEmail);
        authService.sendVerifyNewEmailOtp(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangeEmailActivity.this, "Đã gửi mã OTP về email mới", Toast.LENGTH_SHORT).show();
                    startNewOtpTimer();
                } else {
                    btnSendNewOtp.setEnabled(true);
                    btnSendNewOtp.setText("Gửi mã");
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "";
                        if (error.contains("EMAIL_ALREADY_EXISTS")) {
                            etNewEmail.setError("Email này đã được sử dụng");
                        } else {
                            Toast.makeText(ChangeEmailActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSendNewOtp.setEnabled(true);
                btnSendNewOtp.setText("Gửi mã");
                Toast.makeText(ChangeEmailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startNewOtpTimer() {
        newOtpTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                btnSendNewOtp.setText("Gửi lại (" + millisUntilFinished / 1000 + "s)");
            }
            public void onFinish() {
                btnSendNewOtp.setEnabled(true);
                btnSendNewOtp.setText("Gửi lại");
            }
        }.start();
    }

    private void handleConfirmChange() {
        String newEmail = etNewEmail.getText().toString().trim();
        String code = etNewOtp.getText().toString().trim();

        if (TextUtils.isEmpty(newEmail) || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            etNewEmail.setError("Email không hợp lệ");
            return;
        }
        if (code.length() != 6) {
            etNewOtp.setError("Mã OTP phải 6 số");
            return;
        }

        btnConfirmChange.setEnabled(false);
        btnConfirmChange.setText("Đang xử lý...");

        ChangeEmailRequest request = new ChangeEmailRequest(newEmail, verifiedOldOtp, code);
        userService.changeEmail(tokenManager.getUserId(), request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(ChangeEmailActivity.this, "Đổi Email thành công!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    btnConfirmChange.setEnabled(true);
                    btnConfirmChange.setText("Hoàn tất thay đổi");
                    try {
                        String error = response.errorBody() != null ? response.errorBody().string() : "";
                        Toast.makeText(ChangeEmailActivity.this, "Lỗi: " + error, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {}
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnConfirmChange.setEnabled(true);
                btnConfirmChange.setText("Hoàn tất thay đổi");
                Toast.makeText(ChangeEmailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (oldOtpTimer != null) oldOtpTimer.cancel();
        if (newOtpTimer != null) newOtpTimer.cancel();
    }
}
