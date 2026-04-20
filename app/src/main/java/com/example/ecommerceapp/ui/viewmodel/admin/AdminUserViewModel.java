package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.model.response.admin.user.AdminUserResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.repository.admin.user.AdminUserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserViewModel extends ViewModel {

    private final AdminUserRepository repository;

    private String currentKeyword = "";
    private UserStatus currentStatus = null;
    private boolean isDataLoaded = false;

    public AdminUserViewModel(AdminUserRepository repository) {
        this.repository = repository;
    }

    public String getCurrentKeyword() { return currentKeyword; }
    public void setCurrentKeyword(String keyword) { this.currentKeyword = keyword; }

    public UserStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(UserStatus status) { this.currentStatus = status; }

    public boolean isDataLoaded() { return isDataLoaded; }

    private final MutableLiveData<List<AdminUserResponse>> customersLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<AdminUserResponse>> sellersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<AdminUserResponse>> getCustomersLiveData() {
        return customersLiveData;
    }

    public LiveData<List<AdminUserResponse>> getSellersLiveData() {
        return sellersLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private final Map<Role, Integer> currentPageMap = new HashMap<>();
    private final Map<Role, Boolean> lastPageMap = new HashMap<>();
    private static final int PAGE_SIZE = 10;
    private int pendingRequests = 0;

    public void fetchUsers(Role role, boolean isLoadMore, boolean isSilent) {
        int page = currentPageMap.getOrDefault(role, 0);

        if (!isLoadMore) {
            page = 0;
            currentPageMap.put(role, 0);
            lastPageMap.put(role, false);
            isDataLoaded = true;
        }

        if (Boolean.TRUE.equals(lastPageMap.get(role))) return;

        if (!isSilent) loading.setValue(true);
        pendingRequests++;

        repository.getUsers(page, PAGE_SIZE, role, currentStatus, currentKeyword)
                .enqueue(new Callback<PageResponse<AdminUserResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminUserResponse>> call, Response<PageResponse<AdminUserResponse>> response) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<AdminUserResponse> body = response.body();
                            
                            MutableLiveData<List<AdminUserResponse>> liveData = 
                                    (role == Role.CUSTOMER) ? customersLiveData : sellersLiveData;
                                    
                            List<AdminUserResponse> current = liveData.getValue();
                            if (current == null) current = new ArrayList<>();

                            List<AdminUserResponse> newItems = body.getItems();
                            if (newItems == null) newItems = new ArrayList<>();

                            if (isLoadMore) {
                                current.addAll(newItems);
                            } else {
                                current = new ArrayList<>(newItems);
                            }

                            liveData.setValue(current);

                            currentPageMap.put(role, body.getPage() + 1);

                            boolean isLast = newItems.size() < PAGE_SIZE || current.size() >= body.getTotalElements();
                            lastPageMap.put(role, isLast);
                        } else {
                            error.setValue("Failed to load " + role.name());
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminUserResponse>> call, Throwable t) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    public void updateUserStatusLocally(int userId, UserStatus newStatus) {
        updateStatusInList(customersLiveData, userId, newStatus);
        updateStatusInList(sellersLiveData, userId, newStatus);
    }

    private void updateStatusInList(MutableLiveData<List<AdminUserResponse>> liveData, int userId, UserStatus newStatus) {
        List<AdminUserResponse> list = liveData.getValue();
        if (list != null) {
            boolean updated = false;
            List<AdminUserResponse> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                AdminUserResponse oldObj = list.get(i);
                if (oldObj.getId() != null && oldObj.getId() == userId) {
                    AdminUserResponse newObj = new AdminUserResponse(
                            oldObj.getId(),
                            oldObj.getFullName(),
                            oldObj.getEmail(),
                            oldObj.getPhone(),
                            oldObj.getRole(),
                            newStatus,
                            oldObj.getAvatar()
                    );
                    newList.add(newObj);
                    updated = true;
                } else {
                    newList.add(oldObj);
                }
            }
            if (updated) {
                liveData.setValue(newList);
            }
        }
    }
}
