package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserCategoryApiService;
import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.UserCategoryRepository;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.adapter.user.UserCategoryAdapter;
import com.example.ecommerceapp.ui.adapter.user.UserProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.UserHomeViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.UserHomeViewModelFactory;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

public class HomeFragment extends Fragment {

    private UserHomeViewModel viewModel;
    private UserProductAdapter productAdapter;
    private UserCategoryAdapter categoryAdapter; // Thêm Adapter cho Danh mục

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Nút Giỏ hàng
        ImageView ivCartHome = view.findViewById(R.id.ivCartHome);
        ivCartHome.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        // --- CHỨC NĂNG TÌM KIẾM ---
        android.widget.EditText etSearch = view.findViewById(R.id.tvSearch);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            // Khi người dùng bấm nút Kính Lúp (Search) trên bàn phím ảo
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();

                if (!keyword.isEmpty()) {
                    // Gọi API tìm kiếm
                    viewModel.searchProducts(keyword);
                } else {
                    // Nếu xóa trắng ô tìm kiếm rồi ấn Search -> Tải lại toàn bộ sản phẩm
                    viewModel.fetchProducts();
                }

                // Ẩn bàn phím đi sau khi bấm tìm kiếm cho đỡ vướng
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) requireActivity().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });

        // 2. Setup RecyclerView Sản phẩm
        RecyclerView rvProducts = view.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new UserProductAdapter(getContext());
        rvProducts.setAdapter(productAdapter);

        // 3. Setup RecyclerView Danh mục (Thêm mới)
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);
        if (rvCategories != null) {
            // Danh mục hiển thị cuộn ngang (HORIZONTAL)
            rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

            categoryAdapter = new UserCategoryAdapter(categoryId -> {
                // Sự kiện: Bấm vào icon Danh mục -> Báo ViewModel đi lọc sản phẩm
                viewModel.fetchProductsByCategory(categoryId);
            });
            rvCategories.setAdapter(categoryAdapter);
        }

        // 4. Khởi tạo MVVM (Đã kết hợp cả 2 Kho dữ liệu)
        UserProductService productService = ApiClient.getUserProductService();
        UserProductRepository productRepository = new UserProductRepository(productService);

        //THÊM TOKEN VÀO API DANH MỤC =====
        TokenManager tokenManager = TokenManager.getInstance(getContext());
        UserCategoryApiService categoryService = ApiClient.getUserCategoryApiService(tokenManager);
        // =======================================================

        UserCategoryRepository categoryRepository = new UserCategoryRepository(categoryService);

        // Truyền cả 2 kho vào Factory
        UserHomeViewModelFactory factory = new UserHomeViewModelFactory(productRepository, categoryRepository);
        viewModel = new ViewModelProvider(this, factory).get(UserHomeViewModel.class);

        // 5. LẮNG NGHE DỮ LIỆU TỪ VIEWMODEL

        // Lắng nghe Danh mục
        viewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty() && categoryAdapter != null) {
                categoryAdapter.updateData(categories);
            }
        });

        // Lắng nghe Sản phẩm
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                productAdapter.updateData(products);
            }
        });

        // Lắng nghe lỗi (Để dễ biết API có bị sập không)
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        // 6. KÍCH HOẠT GỌI API KHI VỪA VÀO TRANG
        viewModel.fetchCategories(); // Kéo thanh danh mục
        viewModel.fetchProducts();   // Kéo lưới sản phẩm mặc định

        return view;
    }
}