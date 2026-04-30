package com.example.ecommerceapp.ui.adapter.seller.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.order.SellerOrderFragment;
import com.example.ecommerceapp.ui.fragment.seller.order.SellerOrderListFragment;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderPagerAdapter extends FragmentStateAdapter {

    private List<SellerOrderListFragment> fragments = new ArrayList<>();

    public SellerOrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        String status;

        switch (position) {
            case 0: status = "PENDING"; break;
            case 1: status = "CONFIRMED"; break;
            case 2: status = "SHIPPING"; break;
            case 3: status = "COMPLETED"; break;
            case 4: status = "CANCELED"; break;
            default: status = "PENDING";
        }

        SellerOrderListFragment fragment = SellerOrderListFragment.newInstance(status);
        fragments.add(fragment);
        return fragment;
    }

    public List<SellerOrderListFragment> getFragments() {
        return fragments;
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}