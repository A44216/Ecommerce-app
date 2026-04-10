package com.example.ecommerceapp.ui.activity.home.seller.review;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.adapter.seller.review.SellerReviewPagerAdapter;
import com.example.ecommerceapp.ui.fragment.seller.review.SellerReviewListFragment;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerReviewViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerReviewViewModelFactory;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SellerReviewActivity extends AppCompatActivity {

    private ImageView ivBack;
    private MaterialButton btnSortRating, btnSortTime;
    private TabLayout tabReview;
    private ViewPager2 vpReview;

    private SellerReviewViewModel viewModel;

    private int productId;

    private boolean isSortRatingDesc = true;
    private boolean isSortTimeDesc = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_review);

        productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) {
            finish();
            return;
        }

        initViews();

        viewModel = new ViewModelProvider(this,
                new SellerReviewViewModelFactory(TokenManager.getInstance(this)))
                .get(SellerReviewViewModel.class);

        setupViewPager();
        setListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        btnSortRating = findViewById(R.id.btnSortRating);
        btnSortTime = findViewById(R.id.btnSortTime);
        tabReview = findViewById(R.id.tabReview);
        vpReview = findViewById(R.id.vpReview);
    }

    private void setupViewPager() {

        SellerReviewPagerAdapter pagerAdapter = new SellerReviewPagerAdapter(this, productId);
        vpReview.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabReview, vpReview, (tab, position) -> {
            tab.setText(position == 0 ? "Chưa trả lời" : "Đã trả lời");
        }).attach();
    }

    private void setListeners() {

        ivBack.setOnClickListener(v -> finish());

        btnSortRating.setOnClickListener(v -> {

            String sort = isSortRatingDesc
                    ? "rating_asc,time_desc"
                    : "rating_desc,time_desc";

            isSortRatingDesc = !isSortRatingDesc;

            updateSort(sort);
        });

        btnSortTime.setOnClickListener(v -> {

            String sort = isSortTimeDesc
                    ? "time_asc,rating_desc"
                    : "time_desc,rating_desc";

            isSortTimeDesc = !isSortTimeDesc;

            updateSort(sort);
        });
    }

    private void updateSort(String sort) {

        int position = vpReview.getCurrentItem();
        boolean isReplied = (position == 1);

        viewModel.setSort(sort, productId, isReplied);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("f" + position);

        if (fragment instanceof SellerReviewListFragment) {
            ((SellerReviewListFragment) fragment).resetAndReload();
        }
    }
}