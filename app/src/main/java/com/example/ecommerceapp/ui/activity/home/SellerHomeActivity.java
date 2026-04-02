package com.example.ecommerceapp.ui.activity.home;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ShopResponse;
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

        NavController navController = navHostFragment.getNavController();

        // 3. connect BottomNav với Navigation
        NavigationUI.setupWithNavController(bnvMenu, navController);

    }

    private void loadShopByUser() {

        long userId = tokenManager.getUserId();

        if (userId <= 0) return;

        ShopService shopService = ApiClient.getShopService(tokenManager);
        
        shopService.getShopByUser((int)userId)
                .enqueue(new retrofit2.Callback<ShopResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<ShopResponse> call,
                                           retrofit2.Response<ShopResponse> response) {

                        if (response.isSuccessful() && response.body() != null) {

                            ShopResponse shop = response.body();

                            tokenManager.saveShopId(shop.getId());
                        } else {
                            // seller chưa có shop
                            tokenManager.saveShopId(0);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ShopResponse> call, Throwable t) {
                        tokenManager.saveShopId(0);
                    }
                });
    }

}