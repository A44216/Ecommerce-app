package com.example.ecommerceapp.ui.adapter.seller.product;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;
import com.example.ecommerceapp.ui.viewholder.seller.product.ImageVH;
import com.example.ecommerceapp.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class ImageEditAdapter extends RecyclerView.Adapter<ImageVH> {

    private static final int TYPE_SERVER = 1;
    private static final int TYPE_LOCAL = 2;

    private List<ProductImageResponse> serverImages = new ArrayList<>();
    private List<Uri> localImages = new ArrayList<>();
    private List<ProductImageResponse> deletedServerImages = new ArrayList<>();

    // ===== SET SERVER IMAGES (LOAD FROM MYSQL) =====
    @SuppressLint("NotifyDataSetChanged")
    public void setServerImages(List<ProductImageResponse> images) {
        serverImages.clear();
        if (images != null) serverImages.addAll(images);
        notifyDataSetChanged();
    }

    // ===== ADD LOCAL IMAGES (USER PICK) =====
    @SuppressLint("NotifyDataSetChanged")
    public void addImages(List<Uri> images) {
        localImages.addAll(images);
        notifyDataSetChanged();
    }

    public List<ProductImageResponse> getDeletedServerImages() {
        return deletedServerImages;
    }

    public List<Uri> getLocalImages() {
        return localImages;
    }

    public List<ProductImageResponse> getServerImages() {
        return serverImages;
    }

    // ===== TOTAL SIZE =====
    @Override
    public int getItemCount() {
        return serverImages.size() + localImages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < serverImages.size()) {
            return TYPE_SERVER;
        } else {
            return TYPE_LOCAL;
        }
    }

    @NonNull
    @Override
    public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_edit, parent, false);
        return new ImageVH(view);
    }

    @Override
    public void onViewRecycled(@NonNull ImageVH holder) {
        super.onViewRecycled(holder);
        Glide.with(holder.itemView.getContext()).clear(holder.getImgProduct());
    }

    @Override
    public void onBindViewHolder(@NonNull ImageVH holder, int position) {

        if (getItemViewType(position) == TYPE_SERVER) {

            ProductImageResponse image = serverImages.get(position);

            String url = image.getImageUrl();

            ImageLoader.load(
                    holder.itemView.getContext(),
                    holder.getImgProduct(),
                    url
            );

            holder.getBtnDelete().setOnClickListener(v -> {

                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                if (pos < serverImages.size()) {

                    ProductImageResponse removed = serverImages.get(pos);

                    deletedServerImages.add(removed);

                    serverImages.remove(pos);
                    notifyItemRemoved(pos);
                }
            });

        } else {

            int localIndex = position - serverImages.size();
            Uri uri = localImages.get(localIndex);

            ImageLoader.load(
                    holder.itemView.getContext(),
                    holder.getImgProduct(),
                    uri
            );

            holder.getBtnDelete().setOnClickListener(v -> {

                int pos = holder.getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                int index = pos - serverImages.size();

                if (index >= 0 && index < localImages.size()) {
                    localImages.remove(index);
                    notifyItemRemoved(pos);
                }
            });
        }
    }

}