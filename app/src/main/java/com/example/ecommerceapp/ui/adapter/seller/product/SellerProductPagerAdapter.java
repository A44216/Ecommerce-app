package com.example.ecommerceapp.ui.adapter.seller.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.product.SellerProductListFragment;

import java.util.ArrayList;
import java.util.List;

public class SellerProductPagerAdapter extends FragmentStateAdapter {

    private final List<SellerProductListFragment> fragments = new ArrayList<>();

    public SellerProductPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

        fragments.add(new SellerProductListFragment());
        fragments.add(new SellerProductListFragment());
        fragments.add(new SellerProductListFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    public SellerProductListFragment getFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}