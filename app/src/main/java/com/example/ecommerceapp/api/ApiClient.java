package com.example.ecommerceapp.api;

import android.os.Build;

import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.api.service.ChatApiService;
import com.example.ecommerceapp.api.service.PaymentApiService;
import com.example.ecommerceapp.api.service.ProductEvaluationService;
import com.example.ecommerceapp.api.service.ProductImageService;
import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.api.service.RecommendationService;
import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.api.service.UserAddressApiService;
import com.example.ecommerceapp.api.service.UserCategoryApiService;
import com.example.ecommerceapp.api.service.UserCouponApiService;
import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.api.service.admin.AdminCategoryService;
import com.example.ecommerceapp.api.service.admin.AdminComplaintService;
import com.example.ecommerceapp.api.service.admin.AdminCouponService;
import com.example.ecommerceapp.api.service.admin.AdminDashboardService;
import com.example.ecommerceapp.api.service.admin.AdminNotificationService;
import com.example.ecommerceapp.api.service.admin.AdminOrderService;
import com.example.ecommerceapp.api.service.admin.AdminProductService;
import com.example.ecommerceapp.api.service.admin.AdminProfileService;
import com.example.ecommerceapp.api.service.admin.AdminShopService;
import com.example.ecommerceapp.api.service.admin.AdminUserService;
import com.example.ecommerceapp.api.service.seller.SellerCategoryService;
import com.example.ecommerceapp.api.service.seller.SellerDashboardService;
import com.example.ecommerceapp.api.service.seller.SellerOrderService;
import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.api.service.seller.SellerReviewService;
import com.example.ecommerceapp.api.service.seller.SellerShopService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://ecommerce-backend-v9py.onrender.com/api/";
    private static Retrofit publicRetrofit;

    private static Gson getGson() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new GsonBuilder()
                    .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                        @Override
                        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                            return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        }
                    })
                    .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                        @Override
                        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        }
                    })
                    .create();
        } else {
            return new Gson(); // Hoặc xử lý custom cho API < 26 nếu cần
        }
    }

    // PUBLIC
    private static Retrofit getPublicRetrofit() {
        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
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
                .addConverterFactory(GsonConverterFactory.create(getGson()))
                .build();
    }

    // ===== PUBLIC API =====
    public static AuthService getAuthService() {
        return getPublicRetrofit().create(AuthService.class);
    }

    // ===== AUTH API =====
    public static AuthService getAuthService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AuthService.class);
    }

    public static ProductService getPublicProductService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ProductService.class);
    }

    public static CategoryService getPublicCategoryService(TokenManager tm) {
        return createAuthRetrofit(tm).create(CategoryService.class);
    }

    public static ShopService getPublicShopService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ShopService.class);
    }

    public static UserService getUserService(TokenManager tm) {
        return createAuthRetrofit(tm).create(UserService.class);
    }

    public static ProductImageService getProductImageService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ProductImageService.class);
    }

    // ===== USER API =====
    public static UserProductService getUserProductService() {
        return getPublicRetrofit().create(UserProductService.class);
    }

    public static UserOrderApiService getUserOrderApiService(TokenManager tm) {
        return createAuthRetrofit(tm).create(UserOrderApiService.class);
    }

    public static UserCategoryApiService getUserCategoryApiService(TokenManager tm) {
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

    public static com.example.ecommerceapp.api.service.AiChatApiService getAiChatApiService(TokenManager tokenManager) {
        return createAuthRetrofit(tokenManager).create(com.example.ecommerceapp.api.service.AiChatApiService.class);
    }

    public static PaymentApiService getPaymentApiService() {
        return getPublicRetrofit().create(com.example.ecommerceapp.api.service.PaymentApiService.class);
    }

    public static ProductEvaluationService getProductEvaluationService(TokenManager tm) {
        return createAuthRetrofit(tm).create(ProductEvaluationService.class);
    }

    public static RecommendationService getRecommendationService(TokenManager tm) {
        return createAuthRetrofit(tm).create(RecommendationService.class);
    }

    // ===== SELLER API =====
    public static SellerDashboardService getDashboardService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerDashboardService.class);
    }

    public static SellerOrderService getOrderService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerOrderService.class);
    }

    public static SellerReviewService getReviewService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerReviewService.class);
    }

    public static SellerProductService getProductService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerProductService.class);
    }

    public static SellerCategoryService getCategoryService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerCategoryService.class);
    }

    public static SellerShopService getShopService(TokenManager tm) {
        return createAuthRetrofit(tm).create(SellerShopService.class);
    }

    // ===== ADMIN API =====
    public static AdminCategoryService getAdminCategoryService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminCategoryService.class);
    }

    public static AdminProfileService getAdminProfileService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminProfileService.class);
    }

    public static AdminCouponService getAdminCouponService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminCouponService.class);
    }

    public static AdminUserService getAdminUserService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminUserService.class);
    }

    public static AdminDashboardService getAdminDashboardService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminDashboardService.class);
    }

    public static AdminShopService getAdminShopService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminShopService.class);
    }

    public static AdminOrderService getAdminOrderService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminOrderService.class);
    }

    public static AdminProductService getAdminProductService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminProductService.class);
    }

    public static AdminNotificationService getAdminNotificationService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminNotificationService.class);
    }

    public static AdminComplaintService getAdminComplaintService(TokenManager tm) {
        return createAuthRetrofit(tm).create(AdminComplaintService.class);
    }

}
