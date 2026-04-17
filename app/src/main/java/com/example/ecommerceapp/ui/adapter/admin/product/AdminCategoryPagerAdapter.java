package com.example.ecommerceapp.ui.adapter.admin.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.admin.product.AdminCategoryListFragment;

public class AdminCategoryPagerAdapter extends FragmentStateAdapter {

    public AdminCategoryPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        if (position == 0) {
            return AdminCategoryListFragment.newInstance(
                    AdminCategoryListFragment.TYPE_ALL);
        } else {
            return AdminCategoryListFragment.newInstance(
                    AdminCategoryListFragment.TYPE_DELETED);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}