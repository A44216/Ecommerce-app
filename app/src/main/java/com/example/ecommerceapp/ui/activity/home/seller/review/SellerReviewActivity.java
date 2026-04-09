package com.example.ecommerceapp.ui.activity.home.seller.review;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;

import com.example.ecommerceapp.R;

public class SellerReviewActivity extends AppCompatActivity {

    private ImageView ivBack;
    private MaterialButton btnSortRating, btnSortTime;
    private TabLayout tabReview;
    private ViewPager2 vpReview;

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

        initViews();
        setListeners();
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);

        btnSortRating = findViewById(R.id.btnSortRating);
        btnSortTime = findViewById(R.id.btnSortTime);

        tabReview = findViewById(R.id.tabReview);
        vpReview = findViewById(R.id.vpReview);
    }

    private void setListeners() {

        // BACK
        ivBack.setOnClickListener(v -> finish());

        // SORT RATING
        btnSortRating.setOnClickListener(v -> {
            // TODO: toggle sort rating
        });

        // SORT TIME
        btnSortTime.setOnClickListener(v -> {
            // TODO: toggle sort time
        });

        // TAB SELECT (nếu cần xử lý thêm)
        tabReview.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vpReview.setCurrentItem(tab.getPosition());
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });

        // VIEWPAGER SWIPE -> TAB sync
        vpReview.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                TabLayout.Tab tab = tabReview.getTabAt(position);
                if (tab != null) tab.select();
            }
        });
    }
}