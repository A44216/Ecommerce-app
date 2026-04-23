package com.example.ecommerceapp.ui.activity.home.seller.review;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.SellerReviewRepository;
import com.example.ecommerceapp.ui.adapter.seller.review.SellerReviewPagerAdapter;
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

        SellerReviewRepository repository = new SellerReviewRepository(TokenManager.getInstance(this));
        viewModel = new ViewModelProvider(this, new SellerReviewViewModelFactory(repository)).get(SellerReviewViewModel.class);

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

        vpReview.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                boolean isReplied = (position == 1);

                // luôn reload tab mỗi lần chuyển
                viewModel.loadReviews(productId, isReplied, 0, 10);
            }
        });

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

            // Đổi icon
            if (isSortRatingDesc) {
                btnSortRating.setIconResource(R.drawable.ic_arrow_up);
            } else {
                btnSortRating.setIconResource(R.drawable.ic_arrow_down);
            }

            isSortRatingDesc = !isSortRatingDesc;

            updateSortUI(true);

            updateSort(sort);
        });

        btnSortTime.setOnClickListener(v -> {

            String sort = isSortTimeDesc
                    ? "time_asc,rating_desc"
                    : "time_desc,rating_desc";

            // Đổi icon
            if (isSortTimeDesc) {
                btnSortTime.setIconResource(R.drawable.ic_arrow_up);
            } else {
                btnSortTime.setIconResource(R.drawable.ic_arrow_down);
            }

            isSortTimeDesc = !isSortTimeDesc;

            updateSortUI(false);

            updateSort(sort);
        });
    }

    private void updateSort(String sort) {

        int position = vpReview.getCurrentItem();
        boolean isReplied = (position == 1);

        viewModel.setSort(sort, productId, isReplied);

        viewModel.loadReviews(productId, isReplied, 0, 10);
    }

    // Hàm đổi màu btnSort được chọn
    private void updateSortUI(boolean isRatingActive) {

        if (isRatingActive) {

            // Rating ACTIVE
            btnSortRating.setBackgroundTintList(getColorStateList(R.color.blue));
            btnSortRating.setTextColor(getColor(R.color.white));
            btnSortRating.setIconTintResource(R.color.white);

            // Time INACTIVE
            btnSortTime.setBackgroundTintList(getColorStateList(R.color.background_light));
            btnSortTime.setTextColor(getColor(R.color.blue));
            btnSortTime.setIconTintResource(R.color.blue);

        } else {

            // Time ACTIVE
            btnSortTime.setBackgroundTintList(getColorStateList(R.color.blue));
            btnSortTime.setTextColor(getColor(R.color.white));
            btnSortTime.setIconTintResource(R.color.white);
            // Rating INACTIVE
            btnSortRating.setBackgroundTintList(getColorStateList(R.color.background_light));
            btnSortRating.setTextColor(getColor(R.color.blue));
            btnSortRating.setIconTintResource(R.color.blue);
        }
    }

}