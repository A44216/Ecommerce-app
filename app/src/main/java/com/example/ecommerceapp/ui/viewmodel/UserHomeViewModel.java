package com.example.ecommerceapp.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerceapp.data.model.response.PageResponse;
import com.example.ecommerceapp.data.model.response.UserCategoryResponse;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.data.repository.UserCategoryRepository;
import com.example.ecommerceapp.data.repository.UserProductRepository;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserHomeViewModel extends ViewModel {
    
    private final MutableLiveData<List<UserProductResponse>> productList = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<UserCategoryResponse>> categoryList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    // Pagination state
    private int currentPage = 0;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    private final int PAGE_SIZE = 10;

    // Filter state
    private Integer currentCategoryId = null;

    private final UserProductRepository productRepository;
    private final UserCategoryRepository categoryRepository;

    public UserHomeViewModel(UserProductRepository productRepository, UserCategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public LiveData<List<UserProductResponse>> getProducts() { return productList; }
    public LiveData<List<UserCategoryResponse>> getCategoryList() { return categoryList; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    /**
     * Tải sản phẩm (Hỗ trợ cả tải mới và tải thêm trang)
     * @param isRefresh true nếu muốn xóa trắng dữ liệu cũ để tải lại từ đầu
     */
    public void fetchProducts(boolean isRefresh) {
        if (isRefresh) {
            currentPage = 0;
            isLastPage = false;
        }
        
        if (isLoading || isLastPage) return;

        isLoading = true;
        
        Call<PageResponse<UserProductResponse>> call;
        if (currentCategoryId != null) {
            call = productRepository.getProductsByCategoryPaginated(currentCategoryId, currentPage, PAGE_SIZE, "id,desc");
        } else {
            call = productRepository.getProductsPaginated(currentPage, PAGE_SIZE, "id,desc");
        }

        call.enqueue(new Callback<PageResponse<UserProductResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<UserProductResponse>> call, Response<PageResponse<UserProductResponse>> response) {
                isLoading = false;
                if (response.isSuccessful() && response.body() != null) {
                    PageResponse<UserProductResponse> pageData = response.body();
                    isLastPage = pageData.isLast();

                    List<UserProductResponse> currentList = productList.getValue();
                    if (isRefresh || currentList == null) {
                        productList.setValue(new ArrayList<>(pageData.getContent()));
                    } else {
                        // Tạo list mới để DiffUtil nhận diện thay đổi
                        List<UserProductResponse> updatedList = new ArrayList<>(currentList);
                        updatedList.addAll(pageData.getContent());
                        productList.setValue(updatedList);
                    }
                    currentPage++;
                } else {
                    errorMessage.setValue("Không thể lấy dữ liệu sản phẩm");
                }
            }

            @Override
            public void onFailure(Call<PageResponse<UserProductResponse>> call, Throwable t) {
                isLoading = false;
                errorMessage.setValue("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    /**
     * Lấy danh sách danh mục (Không phân trang vì số lượng ít)
     */
    public void fetchCategories() {
        categoryRepository.getAllCategories().enqueue(new Callback<List<UserCategoryResponse>>() {
            @Override
            public void onResponse(Call<List<UserCategoryResponse>> call, Response<List<UserCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.setValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<UserCategoryResponse>> call, Throwable t) {
                errorMessage.setValue("Lỗi tải danh mục");
            }
        });
    }

    /**
     * Lọc sản phẩm theo danh mục
     */
    public void fetchProductsByCategory(int categoryId) {
        if (currentCategoryId != null && currentCategoryId == categoryId) return;
        
        currentCategoryId = categoryId;
        fetchProducts(true); // Tải lại từ đầu với filter mới
    }
    
    /**
     * Xóa filter để quay về danh sách tất cả
     */
    public void clearCategoryFilter() {
        currentCategoryId = null;
        fetchProducts(true);
    }
}