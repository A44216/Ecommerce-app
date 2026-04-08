package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.UserOrderResponse;
import com.example.ecommerceapp.data.repository.UserOrderRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrderViewModel extends ViewModel {

    private final UserOrderRepository repository;
    private final MutableLiveData<List<UserOrderResponse>> orderList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public UserOrderViewModel(UserOrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<UserOrderResponse>> getOrderList() { return orderList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchOrdersByUserAndStatus(int userId, String statusFilter) {
        repository.getOrdersByUser(userId).enqueue(new Callback<List<UserOrderResponse>>() {
            @Override
            public void onResponse(Call<List<UserOrderResponse>> call, Response<List<UserOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserOrderResponse> allOrders = response.body();

                    // NẾU CHỌN "ALL" THÌ HIỂN THỊ HẾT
                    if ("ALL".equals(statusFilter)) {
                        orderList.setValue(allOrders);
                        return;
                    }

                    // NẾU CHỌN TRẠNG THÁI CỤ THỂ -> LỌC RA
                    List<UserOrderResponse> filteredList = new java.util.ArrayList<>();
                    for (UserOrderResponse order : allOrders) {
                        if (statusFilter.equals(order.getStatus())) {
                            filteredList.add(order);
                        }
                    }
                    orderList.setValue(filteredList);
                }
            }

            @Override
            public void onFailure(Call<List<UserOrderResponse>> call, Throwable t) {
                errorMessage.setValue("Lỗi tải đơn hàng: " + t.getMessage());
            }
        });
    }
}