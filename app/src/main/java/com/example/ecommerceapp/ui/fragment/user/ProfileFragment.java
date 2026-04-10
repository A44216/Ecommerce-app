package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.activity.home.user.order.UserOrderHistoryActivity;
import com.example.ecommerceapp.ui.activity.home.user.editprofile.EditUserProfileActivity;
import com.example.ecommerceapp.utils.CartManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.model.response.UserProfileResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    private TextView tvCartBadge;
    private TextView tvUsername;
    private TextView tvFollowers;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Ánh xạ icon Giỏ hàng
        ImageView ivCartProfile = view.findViewById(R.id.ivCartProfile);
        tvCartBadge = view.findViewById(R.id.tvCartBadge);

        ivCartProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        // 1. Ánh xạ chữ "Xem lịch sử mua hàng >" và 4 nút trạng thái
        TextView tvViewOrderHistory = view.findViewById(R.id.tvViewOrderHistory);
        View btnPending = view.findViewById(R.id.btnPending);
        View btnProcessing = view.findViewById(R.id.btnProcessing);
        View btnShipping = view.findViewById(R.id.btnShipping);
        View btnDelivered = view.findViewById(R.id.btnDelivered);

        // 2. Bắt sự kiện Click để chuyển trang & truyền Trạng thái tương ứng
        tvViewOrderHistory.setOnClickListener(v -> openOrderHistory("ALL"));

        if (btnPending != null) btnPending.setOnClickListener(v -> openOrderHistory("PENDING"));
        if (btnProcessing != null) btnProcessing.setOnClickListener(v -> openOrderHistory("PROCESSING"));
        if (btnShipping != null) btnShipping.setOnClickListener(v -> openOrderHistory("SHIPPING"));
        if (btnDelivered != null) btnDelivered.setOnClickListener(v -> openOrderHistory("DELIVERED"));


        TextView tvUsername = view.findViewById(R.id.tvUsername);
        tvFollowers = view.findViewById(R.id.tvFollowers);
        TokenManager tokenManager = TokenManager.getInstance(getContext());
        long currentUserId = tokenManager.getUserId();

        if (currentUserId != -1) {
            UserService apiService = ApiClient.getUserService(tokenManager);

            // Gọi API
            apiService.getUserProfile(currentUserId).enqueue(new Callback<UserProfileResponse>() {
                @Override
                public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserProfileResponse user = response.body();

                        // Ưu tiên hiển thị Tên đầy đủ (fullName), nếu không có thì hiện Username
                        String displayName = user.getFullName() != null ? user.getFullName() : user.getUsername();
                        tvUsername.setText(displayName);

                        // Tận dụng dòng ở dưới để hiển thị Email hoặc SĐT thay cho "0 người theo dõi"
                        String extraInfo = user.getEmail() != null ? user.getEmail() : (user.getPhone() != null ? user.getPhone() : "");
                        tvFollowers.setText(extraInfo);
                    }
                }

                @Override
                public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                    tvUsername.setText("Khách hàng #" + currentUserId);
                    Toast.makeText(getContext(), "Không thể tải thông tin: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            tvUsername.setText("Chưa đăng nhập");
            tvFollowers.setText("Vui lòng đăng nhập");
        }

        //  CHỨC NĂNG ĐĂNG XUẤT
        Button btnLogout = view.findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // 1. Xóa toàn bộ dữ liệu đăng nhập
            tokenManager.clearAllData();

            // 2. Xóa sạch giỏ hàng
            CartManager.getInstance().clearCart();

            // 3. Chuyển hướng về màn hình Đăng nhập & Xóa lịch sử trang cũ
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(getContext(), "Đã đăng xuất thành công!", Toast.LENGTH_SHORT).show();
        });

        ImageView ivSettings = view.findViewById(R.id.ivSettings);

        ivSettings.setOnClickListener(v -> {
            // Nhớ đảm bảo bạn đã import Intent và EditProfileActivity ở trên cùng file nhé!
            Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.editprofile.EditUserProfileActivity.class);
            startActivity(intent);
        });

        tvUsername.setOnClickListener(v -> ivSettings.performClick());

        ImageView ivAvatar = view.findViewById(R.id.ivAvatar);
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> ivSettings.performClick());
        }
        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        updateCartBadge();
    }
    // Hàm phụ trợ kiểm tra và hiển thị chấm đỏ
    private void updateCartBadge() {
        if (tvCartBadge != null) {
            int totalItems = CartManager.getInstance().getTotalQuantity();

            if (totalItems > 0) {
                tvCartBadge.setVisibility(View.VISIBLE); // Hiện chấm đỏ

                // Nếu > 99 thì chỉ hiện "99+" cho đỡ bị tràn viền
                if (totalItems > 99) {
                    tvCartBadge.setText("99+");
                } else {
                    tvCartBadge.setText(String.valueOf(totalItems));
                }
            } else {
                tvCartBadge.setVisibility(View.GONE); // Ẩn chấm đỏ nếu giỏ trống
            }
        }
    }

    // Hàm phụ trợ giúp chuyển trang và mang theo trạng thái (Status)
    private void openOrderHistory(String status) {
        Intent intent = new Intent(getActivity(), UserOrderHistoryActivity.class);
        intent.putExtra("ORDER_STATUS", status);
        startActivity(intent);
    }
}