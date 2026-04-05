package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.adapter.user.UserProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.UserHomeViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.UserHomeViewModelFactory;

public class HomeFragment extends Fragment {

    private UserHomeViewModel viewModel;
    private UserProductAdapter productAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Giữ nguyên Nút Giỏ hàng của bạn
        ImageView ivCartHome = view.findViewById(R.id.ivCartHome);
        ivCartHome.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        // 2. Setup RecyclerView và Adapter (Ban đầu chưa có dữ liệu)
        RecyclerView rvProducts = view.findViewById(R.id.rvProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2)); // Hiển thị 2 cột
        productAdapter = new UserProductAdapter(getContext());
        rvProducts.setAdapter(productAdapter);

        // 3. Khởi tạo MVVM (Repository -> Factory -> ViewModel)
        UserProductService apiService = ApiClient.getUserProductService();
        UserProductRepository repository = new UserProductRepository(apiService);
        UserHomeViewModelFactory factory = new UserHomeViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory).get(UserHomeViewModel.class);

        // 4. LẮNG NGHE LIVEDATA (Quan trọng nhất)
        // Khi ViewModel lấy được dữ liệu từ API, nó sẽ nhét vào "productList" và báo cho dòng này biết
        viewModel.getProducts().observe(getViewLifecycleOwner(), userProductResponses -> {
            if (userProductResponses != null && !userProductResponses.isEmpty()) {
                // Đẩy dữ liệu mới vào Adapter để nó tự vẽ lên màn hình
                productAdapter.updateData(userProductResponses);
            }
        });

        // 5. Ra lệnh cho ViewModel gọi API
        viewModel.fetchProducts();

        return view;
    }
}