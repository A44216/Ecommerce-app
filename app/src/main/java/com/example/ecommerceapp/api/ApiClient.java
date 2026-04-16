package com.example.ecommerceapp.api;

import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.seller.SellerCategoryService;
import com.example.ecommerceapp.api.service.admin.AdminCategoryService;
import com.example.ecommerceapp.api.service.admin.AdminProfileService;
import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.api.service.seller.SellerOrderService;
import com.example.ecommerceapp.api.service.ProductImageService;
import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.api.service.seller.SellerReviewService;
import com.example.ecommerceapp.api.service.seller.SellerShopService;
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

    // PUBLIC API
    public static AuthService getAuthService() {
        return getPublicRetrofit().create(AuthService.class);
    }

    // AUTH API

    public static AuthService getAuthService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AuthService.class);
    }

    public static SellerProductService getProductService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerProductService.class);
    }

    public static SellerCategoryService getCategoryService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerCategoryService.class);
    }

    public static UserService getUserService(TokenManager tm) {
        return createAuthRetrofit(tm).create(UserService.class);
    }

    public static SellerShopService getShopService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerShopService.class);
    }
    public static ProductImageService getProductImageService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ProductImageService.class);
    }

    public static SellerDashboardService getDashboardService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerDashboardService.class);
    }

    public static SellerOrderService getOrderService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerOrderService.class);
    }

    public static SellerReviewService getReviewService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerReviewService.class);
    }

    public static AdminCategoryService getAdminCategoryService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminCategoryService.class);
    }

    public static AdminProfileService getAdminProfileService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminProfileService.class);
    }

}