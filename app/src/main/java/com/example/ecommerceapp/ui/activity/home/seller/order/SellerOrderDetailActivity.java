package com.example.ecommerceapp.ui.activity.home.seller.order;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;

public class SellerOrderDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_order_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView rv = findViewById(R.id.rvOrderItems);

        rv.setLayoutManager(new LinearLayoutManager(this));

// fake 100 item (chỉ cần số lượng thôi)
        int itemCount = 100;

// 👉 Adapter inline
        RecyclerView.Adapter adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                ImageView img = new ImageView(parent.getContext());
                img.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        200
                ));
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setImageResource(R.drawable.ic_launcher_background); // ảnh mặc định

                return new RecyclerView.ViewHolder(img) {};
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                // không cần bind gì
            }

            @Override
            public int getItemCount() {
                return itemCount;
            }
        };

        rv.setAdapter(adapter);

    }
}