package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.model.response.admin.management.user.AdminUserResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.data.repository.admin.AdminUserRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminUserViewModel extends ViewModel {

    private final AdminUserRepository repository;

    private String currentKeyword = "";
    private UserStatus currentStatus = null;
    private Role currentRole = null;
    private boolean isDataLoaded = false;

    public AdminUserViewModel(AdminUserRepository repository) {
        this.repository = repository;
    }

    public String getCurrentKeyword() { return currentKeyword; }
    public void setCurrentKeyword(String keyword) { this.currentKeyword = keyword; }

    public UserStatus getCurrentStatus() { return currentStatus; }
    public void setCurrentStatus(UserStatus status) { this.currentStatus = status; }

    public Role getCurrentRole() { return currentRole; }
    public void setCurrentRole(Role role) { this.currentRole = role; }

    public boolean isDataLoaded() { return isDataLoaded; }

    private final MutableLiveData<List<AdminUserResponse>> usersLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LiveData<List<AdminUserResponse>> getUsersLiveData() {
        return usersLiveData;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    private int currentPage = 0;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 10;
    private int pendingRequests = 0;

    public void fetchUsers(boolean isLoadMore, boolean isSilent) {
        if (!isLoadMore) {
            currentPage = 0;
            isLastPage = false;
            isDataLoaded = true;
        }

        if (isLastPage) return;

        if (!isSilent) loading.setValue(true);
        pendingRequests++;

        repository.getUsers(currentPage, PAGE_SIZE, currentRole, currentStatus, currentKeyword)
                .enqueue(new Callback<PageResponse<AdminUserResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminUserResponse>> call, Response<PageResponse<AdminUserResponse>> response) {
                        pendingRequests--;
                        if (!isSilent && pendingRequests == 0) loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<AdminUserResponse> body = response.body();
                            
                            List<AdminUserResponse> current = usersLiveData.getValue();
                            if (current == null) current = new ArrayList<>();

                            List<AdminUserResponse> newItems = body.getItems();
                            if (newItems == null) newItems = new ArrayList<>();

                            if (isLoadMore) {
                                current.addAll(newItems);
                            } else {
                                current = new ArrayList<>(newItems);
                            }

                            usersLiveData.setValue(current);

                            currentPage = body.getPage() + 1;
                            isLastPage = newItems.size() < PAGE_SIZE || current.size() >= body.getTotalElements();
                        } else {
                            error.setValue("Failed to load users");
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
        List<AdminUserResponse> list = usersLiveData.getValue();
        if (list != null) {
            boolean updated = false;
            List<AdminUserResponse> newList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                AdminUserResponse oldObj = list.get(i);
                if (oldObj.getId() != null && oldObj.getId() == userId) {
                    // Nếu đang lọc theo 1 trạng thái cụ thể mà trạng thái mới của user không khớp với bộ lọc đó, ta xóa user này khỏi danh sách hiển thị
                    if (currentStatus != null && currentStatus != newStatus) {
                        updated = true;
                        continue; // Bỏ qua không add vào newList
                    }

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
                usersLiveData.setValue(newList);
            }
        }
    }
}
