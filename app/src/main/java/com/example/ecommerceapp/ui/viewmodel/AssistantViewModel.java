package com.example.ecommerceapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.AssistantApiService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.assistant.AssistantChatItem;
import com.example.ecommerceapp.data.model.assistant.AssistantChatRequest;
import com.example.ecommerceapp.data.model.assistant.AssistantChatResponse;
import com.example.ecommerceapp.data.model.assistant.MessageContext;
import com.example.ecommerceapp.utils.AssistantHistoryManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssistantViewModel extends AndroidViewModel {

    private final AssistantApiService apiService;
    private final MutableLiveData<List<AssistantChatItem>> chatItemsLiveData = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    
    // Lưu lịch sử raw để gửi cho API
    private final List<MessageContext> rawHistory = new ArrayList<>();
    // Lưu list hiển thị UI
    private final List<AssistantChatItem> displayItems = new ArrayList<>();

    private final AssistantHistoryManager historyManager;
    private final long userId;

    public AssistantViewModel(@NonNull Application application) {
        super(application);
        TokenManager tokenManager = TokenManager.getInstance(application);
        apiService = ApiClient.getAssistantApiService(tokenManager);
        historyManager = new AssistantHistoryManager(application);
        userId = tokenManager.getUserId();

        // Load history
        if (historyManager.isHistoryValid(userId)) {
            List<AssistantChatItem> savedDisplay = historyManager.getDisplayItems(userId);
            List<MessageContext> savedRaw = historyManager.getRawHistory(userId);

            if (!savedDisplay.isEmpty()) {
                displayItems.addAll(savedDisplay);
                rawHistory.addAll(savedRaw);
                chatItemsLiveData.setValue(displayItems);
            } else {
                addAiMessage("Xin chào! Tôi là Trợ lý ảo mua sắm. Bạn muốn tìm gì hôm nay?", null);
            }
        } else {
            // Xóa lịch sử cũ đi nếu quá 24h
            historyManager.clearHistory(userId);
            addAiMessage("Xin chào! Tôi là Trợ lý ảo mua sắm. Bạn muốn tìm gì hôm nay?", null);
        }
    }

    public LiveData<List<AssistantChatItem>> getChatItems() {
        return chatItemsLiveData;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void sendMessage(String text) {
        if (text == null || text.trim().isEmpty()) return;

        // 1. Thêm tin nhắn của User vào UI và History
        addUserMessage(text.trim());
        
        // 2. Gọi API
        isLoading.setValue(true);
        AssistantChatRequest request = new AssistantChatRequest(new ArrayList<>(rawHistory), text.trim());
        
        // Add to history AFTER sending, so history doesn't contain current message twice
        rawHistory.add(new MessageContext("user", text.trim()));
        saveHistory();

        apiService.chatWithAssistant(request).enqueue(new Callback<AssistantChatResponse>() {
            @Override
            public void onResponse(Call<AssistantChatResponse> call, Response<AssistantChatResponse> response) {
                isLoading.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    AssistantChatResponse chatResponse = response.body();
                    
                    // Thêm vào lịch sử API
                    rawHistory.add(new MessageContext("assistant", chatResponse.getText()));

                    // Thêm vào UI
                    if ("PRODUCT_CAROUSEL".equals(chatResponse.getType()) && chatResponse.getData() != null) {
                        addAiCarousel(chatResponse.getText(), chatResponse.getData());
                    } else {
                        addAiMessage(chatResponse.getText(), null);
                    }
                    
                    saveHistory();
                } else {
                    addAiMessage("Xin lỗi, tôi không thể xử lý yêu cầu lúc này.", null);
                }
            }

            @Override
            public void onFailure(Call<AssistantChatResponse> call, Throwable t) {
                isLoading.setValue(false);
                addAiMessage("Có lỗi kết nối xảy ra. Vui lòng thử lại sau.", null);
            }
        });
    }

    private void saveHistory() {
        historyManager.saveHistory(userId, displayItems, rawHistory);
    }

    private void addUserMessage(String text) {
        displayItems.add(new AssistantChatItem(AssistantChatItem.VIEW_TYPE_USER, text, null));
        chatItemsLiveData.setValue(displayItems);
    }

    private void addAiMessage(String text, Object data) {
        displayItems.add(new AssistantChatItem(AssistantChatItem.VIEW_TYPE_AI_TEXT, text, null));
        chatItemsLiveData.setValue(displayItems);
    }

    private void addAiCarousel(String text, List<com.example.ecommerceapp.data.model.response.ProductResponse> products) {
        displayItems.add(new AssistantChatItem(AssistantChatItem.VIEW_TYPE_AI_CAROUSEL, text, products));
        chatItemsLiveData.setValue(displayItems);
    }
}
