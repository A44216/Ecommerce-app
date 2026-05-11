package com.example.ecommerceapp.ui.activity.home.user.shop;

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
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import com.example.ecommerceapp.ui.adapter.user.UserProductAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopProductsFragment extends Fragment {

    private int shopId;
    private RecyclerView rvProducts;
    private TextView tvEmpty;
    private UserProductAdapter adapter;

    public static ShopProductsFragment newInstance(int shopId) {
        ShopProductsFragment fragment = new ShopProductsFragment();
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shop_products, container, false);

        rvProducts = view.findViewById(R.id.rvShopProducts);
        tvEmpty = view.findViewById(R.id.tvEmptyProducts);

        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new UserProductAdapter(requireContext()); // Initialize with context
        rvProducts.setAdapter(adapter);

        loadProducts();

        return view;
    }

    private void loadProducts() {
        if (shopId == -1) return;

        ApiClient.getUserProductService().searchProducts("", shopId).enqueue(new Callback<List<UserProductResponse>>() {
            @Override
            public void onResponse(Call<List<UserProductResponse>> call, Response<List<UserProductResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<UserProductResponse> products = response.body();
                    if (products.isEmpty()) {
                        rvProducts.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvProducts.setVisibility(View.VISIBLE);
                        tvEmpty.setVisibility(View.GONE);
                        adapter.updateData(products);
                    }
                } else {
                    Toast.makeText(getContext(), "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<UserProductResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
