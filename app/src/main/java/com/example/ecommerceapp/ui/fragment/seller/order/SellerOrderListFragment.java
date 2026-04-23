package com.example.ecommerceapp.ui.fragment.seller.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.SellerOrderRepository;
import com.example.ecommerceapp.ui.activity.home.seller.order.SellerOrderDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerOrderViewModelFactory;

public class SellerOrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";

    private String status;

    private SellerOrderViewModel viewModel;
    private SellerOrderAdapter adapter;

    private LinearLayoutManager layoutManager;

    private boolean isLoadingMore = false;

    private RecyclerView rvOrders;

    public static SellerOrderListFragment newInstance(String status) {
        SellerOrderListFragment fragment = new SellerOrderListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            status = getArguments().getString(ARG_STATUS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_order_list, container, false);
        initViews(view);
        setupRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
        setupObservers();
    }

    private void initViews(View view) {
        rvOrders = view.findViewById(R.id.rvOrders);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        rvOrders.setLayoutManager(layoutManager);

        adapter = new SellerOrderAdapter();
        rvOrders.setAdapter(adapter);

        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireContext(), SellerOrderDetailActivity.class);
            intent.putExtra("orderId", item.getOrderId());
            startActivity(intent);
        });

        rvOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (totalItemCount == 0) return;

                if (!isLoadingMore && lastVisibleItem >= totalItemCount - 2) {
                    isLoadingMore = true;
                    viewModel.loadOrders(status, true);
                }
            }
        });
    }

    private void initViewModel() {
        SellerOrderRepository repository = new SellerOrderRepository(TokenManager.getInstance(requireContext()));
        viewModel = new ViewModelProvider(this, new SellerOrderViewModelFactory(repository)).get(SellerOrderViewModel.class);
    }

    private void setupObservers() {
        viewModel.getOrders(status).observe(getViewLifecycleOwner(), data -> {
            isLoadingMore = false;
            if (data != null) {
                adapter.setData(data);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        viewModel.loadOrders(status, false);
    }
}