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
import com.example.ecommerceapp.data.model.response.UserProductImageResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
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

        LinearLayout btnAddToCart = findViewById(R.id.btnViewCart);
        Button btnBuyNow = findViewById(R.id.btnBuyNow);

        // 2. Nhận dữ liệu từ Intent gửi sang
        int productId = getIntent().getIntExtra("product_id", -1);
        int shopId = getIntent().getIntExtra("shop_id", -1);
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

        UserProductResponse currentProduct = new UserProductResponse();
        if (productId != -1) {
            currentProduct.setId(productId);
        }
        if (shopId != -1) currentProduct.setShopId(shopId);
        if (name != null) currentProduct.setName(name);
        if (priceString != null) {
            try {
                currentProduct.setPrice(new BigDecimal(priceString));
            } catch (Exception e) {
                currentProduct.setPrice(BigDecimal.ZERO);
            }
        }
        if (imageUrl != null) {
            UserProductImageResponse imgObj = new UserProductImageResponse();
            imgObj.setImageUrl(imageUrl);
            currentProduct.setImages(Collections.singletonList(imgObj));
        }

        // Đảm bảo sự kiện Click ở đây vẫn tồn tại
        btnAddToCart.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Toast.makeText(UserProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnBuyNow.setOnClickListener(v -> {
            CartManager.getInstance().addToCart(currentProduct);
            Intent intent = new Intent(UserProductDetailActivity.this, UserCartActivity.class);
            startActivity(intent);
        });

        // 5. Nút Back
        btnBack.setOnClickListener(v -> finish());
    }
}