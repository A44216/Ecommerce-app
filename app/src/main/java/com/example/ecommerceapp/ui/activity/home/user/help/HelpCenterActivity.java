package com.example.ecommerceapp.ui.activity.home.user.help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.ecommerceapp.R;

public class HelpCenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        CardView btnSubmitRequest = findViewById(R.id.btnSubmitRequest);
        btnSubmitRequest.setOnClickListener(v -> {
            startActivity(new Intent(this, SubmitComplaintActivity.class));
        });

        CardView btnViewHistory = findViewById(R.id.btnViewHistory);
        btnViewHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, ComplaintHistoryActivity.class));
        });

        boolean isSeller = getIntent().getBooleanExtra("is_seller", false);
        if (isSeller) {
            android.view.View tvFaqTitle = findViewById(R.id.tvFaqTitle);
            android.view.View cardFaqItems = findViewById(R.id.cardFaqItems);
            if (tvFaqTitle != null) tvFaqTitle.setVisibility(android.view.View.GONE);
            if (cardFaqItems != null) cardFaqItems.setVisibility(android.view.View.GONE);
        }
    }
}
