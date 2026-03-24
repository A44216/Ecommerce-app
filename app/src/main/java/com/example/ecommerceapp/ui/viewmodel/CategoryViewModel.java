package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.repository.CategoryRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryViewModel extends ViewModel {

    private MutableLiveData<List<CategoryResponse>> categoryList = new MutableLiveData<>();
    private CategoryRepository repository = new CategoryRepository();

    public LiveData<List<CategoryResponse>> getCategories() {
        return categoryList;
    }

    public void fetchCategories() {
        repository.getCategories().enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful()) {
                    categoryList.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}
