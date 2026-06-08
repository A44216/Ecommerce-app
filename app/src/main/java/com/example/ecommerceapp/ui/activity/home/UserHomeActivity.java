package com.example.ecommerceapp.ui.activity.home;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.fragment.user.HomeFragment;
import com.example.ecommerceapp.ui.fragment.user.NotificationFragment;
import com.example.ecommerceapp.ui.fragment.user.ProfileFragment;
import com.example.ecommerceapp.utils.NetworkMonitor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.widget.TextView;
import android.view.View;

public class UserHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NetworkMonitor networkMonitor;
    private TextView tvNoInternet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Đáy để 0 vì đã có BottomNav
            return insets;
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Mặc định khi mới mở App sẽ load Trang chủ
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        // Bắt sự kiện khi bấm vào các tab
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(new HomeFragment());
                return true;
            } else if (itemId == R.id.nav_notification) {
                loadFragment(new NotificationFragment());
                return true;
            } else if (itemId == R.id.nav_profile) {
                loadFragment(new ProfileFragment());
                return true;
            }

            return false;
        });

        // Xử lý giám sát mạng
        tvNoInternet = findViewById(R.id.tvNoInternet);
        networkMonitor = new NetworkMonitor(this);
        networkMonitor.getIsConnected().observe(this, isConnected -> {
            if (isConnected) {
                tvNoInternet.setVisibility(View.GONE);
            } else {
                tvNoInternet.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkMonitor.registerCallback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        networkMonitor.unregisterCallback();
    }

    // Hàm phụ trợ để chuyển đổi giữa các Fragment
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void switchToHome() {
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
    }
}