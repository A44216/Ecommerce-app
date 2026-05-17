package com.example.ecommerceapp.ui.activity.home.user.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.AiChatRequest;
import com.example.ecommerceapp.data.model.response.MessageResponse;
import com.example.ecommerceapp.ui.adapter.user.MessageAdapter;

import java.time.LocalDateTime;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AiChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText edtChatMessage;
    private ImageView btnSendMessage, btnBackChat;
    private TextView tvChatShopName;
    private ProgressBar pbLoading;

    private MessageAdapter adapter;
    private TokenManager tokenManager;
    private int shopId;
    private int currentUserId;

    private int conversationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_chat);

        tokenManager = TokenManager.getInstance(this);
        currentUserId = (int) tokenManager.getUserId();

        shopId = getIntent().getIntExtra("SHOP_ID", -1);
        String shopName = getIntent().getStringExtra("SHOP_NAME");
        conversationId = getIntent().getIntExtra("CONVERSATION_ID", -1);

        if (shopId == -1 || conversationId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy Shop hoặc Phòng chat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews(shopName);
        setupRecyclerView();
        loadAiChatHistory();
    }

    private void initViews(String shopName) {
        rvMessages = findViewById(R.id.rvMessages);
        edtChatMessage = findViewById(R.id.edtChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnBackChat = findViewById(R.id.btnBackChat);
        tvChatShopName = findViewById(R.id.tvChatShopName);
        pbLoading = findViewById(R.id.pbLoading);

        if (shopName != null) {
            tvChatShopName.setText("Trợ Lý AI - " + shopName);
        }

        btnBackChat.setOnClickListener(v -> finish());
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private boolean isLoading = false;

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(currentUserId);
        rvMessages.setAdapter(adapter);

        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && !isLoading) { // Lướt lên
                    int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstVisiblePosition == 0) {
                        loadOlderAiMessages();
                    }
                }
            }
        });
    }

    private void loadAiChatHistory() {
        pbLoading.setVisibility(View.VISIBLE);
        ApiClient.getChatApiService(tokenManager).getAiChatMessages(conversationId, null).enqueue(new Callback<java.util.List<MessageResponse>>() {
            @Override
            public void onResponse(Call<java.util.List<MessageResponse>> call, Response<java.util.List<MessageResponse>> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    if (adapter.getItemCount() > 0) {
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
                
                if (adapter.getItemCount() == 0) {
                    showDefaultWelcome();
                }
            }

            @Override
            public void onFailure(Call<java.util.List<MessageResponse>> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(AiChatActivity.this, "Không thể tải lịch sử chat", Toast.LENGTH_SHORT).show();
                showDefaultWelcome();
            }
        });
    }

    private void loadOlderAiMessages() {
        if (adapter.getItemCount() == 0) return;
        
        isLoading = true;
        MessageResponse oldestMsg = adapter.getMessages().get(0);
        String beforeTime = oldestMsg.getCreatedAt();

        ApiClient.getChatApiService(tokenManager).getAiChatMessages(conversationId, beforeTime).enqueue(new Callback<java.util.List<MessageResponse>>() {
            @Override
            public void onResponse(Call<java.util.List<MessageResponse>> call, Response<java.util.List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    adapter.addOldMessages(response.body());
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<java.util.List<MessageResponse>> call, Throwable t) {
                isLoading = false;
            }
        });
    }

    private void showDefaultWelcome() {
        MessageResponse welcomeMsg = new MessageResponse();
        welcomeMsg.setSenderId(-1); // ID giả cho AI
        welcomeMsg.setMessage("Chào bạn, tôi là trợ lý ảo. Tôi có thể giúp gì cho bạn?");
        welcomeMsg.setIsAiGenerated(true);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            welcomeMsg.setCreatedAt(LocalDateTime.now().toString());
        }
        adapter.addMessage(welcomeMsg);
    }

    private void sendMessage() {
        String text = edtChatMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        edtChatMessage.setText("");
        pbLoading.setVisibility(View.VISIBLE);

        // Hiển thị tạm tin nhắn của User
        MessageResponse userMsg = new MessageResponse();
        userMsg.setSenderId(currentUserId);
        userMsg.setMessage(text);
        userMsg.setIsAiGenerated(false);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            userMsg.setCreatedAt(LocalDateTime.now().toString());
        }
        adapter.addMessage(userMsg);
        rvMessages.scrollToPosition(adapter.getItemCount() - 1);

        // Gọi API hỏi AI (đã lưu vào DB ở Backend)
        AiChatRequest request = new AiChatRequest(shopId, conversationId, currentUserId, text);
        ApiClient.getAiChatApiService(tokenManager).askAi(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                pbLoading.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.addMessage(response.body());
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                } else {
                    Toast.makeText(AiChatActivity.this, "AI đang bận, thử lại sau", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                pbLoading.setVisibility(View.GONE);
                Toast.makeText(AiChatActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
