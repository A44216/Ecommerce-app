package com.example.ecommerceapp.ui.adapter.user;

import android.app.AlertDialog; // Import thêm cái này để làm bảng thông báo
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.example.ecommerceapp.utils.CartManager;
import com.example.ecommerceapp.utils.ImageLoader;
import java.text.DecimalFormat;
import java.util.List;

public class UserCartAdapter extends RecyclerView.Adapter<UserCartAdapter.UserCartViewHolder> {

    private List<UserCartItem> cartItems;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onPriceChanged();
    }

    public UserCartAdapter(List<UserCartItem> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_cart, parent, false);
        return new UserCartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserCartViewHolder holder, int position) {
        UserCartItem item = cartItems.get(position);

        holder.tvUserCartName.setText(item.getProduct().getName());
        holder.tvUserCartQty.setText(String.valueOf(item.getQuantity()));

        holder.cbUserCart.setOnCheckedChangeListener(null);
        holder.cbUserCart.setChecked(item.isChecked());

        DecimalFormat df = new DecimalFormat("#,###");
        if (item.getProduct().getPrice() != null) {
            holder.tvUserCartPrice.setText(df.format(item.getProduct().getPrice()) + "đ");
        }

        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            String imgUrl = item.getProduct().getImages().get(0).getImageUrl();
            if (imgUrl != null) {
                ImageLoader.load(holder.itemView.getContext(), holder.ivUserCartImage, imgUrl);
            }
        } else {
            holder.ivUserCartImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // ==========================================
        // CÁC SỰ KIỆN CLICK
        // ==========================================

        holder.cbUserCart.setOnCheckedChangeListener((b, checked) -> {
            item.setChecked(checked);
            listener.onPriceChanged();
        });

        holder.btnUserCartPlus.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.tvUserCartQty.setText(String.valueOf(item.getQuantity()));
            CartManager.getInstance().saveCart();
            if (item.isChecked()) listener.onPriceChanged();
        });

        holder.btnUserCartMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.tvUserCartQty.setText(String.valueOf(item.getQuantity()));
                CartManager.getInstance().saveCart();
                if (item.isChecked()) listener.onPriceChanged();
            }
        });

        // 🟢 SỰ KIỆN NÚT XÓA 🟢
        holder.tvUserCartDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Xóa sản phẩm")
                    .setMessage("Bạn có chắc chắn muốn xóa sản phẩm này?")
                    .setPositiveButton("Xóa", (dialog, which) -> {

                        // Đổi tên thành actualPosition để không bị trùng với biến position của hàm
                        int actualPosition = holder.getAdapterPosition();

                        if (actualPosition != RecyclerView.NO_POSITION) {

                            cartItems.remove(actualPosition);
                            notifyItemRemoved(actualPosition);
                            notifyItemRangeChanged(actualPosition, cartItems.size());
                            CartManager.getInstance().saveCart();

                            listener.onPriceChanged();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() { return cartItems.size(); }

    public static class UserCartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbUserCart;
        ImageView ivUserCartImage;
        TextView tvUserCartName, tvUserCartPrice, btnUserCartMinus, tvUserCartQty, btnUserCartPlus, tvUserCartDelete; // Thêm biến tvUserCartDelete

        public UserCartViewHolder(@NonNull View itemView) {
            super(itemView);
            cbUserCart = itemView.findViewById(R.id.cbUserCart);
            ivUserCartImage = itemView.findViewById(R.id.ivUserCartImage);
            tvUserCartName = itemView.findViewById(R.id.tvUserCartName);
            tvUserCartPrice = itemView.findViewById(R.id.tvUserCartPrice);
            btnUserCartMinus = itemView.findViewById(R.id.btnUserCartMinus);
            tvUserCartQty = itemView.findViewById(R.id.tvUserCartQty);
            btnUserCartPlus = itemView.findViewById(R.id.btnUserCartPlus);
            tvUserCartDelete = itemView.findViewById(R.id.tvUserCartDelete); // Ánh xạ
        }
    }
}