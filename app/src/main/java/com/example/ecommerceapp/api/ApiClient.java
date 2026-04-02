package com.example.ecommerceapp.api;

import com.example.ecommerceapp.api.service.AddressService;
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8081/api/";

    private static Retrofit publicRetrofit;

    // PUBLIC
    private static Retrofit getPublicRetrofit() {
        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return publicRetrofit;
    }

    // AUTH (có token)
    private static Retrofit createAuthRetrofit(TokenManager tokenManager) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(tokenManager))
                .build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // ===== PUBLIC API =====
    public static AuthService getAuthService() {
        return getPublicRetrofit().create(AuthService.class);
    }

    public static ProductService getProductService() {
        return getPublicRetrofit().create(ProductService.class);
    }

    public static CategoryService getCategoryService() {
        return getPublicRetrofit().create(CategoryService.class);
    }

    public static AddressService getAddressService() {
        return getPublicRetrofit().create(AddressService.class);
    }

    // ===== AUTH API =====
    public static ProductService getProductService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ProductService.class);
    }

    public static CategoryService getCategoryService(TokenManager tm) {
        return createAuthRetrofit(tm).create(CategoryService.class);
    }

    public static UserService getUserService(TokenManager tm) {
        return createAuthRetrofit(tm).create(UserService.class);
    }

    public static ShopService getShopService() {
        return getPublicRetrofit().create(ShopService.class);
    }

    public static ShopService getShopService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ShopService.class);
    }
}