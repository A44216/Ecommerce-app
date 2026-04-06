package com.example.ecommerceapp.ui.activity.home.seller.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderDetailAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerOrderViewModelFactory;

public class SellerOrderDetailActivity extends AppCompatActivity {

    private RecyclerView rvOrder;
    private ImageView ivBack;
    private SellerOrderDetailAdapter adapter;
    private SellerOrderViewModel viewModel;

    private TokenManager tokenManager;
    private int orderId;
    private int shopId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_order_detail);

        tokenManager = TokenManager.getInstance(this);

        initViews();
        initViewModel();
        initListeners();
        observeData();
    }

    private void initViews() {
        rvOrder = findViewById(R.id.rvOrderItems);
        ivBack = findViewById(R.id.ivBack);

        adapter = new SellerOrderDetailAdapter();
        rvOrder.setLayoutManager(new LinearLayoutManager(this));
        rvOrder.setAdapter(adapter);

        orderId = getIntent().getIntExtra("orderId", 0);
        shopId = (int) tokenManager.getShopId();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(
                this,
                new SellerOrderViewModelFactory(tokenManager)
        ).get(SellerOrderViewModel.class);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(this, SellerProductDetailActivity.class);
            intent.putExtra("productId", item.getProductId());
            startActivity(intent);
        });
    }

    private void observeData() {
        viewModel.getOrderDetail(orderId, shopId)
                .observe(this, data -> {
                    if (data != null) {
                        adapter.setData(data);
                    }
                });
    }
}