package com.example.ecommerceapp.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;

public class ImageLoader {

    public static void load(Context context, ImageView imageView, String url) {

        if (url == null || url.trim().isEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        // 1. Nếu là link ảnh từ Cloudinary, tải thẳng trực tiếp (Không gửi kèm Token JWT nội bộ)
        if (url.contains("cloudinary.com")) {
            Glide.with(context)
                    .load(url)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageView);
            return; // Dừng hàm tại đây luôn
        }

        // 2. Nếu là các link API nội bộ cũ, giữ nguyên cơ chế gửi kèm Token để qua cổng Spring Security
        TokenManager tm = TokenManager.getInstance(context);

        GlideUrl glideUrl = new GlideUrl(
                url,
                new LazyHeaders.Builder()
                        .addHeader("Authorization", "Bearer " + tm.getToken())
                        .build()
        );

        Glide.with(context)
                .load(glideUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    public static void load(Context context, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()

                .into(imageView);
    }

}
