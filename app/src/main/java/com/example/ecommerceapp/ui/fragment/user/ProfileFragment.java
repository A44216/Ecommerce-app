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
import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.home.user.editprofile.EditUserProfileActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity;
import com.example.ecommerceapp.ui.activity.home.user.settings.SettingsActivity;
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
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvUsername = view.findViewById(R.id.tvUsername);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);
        tokenManager = TokenManager.getInstance(getContext());

        ImageView ivCartProfile = view.findViewById(R.id.ivCartProfile);
        ivCartProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), UserCartActivity.class));
        });

        TextView tvViewOrderHistory = view.findViewById(R.id.tvViewOrderHistory);
        View btnPending = view.findViewById(R.id.btnPending);
        View btnProcessing = view.findViewById(R.id.btnProcessing);
        View btnShipping = view.findViewById(R.id.btnShipping);
        View btnDelivered = view.findViewById(R.id.btnDelivered);

        tvViewOrderHistory.setOnClickListener(v -> openOrderHistory("ALL"));
        if (btnPending != null) btnPending.setOnClickListener(v -> openOrderHistory("PENDING"));
        if (btnProcessing != null) btnProcessing.setOnClickListener(v -> openOrderHistory("PROCESSING"));
        if (btnShipping != null) btnShipping.setOnClickListener(v -> openOrderHistory("SHIPPING"));
        if (btnDelivered != null) btnDelivered.setOnClickListener(v -> openOrderHistory("DELIVERED"));

        Button btnLogout = view.findViewById(R.id.btnLogout);
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

        ImageView ivSettings = view.findViewById(R.id.ivSettings);
        ivSettings.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        });

        tvUsername.setOnClickListener(v -> ivSettings.performClick());
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> ivSettings.performClick());
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
        loadUserProfile();
    }

    private void loadUserProfile() {
        long currentUserId = tokenManager.getUserId();

        if (currentUserId != -1) {
            UserService apiService = ApiClient.getUserService(tokenManager);

            apiService.getUserProfile(currentUserId).enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    // Check isAdded() để tránh lỗi crash khi Fragment đã bị hủy nhưng API mới gọi xong
                    if (isAdded() && getContext() != null && response.isSuccessful() && response.body() != null) {
                        UserProfileResponse user = response.body();

                        String displayName = user.getFullName() != null ? user.getFullName() : user.getUsername();
                        tvUsername.setText(displayName);

                        String extraInfo = user.getEmail() != null ? user.getEmail() : (user.getPhone() != null ? user.getPhone() : "");
                        tvFollowers.setText(extraInfo);

                        // Load Avatar
                        String avatarUrl = user.getAvatar();
                        if (avatarUrl != null && !avatarUrl.isEmpty()) {
                            ImageLoader.load(getContext(), ivAvatar, avatarUrl);
                        } else {
                            ivAvatar.setImageResource(R.drawable.bg_avatar_placeholder);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    if (isAdded() && getContext() != null) {
                        tvUsername.setText("Khách hàng #" + currentUserId);
                        Toast.makeText(getContext(), "Không thể tải thông tin: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            if (isAdded()) {
                tvUsername.setText("Chưa đăng nhập");
                tvFollowers.setText("Vui lòng đăng nhập");
            }
        }
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
        Intent intent = new Intent(getActivity(), UserOrderHistoryActivity.class);
        intent.putExtra("ORDER_STATUS", status);
        startActivity(intent);
    }
}