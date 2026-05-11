package com.example.ecommerceapp.ui.activity.home.user.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.ui.adapter.user.UserProductAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopCategoryProductsActivity extends AppCompatActivity {

    private int shopId;
    private int categoryId;
    private String categoryName;

    private RecyclerView rvProducts;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private UserProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_category_products);

        shopId = getIntent().getIntExtra("shopId", -1);
        categoryId = getIntent().getIntExtra("categoryId", -1);
        categoryName = getIntent().getStringExtra("categoryName");

        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        TextView tvCategoryNameTitle = findViewById(R.id.tvCategoryNameTitle);
        if (categoryName != null) {
            tvCategoryNameTitle.setText(categoryName);
        }

        rvProducts = findViewById(R.id.rvCategoryProducts);
        progressBar = findViewById(R.id.progressBar);
        tvEmpty = findViewById(R.id.tvEmpty);

        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new UserProductAdapter(this);
        rvProducts.setAdapter(adapter);

        loadProducts();
    }

    private void loadProducts() {
        if (shopId == -1 || categoryId == -1) {
            Toast.makeText(this, "Thông tin không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        rvProducts.setVisibility(View.GONE);

        // Chúng ta có thể dùng searchProducts với từ khóa trống để lấy toàn bộ sản phẩm của shop
        // Sau đó lọc bằng categoryId
        ApiClient.getUserProductService().searchProducts("", shopId).enqueue(new Callback<List<UserProductResponse>>() {
            @Override
            public void onResponse(Call<List<UserProductResponse>> call, Response<List<UserProductResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    List<UserProductResponse> allProducts = response.body();
                    List<UserProductResponse> filtered = new ArrayList<>();
                    
                    for (UserProductResponse p : allProducts) {
                        if (p.getCategoryId() != null && p.getCategoryId() == categoryId) {
                            filtered.add(p);
                        }
                    }

                    if (filtered.isEmpty()) {
                        rvProducts.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvProducts.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                        adapter.updateData(filtered);
                    }
                } else {
                    Toast.makeText(ShopCategoryProductsActivity.this, "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProductResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ShopCategoryProductsActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
