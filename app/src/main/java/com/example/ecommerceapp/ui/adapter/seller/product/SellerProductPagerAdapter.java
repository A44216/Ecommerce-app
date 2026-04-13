package com.example.ecommerceapp.ui.adapter.seller.product;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.product.SellerProductListFragment;

public class SellerProductPagerAdapter extends FragmentStateAdapter {

    private final FragmentManager fragmentManager;

    public SellerProductPagerAdapter(@NonNull FragmentActivity fa) {
        super(fa);
        this.fragmentManager = fa.getSupportFragmentManager();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        switch (position) {
            case 0:
                return SellerProductListFragment.newInstance("PENDING");
            case 1:
                return SellerProductListFragment.newInstance("APPROVED");
            default:
                return SellerProductListFragment.newInstance("REJECTED");
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setKeywordToAll(String keyword) {

        for (Fragment fragment : fragmentManager.getFragments()) {

            if (fragment instanceof SellerProductListFragment) {
                ((SellerProductListFragment) fragment).setKeyword(keyword);
            }
        }
    }

    public void reloadAll() {

        for (Fragment fragment : fragmentManager.getFragments()) {

            if (fragment instanceof SellerProductListFragment) {
                ((SellerProductListFragment) fragment).reload();
            }
        }
    }
}