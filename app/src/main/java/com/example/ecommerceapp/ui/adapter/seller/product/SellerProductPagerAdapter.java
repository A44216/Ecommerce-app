package com.example.ecommerceapp.ui.adapter.seller.product;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.product.SellerProductListFragment;

public class SellerProductPagerAdapter extends FragmentStateAdapter {

    private String keyword = "";

    public SellerProductPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setKeyword(String keyword) {
        this.keyword = (keyword == null) ? "" : keyword;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        String status;

        switch (position) {
            case 0:
                status = "PENDING";
                break;
            case 1:
                status = "APPROVED";
                break;
            case 2:
                status = "REJECTED";
                break;
            default:
                status = "PENDING";
        }

        return SellerProductListFragment.newInstance(status, keyword);
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return (position + keyword).hashCode();
    }

    @Override
    public boolean containsItem(long itemId) {
        return false;
    }
}