package com.example.ecommerceapp.ui.activity.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.ecommerceapp.R;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AdminHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. BottomNav
        BottomNavigationView bnvMenu = findViewById(R.id.bnvMenu);

        // 2. NavHostFragment
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.navHost);

        if (navHostFragment == null) return;

        // 3. NavController
        NavController navController = navHostFragment.getNavController();

        // 4. connect BottomNav với NavController
        NavigationUI.setupWithNavController(bnvMenu, navController);

        // 5. Xử lý nút Back (đăng xuất luôn không cần hỏi)
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (navController.popBackStack()) {
                    return;
                }
                
                // Đăng xuất ngay lập tức
                TokenManager.getInstance(AdminHomeActivity.this).clearAllData();
                Intent intent = new Intent(AdminHomeActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}