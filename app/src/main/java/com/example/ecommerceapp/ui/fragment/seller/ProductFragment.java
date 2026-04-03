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

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.ProductService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ProductResponse;
import com.example.ecommerceapp.data.repository.ProductRepository;
import com.example.ecommerceapp.ui.activity.home.seller.product.AddAndEditProductActivity;
import com.example.ecommerceapp.ui.activity.home.seller.product.ProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.ProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.ProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.ProductViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

public class ProductFragment extends Fragment {

    private ProductAdapter adapter;
    private ProductViewModel viewModel;

    TextInputEditText etSearch;

    TokenManager tokenManager;
    private long shopId;
    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        tokenManager = TokenManager.getInstance(requireContext());

        updateShopId();

        initRecyclerView(view);
        setupViewModel();
        etSearch = view.findViewById(R.id.etSearch);
        setupListeners(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateShopId();

        if (getContext() != null && tokenManager == null) {
            tokenManager = TokenManager.getInstance(getContext());
        }

        if (shopId > 0) {
            viewModel.fetchProductsByShop((int) shopId);
        }
    }

    private void initRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.listProduct);

        adapter = new ProductAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void setupViewModel() {
        ProductService apiService = ApiClient.getProductService(tokenManager);
        ProductRepository repository = new ProductRepository(apiService);

        ProductViewModelFactory factory = new ProductViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory)
                .get(ProductViewModel.class);

        observeProducts();
    }

    private void observeProducts() {

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {

            if (products == null) {
                adapter.setData(new ArrayList<>());
                return;
            }

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

        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            boolean isEnter =
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                            actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                            (event != null && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER);

            if (isEnter) {

                String keyword = Objects.requireNonNull(etSearch.getText()).toString().trim();

                if (keyword.isEmpty()) {
                    viewModel.fetchProductsByShop((int) shopId);
                } else {
                    viewModel.searchProducts(keyword, (int) shopId);
                }

                return true;
            }

            return false;
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
        viewModel.deleteProduct(productId, (int)shopId);
    }

    private void updateShopId() {
        if (tokenManager != null) {
            shopId = tokenManager.getShopId();
        }
    }

}