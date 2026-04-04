package com.example.ecommerceapp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class ImageUploadHelper {

    public static void uploadImage(
            TokenManager tokenManager,
            Uri uri,
            Context context,
            Callback<String> callback
    ) {

        try {
            File file = uriToFile(uri, context);

            RequestBody requestFile =
                    RequestBody.create(file, MediaType.parse("image/*"));

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData(
                            "file",
                            file.getName(),
                            requestFile
                    );

            ApiClient.getProductImageService(tokenManager)
                    .uploadImage(body)
                    .enqueue(new retrofit2.Callback<String>() {

                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                callback.onSuccess(response.body());
                            } else {
                                callback.onError("Upload failed");
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            callback.onError(t.getMessage());
                        }
                    });

        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private static File uriToFile(Uri uri, Context context) throws IOException {

        File file = new File(
                context.getCacheDir(),
                "img_" + System.currentTimeMillis() + ".jpg"
        );

        try (InputStream input = context.getContentResolver().openInputStream(uri);
             java.io.FileOutputStream output = new java.io.FileOutputStream(file)) {

            byte[] buffer = new byte[4096];
            int read;

            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }

        return file;
    }

    public interface Callback<T> {
        void onSuccess(T data);
        void onError(String error);
    }
}