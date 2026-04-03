package com.example.ecommerceapp.ui.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.AddressService;
import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.api.service.UserService;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}