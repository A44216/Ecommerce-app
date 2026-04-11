package com.example.ecommerceapp.ui.adapter.seller.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.product.SellerProductListFragment;

import java.util.HashMap;
import java.util.Map;

public class SellerProductPagerAdapter extends FragmentStateAdapter {

    private final Map<Integer, SellerProductListFragment> fragmentMap = new HashMap<>();

    public SellerProductPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        SellerProductListFragment fragment;

        switch (position) {
            case 0:
                fragment = SellerProductListFragment.newInstance("PENDING");
                break;
            case 1:
                fragment = SellerProductListFragment.newInstance("APPROVED");
                break;
            case 2:
                fragment = SellerProductListFragment.newInstance("REJECTED");
                break;
            default:
                fragment = SellerProductListFragment.newInstance("PENDING");
        }

        fragmentMap.put(position, fragment);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setKeywordToAll(String keyword) {
        for (SellerProductListFragment f : fragmentMap.values()) {
            if (f != null) {
                f.setKeyword(keyword);
            }
        }
    }
}