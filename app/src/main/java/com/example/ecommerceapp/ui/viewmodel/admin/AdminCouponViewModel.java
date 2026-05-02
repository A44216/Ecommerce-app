package com.example.ecommerceapp.ui.viewmodel.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.model.request.admin.management.AdminCouponRequest;
import com.example.ecommerceapp.data.model.response.admin.management.coupon.AdminCouponResponse;
import com.example.ecommerceapp.data.model.response.PageResponse;
import com.example.ecommerceapp.data.repository.admin.AdminCouponRepository;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCouponViewModel extends ViewModel {

    private final AdminCouponRepository repository;

    public AdminCouponViewModel(AdminCouponRepository repository) {
        this.repository = repository;
    }

    private final MutableLiveData<List<String>> autocompleteSuggestions = new MutableLiveData<>();

    private final Map<String, MutableLiveData<List<AdminCouponResponse>>> cache = new java.util.HashMap<>();
    private final Map<String, Integer> currentPageMap = new java.util.HashMap<>();
    private final Map<String, Boolean> lastPageMap = new java.util.HashMap<>();
    private static final int PAGE_SIZE = 10;

    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> actionSuccess = new MutableLiveData<>();

    private String getKey(CouponStatus status, Boolean isDeleted) {
        return status + "_" + isDeleted;
    }

    public LiveData<java.util.List<AdminCouponResponse>> getCouponsLiveData(CouponStatus status, Boolean isDeleted) {
        String key = getKey(status, isDeleted);
        if (!cache.containsKey(key)) {
            cache.put(key, new MutableLiveData<>());
        }
        return cache.get(key);
    }

    public LiveData<Boolean> getActionSuccess() {
        return actionSuccess;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<String>> getAutocompleteSuggestions() {
        return autocompleteSuggestions;
    }

    // AUTOCOMPLETE
    public void autocomplete(String keyword) {
        if (repository == null) return;
        
        if (keyword == null || keyword.trim().isEmpty()) {
            autocompleteSuggestions.setValue(null);
            return;
        }

        repository.autocompleteCoupons(keyword).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    autocompleteSuggestions.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Ignore errors for autocomplete
            }
        });
    }

    public void loadCoupons(CouponStatus status, String keyword, Boolean isDeleted, boolean isLoadMore, boolean isSilent) {
        String key = getKey(status, isDeleted);
        int page = currentPageMap.getOrDefault(key, 0);

        if (!isLoadMore) {
            page = 0;
            currentPageMap.put(key, 0);
            lastPageMap.put(key, false);
        }

        if (Boolean.TRUE.equals(lastPageMap.get(key))) return;

        if (!isSilent) loading.setValue(true);

        repository.getCoupons(page, PAGE_SIZE, status, keyword, isDeleted)
                .enqueue(new Callback<PageResponse<AdminCouponResponse>>() {
                    @Override
                    public void onResponse(Call<PageResponse<AdminCouponResponse>> call,
                                           Response<PageResponse<AdminCouponResponse>> response) {

                        if (!isSilent) loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            PageResponse<AdminCouponResponse> body = response.body();

                            MutableLiveData<java.util.List<AdminCouponResponse>> liveData = cache.get(key);
                            if (liveData == null) {
                                liveData = new MutableLiveData<>();
                                cache.put(key, liveData);
                            }

                            java.util.List<AdminCouponResponse> current = liveData.getValue();
                            if (current == null) current = new java.util.ArrayList<>();

                            java.util.List<AdminCouponResponse> newItems = body.getItems();
                            if (newItems == null) newItems = new java.util.ArrayList<>();

                            if (status != null) {
                                java.util.Iterator<AdminCouponResponse> it = newItems.iterator();
                                while (it.hasNext()) {
                                    AdminCouponResponse item = it.next();
                                    if (item.getStatus() != status) {
                                        it.remove();
                                    }
                                }
                            }

                            if (isLoadMore) {
                                current.addAll(newItems);
                            } else {
                                current = new java.util.ArrayList<>(newItems);
                            }

                            liveData.setValue(current);

                            currentPageMap.put(key, body.getPage() + 1);

                            boolean isLast = newItems.size() < PAGE_SIZE || current.size() >= body.getTotalElements();
                            lastPageMap.put(key, isLast);

                        } else {
                            error.setValue("Failed to load coupons");
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<AdminCouponResponse>> call, Throwable t) {
                        if (!isSilent) loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    // CREATE
    private final MutableLiveData<AdminCouponResponse> createResult = new MutableLiveData<>();

    public LiveData<AdminCouponResponse> getCreateResult() {
        return createResult;
    }

    public void createCoupon(AdminCouponRequest request) {
        loading.setValue(true);

        repository.createCoupon(request)
                .enqueue(new Callback<AdminCouponResponse>() {
                    @Override
                    public void onResponse(Call<AdminCouponResponse> call,
                                           Response<AdminCouponResponse> response) {

                        loading.setValue(false);

                        if (response.isSuccessful() && response.body() != null) {
                            createResult.setValue(response.body());
                        } else {
                            error.setValue("Create failed");
                        }
                    }

                    @Override
                    public void onFailure(Call<AdminCouponResponse> call, Throwable t) {
                        loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    // DELETE
    public void deleteCoupon(Integer id) {
        loading.setValue(true);

        repository.deleteCoupon(id)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        loading.setValue(false);

                        if (!response.isSuccessful()) {
                            error.setValue("Delete failed");
                        } else {
                            actionSuccess.setValue(true);
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        loading.setValue(false);
                        error.setValue(t.getMessage());
                    }
                });
    }

    // ENABLE
    public void enableCoupon(Integer id) {
        repository.enableCoupon(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actionSuccess.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    // DISABLE
    public void disableCoupon(Integer id) {
        repository.disableCoupon(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actionSuccess.setValue(true);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }

    // RESTORE
    public void restoreCoupon(Integer id) {
        repository.restoreCoupon(id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    actionSuccess.setValue(true);
                } else {
                    error.setValue("Restore failed");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue(t.getMessage());
            }
        });
    }
}