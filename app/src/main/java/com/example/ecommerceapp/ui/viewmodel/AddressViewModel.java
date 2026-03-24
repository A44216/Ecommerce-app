package com.example.ecommerceapp.ui.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.AddressResponse;
import com.example.ecommerceapp.data.repository.AddressRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressViewModel extends ViewModel {

    private MutableLiveData<List<AddressResponse>> addresses = new MutableLiveData<>();
    private AddressRepository repository = new AddressRepository();

    public LiveData<List<AddressResponse>> getAddresses() {
        return addresses;
    }

    public void fetchAddresses() {
        repository.getAddresses().enqueue(new Callback<List<AddressResponse>>() {
            @Override
            public void onResponse(Call<List<AddressResponse>> call, Response<List<AddressResponse>> response) {
                if (response.isSuccessful()) {
                    addresses.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<AddressResponse>> call, Throwable t) {
                Log.e("ADDRESS_ERROR", t.getMessage());
            }
        });
    }
}
