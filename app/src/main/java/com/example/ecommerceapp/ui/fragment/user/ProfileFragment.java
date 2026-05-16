package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.SetPasswordRequest;
import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.home.user.settings.SettingsActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.utils.ImageLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvCartBadge;
    private TextView tvUsername;
    private TextView tvFollowers;
    private ImageView ivAvatar;

    private Button btnLogout;
    private ImageView ivSettings;
    private Button btnSetPassword;

    private TokenManager tokenManager;
    private android.widget.ProgressBar progressBar;
    private View nsvProfileContent;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivSettings = view.findViewById(R.id.ivSettings);
        btnSetPassword = view.findViewById(R.id.btnSetPassword);
        progressBar = view.findViewById(R.id.progressBar);
        nsvProfileContent = view.findViewById(R.id.nsvProfileContent);

        tokenManager = TokenManager.getInstance(getContext());

        ImageView ivCartProfile = view.findViewById(R.id.ivCartProfile);
        ivCartProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserCartActivity.class));
        });

        ImageView ivChatProfile = view.findViewById(R.id.ivChatProfile);
        ivChatProfile.setOnClickListener(v -> {
            if (tokenManager.getUserId() == -1) {
                showLoginRequireDialog();
            } else {
                startActivity(new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.chat.UserConversationListActivity.class));
            }
        });

        TextView tvViewOrderHistory = view.findViewById(R.id.tvViewOrderHistory);
        View btnPending = view.findViewById(R.id.btnPending);
        View btnProcessing = view.findViewById(R.id.btnProcessing);
        View btnShipping = view.findViewById(R.id.btnShipping);
        View btnDelivered = view.findViewById(R.id.btnDelivered);

        tvViewOrderHistory.setOnClickListener(v -> openOrderHistory("ALL"));
        if (btnPending != null) btnPending.setOnClickListener(v -> openOrderHistory("PENDING"));
        if (btnProcessing != null) btnProcessing.setOnClickListener(v -> openOrderHistory("CONFIRMED"));
        if (btnShipping != null) btnShipping.setOnClickListener(v -> openOrderHistory("SHIPPING"));
        if (btnDelivered != null) btnDelivered.setOnClickListener(v -> openOrderHistory("COMPLETED"));

        tvUsername.setOnClickListener(v -> ivSettings.performClick());
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> ivSettings.performClick());
        }

        // Bắt sự kiện mở Popup đặt mật khẩu
        btnSetPassword.setOnClickListener(v -> showSetPasswordDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
        
        // Hide content and show progress bar only if this is the first load or data isn't loaded yet
        if (tvUsername.getText().toString().equals("Tên Người Dùng") || tvUsername.getText().toString().isEmpty()) {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
            if (nsvProfileContent != null) nsvProfileContent.setVisibility(View.INVISIBLE);
        }
        
        loadUserProfile();
    }

    private void loadUserProfile() {
        long currentUserId = tokenManager.getUserId();

        if (currentUserId != -1) {
            btnLogout.setText("Đăng xuất");
            btnLogout.setOnClickListener(v -> {
                tokenManager.clearAllData();
                CartManager.getInstance().clearCart();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
                }
            });

            ivSettings.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            });

            UserService apiService = ApiClient.getUserService(tokenManager);
            apiService.getUserProfile(currentUserId).enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (isAdded() && getContext() != null && response.isSuccessful() && response.body() != null) {
                        UserProfileResponse user = response.body();

                        String displayName = user.getFullName() != null ? user.getFullName() : user.getUsername();
                        tvUsername.setText(displayName);

                        String extraInfo = user.getEmail() != null ? user.getEmail() : (user.getPhone() != null ? user.getPhone() : "");
                        tvFollowers.setText(extraInfo);

                        String avatarUrl = user.getAvatar();
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            ImageLoader.load(getContext(), ivAvatar, avatarUrl);
                        } else {
                            ivAvatar.setImageResource(R.drawable.bg_avatar_placeholder);
                        }

                        // ==========================================
                        // LOGIC ẨN/HIỆN NÚT ĐẶT MẬT KHẨU MỚI (CHUẨN 100%)
                        // ==========================================
                        if (!user.isHasPassword()) {
                            btnSetPassword.setVisibility(View.VISIBLE);
                        } else {
                            btnSetPassword.setVisibility(View.GONE);
                        }
                        
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (nsvProfileContent != null) nsvProfileContent.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    if (isAdded() && getContext() != null) {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (nsvProfileContent != null) nsvProfileContent.setVisibility(View.VISIBLE);
                        tvUsername.setText("Khách hàng #" + currentUserId);
                        Toast.makeText(getContext(), "Không thể tải thông tin: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } else {
            if (isAdded()) {
                tvUsername.setText("Chưa đăng nhập");
                tvFollowers.setText("Vui lòng đăng nhập");
                ivAvatar.setImageResource(R.drawable.bg_avatar_placeholder);

                btnLogout.setText("Đăng nhập");
                btnLogout.setOnClickListener(v -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                });

                ivSettings.setOnClickListener(v -> {
                    showLoginRequireDialog();
                });

                btnSetPassword.setVisibility(View.GONE);
                
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                if (nsvProfileContent != null) nsvProfileContent.setVisibility(View.VISIBLE);
            }
        }
    }

    private void showSetPasswordDialog() {
        if (getContext() == null) return;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Thiết lập mật khẩu");
        builder.setMessage("Tạo mật khẩu để có thể đăng nhập bằng Email cho những lần sau.");

        android.widget.LinearLayout layout = new android.widget.LinearLayout(getContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 10);

        final android.widget.EditText edtPassword = new android.widget.EditText(getContext());
        edtPassword.setHint("Nhập mật khẩu mới (ít nhất 6 ký tự)");
        edtPassword.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(edtPassword);

        final android.widget.EditText edtConfirm = new android.widget.EditText(getContext());
        edtConfirm.setHint("Xác nhận lại mật khẩu");
        edtConfirm.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(edtConfirm);

        builder.setView(layout);

        builder.setNegativeButton("Hủy bỏ", (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton("Lưu mật khẩu", null);

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String pass = edtPassword.getText().toString().trim();
            String confirm = edtConfirm.getText().toString().trim();

            if (pass.length() < 6) {
                Toast.makeText(getContext(), "Mật khẩu phải từ 6 ký tự trở lên!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pass.equals(confirm)) {
                Toast.makeText(getContext(), "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show();
                return;
            }

            executeSetPassword(pass, dialog);
        });
    }

    private void executeSetPassword(String newPassword, androidx.appcompat.app.AlertDialog dialog) {
        long userId = tokenManager.getUserId();
        if (userId == -1) return;

        SetPasswordRequest request = new SetPasswordRequest(newPassword);

        ApiClient.getUserService(tokenManager).setPassword((int) userId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (isAdded() && getContext() != null) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Thiết lập mật khẩu thành công!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadUserProfile();
                    } else {
                        Toast.makeText(getContext(), "Cập nhật thất bại. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (isAdded() && getContext() != null) {
                    Toast.makeText(getContext(), "Lỗi kết nối mạng!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCartBadge() {
        if (tvCartBadge != null) {
            int totalItems = CartManager.getInstance().getTotalQuantity();
            if (totalItems > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(totalItems > 99 ? "99+" : String.valueOf(totalItems));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
    }

    private void openOrderHistory(String status) {
        if (tokenManager.getUserId() == -1) {
            showLoginRequireDialog();
            return;
        }
        Intent intent = new Intent(getActivity(), UserOrderHistoryActivity.class);
        intent.putExtra("ORDER_STATUS", status);
        startActivity(intent);
    }

    private void showLoginRequireDialog() {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này. Đi đến trang đăng nhập ngay?")
                .setCancelable(true)
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                })
                .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
                .show();
    }
}