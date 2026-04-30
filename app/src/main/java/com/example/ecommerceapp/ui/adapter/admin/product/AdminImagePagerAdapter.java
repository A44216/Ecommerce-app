package com.example.ecommerceapp.ui.adapter.admin.product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.List;

public class AdminImagePagerAdapter extends RecyclerView.Adapter<AdminImagePagerAdapter.ImageHolder> {

    private final Context context;
    private final List<String> imageUrls;

    public AdminImagePagerAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_seller_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        String url = imageUrls.get(position);
        ImageLoader.load(context, holder.imageView, url);
    }

    @Override
    public int getItemCount() {
        return imageUrls != null ? imageUrls.size() : 0;
    }

    static class ImageHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            // Assuming item_seller_image.xml has an ImageView with id imgProduct
            imageView = itemView.findViewById(R.id.imgProduct);
        }
    }
}
