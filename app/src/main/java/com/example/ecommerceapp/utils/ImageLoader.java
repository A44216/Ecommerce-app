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

        if (url == null || url.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return;
        }

        TokenManager tm = TokenManager.getInstance(context);
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        
        // Chỉ thêm header Authorization nếu có token thực sự
        if (tm.getToken() != null && !tm.getToken().isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + tm.getToken());
        }

        GlideUrl glideUrl = new GlideUrl(url, builder.build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL) // Cache cả ảnh gốc và ảnh đã resize
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

    public static void load(Context context, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(imageView);
    }

}
