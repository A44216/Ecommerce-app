package com.example.ecommerceapp.ui.activity.home.user.cart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.Product;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.example.ecommerceapp.ui.adapter.user.UserCartAdapter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserCartActivity extends AppCompatActivity implements UserCartAdapter.OnCartChangeListener {

    private List<UserCartItem> cartItems;
    private UserCartAdapter adapter;
    private TextView tvUserCartTotal;
    private Button btnUserCartCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_cart);

        tvUserCartTotal = findViewById(R.id.tvUserCartTotal);
        btnUserCartCheckout = findViewById(R.id.btnUserCartCheckout);
        findViewById(R.id.btnUserCartBack).setOnClickListener(v -> finish());

        RecyclerView rv = findViewById(R.id.rvUserCart);
        rv.setLayoutManager(new LinearLayoutManager(this));

        cartItems = com.example.ecommerceapp.utils.CartManager.getInstance().getCartItems();
        adapter = new UserCartAdapter(cartItems, this);
        rv.setAdapter(adapter);
    }

    @Override
    public void onPriceChanged() {
        BigDecimal total = BigDecimal.ZERO;
        int count = 0;
        for (UserCartItem item : cartItems) {
            if (item.isChecked()) {
                BigDecimal price = item.getProduct().getPrice();
                total = total.add(price.multiply(new BigDecimal(item.getQuantity())));
                count++;
            }
        }
        DecimalFormat df = new DecimalFormat("#,###");
        tvUserCartTotal.setText(df.format(total) + "đ");
        btnUserCartCheckout.setText("Mua hàng (" + count + ")");
    }


}