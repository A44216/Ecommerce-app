package com.example.ecommerceapp.ui.adapter.seller.review;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.review.SellerReviewListFragment;

public class SellerReviewPagerAdapter extends FragmentStateAdapter {

    private final int productId;

    public SellerReviewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int productId) {
        super(fragmentActivity);
        this.productId = productId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        boolean isReplied = (position == 1);

        return SellerReviewListFragment.newInstance(productId, isReplied);
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}