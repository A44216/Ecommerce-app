package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.admin.shop.AdminShopResponse;
import com.example.ecommerceapp.data.repository.admin.shop.AdminShopRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminShopViewModel extends ViewModel {

    private final AdminShopRepository repository;

    private final MutableLiveData<List<AdminShopResponse>> shops = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLastPage = new MutableLiveData<>(false);

    private int currentPage = 0;
    private final int pageSize = 10;
    private ShopStatus currentStatus = null;
    private String currentKeyword = "";
    private String currentSortBy = "createdAt";
    private String currentSortDir = "desc";

    public AdminShopViewModel(AdminShopRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<AdminShopResponse>> getShops() {
        return shops;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLastPage() {
        return isLastPage;
    }

    public void setFilters(ShopStatus status, String keyword, String sortBy, String sortDir) {
        this.currentStatus = status;
        this.currentKeyword = keyword;
        this.currentSortBy = sortBy;
        this.currentSortDir = sortDir;
        this.currentPage = 0;
        this.shops.setValue(new ArrayList<>());
        this.isLastPage.setValue(false);
        loadShops();
    }

    public void loadNextPage() {
        if (Boolean.TRUE.equals(isLoading.getValue()) || Boolean.TRUE.equals(isLastPage.getValue())) {
            return;
        }
        currentPage++;
        loadShops();
    }

    private void loadShops() {
        isLoading.setValue(true);
        repository.getShops(currentPage, pageSize, currentStatus, currentKeyword, currentSortBy, currentSortDir)
                .enqueue(new Callback<PageResponse<AdminShopResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminShopResponse>> call, Response<PageResponse<AdminShopResponse>> response) {
                        isLoading.setValue(false);
                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<AdminShopResponse> pageResponse = response.body();
                            List<AdminShopResponse> currentList = shops.getValue();
                            if (currentList == null) currentList = new ArrayList<>();
                            
                            if (currentPage == 0) {
                                currentList.clear();
                            }
                            currentList.addAll(pageResponse.getItems());
                            shops.setValue(currentList);
                            isLastPage.setValue((currentPage + 1) >= pageResponse.getTotalPages());
                        } else {
                            error.setValue("Lỗi tải danh sách cửa hàng: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminShopResponse>> call, Throwable t) {
                        isLoading.setValue(false);
                        error.setValue("Lỗi kết nối: " + t.getMessage());
                    }
                });
    }

    public void updateShopStatus(int shopId, ShopStatus newStatus) {
        repository.updateShopStatus(shopId, newStatus).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    List<AdminShopResponse> currentList = shops.getValue();
                    if (currentList != null) {
                        for (int i = 0; i < currentList.size(); i++) {
                            if (currentList.get(i).getId() == shopId) {
                                currentList.get(i).setStatus(newStatus);
                                shops.setValue(currentList); // Trigger update
                                break;
                            }
                        }
                    }
                } else {
                    error.setValue("Cập nhật trạng thái thất bại: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }
}
