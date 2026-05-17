package com.example.ecommerceapp.ui.activity.home.user.chat;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.MessageRequest;
import com.example.ecommerceapp.data.model.response.MessageResponse;
import com.example.ecommerceapp.ui.adapter.user.MessageAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText edtChatMessage;
    private ImageView btnSendMessage, btnBackChat;
    private TextView tvChatShopName;

    private MessageAdapter adapter;
    private int conversationId;
    private TokenManager tokenManager;
    private int currentUserId;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable pollingRunnable;
    private static final int POLLING_INTERVAL = 5000; // Tăng lên 5 giây để giảm tải Server

    private boolean isLoading = false;
    private androidx.appcompat.widget.AppCompatButton btnAiAssistant;
    private int shopId; // Sẽ lấy từ API hoặc Intent nếu có

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 1. Khởi tạo
        tokenManager = TokenManager.getInstance(this);
        currentUserId = (int) tokenManager.getUserId();

        // 2. Nhận dữ liệu từ Intent
        conversationId = getIntent().getIntExtra("CONVERSATION_ID", -1);
        String shopName = getIntent().getStringExtra("SHOP_NAME");
        shopId = getIntent().getIntExtra("SHOP_ID", -1);

        if (conversationId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy phòng chat!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 3. Ánh xạ View
        rvMessages = findViewById(R.id.rvMessages);
        edtChatMessage = findViewById(R.id.edtChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        btnBackChat = findViewById(R.id.btnBackChat);
        tvChatShopName = findViewById(R.id.tvChatShopName);
        btnAiAssistant = findViewById(R.id.btnAiAssistant);

        boolean isSeller = getIntent().getBooleanExtra("IS_SELLER", false);
        if (isSeller) {
            btnAiAssistant.setVisibility(View.GONE);
        }

        if (shopName != null) {
            tvChatShopName.setText(shopName);
        }

        // 4. Setup RecyclerView (Rất quan trọng: StackFromEnd giúp tin nhắn đẩy từ dưới lên)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(currentUserId);
        rvMessages.setAdapter(adapter);

        // Lắng nghe sự kiện lướt lên để tải thêm tin nhắn
        rvMessages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0 && !isLoading) { // Lướt lên
                    int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    if (firstVisiblePosition == 0) {
                        loadOlderMessages();
                    }
                }
            }
        });

        // 5. Bắt sự kiện
        btnBackChat.setOnClickListener(v -> finish());
        btnSendMessage.setOnClickListener(v -> sendMessage());
        btnAiAssistant.setOnClickListener(v -> {
            if (shopId == -1) {
                Toast.makeText(this, "Chưa xác định được Shop", Toast.LENGTH_SHORT).show();
                return;
            }
            android.content.Intent intent = new android.content.Intent(ChatActivity.this, AiChatActivity.class);
            intent.putExtra("SHOP_NAME", shopName);
            intent.putExtra("SHOP_ID", shopId);
            intent.putExtra("CONVERSATION_ID", conversationId);
            startActivity(intent);
        });

        // 6. Tải lịch sử tin nhắn và bắt đầu Polling
        loadMessages();
        startPolling();
    }

    private void startPolling() {
        stopPolling(); // Dừng cái cũ nếu có
        pollingRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessagesQuietly();
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        };
        handler.postDelayed(pollingRunnable, POLLING_INTERVAL);
    }

    private void stopPolling() {
        if (handler != null && pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPolling(); // Chạy lại khi quay lại app
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling(); // Dừng ngay khi ẩn app/thoát màn hình để tiết kiệm Railway
    }

    private void loadMessagesQuietly() {
        ApiClient.getChatApiService(tokenManager).getMessages(conversationId, null).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int oldCount = adapter.getItemCount();
                    List<MessageResponse> newMessages = response.body();
                    adapter.updateData(newMessages);

                    // Chỉ cuộn xuống dưới cùng nếu phát hiện có tin nhắn mới (chống giật màn hình)
                    if (newMessages.size() > oldCount && adapter.getItemCount() > 0) {
                        rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);
                        markMessagesAsRead();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                // Polling ngầm thất bại (do mạng yếu) -> im lặng bỏ qua, không hiển thị Toast làm phiền người dùng
            }
        });
    }

    private void loadOlderMessages() {
        if (adapter.getItemCount() == 0) return;
        
        isLoading = true;
        MessageResponse oldestMsg = adapter.getMessages().get(0);
        String beforeTime = oldestMsg.getCreatedAt().toString();

        ApiClient.getChatApiService(tokenManager).getMessages(conversationId, beforeTime).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    List<MessageResponse> oldMessages = response.body();
                    adapter.addOldMessages(oldMessages);
                }
                isLoading = false;
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                isLoading = false;
            }
        });
    }

    private void loadMessages() {
        ApiClient.getChatApiService(tokenManager).getMessages(conversationId, null).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    // Cuộn xuống tin nhắn cuối cùng để người dùng dễ đọc
                    if (adapter.getItemCount() > 0) {
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                    }
                    markMessagesAsRead();
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Không thể tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void markMessagesAsRead() {
        ApiClient.getChatApiService(tokenManager).markAsRead(conversationId, currentUserId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {}
            @Override
            public void onFailure(Call<Void> call, Throwable t) {}
        });
    }

    private void sendMessage() {
        String text = edtChatMessage.getText().toString().trim();
        if (text.isEmpty()) return;

        // Xóa trắng ô nhập ngay lập tức để tạo cảm giác mượt mà cho người dùng
        edtChatMessage.setText("");

        MessageRequest request = new MessageRequest(conversationId, text, currentUserId);

        ApiClient.getChatApiService(tokenManager).sendMessage(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Thêm tin nhắn mới vào Adapter và cuộn xuống dưới cùng
                    adapter.addMessage(response.body());
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                } else {
                    Toast.makeText(ChatActivity.this, "Gửi thất bại", Toast.LENGTH_SHORT).show();
                    // Nếu gửi xịt, trả lại nội dung vào ô nhập để khách không phải gõ lại
                    edtChatMessage.setText(text);
                }
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                edtChatMessage.setText(text);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hủy Polling khi thoát màn hình Chat để tiết kiệm bộ nhớ và băng thông
        if (handler != null && pollingRunnable != null) {
            handler.removeCallbacks(pollingRunnable);
        }
    }
}