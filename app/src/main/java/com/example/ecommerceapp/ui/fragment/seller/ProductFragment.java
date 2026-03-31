package com.example.ecommerceapp.ui.fragment.seller;

import androidx.lifecycle.ViewModelProvider;

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
import com.example.ecommerceapp.data.repository.ProductRepository;
import com.example.ecommerceapp.ui.adapter.seller.ProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.ProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.ProductViewModelFactory;

public class ProductFragment extends Fragment {

    private ProductAdapter adapter;
    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.listProduct);

        adapter = new ProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        ApiService apiService = ApiClient.getPublicApiService();

        ProductRepository repository = new ProductRepository(apiService);

        ProductViewModelFactory factory = new ProductViewModelFactory(repository);

        ProductViewModel viewModel = new ViewModelProvider(this, factory)
                .get(ProductViewModel.class);

        viewModel.fetchProducts();

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setData(products);
        });

        return view;
    }

}