package com.example.ecommerceapp.api;

import com.example.ecommerceapp.data.local.TokenManager;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:8081/api/";

    private static Retrofit publicRetrofit;
    private static Retrofit authRetrofit;

    // AUTH API (WITH TOKEN)
    public static ApiService getApiService(TokenManager tokenManager) {

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

        return authRetrofit.create(ApiService.class);
    }

    // PUBLIC API (NO TOKEN)
    public static ApiService getPublicApiService() {

        if (publicRetrofit == null) {
            publicRetrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return publicRetrofit.create(ApiService.class);
    }
}