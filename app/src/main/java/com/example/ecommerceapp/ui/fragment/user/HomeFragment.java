package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.example.ecommerceapp.ui.adapter.user.UserCategoryAdapter;
import com.example.ecommerceapp.ui.adapter.user.UserProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.UserHomeViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.UserHomeViewModelFactory;
import com.example.ecommerceapp.utils.CartManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.ecommerceapp.api.service.ChatApiService;

public class HomeFragment extends Fragment {

    private UserHomeViewModel viewModel;
    private UserProductAdapter productAdapter;
    private UserCategoryAdapter categoryAdapter;
    private com.facebook.shimmer.ShimmerFrameLayout shimmerContainer;
    private RecyclerView rvProducts;

    private TextView tvCartBadge;
    private View vChatBadgeHome;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SwipeRefreshLayout swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_orange_dark);
        tvCartBadge = view.findViewById(R.id.tvCartBadgeHome);
        vChatBadgeHome = view.findViewById(R.id.vChatBadgeHome);

        // 1. Nút Giỏ hàng
        ImageView ivCartHome = view.findViewById(R.id.ivCartHome);
        ivCartHome.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        // Nút Chat
        ImageView ivChat = view.findViewById(R.id.ivChat);
        ivChat.setOnClickListener(v -> {
            TokenManager tm = TokenManager.getInstance(getContext());
            if (tm.getUserId() == -1) {
                showLoginRequireDialog();
            } else {
                startActivity(new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.chat.UserConversationListActivity.class));
            }
        });

        // --- CHỨC NĂNG TÌM KIẾM ---
        TextView tvSearch = view.findViewById(R.id.tvSearch);
        tvSearch.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.search.UserSearchActivity.class);
            startActivity(intent);
        });

        // Nút Mở Trợ lý ảo AI
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAssistant = view.findViewById(R.id.fabAssistant);
        fabAssistant.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), com.example.ecommerceapp.ui.activity.AssistantChatActivity.class));
        });

        // 2. Setup RecyclerView Sản phẩm
        rvProducts = view.findViewById(R.id.rvProducts);
        shimmerContainer = view.findViewById(R.id.shimmer_view_container);

        // --- GỢI Ý THÔNG MINH ---
        View cardSuggestionEntry = view.findViewById(R.id.cardSuggestionEntry);
        if (cardSuggestionEntry != null) {
            cardSuggestionEntry.setOnClickListener(v -> {
                TokenManager tm = TokenManager.getInstance(getContext());
                if (tm.getUserId() == -1) {
                    showLoginRequireDialog();
                } else {
                    Intent intent = new Intent(getActivity(), com.example.ecommerceapp.ui.activity.home.user.suggestion.SuggestionCenterActivity.class);
                    startActivity(intent);
                }
            });
        }
        
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        productAdapter = new UserProductAdapter(getContext());
        rvProducts.setAdapter(productAdapter);

        // Lắng nghe sự kiện cuộn để tải thêm (Pagination)
        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Cuộn xuống
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            viewModel.fetchProducts(false); // Gọi thêm trang tiếp theo
                        }
                    }
                }
            }
        });

        // 3. Setup RecyclerView Danh mục
        RecyclerView rvCategories = view.findViewById(R.id.rvCategories);
        if (rvCategories != null) {
            rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            categoryAdapter = new UserCategoryAdapter(categoryId -> {
                viewModel.fetchProductsByCategory(categoryId);
            });
            rvCategories.setAdapter(categoryAdapter);
        }

        // 4. Khởi tạo MVVM
        UserProductService productService = ApiClient.getUserProductService();
        UserProductRepository productRepository = new UserProductRepository(productService);
        TokenManager tokenManager = TokenManager.getInstance(getContext());
        UserCategoryApiService categoryService = ApiClient.getUserCategoryApiService(tokenManager);
        UserCategoryRepository categoryRepository = new UserCategoryRepository(categoryService);
        UserHomeViewModelFactory factory = new UserHomeViewModelFactory(requireActivity().getApplication(), productRepository, categoryRepository);
        viewModel = new ViewModelProvider(this, factory).get(UserHomeViewModel.class);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            shimmerContainer.setVisibility(View.VISIBLE);
            shimmerContainer.startShimmer();
            rvProducts.setVisibility(View.GONE);
            viewModel.fetchCategories();
            viewModel.fetchProducts(true);
        });

        // 5. LẮNG NGHE DỮ LIỆU
        viewModel.getCategoryList().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null && !categories.isEmpty() && categoryAdapter != null) {
                categoryAdapter.updateData(categories);
            }
        });

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            if (products != null) {
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                rvProducts.setVisibility(View.VISIBLE);

                productAdapter.updateData(products);
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                shimmerContainer.stopShimmer();
                shimmerContainer.setVisibility(View.GONE);
                rvProducts.setVisibility(View.VISIBLE);
                
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        // 6. KÍCH HOẠT GỌI API
        viewModel.fetchCategories();
        viewModel.fetchProducts(true);

        return view;
    }

    private void showLoginRequireDialog() {
        if (getContext() == null) return;
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để sử dụng tính năng này. Đi đến trang đăng nhập ngay?")
                .setCancelable(true)
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                })
                .setNegativeButton("Để sau", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (tvCartBadge != null) {
            int totalItems = CartManager.getInstance().getTotalQuantity();
            if (totalItems > 0) {
                tvCartBadge.setVisibility(View.VISIBLE);
                tvCartBadge.setText(totalItems > 99 ? "99+" : String.valueOf(totalItems));
            } else {
                tvCartBadge.setVisibility(View.GONE);
            }
        }
        
        TokenManager tm = TokenManager.getInstance(getContext());
        if (tm.getUserId() != -1 && vChatBadgeHome != null) {
            ChatApiService chatApiService = ApiClient.getChatApiService(tm);
            chatApiService.getUnreadCountForCustomer((int) tm.getUserId()).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        int unreadCount = response.body();
                        if (unreadCount > 0) {
                            vChatBadgeHome.setVisibility(View.VISIBLE);
                        } else {
                            vChatBadgeHome.setVisibility(View.GONE);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    Log.e("HomeFragment", "Failed to fetch unread messages count", t);
                }
            });
        }

    }

}