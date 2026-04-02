package com.example.ecommerceapp.ui.adapter.seller;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.viewholder.seller.ImageVH;

import java.util.ArrayList;
import java.util.List;

public class ImageEditAdapter extends RecyclerView.Adapter<ImageVH> {

    private List<Uri> images = new ArrayList<>();

    @SuppressLint("NotifyDataSetChanged")
    public void addImages(List<Uri> newImages) {
        images.addAll(newImages);
        notifyDataSetChanged();
    }

    public List<Uri> getImages() {
        return images;
    }

    @NonNull
    @Override
    public ImageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image_edit, parent, false);
        return new ImageVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageVH holder, int position) {

        Uri uri = images.get(position);

        Glide.with(holder.itemView.getContext())
                .load(uri)
                .into(holder.getImgProduct());

        holder.getBtnDelete().setOnClickListener(v -> {
            images.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, images.size());
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}