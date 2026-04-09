package com.example.ecommerceapp.ui.activity.home.seller.review;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.adapter.seller.review.SellerReviewPagerAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SellerReviewActivity extends AppCompatActivity {

    private static final String TAG = "SellerReviewActivity";

    private ImageView ivBack;
    private MaterialButton btnSortRating, btnSortTime;
    private TabLayout tabReview;
    private ViewPager2 vpReview;

    private SellerReviewPagerAdapter pagerAdapter;

    private int productId = 1; // TODO: lấy từ intent nếu bạn truyền

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_review);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Log.d(TAG, "onCreate called");

        initViews();
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

        pagerAdapter = new SellerReviewPagerAdapter(this, productId);
        vpReview.setAdapter(pagerAdapter);

        Log.d(TAG, "ViewPager adapter set");

        new TabLayoutMediator(tabReview, vpReview, (tab, position) -> {
            tab.setText("Reviews"); // hiện tại 1 tab
        }).attach();

        Log.d(TAG, "TabLayoutMediator attached");
    }

    private void setListeners() {

        ivBack.setOnClickListener(v -> finish());

        btnSortRating.setOnClickListener(v ->
                Log.d(TAG, "Sort by rating clicked"));

        btnSortTime.setOnClickListener(v ->
                Log.d(TAG, "Sort by time clicked"));

        vpReview.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "Page selected: " + position);
            }
        });
    }
}