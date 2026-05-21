package com.example.ecommerceapp.ui.viewmodel.seller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.request.seller.shop.SellerShopRequest;
import com.example.ecommerceapp.data.model.response.seller.shop.SellerShopResponse;
import com.example.ecommerceapp.data.model.response.PlatformFeeResponse;
import com.example.ecommerceapp.data.repository.seller.SellerShopRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SellerShopViewModel extends ViewModel {

    private final MutableLiveData<SellerShopResponse> shop = new MutableLiveData<>();
    private final MutableLiveData<PlatformFeeResponse> platformFee = new MutableLiveData<>();
    private final SellerShopRepository repository;

    public SellerShopViewModel(SellerShopRepository repository) {
        this.repository = repository;
    }

    public LiveData<SellerShopResponse> getShop() {
        return shop;
    }

    public LiveData<PlatformFeeResponse> getPlatformFee() {
        return platformFee;
    }

    // GET CURRENT PLATFORM FEE
    public void fetchPlatformFee() {
        repository.getCurrentFee().enqueue(new Callback<PlatformFeeResponse>() {
            @Override
            public void onResponse(Call<PlatformFeeResponse> call, Response<PlatformFeeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    platformFee.setValue(response.body());
                } else {
                    platformFee.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<PlatformFeeResponse> call, Throwable t) {
                t.printStackTrace();
                platformFee.setValue(null);
            }
        });
    }

    // GET MY SHOP
    public void fetchMyShop() {
        repository.getMyShop().enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // CREATE SHOP
    public void createShop(SellerShopRequest request) {
        repository.createShop(request).enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // UPDATE SHOP
    public void updateShop(SellerShopRequest request) {
        repository.updateShop(request).enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    // UPDATE AVATAR
    public void updateAvatar(String avatar) {
        repository.updateAvatar(avatar).enqueue(new Callback<SellerShopResponse>() {
            @Override
            public void onResponse(Call<SellerShopResponse> call, Response<SellerShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    shop.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<SellerShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void cancelRegistration() {
        repository.cancelRegistration().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    fetchMyShop(); // Tải lại để thấy trạng thái CANCELED (hoặc xóa)
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void toggleAiReply(Integer shopId, Boolean enabled) {
        repository.toggleAiReply(shopId, enabled).enqueue(new Callback<com.example.ecommerceapp.data.model.response.ShopResponse>() {
            @Override
            public void onResponse(Call<com.example.ecommerceapp.data.model.response.ShopResponse> call, Response<com.example.ecommerceapp.data.model.response.ShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật lại shop state
                    SellerShopResponse currentShop = shop.getValue();
                    if (currentShop != null) {
                        currentShop.setIsAiReplyEnabled(response.body().getIsAiReplyEnabled());
                        shop.setValue(currentShop);
                    }
                }
            }

            @Override
            public void onFailure(Call<com.example.ecommerceapp.data.model.response.ShopResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}