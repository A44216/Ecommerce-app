package com.example.ecommerceapp.ui.fragment.admin.management;

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
import com.example.ecommerceapp.ui.activity.home.admin.management.AdminNotificationActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.complaint.AdminComplaintActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.product.AdminCategoryActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.product.AdminProductActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.coupon.AdminCouponActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.shop.AdminShopActivity;
import com.example.ecommerceapp.ui.activity.home.admin.management.user.AdminUserActivity;

public class AdminManagementFragment extends Fragment {

    private LinearLayout itemUser, itemShop, itemProduct, itemCategory, itemCoupon, itemComplaint, itemNotification;

    public static AdminManagementFragment newInstance() {
        return new AdminManagementFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setListeners();
    }

    private void initViews(View view) {
        itemUser = view.findViewById(R.id.itemUser);
        itemShop = view.findViewById(R.id.itemShop);
        itemProduct = view.findViewById(R.id.itemProduct);
        itemCategory = view.findViewById(R.id.itemCategory);
        itemCoupon = view.findViewById(R.id.itemCoupon);
        itemComplaint = view.findViewById(R.id.itemComplaint);
        itemNotification = view.findViewById(R.id.itemNotification);
    }

    private void setListeners() {
        itemUser.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminUserActivity.class)));
        itemShop.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminShopActivity.class)));
        itemProduct.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminProductActivity.class)));
        itemCategory.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminCategoryActivity.class)));
        itemCoupon.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminCouponActivity.class)));
        itemComplaint.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminComplaintActivity.class)));
        itemNotification.setOnClickListener(v -> startActivity(new Intent(requireContext(), AdminNotificationActivity.class)));
    }
}
