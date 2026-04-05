package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import com.example.ecommerceapp.data.repository.UserOrderRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCheckoutViewModel extends ViewModel {

    private final UserOrderRepository repository;
    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> orderError = new MutableLiveData<>();

    public UserCheckoutViewModel(UserOrderRepository repository) {
        this.repository = repository;
    }

    public LiveData<Boolean> getOrderSuccess() { return orderSuccess; }
    public LiveData<String> getOrderError() { return orderError; }

    public void placeOrder(UserOrderRequest request) {
        repository.createOrder(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    orderSuccess.setValue(true);
                } else {
                    orderError.setValue("Lỗi từ server: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                orderError.setValue("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }
}