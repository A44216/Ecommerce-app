package com.example.ecommerceapp.ui.adapter.admin.management.coupon;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.admin.management.coupon.AdminCouponFragment;

import java.util.HashMap;
import java.util.Map;

public class AdminCouponPagerAdapter extends FragmentStateAdapter {

    private final Map<Integer, AdminCouponFragment> fragments = new HashMap<>();

    public AdminCouponPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        AdminCouponFragment fragment = AdminCouponFragment.newInstance(position);
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 4; // Hoạt động, Vô hiệu, Hết hạn, Đã xóa
    }

    public void searchAll(String keyword) {
        for (AdminCouponFragment fragment : fragments.values()) {
            if (fragment != null && fragment.isAdded()) {
                fragment.search(keyword);
            }
        }
    }
}
