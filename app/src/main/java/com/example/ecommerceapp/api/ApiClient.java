package com.example.ecommerceapp.api;

import com.example.ecommerceapp.api.service.AddressService;
import com.example.ecommerceapp.api.service.AuthService;
import com.example.ecommerceapp.api.service.CategoryService;
import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8081/api/";

    private static Retrofit publicRetrofit;
    private static Retrofit authRetrofit;

    // RETROFIT WITH TOKEN
    private static Retrofit getAuthRetrofit(TokenManager tokenManager) {

        if (authRetrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        if (tokenManager == null) {
                            return chain.proceed(chain.request());
                        }
                        return new AuthInterceptor(tokenManager).intercept(chain);
                    })
                    .build();

            authRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return authRetrofit;
    }


    // RETROFIT NO TOKEN
    private static Retrofit getPublicRetrofit() {

        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return publicRetrofit;
    }

    // SERVICES - PUBLIC
    public static ProductService getProductService() {
        return getPublicRetrofit().create(ProductService.class);
    }

    public static CategoryService getCategoryService() {
        return getPublicRetrofit().create(CategoryService.class);
    }

    public static UserService getUserService() {
        return getPublicRetrofit().create(UserService.class);
    }

    // SERVICE - AUTH (CÓ TOKEN)
    public static AuthService getAuthService(TokenManager tokenManager) {
        return getAuthRetrofit(tokenManager).create(AuthService.class);
    }

    public static AddressService getAddressService() {
        return getPublicRetrofit().create(AddressService.class);
    }
}