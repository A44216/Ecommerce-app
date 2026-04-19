package com.example.ecommerceapp.ui.activity.home.admin.user;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;

public class AdminUserDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_detail);
        
        // This is an empty skeleton as requested.
        // Detailed code will be implemented later.
        
        int userId = getIntent().getIntExtra("userId", -1);
    }
}
