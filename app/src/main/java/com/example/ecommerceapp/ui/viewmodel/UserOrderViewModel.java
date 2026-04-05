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

    public void fetchOrdersByUser(int userId) {
        repository.getOrdersByUser(userId).enqueue(new Callback<List<UserOrderResponse>>() {
            @Override
            public void onResponse(Call<List<UserOrderResponse>> call, Response<List<UserOrderResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.setValue(response.body());
                } else {
                    errorMessage.setValue("Không thể tải đơn hàng");
                }
            }

            @Override
            public void onFailure(Call<List<UserOrderResponse>> call, Throwable t) {
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}