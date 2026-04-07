package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import com.example.ecommerceapp.data.repository.UserAddressRepository;
import com.example.ecommerceapp.data.repository.UserOrderRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserCheckoutViewModel extends ViewModel {

    private final UserOrderRepository orderRepository;
    private final UserAddressRepository addressRepository; // Kho địa chỉ mới

    private final MutableLiveData<Boolean> orderSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> orderError = new MutableLiveData<>();
    private final MutableLiveData<List<UserAddressResponse>> addressList = new MutableLiveData<>();

    public UserCheckoutViewModel(UserOrderRepository orderRepository, UserAddressRepository addressRepository) {
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
    }

    public LiveData<Boolean> getOrderSuccess() { return orderSuccess; }
    public LiveData<String> getOrderError() { return orderError; }
    public LiveData<List<UserAddressResponse>> getAddressList() { return addressList; }

    // API lấy địa chỉ
    public void fetchAddresses(int userId) {
        addressRepository.getAddressesByUserId(userId).enqueue(new Callback<List<UserAddressResponse>>() {
            @Override
            public void onResponse(Call<List<UserAddressResponse>> call, Response<List<UserAddressResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addressList.setValue(response.body());
                } else {
                    orderError.setValue("Không thể tải danh sách địa chỉ");
                }
            }

            @Override
            public void onFailure(Call<List<UserAddressResponse>> call, Throwable t) {
                orderError.setValue("Lỗi kết nối địa chỉ: " + t.getMessage());
            }
        });
    }

    // API đặt hàng
    public void placeOrder(UserOrderRequest request) {
        orderRepository.createOrder(request).enqueue(new Callback<Void>() {
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