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
import com.example.ecommerceapp.ui.adapter.seller.ProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.ProductViewModel;

public class ProductFragment extends Fragment {

    private ProductViewModel viewModel;
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    public static ProductFragment newInstance() {
        return new ProductFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        recyclerView = view.findViewById(R.id.listProduct);

        adapter = new ProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(ProductViewModel.class);

        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.setData(products);
        });

        return view;
    }

}