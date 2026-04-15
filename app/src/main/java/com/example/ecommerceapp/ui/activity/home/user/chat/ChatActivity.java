package com.example.ecommerceapp.ui.activity.home.user.chat;

import android.os.Bundle;
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

        if (shopName != null) {
            tvChatShopName.setText(shopName);
        }

        // 4. Setup RecyclerView (Rất quan trọng: StackFromEnd giúp tin nhắn đẩy từ dưới lên)
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);

        adapter = new MessageAdapter(currentUserId);
        rvMessages.setAdapter(adapter);

        // 5. Bắt sự kiện
        btnBackChat.setOnClickListener(v -> finish());
        btnSendMessage.setOnClickListener(v -> sendMessage());

        // 6. Tải lịch sử tin nhắn
        loadMessages();
    }

    private void loadMessages() {
        ApiClient.getChatApiService(tokenManager).getMessages(conversationId).enqueue(new Callback<List<MessageResponse>>() {
            @Override
            public void onResponse(Call<List<MessageResponse>> call, Response<List<MessageResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    // Cuộn xuống tin nhắn cuối cùng để người dùng dễ đọc
                    if (adapter.getItemCount() > 0) {
                        rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MessageResponse>> call, Throwable t) {
                Toast.makeText(ChatActivity.this, "Không thể tải tin nhắn", Toast.LENGTH_SHORT).show();
            }
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
}