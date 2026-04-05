package com.example.ecommerceapp.ui.activity.home.user.checkout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.example.ecommerceapp.ui.adapter.user.UserCheckoutAdapter;
import com.example.ecommerceapp.utils.CartManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserCheckoutActivity extends AppCompatActivity {

    private List<UserCartItem> selectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkout);

        findViewById(R.id.btnCheckoutBack).setOnClickListener(v -> finish());
        TextView tvCheckoutFinalTotal = findViewById(R.id.tvCheckoutFinalTotal);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        RecyclerView rvCheckoutItems = findViewById(R.id.rvCheckoutItems);

        // 1. CHỈ LẤY NHỮNG MÓN ĐÃ ĐƯỢC TÍCH CHECKBOX Ở GIỎ HÀNG
        selectedItems = new ArrayList<>();
        List<UserCartItem> allCartItems = CartManager.getInstance().getCartItems();
        for (UserCartItem item : allCartItems) {
            if (item.isChecked()) {
                selectedItems.add(item);
            }
        }

        // Chống lỗi nếu không có món nào (dù đã chặn ở giỏ hàng)
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. SETUP GIAO DIỆN DANH SÁCH
        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        UserCheckoutAdapter adapter = new UserCheckoutAdapter(selectedItems);
        rvCheckoutItems.setAdapter(adapter);

        // 3. TÍNH TỔNG TIỀN (Có thể cộng thêm phí ship ở đây)
        BigDecimal total = BigDecimal.ZERO;
        for (UserCartItem item : selectedItems) {
            BigDecimal price = item.getProduct().getPrice();
            total = total.add(price.multiply(new BigDecimal(item.getQuantity())));
        }
        // Giả sử phí ship là 30.000đ
        BigDecimal shippingFee = new BigDecimal("30000");
        BigDecimal finalTotal = total.add(shippingFee);

        DecimalFormat df = new DecimalFormat("#,###");
        tvCheckoutFinalTotal.setText(df.format(finalTotal) + "đ");

        // 4. SỰ KIỆN BẤM ĐẶT HÀNG
        // 4. SỰ KIỆN BẤM ĐẶT HÀNG
        btnPlaceOrder.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đặt hàng thành công!")
                    .setMessage("Cảm ơn bạn đã mua sắm. Đơn hàng sẽ sớm được giao đến bạn.")
                    .setCancelable(false) // Không cho bấm ra ngoài để đóng bảng
                    .setPositiveButton("Về Trang chủ", (dialog, which) -> {

                        // 1. Xóa các món đã mua khỏi giỏ hàng chung
                        CartManager.getInstance().getCartItems().removeAll(selectedItems);

                        // 2. Chuyển hướng về màn hình chính và xóa lịch sử các trang trước đó
                        // LƯU Ý: Thay "MainActivity.class" bằng tên Activity chứa HomeFragment của bạn nếu nó tên khác
                        Intent intent = new Intent(UserCheckoutActivity.this, com.example.ecommerceapp.ui.activity.home.UserHomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        // 3. Kết thúc Activity thanh toán hiện tại
                        finish();
                    })
                    .show();
        });
    }
}