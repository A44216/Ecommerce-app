package com.example.ecommerceapp.ui.activity.home.user.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.ui.adapter.user.UserConversationAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserConversationListActivity extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView rvConversations;
    private TextView tvEmpty;

    private UserConversationAdapter adapter;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_conversation_list);

        tokenManager = TokenManager.getInstance(this);

        ivBack = findViewById(R.id.ivBack);
        rvConversations = findViewById(R.id.rvConversations);
        tvEmpty = findViewById(R.id.tvEmpty);

        ivBack.setOnClickListener(v -> finish());

        rvConversations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserConversationAdapter();
        rvConversations.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadConversations();
    }

    private void loadConversations() {
        int currentUserId = (int) tokenManager.getUserId();
        if (currentUserId == -1) return;
        
        ApiClient.getChatApiService(tokenManager).getConversationsByCustomer(currentUserId).enqueue(new Callback<List<ConversationResponse>>() {
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
                    Toast.makeText(UserConversationListActivity.this, "Không thể tải danh sách chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ConversationResponse>> call, Throwable t) {
                Toast.makeText(UserConversationListActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
