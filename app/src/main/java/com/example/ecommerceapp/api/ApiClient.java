package com.example.ecommerceapp.api;

import com.example.ecommerceapp.api.service.AddressService;
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.api.service.ChatApiService;
import com.example.ecommerceapp.api.service.ProductImageService;
import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.api.service.UserAddressApiService;
import com.example.ecommerceapp.api.service.UserCategoryApiService;
import com.example.ecommerceapp.api.service.UserCouponApiService;
import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8081/api/";

    private static Retrofit publicRetrofit;

    // PUBLIC
    private static Retrofit getPublicRetrofit() {
        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
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
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    // ===== PUBLIC API =====
    public static AuthService getAuthService() {
        return getPublicRetrofit().create(AuthService.class);
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

    public static ShopService getShopService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ShopService.class);
    }
    public static
    ProductImageService getProductImageService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ProductImageService.class);
    }

    // ===== USER API =====
    public static UserProductService getUserProductService() {
        return getPublicRetrofit().create(UserProductService.class);
    }

    // ===== USER ORDER API =====
    public static UserOrderApiService getUserOrderApiService(TokenManager tm) {
        // Dùng AuthRetrofit vì gửi đơn hàng cần biết ai đang mua
        return createAuthRetrofit(tm).create(UserOrderApiService.class);
    }

    public static UserCategoryApiService getUserCategoryApiService(TokenManager tm) {
        // Đổi từ getPublicRetrofit sang createAuthRetrofit
        return createAuthRetrofit(tm).create(UserCategoryApiService.class);
    }

    public static UserAddressApiService getUserAddressApiService(TokenManager tm) {
        return createAuthRetrofit(tm).create(UserAddressApiService.class);
    }

    public static UserCouponApiService getUserCouponApiService(TokenManager tm) {
        return createAuthRetrofit(tm).create(UserCouponApiService.class);
    }

    public static ChatApiService getChatApiService(TokenManager tokenManager) {
        return createAuthRetrofit(tokenManager).create(ChatApiService.class);
    }
}