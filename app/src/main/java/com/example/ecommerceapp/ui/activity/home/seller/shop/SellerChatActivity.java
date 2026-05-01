package com.example.ecommerceapp.ui.activity.home.seller.shop;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.ui.adapter.seller.SellerConversationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerChatActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvConversations;
    private TextView tvEmpty;

    private SellerConversationAdapter adapter;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seller_chat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ivBack), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop() + systemBars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        tokenManager = TokenManager.getInstance(this);

        ivBack = findViewById(R.id.ivBack);
        rvConversations = findViewById(R.id.rvConversations);
        tvEmpty = findViewById(R.id.tvEmpty);

        ivBack.setOnClickListener(v -> finish());

        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SellerConversationAdapter();
        rvConversations.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        long currentShopId = tokenManager.getShopId();
        if (currentShopId <= 0) return;
        
        ApiClient.getChatApiService(tokenManager).getConversationsByShop((int) currentShopId).enqueue(new Callback<List<ConversationResponse>>() {
            @Override
            public void onResponse(Call<List<ConversationResponse>> call, Response<List<ConversationResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ConversationResponse> list = response.body();
                    adapter.setData(list);

                    if (list.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        rvConversations.setVisibility(View.GONE);
                    } else {
                        tvEmpty.setVisibility(View.GONE);
                        rvConversations.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SellerChatActivity.this, "Không thể tải danh sách chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ConversationResponse>> call, Throwable t) {
                Toast.makeText(SellerChatActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}