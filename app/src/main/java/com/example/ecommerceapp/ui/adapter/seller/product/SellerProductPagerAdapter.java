package com.example.ecommerceapp.ui.adapter.seller.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.product.SellerProductListFragment;

import java.util.ArrayList;
import java.util.List;

public class SellerProductPagerAdapter extends FragmentStateAdapter {

    private final List<SellerProductListFragment> fragmentList = new ArrayList<>();

    public SellerProductPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);

        fragmentList.add(SellerProductListFragment.newInstance("PENDING"));
        fragmentList.add(SellerProductListFragment.newInstance("APPROVED"));
        fragmentList.add(SellerProductListFragment.newInstance("REJECTED"));
    }

    public SellerProductListFragment getFragment(int position) {
        return fragmentList.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void setKeywordToAll(String keyword) {
        for (SellerProductListFragment f : fragmentList) {
            f.setKeyword(keyword);
        }
    }

    public void reloadAll() {
        for (SellerProductListFragment f : fragmentList) {
            f.reload();
        }
    }
}