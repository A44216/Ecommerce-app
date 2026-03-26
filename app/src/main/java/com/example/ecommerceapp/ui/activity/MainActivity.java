package com.example.ecommerceapp.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.AddressResponse;
import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.model.response.UserResponse;

import com.example.ecommerceapp.data.repository.AddressRepository;
import com.example.ecommerceapp.data.repository.CategoryRepository;
import com.example.ecommerceapp.data.repository.ProductRepository;
import com.example.ecommerceapp.data.repository.UserRepository;

import com.example.ecommerceapp.data.local.TokenManager;

import com.example.ecommerceapp.ui.viewmodel.AddressViewModel;
import com.example.ecommerceapp.ui.viewmodel.CategoryViewModel;
import com.example.ecommerceapp.ui.viewmodel.ProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.UserViewModel;

import com.example.ecommerceapp.ui.viewmodel.factory.AddressViewModelFactory;
import com.example.ecommerceapp.ui.viewmodel.factory.CategoryViewModelFactory;
import com.example.ecommerceapp.ui.viewmodel.factory.ProductViewModelFactory;
import com.example.ecommerceapp.ui.viewmodel.factory.UserViewModelFactory;

public class MainActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private ProductViewModel productViewModel;
    private CategoryViewModel categoryViewModel;
    private AddressViewModel addressViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // =========================
        // 1. TOKEN + API SERVICE
        // =========================
        TokenManager tokenManager = new TokenManager(this);
        ApiService apiService = ApiClient.getApiService(tokenManager);

        // =========================
        // 2. REPOSITORIES
        // =========================
        UserRepository userRepo = new UserRepository(apiService);
        ProductRepository productRepo = new ProductRepository(apiService);
        CategoryRepository categoryRepo = new CategoryRepository(apiService);
        AddressRepository addressRepo = new AddressRepository(apiService);

        // =========================
        // 3. FACTORIES
        // =========================
        UserViewModelFactory userFactory = new UserViewModelFactory(userRepo);
        ProductViewModelFactory productFactory = new ProductViewModelFactory(productRepo);
        CategoryViewModelFactory categoryFactory = new CategoryViewModelFactory(categoryRepo);
        AddressViewModelFactory addressFactory = new AddressViewModelFactory(addressRepo);

        // =========================
        // 4. VIEWMODELS
        // =========================
        userViewModel =
                new ViewModelProvider(this, userFactory).get(UserViewModel.class);

        productViewModel =
                new ViewModelProvider(this, productFactory).get(ProductViewModel.class);

        categoryViewModel =
                new ViewModelProvider(this, categoryFactory).get(CategoryViewModel.class);

        addressViewModel =
                new ViewModelProvider(this, addressFactory).get(AddressViewModel.class);

        // =========================
        // 5. OBSERVE USER
        // =========================
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

        // =========================
        // 6. OBSERVE PRODUCT
        // =========================
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

        // =========================
        // 7. OBSERVE CATEGORY
        // =========================
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

        // =========================
        // 8. OBSERVE ADDRESS
        // =========================
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