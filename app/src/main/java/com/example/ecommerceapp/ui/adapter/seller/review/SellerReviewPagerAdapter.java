package com.example.ecommerceapp.ui.adapter.seller.review;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.ui.fragment.seller.review.SellerReviewListFragment;

import android.util.Log;

public class SellerReviewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "SellerReviewPagerAdapter";
    private final int productId;

    public SellerReviewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int productId) {
        super(fragmentActivity);
        this.productId = productId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d(TAG, "createFragment position=" + position);
        return SellerReviewListFragment.newInstance(productId);
    }

    @Override
    public int getItemCount() {
        return 1; // hiện tại 1 tab
    }
}