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
                .into(imageView);
    }

}
