package com.example.ecommerceapp.ui.fragment.user;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.utils.ImageLoader;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;

public class AddToCartBottomSheet extends BottomSheetDialogFragment {

    private UserProductResponse product;
    private int stock;
    private int currentQuantity = 1;
    private OnConfirmListener listener;

    public interface OnConfirmListener {
        void onConfirm(int quantity);
    }

    public AddToCartBottomSheet(UserProductResponse product, int stock, OnConfirmListener listener) {
        this.product = product;
        this.stock = stock;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_bottom_sheet_cart, container, false);

        ImageView ivImage = view.findViewById(R.id.ivBsProductImage);
        TextView tvPrice = view.findViewById(R.id.tvBsProductPrice);
        TextView tvStock = view.findViewById(R.id.tvBsProductStock);
        TextView btnMinus = view.findViewById(R.id.btnBsMinus);
        TextView tvQuantity = view.findViewById(R.id.tvBsQuantity);
        TextView btnPlus = view.findViewById(R.id.btnBsPlus);
        Button btnConfirm = view.findViewById(R.id.btnBsConfirm);

        // Hiển thị dữ liệu
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            ImageLoader.load(getContext(), ivImage, product.getImages().get(0).getImageUrl());
        } else {
            ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        DecimalFormat df = new DecimalFormat("#,###");
        if (product.getPrice() != null) {
            tvPrice.setText(df.format(product.getPrice()) + "đ");
        }

        tvStock.setText("Kho: " + stock);
        tvQuantity.setText(String.valueOf(currentQuantity));

        // Nút trừ
        btnMinus.setOnClickListener(v -> {
            if (currentQuantity > 1) {
                currentQuantity--;
                tvQuantity.setText(String.valueOf(currentQuantity));
            }
        });

        // Nút cộng
        btnPlus.setOnClickListener(v -> {
            if (currentQuantity < stock) {
                currentQuantity++;
                tvQuantity.setText(String.valueOf(currentQuantity));
            } else {
                Toast.makeText(getContext(), "Đã đạt số lượng tồn kho tối đa", Toast.LENGTH_SHORT).show();
            }
        });

        // Nút xác nhận
        btnConfirm.setOnClickListener(v -> {
            if (stock <= 0) {
                Toast.makeText(getContext(), "Sản phẩm đã hết hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            if (listener != null) {
                listener.onConfirm(currentQuantity);
            }
            dismiss();
        });

        return view;
    }
}
