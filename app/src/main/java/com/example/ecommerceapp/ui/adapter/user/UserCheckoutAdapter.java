package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.example.ecommerceapp.utils.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

public class UserCheckoutAdapter extends RecyclerView.Adapter<UserCheckoutAdapter.CheckoutViewHolder> {

    private List<UserCartItem> checkoutList;

    public UserCheckoutAdapter(List<UserCartItem> checkoutList) {
        this.checkoutList = checkoutList;
    }

    @NonNull
    @Override
    public CheckoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_checkout, parent, false);
        return new CheckoutViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutViewHolder holder, int position) {
        UserCartItem item = checkoutList.get(position);

        holder.tvCheckoutName.setText(item.getProduct().getName());
        holder.tvCheckoutQty.setText("x" + item.getQuantity());

        if (item.getProduct().getPrice() != null) {
            DecimalFormat df = new DecimalFormat("#,###");
            holder.tvCheckoutPrice.setText(df.format(item.getProduct().getPrice()) + "đ");
        }

        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            String imgUrl = item.getProduct().getImages().get(0).getImageUrl();
            if (imgUrl != null) {
                ImageLoader.load(holder.itemView.getContext(), holder.ivCheckoutImage, imgUrl);
                // CHÚ Ý: Ở file CheckoutAdapter thì đổi ivUserCartImage thành ivCheckoutImage nhé
            }
        } else {
            holder.ivCheckoutImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() { return checkoutList.size(); }

    public static class CheckoutViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCheckoutImage;
        TextView tvCheckoutName, tvCheckoutPrice, tvCheckoutQty;

        public CheckoutViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCheckoutImage = itemView.findViewById(R.id.ivCheckoutImage);
            tvCheckoutName = itemView.findViewById(R.id.tvCheckoutName);
            tvCheckoutPrice = itemView.findViewById(R.id.tvCheckoutPrice);
            tvCheckoutQty = itemView.findViewById(R.id.tvCheckoutQty);
        }
    }
}