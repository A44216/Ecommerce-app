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
        
        if (tm.getToken() != null && !tm.getToken().isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + tm.getToken());
        }

        GlideUrl glideUrl = new GlideUrl(url, builder.build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(imageView);
    }

    public static void loadAvatar(Context context, ImageView imageView, String url) {
        if (url == null || url.isEmpty()) {
            imageView.setImageResource(R.drawable.ic_user);
            return;
        }

        TokenManager tm = TokenManager.getInstance(context);
        LazyHeaders.Builder builder = new LazyHeaders.Builder();
        
        if (tm.getToken() != null && !tm.getToken().isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + tm.getToken());
        }

        GlideUrl glideUrl = new GlideUrl(url, builder.build());

        Glide.with(context)
                .load(glideUrl)
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.ic_user)
                .circleCrop()
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
