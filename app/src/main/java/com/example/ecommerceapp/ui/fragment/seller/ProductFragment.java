package com.example.ecommerceapp.ui.fragment.seller;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.ApiService;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.repository.ProductRepository;
import com.example.ecommerceapp.ui.activity.home.seller.product.AddAndEditProductActivity;
import com.example.ecommerceapp.ui.activity.home.seller.product.ProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.ProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.ProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.ProductViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProductFragment extends Fragment {

    private ProductAdapter adapter;
    private ProductViewModel viewModel;

    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        initRecyclerView(view);
        setupViewModel();
        setupListeners(view);

        return view;
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.listProduct);

        adapter = new ProductAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {

        ApiService apiService = ApiClient.getPublicApiService();
        ProductRepository repository = new ProductRepository(apiService);
        ProductViewModelFactory factory = new ProductViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory)
                .get(ProductViewModel.class);

        viewModel.fetchProducts();

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setData(products);
        });
    }

    private void setupListeners(View view) {

        adapter.setListener(new ProductAdapter.OnProductActionListener() {
            @Override
            public void onClick(ProductResponse product) {
                Intent intent = new Intent(getContext(), ProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(ProductResponse product) {
                Intent intent = new Intent(getContext(), AddAndEditProductActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }

            @Override
            public void onDelete(ProductResponse product) {
                showConfirmDeleteDialog(product);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.fabAddNew);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddAndEditProductActivity.class);
            startActivity(intent);
        });
    }

    // Dialog xác nhận delete
    private void showConfirmDeleteDialog(ProductResponse product) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc muốn xóa \"" + product.getName() + "\" không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteProduct(product.getId());
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteProduct(Integer productId) {
        // TODO: gọi API delete

        // Sau khi xóa xong reload lại list
        viewModel.fetchProducts();
    }
}