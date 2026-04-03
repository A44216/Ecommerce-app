package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.UserResponse;
import com.example.ecommerceapp.data.repository.UserRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private final MutableLiveData<List<UserResponse>> userList = new MutableLiveData<>();
    private final UserRepository repository;

    public UserViewModel(UserRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<UserResponse>> getUsers() {
        return userList;
    }

    public void fetchUsers() {
        repository.getUsers().enqueue(new Callback<List<UserResponse>>() {
            @Override
            public void onResponse(Call<List<UserResponse>> call, Response<List<UserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<UserResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}