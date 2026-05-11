package com.example.ecommerceapp.ui.activity.home.user.shop;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.CategoryResponse;
import com.example.ecommerceapp.data.model.response.UserCategoryResponse;
import com.example.ecommerceapp.ui.activity.home.user.search.UserSearchActivity;
import com.example.ecommerceapp.ui.adapter.user.UserCategoryAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopCategoriesFragment extends Fragment {

    private int shopId;
    private RecyclerView rvCategories;
    private TextView tvEmpty;
    private UserCategoryAdapter adapter;
    private TokenManager tokenManager;

    public static ShopCategoriesFragment newInstance(int shopId) {
        ShopCategoriesFragment fragment = new ShopCategoriesFragment();
        Bundle args = new Bundle();
        args.putInt("SHOP_ID", shopId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            shopId = getArguments().getInt("SHOP_ID", -1);
        }
        tokenManager = TokenManager.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_categories, container, false);

        rvCategories = view.findViewById(R.id.rvShopCategories);
        tvEmpty = view.findViewById(R.id.tvEmptyCategories);

        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 4)); // Using grid like the home page
        
        adapter = new UserCategoryAdapter(categoryId -> {
            Intent intent = new Intent(requireContext(), UserSearchActivity.class);
            // SearchActivity might not support categoryId filtering directly out of the box with the search API,
            // but we can pass it if the backend search API supports it or just use keyword for now.
            // As decided, we will open a new screen. We can use UserSearchActivity.
            intent.putExtra("categoryId", categoryId);
            intent.putExtra("shopId", shopId);
            startActivity(intent);
        });
        
        rvCategories.setAdapter(adapter);

        loadCategories();

        return view;
    }

    private void loadCategories() {
        if (shopId == -1) return;

        ApiClient.getPublicCategoryService(tokenManager).getCategoriesByShopId(shopId).enqueue(new Callback<List<CategoryResponse>>() {
            @Override
            public void onResponse(Call<List<CategoryResponse>> call, Response<List<CategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<CategoryResponse> categories = response.body();
                    if (categories.isEmpty()) {
                        rvCategories.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvCategories.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                        
                        // Map CategoryResponse to UserCategoryResponse for the adapter
                        List<UserCategoryResponse> userCategories = new ArrayList<>();
                        for (CategoryResponse cat : categories) {
                            UserCategoryResponse uCat = new UserCategoryResponse();
                            uCat.setId(cat.getId());
                            uCat.setName(cat.getName());
                            // Backend category doesn't have image for now, leave null
                            userCategories.add(uCat);
                        }
                        adapter.updateData(userCategories);
                    }
                } else {
                    Toast.makeText(getContext(), "Không tải được danh mục", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CategoryResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
