package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.admin.CategoryAdminResponse;
import com.example.ecommerceapp.data.repository.admin.category.AdminCategoryRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryViewModel extends ViewModel {

    private final AdminCategoryRepository repository;

    private final MutableLiveData<List<CategoryAdminResponse>> all = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryAdminResponse>> deleted = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private String currentKeyword = "";
    private boolean isDeletedTab = false;

    public AdminCategoryViewModel(AdminCategoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<CategoryAdminResponse>> getAll() {
        return all;
    }

    public LiveData<List<CategoryAdminResponse>> getDeleted() {
        return deleted;
    }

    public LiveData<String> getError() {
        return error;
    }

    // LOAD
    public void fetchCategories(Boolean isDeleted) {
        repository.getAllCategories(isDeleted, currentKeyword)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<List<CategoryAdminResponse>> call,
                                           Response<List<CategoryAdminResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            if (Boolean.TRUE.equals(isDeleted)) {
                                deleted.setValue(response.body());
                            } else {
                                all.setValue(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<CategoryAdminResponse>> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
    }

    // SEARCH (FIX)
    public void search(String keyword) {
        currentKeyword = keyword;

        fetchCategories(false);
        fetchCategories(true);
    }

    // RELOAD TAB
    public void reload(Boolean isDeleted) {
        isDeletedTab = isDeleted;
        fetchCategories(isDeleted);
    }

    // DELETE
    public void delete(int id, Boolean isDeleted) {
        repository.deleteCategory(id).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call call, Response response) {
                fetchCategories(false);
                fetchCategories(true);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // CREATE
    public void create(CategoryRequest request) {
        repository.createCategory(request).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {
                    fetchCategories(false);
                    fetchCategories(true);
                    return;
                }

                try {
                    String msg = response.errorBody() != null
                            ? response.errorBody().string().trim()
                            : "SERVER_ERROR";

                    error.setValue(msg);

                } catch (Exception e) {
                    error.setValue("SERVER_ERROR");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                error.setValue("NETWORK_ERROR");
            }
        });
    }

    // UPDATE
    public void update(int id, CategoryRequest request) {
        repository.updateCategory(id, request).enqueue(new Callback<>() {

            @Override
            public void onResponse(Call call, Response response) {

                if (response.isSuccessful()) {
                    fetchCategories(false);
                    fetchCategories(true);
                    return;
                }

                try {
                    String msg = response.errorBody() != null
                            ? response.errorBody().string().trim()
                            : "SERVER_ERROR";

                    error.setValue(msg);

                } catch (Exception e) {
                    error.setValue("SERVER_ERROR");
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                error.setValue("NETWORK_ERROR");
            }
        });
    }

    public void restore(int id) {
        repository.restoreCategory(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (!response.isSuccessful()) return;

                fetchCategories(false);
                fetchCategories(true);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void clearError() {
        error.setValue(null);
    }

}