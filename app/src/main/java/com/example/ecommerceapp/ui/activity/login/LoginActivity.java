package com.example.ecommerceapp.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.CheckBox;
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

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.GoogleLoginRequest;
import com.example.ecommerceapp.data.model.request.LoginRequest;
import com.example.ecommerceapp.data.model.response.LoginResponse;
import com.example.ecommerceapp.ui.activity.home.AdminHomeActivity;
import com.example.ecommerceapp.ui.activity.home.UserHomeActivity;

// --- GOOGLE SIGN IN IMPORTS ---
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
// ------------------------------

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView tvRegister, tvForgotPassword;
    private TextInputEditText etUsernameOrEmail, etPassword;
    private MaterialButton btnLogin;

    // --- GOOGLE SIGN IN VARIABLES ---
    private android.widget.LinearLayout btnLoginGoogle; // Đổi sang LinearLayout và đổi tên biến
    private GoogleSignInClient mGoogleSignInClient;
    // --------------------------------

    private CheckBox chkRememberLogin;
    private ApiService apiService;
    private TokenManager tokenManager;

    // --- GOOGLE SIGN IN LAUNCHER ---
    private final ActivityResultLauncher<Intent> googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        String idToken = account.getIdToken();

                        if (idToken != null) {
                            sendTokenToBackend(idToken);
                        }
                    } catch (ApiException e) {
                        Log.e("GoogleAuth", "Đăng nhập Google thất bại, mã lỗi: " + e.getStatusCode());
                        Toast.makeText(this, "Hủy đăng nhập hoặc có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );
    // -------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int padding = getResources().getDimensionPixelSize(R.dimen.screen_padding_large);
            v.setPadding(
                    padding + systemBars.left,
                    padding + systemBars.top,
                    padding + systemBars.right,
                    padding + systemBars.bottom
            );
            return insets;
        });

        tokenManager = new TokenManager(this);
        apiService = ApiClient.getPublicApiService(); // Sửa lại theo biến của nhóm bạn

        if (tokenManager.isRememberLogin() && tokenManager.getToken() != null) {
//            tokenManager.clearToken();
            checkRememberLogin();
        }

        // --- GOOGLE SIGN IN CONFIG ---
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // -----------------------------

        initViews();
        initEvents();
    }

    private void initViews() {
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        etUsernameOrEmail = findViewById(R.id.etUsernameOrEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        chkRememberLogin = findViewById(R.id.chkRememberLogin);

        // --- GOOGLE SIGN IN VIEW ---
        btnLoginGoogle = findViewById(R.id.btnLoginGoogle); // Khớp chuẩn với ID trong XML
        // ---------------------------
    }

    private void initEvents() {
        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> handleLogin());

        // --- GOOGLE SIGN IN EVENT ---
        // Giả sử nếu bạn chưa thêm nút vào XML thì đoạn này sẽ bị null, nhớ kiểm tra nhé!
        if(btnLoginGoogle != null) {
            btnLoginGoogle.setOnClickListener(v -> {
                mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    googleSignInLauncher.launch(signInIntent);
                });
            });
        }
        // ----------------------------
    }

    // Kiểm tra token hết hạn
    private boolean isTokenExpired(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return true;

            String payload = new String(
                    android.util.Base64.decode(parts[1], android.util.Base64.DEFAULT)
            );

            org.json.JSONObject json = new org.json.JSONObject(payload);
            long exp = json.getLong("exp") * 1000; // giây → ms

            return System.currentTimeMillis() > exp;

        } catch (Exception e) {
            return true; // lỗi coi như hết hạn
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void checkRememberLogin() {
        String token = tokenManager.getToken();

        if (token == null || isTokenExpired(token)) {
            tokenManager.clearToken();
            tokenManager.setRememberLogin(false);
            tokenManager.clearUserId();
            goToLogin();
            return;
        }

        String role = tokenManager.getRole();

        if (isTokenExpired(token)) {
            tokenManager.clearToken();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Intent intent;

        if ("ADMIN".equals(role)) {
            intent = new Intent(this, AdminHomeActivity.class);
        } else {
            intent = new Intent(this, UserHomeActivity.class);
        }

        startActivity(intent);
        finish();
    }

    // --- HÀM GỬI TOKEN GOOGLE XUỐNG SERVER ---
    private void sendTokenToBackend(String idToken) {
        Toast.makeText(this, "Đang xác thực với Server...", Toast.LENGTH_SHORT).show();

        GoogleLoginRequest request = new GoogleLoginRequest(idToken);

        apiService.loginWithGoogle(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();

                    // Tái sử dụng logic lưu token và phân quyền y hệt như đăng nhập thường
                    tokenManager.saveToken(user.token);
                    tokenManager.saveRole(user.role);
                    tokenManager.setRememberLogin(true);
                    tokenManager.saveUserId(user.id);

                    Toast.makeText(LoginActivity.this, "Đăng nhập Google thành công!", Toast.LENGTH_SHORT).show();

                    switch (user.role) {
                        case "ADMIN":
                            startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                            break;
                        case "SELLER":
                        case "CUSTOMER":
                            startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "Role không hợp lệ: " + user.role, Toast.LENGTH_SHORT).show();
                            tokenManager.clearToken();
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            finish();
                            break;
                    }
                    finish();
                } else {
                    String error = parseError(response);
                    Toast.makeText(LoginActivity.this, "Lỗi từ Server: " + error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // -----------------------------------------

    // Hàm xử lý đăng nhập thường (GIỮ NGUYÊN)
    private void handleLogin() {
        String input = Objects.requireNonNull(etUsernameOrEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(etPassword.getText()).toString();

        if (TextUtils.isEmpty(input)) {
            etUsernameOrEmail.setError("Không được để trống");
            etUsernameOrEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Không được để trống");
            etPassword.requestFocus();
            return;
        }

        if (input.contains("@")) {
            if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                etUsernameOrEmail.setError("Email không hợp lệ");
                etUsernameOrEmail.requestFocus();
                return;
            }
        } else {
            if (input.length() < 7) {
                etUsernameOrEmail.setError("Username phải >= 7 ký tự");
                etUsernameOrEmail.requestFocus();
                return;
            }
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải >= 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        LoginRequest request = new LoginRequest();
        if (input.contains("@")) {
            request.email = input;
        } else {
            request.username = input;
        }
        request.password = password;

        apiService.loginUser(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse user = response.body();

                    tokenManager.saveToken(user.token);
                    tokenManager.saveRole(user.role);
                    tokenManager.setRememberLogin(chkRememberLogin.isChecked());
                    tokenManager.saveUserId(user.id);

                    Toast.makeText(LoginActivity.this, "Login success: " + user.role, Toast.LENGTH_SHORT).show();

                    switch (user.role) {
                        case "ADMIN":
                            startActivity(new Intent(LoginActivity.this, AdminHomeActivity.class));
                            break;
                        case "SELLER":
                        case "CUSTOMER":
                            startActivity(new Intent(LoginActivity.this, UserHomeActivity.class));
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "Role không hợp lệ: " + user.role, Toast.LENGTH_SHORT).show();
                            tokenManager.clearToken();
                            startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                            finish();
                            break;
                    }
                    finish();
                } else {
                    String error = parseError(response);
                    showFieldError(error);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() == null) return "UNKNOWN_ERROR";
            return response.errorBody().string().trim();
        } catch (Exception e) {
            return "PARSE_ERROR";
        }
    }

    private void showFieldError(String error) {
        etUsernameOrEmail.setError(null);
        etPassword.setError(null);

        switch (error) {
            case "WRONG_PASSWORD":
                etPassword.setError("Sai mật khẩu");
                etPassword.requestFocus();
                etPassword.setSelection(etPassword.length());
                break;
            case "USER_NOT_FOUND":
                etUsernameOrEmail.setError("Tài khoản không tồn tại");
                etUsernameOrEmail.requestFocus();
                break;
            case "ACCOUNT_BLOCKED":
                etUsernameOrEmail.setError("Tài khoản đã bị khóa");
                etUsernameOrEmail.requestFocus();
                break;
            case "INVALID_INPUT":
                etUsernameOrEmail.setError("Dữ liệu không hợp lệ");
                etUsernameOrEmail.requestFocus();
                break;
            case "ACCOUNT_USER_GOOGLE_LOGIN":
                etUsernameOrEmail.setError("Tài khoản này đăng nhập bằng Google");
                etUsernameOrEmail.requestFocus();
                break;
            default:
                etUsernameOrEmail.setError("Lỗi: " + error);
                etUsernameOrEmail.requestFocus();
                break;
        }
    }
}