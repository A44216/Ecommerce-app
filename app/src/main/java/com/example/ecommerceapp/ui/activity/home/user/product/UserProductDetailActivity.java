package com.example.ecommerceapp.ui.activity.home.user.product;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.utils.ImageLoader;

import java.text.DecimalFormat;

public class UserProductDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_product_detail);

        // 1. Ánh xạ View
        ImageView btnBack = findViewById(R.id.btnBack);
        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        TextView tvDetailName = findViewById(R.id.tvDetailName);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);

        // 2. Nhận dữ liệu từ Intent gửi sang
        String name = getIntent().getStringExtra("product_name");
        String priceString = getIntent().getStringExtra("product_price");
        String imageUrl = getIntent().getStringExtra("product_image");

        // 3. Hiển thị dữ liệu lên màn hình
        if (name != null) tvDetailName.setText(name);

        if (priceString != null) {
            try {
                double price = Double.parseDouble(priceString);
                DecimalFormat formatter = new DecimalFormat("#,###");
                tvDetailPrice.setText(formatter.format(price) + "đ");
            } catch (Exception e) {
                tvDetailPrice.setText(priceString + "đ");
            }
        }

        if (imageUrl != null) {
            ImageLoader.load(this, ivDetailImage, imageUrl);
        }

        // 4. Bắt sự kiện nút Back để quay về Trang chủ
        btnBack.setOnClickListener(v -> finish());
    }
}