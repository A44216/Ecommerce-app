package com.example.ecommerceapp.ui.activity.home;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;

import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ShopResponse;
import com.example.ecommerceapp.api.service.seller.SellerShopService;
import com.example.ecommerceapp.data.model.response.seller.shop.SellerShopResponse;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SellerHomeActivity extends AppCompatActivity {

    private FragmentContainerView navHost;
    private BottomNavigationView bnvMenu;

    private TokenManager tokenManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // 1. init view
        bnvMenu = findViewById(R.id.bnvMenu);
        tokenManager = TokenManager.getInstance(this);

        loadShopByUser();

        // 2. lấy navController từ navHost
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.navHost);

        if (navHostFragment == null) return;
        NavController navController = navHostFragment.getNavController();

        // 3. connect BottomNav với Navigation
        NavigationUI.setupWithNavController(bnvMenu, navController);

    }

    private void loadShopByUser() {

        long userId = tokenManager.getUserId();

        if (userId <= 0) return;
        SellerShopService shopService = ApiClient.getShopService(tokenManager);
        
        shopService.getMyShop()
                .enqueue(new retrofit2.Callback<SellerShopResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<SellerShopResponse> call,
                                           retrofit2.Response<SellerShopResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            SellerShopResponse shop = response.body();

                            tokenManager.saveShopId(shop.getId());
                        } else {
                            // seller chưa có shop
                            tokenManager.saveShopId(0);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<SellerShopResponse> call, Throwable t) {
                        tokenManager.saveShopId(0);
                    }
                });
    }
    @Override
    public boolean dispatchTouchEvent(android.view.MotionEvent ev) {
        if (ev.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            android.view.View v = getCurrentFocus();
            if (v instanceof android.widget.EditText) {
                android.graphics.Rect outRect = new android.graphics.Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                    v.clearFocus();
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }
}