package com.example.ecommerceapp.ui.fragment.admin.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.home.admin.profile.AdminCategoryActivity;
import com.example.ecommerceapp.ui.activity.home.admin.profile.AdminChangePasswordActivity;
import com.example.ecommerceapp.ui.activity.home.admin.profile.AdminProfileInfoActivity;
import com.example.ecommerceapp.ui.activity.home.admin.profile.AdminVoucherActivity;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;

public class AdminProfileFragment extends Fragment {

    private View rootView;

    // views
    private LinearLayout itemProfileInfo, itemVoucher, itemCategory, itemChangePassword, itemLogout;

    public static AdminProfileFragment newInstance() {
        return new AdminProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        initViews();
        setListeners();

        return rootView;
    }

    private void initViews() {
        itemProfileInfo = rootView.findViewById(R.id.itemProfileInfo);
        itemVoucher = rootView.findViewById(R.id.itemVoucher);
        itemCategory = rootView.findViewById(R.id.itemCategory);
        itemChangePassword = rootView.findViewById(R.id.itemChangePassword);
        itemLogout = rootView.findViewById(R.id.itemLogout);
    }

    private void setListeners() {

        itemProfileInfo.setOnClickListener(v -> openProfileInfo());

        itemVoucher.setOnClickListener(v -> openVoucher());

        itemCategory.setOnClickListener(v -> openCategory());

        itemChangePassword.setOnClickListener(v -> openChangePassword());

        itemLogout.setOnClickListener(v -> logout());
    }

    private void openProfileInfo() {
        startActivity(new Intent(requireContext(), AdminProfileInfoActivity.class));
    }

    private void openVoucher() {
        startActivity(new Intent(requireContext(), AdminVoucherActivity.class));
    }

    private void openCategory() {
        startActivity(new Intent(requireContext(), AdminCategoryActivity.class));
    }

    private void openChangePassword() {
        startActivity(new Intent(requireContext(), AdminChangePasswordActivity.class));
    }

    private void logout() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {

                    // Clear toàn bộ session
                    TokenManager.getInstance(requireContext()).logout();

                    // Chuyển về Login và clear stack
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}