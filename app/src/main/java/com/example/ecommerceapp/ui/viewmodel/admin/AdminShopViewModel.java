package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopResponse;
import com.example.ecommerceapp.data.repository.admin.AdminShopRepository;

import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopAutocompleteResponse;

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
    private final MutableLiveData<List<AdminShopAutocompleteResponse>> autocompleteSuggestions = new MutableLiveData<>();

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

    public LiveData<List<AdminShopAutocompleteResponse>> getAutocompleteSuggestions() {
        return autocompleteSuggestions;
    }

    public void autocomplete(String keyword) {
        repository.autocomplete(keyword).enqueue(new Callback<List<AdminShopAutocompleteResponse>>() {
            @Override
            public void onResponse(Call<List<AdminShopAutocompleteResponse>> call, Response<List<AdminShopAutocompleteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    autocompleteSuggestions.setValue(response.body());
                } else {
                    autocompleteSuggestions.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<List<AdminShopAutocompleteResponse>> call, Throwable t) {
                autocompleteSuggestions.setValue(new ArrayList<>());
            }
        });
    }

    public void setFilters(ShopStatus status, String keyword, String sortBy, String sortDir) {
        this.currentStatus = status;
        this.currentKeyword = keyword;
        this.currentSortBy = sortBy;
        this.currentSortDir = sortDir;
        this.currentPage = 0;
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
                            List<AdminShopResponse> newItems = pageResponse.getItems();
                            
                            List<AdminShopResponse> newList;
                            if (currentPage == 0) {
                                // Tạo danh sách mới hoàn toàn cho trang đầu tiên
                                newList = new ArrayList<>(newItems != null ? newItems : new ArrayList<>());
                            } else {
                                // Thêm vào danh sách hiện tại cho các trang tiếp theo
                                List<AdminShopResponse> currentList = shops.getValue();
                                newList = new ArrayList<>(currentList != null ? currentList : new ArrayList<>());
                                if (newItems != null) {
                                    newList.addAll(newItems);
                                }
                            }
                            
                            shops.setValue(newList);
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
        List<AdminShopResponse> currentList = shops.getValue();
        if (currentList != null) {
            boolean shouldRemove = currentStatus != null && currentStatus != newStatus;
            if (shouldRemove) {
                // Tạo danh sách mới để xóa item (tránh ConcurrentModificationException)
                java.util.ArrayList<AdminShopResponse> newList = new java.util.ArrayList<>(currentList);
                for (int i = 0; i < newList.size(); i++) {
                    if (newList.get(i).getId() == shopId) {
                        newList.remove(i);
                        break;
                    }
                }
                shops.setValue(newList);
            } else {
                // Cập nhật trạng thái - tạo bản sao để kích hoạt DiffUtil
                java.util.ArrayList<AdminShopResponse> newList = new java.util.ArrayList<>(currentList);
                for (int i = 0; i < newList.size(); i++) {
                    if (newList.get(i).getId() == shopId) {
                        newList.set(i, createUpdatedShop(newList.get(i), newStatus));
                        break;
                    }
                }
                shops.setValue(newList);
            }
        }
    }

    private AdminShopResponse createUpdatedShop(AdminShopResponse original, ShopStatus newStatus) {
        AdminShopResponse updated = new AdminShopResponse();
        updated.setId(original.getId());
        updated.setShopName(original.getShopName());
        updated.setEmail(original.getEmail());
        updated.setPhone(original.getPhone());
        updated.setAvatar(original.getAvatar());
        updated.setRatingAvg(original.getRatingAvg());
        updated.setTotalOrders(original.getTotalOrders());
        updated.setTotalRevenue(original.getTotalRevenue());
        updated.setCreatedAt(original.getCreatedAt());
        updated.setStatus(newStatus);
        return updated;
    }
}
