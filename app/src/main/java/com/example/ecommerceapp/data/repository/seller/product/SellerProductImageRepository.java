package com.example.ecommerceapp.data.repository.seller.product;

import com.example.ecommerceapp.api.service.ProductImageService;
import com.example.ecommerceapp.data.model.request.ProductImageRequest;
import com.example.ecommerceapp.data.model.response.ProductImageResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;

public class SellerProductImageRepository {

    private final ProductImageService service;

    public SellerProductImageRepository(ProductImageService service) {
        this.service = service;
    }

    // upload ảnh → trả URL
    public Call<String> uploadImage(MultipartBody.Part file) {
        return service.uploadImage(file);
    }

    // lưu ảnh vào DB
    public Call<ProductImageResponse> addProductImage(ProductImageRequest request) {
        return service.addProductImage(request);
    }

    public Call<Void> deleteProductImage(int id) {
        return service.deleteProductImage(id);
    }

}