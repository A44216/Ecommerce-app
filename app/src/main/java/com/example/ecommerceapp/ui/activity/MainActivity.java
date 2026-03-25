package com.example.ecommerceapp.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserResponse;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.model.response.AddressResponse;

import com.example.ecommerceapp.ui.viewmodel.UserViewModel;
import com.example.ecommerceapp.ui.viewmodel.ProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.CategoryViewModel;
import com.example.ecommerceapp.ui.viewmodel.AddressViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private ProductViewModel productViewModel;
    private CategoryViewModel categoryViewModel;
    private AddressViewModel addressViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ===== USER =====
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUsers().observe(this, users -> {
            if (users != null) {
                for (UserResponse user : users) {
                    Log.d("USER", user.fullName + " - " + user.email);
                }
            } else {
                Log.d("USER", "Danh sách user rỗng");
            }
        });

        userViewModel.fetchUsers();

        // ===== PRODUCT =====
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        productViewModel.getProducts().observe(this, products -> {
            if (products != null) {
                for (ProductResponse p : products) {
                    Log.d("PRODUCT", p.getName() + " - " + p.getPrice());
                }
            } else {
                Log.d("PRODUCT", "Danh sách product rỗng");
            }
        });

        productViewModel.fetchProducts();

        // ===== CATEGORY =====
        categoryViewModel = new ViewModelProvider(this).get(CategoryViewModel.class);

        categoryViewModel.getCategories().observe(this, categories -> {
            if (categories != null) {
                for (CategoryResponse c : categories) {
                    Log.d("CATEGORY", c.getName());
                }
            } else {
                Log.d("CATEGORY", "Danh sách category rỗng");
            }
        });

        categoryViewModel.fetchCategories();

        // ===== ADDRESS =====
        addressViewModel = new ViewModelProvider(this).get(AddressViewModel.class);

        addressViewModel.getAddresses().observe(this, addresses -> {
            if (addresses != null) {
                for (AddressResponse a : addresses) {
                    Log.d("ADDRESS", a.getFullName() + " - " + a.getCity());
                }
            } else {
                Log.d("ADDRESS", "Danh sách address rỗng");
            }
        });

        addressViewModel.fetchAddresses();
    }
}