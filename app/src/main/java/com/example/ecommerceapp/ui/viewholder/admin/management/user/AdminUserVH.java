package com.example.ecommerceapp.ui.viewholder.admin.management.user;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserResponse;
import com.google.android.material.imageview.ShapeableImageView;

public class AdminUserVH extends RecyclerView.ViewHolder {

    private final ShapeableImageView ivAvatar;
    private final TextView tvName;
    private final TextView tvStatus;
    private final TextView tvEmail;
    private final TextView tvPhone;
    private final TextView tvRole;

    public AdminUserVH(@NonNull View itemView) {
        super(itemView);
        ivAvatar = itemView.findViewById(R.id.ivAvatar);
        tvName = itemView.findViewById(R.id.tvName);
        tvStatus = itemView.findViewById(R.id.tvStatus);
        tvEmail = itemView.findViewById(R.id.tvEmail);
        tvPhone = itemView.findViewById(R.id.tvPhone);
        tvRole = itemView.findViewById(R.id.tvRole);
    }

    @SuppressLint("SetTextI18n")
    public void bind(AdminUserResponse user) {
        tvName.setText(user.getFullName() != null ? user.getFullName() : "Chưa cập nhật");
        tvEmail.setText("Email: " + (user.getEmail() != null ? user.getEmail() : "Trống"));
        tvPhone.setText("Số điện thoại: " + (user.getPhone() != null ? user.getPhone() : "Trống"));

        // Status
        if (user.getStatus() == UserStatus.ACTIVE) {
            tvStatus.setText("Hoạt động");
            tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_dark));            tvStatus.setBackgroundResource(R.drawable.bg_status_active);
        } else {
            tvStatus.setText("Vô hiệu hóa");
            tvStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_dark));
            tvStatus.setBackgroundResource(R.drawable.bg_status_blocked);
        }

        // Role
        if (user.getRole() == Role.SELLER) {
            tvRole.setText("Người bán");
            tvRole.setBackgroundResource(R.drawable.bg_role_seller);
        } else {
            tvRole.setText("Khách hàng");
            tvRole.setBackgroundResource(R.drawable.bg_role_customer);
        }

        // Avatar
        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            Glide.with(itemView.getContext())
                    .load(user.getAvatar())
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_profile);
        }
    }
}
