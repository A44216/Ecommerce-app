package com.example.ecommerceapp.ui.activity.home.user.product;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.Product;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.utils.ImageLoader;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Collections;

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

        LinearLayout btnAddToCart = findViewById(R.id.btnViewCart); // Nút Thêm Giỏ
        Button btnBuyNow = findViewById(R.id.btnBuyNow);            // Nút Mua ngay

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

        // ==========================================
        // 4. XỬ LÝ LOGIC GIỎ HÀNG
        // ==========================================

        // Gom dữ liệu hiện tại thành 1 đối tượng Product
        Product currentProduct = new Product();
        if (name != null) currentProduct.setName(name);
        if (priceString != null) {
            try {
                // Ép kiểu an toàn từ String sang BigDecimal
                currentProduct.setPrice(new BigDecimal(priceString));
            } catch (Exception e) {
                currentProduct.setPrice(BigDecimal.ZERO);
            }
        }
        if (imageUrl != null) currentProduct.setImages(Collections.singletonList(imageUrl));

        // Nút "Thêm Giỏ": Chỉ lưu vào túi CartManager và hiện thông báo (không chuyển trang)
        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Toast.makeText(UserProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        // Nút "Mua ngay": Lưu vào CartManager và bay thẳng sang trang Giỏ hàng
        btnBuyNow.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Intent intent = new Intent(UserProductDetailActivity.this, UserCartActivity.class);
            startActivity(intent);
        });

        // 5. Bắt sự kiện nút Back để quay về Trang chủ
        btnBack.setOnClickListener(v -> finish());
    }
}