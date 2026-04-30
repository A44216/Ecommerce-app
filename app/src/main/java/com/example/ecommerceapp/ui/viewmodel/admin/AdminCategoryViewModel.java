package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminCategoryResponse;
import com.example.ecommerceapp.data.repository.admin.AdminCategoryRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCategoryViewModel extends ViewModel {

    private final AdminCategoryRepository repository;

    private final MutableLiveData<List<AdminCategoryResponse>> all = new MutableLiveData<>();
    private final MutableLiveData<List<AdminCategoryResponse>> deleted = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<String>> autocompleteSuggestions = new MutableLiveData<>();

    private String currentKeyword = "";
    private boolean isDeletedTab = false;

    public AdminCategoryViewModel(AdminCategoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<AdminCategoryResponse>> getAll() {
        return all;
    }

    public LiveData<List<AdminCategoryResponse>> getDeleted() {
        return deleted;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<String>> getAutocompleteSuggestions() {
        return autocompleteSuggestions;
    }

    // LOAD
    public void fetchCategories(Boolean isDeleted) {
        repository.getAllCategories(isDeleted, currentKeyword)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<List<AdminCategoryResponse>> call,
                                           Response<List<AdminCategoryResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            if (Boolean.TRUE.equals(isDeleted)) {
                                deleted.setValue(response.body());
                            } else {
                                all.setValue(response.body());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AdminCategoryResponse>> call, Throwable t) {
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

    // AUTOCOMPLETE
    public void autocomplete(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            autocompleteSuggestions.setValue(null);
            return;
        }

        repository.autocompleteCategories(keyword).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<AdminCategoryResponse>> call, Response<List<AdminCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> suggestions = new java.util.ArrayList<>();
                    for (AdminCategoryResponse category : response.body()) {
                        suggestions.add(category.getName());
                    }
                    autocompleteSuggestions.setValue(suggestions);
                }
            }

            @Override
            public void onFailure(Call<List<AdminCategoryResponse>> call, Throwable t) {
                // Ignore errors for autocomplete
            }
        });
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