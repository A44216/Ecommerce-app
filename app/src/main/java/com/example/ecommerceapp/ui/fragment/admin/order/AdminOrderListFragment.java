package com.example.ecommerceapp.ui.fragment.admin.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.order.AdminOrderResponse;
import com.example.ecommerceapp.ui.activity.home.admin.management.order.AdminOrderDetailActivity;
import com.example.ecommerceapp.ui.adapter.admin.order.AdminOrderAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminOrderViewModel;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderListFragment extends Fragment {

    private static final String ARG_STATUS = "status";
    private String status;

    private AdminOrderViewModel viewModel;
    private AdminOrderAdapter adapter;
    private LinearLayoutManager layoutManager;

    private SwipeRefreshLayout swipeRefreshOrder;
    private RecyclerView rvOrder;
    private ProgressBar progressBarOrder;
    private TextView tvEmptyOrder;

    private boolean isLoadingMore = false;

    private LiveData<List<AdminOrderResponse>> currentOrdersLiveData;
    private final Observer<List<AdminOrderResponse>> ordersObserver = data -> {
        isLoadingMore = false;
        progressBarOrder.setVisibility(View.GONE);
        swipeRefreshOrder.setRefreshing(false);
        if (data != null) {
            adapter.submitList(new ArrayList<>(data));
            if (data.isEmpty()) {
                tvEmptyOrder.setVisibility(View.VISIBLE);
            } else {
                tvEmptyOrder.setVisibility(View.GONE);
            }
        }
    };

    public static AdminOrderListFragment newInstance(String status) {
        AdminOrderListFragment fragment = new AdminOrderListFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        
        // Use the activity scope view model so filters apply to all tabs
        viewModel = new ViewModelProvider(requireActivity()).get(AdminOrderViewModel.class);

        setupRecyclerView();
        setupObservers();

        swipeRefreshOrder.setOnRefreshListener(() -> {
            loadOrdersWithFilters(false);
        });
    }

    private void initViews(View view) {
        swipeRefreshOrder = view.findViewById(R.id.swipeRefreshOrder);
        rvOrder = view.findViewById(R.id.rvOrder);
        progressBarOrder = view.findViewById(R.id.progressBarOrder);
        tvEmptyOrder = view.findViewById(R.id.tvEmptyOrder);
    }

    private void setupRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        rvOrder.setLayoutManager(layoutManager);

        adapter = new AdminOrderAdapter(requireContext(), order -> {
            Intent intent = new Intent(requireContext(), AdminOrderDetailActivity.class);
            intent.putExtra("orderId", order.getId());
            startActivity(intent);
        });
        rvOrder.setAdapter(adapter);

        rvOrder.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (totalItemCount == 0) return;

                if (!isLoadingMore && lastVisibleItem >= totalItemCount - 2) {
                    isLoadingMore = true;
                    loadOrdersWithFilters(true);
                }
            }
        });
    }

    private void setupObservers() {
        viewModel.getFilterState().observe(getViewLifecycleOwner(), filter -> {
            if (filter != null) {
                String pm = filter.paymentMethod;
                String ps = filter.paymentStatus;
                String kw = filter.keyword;
                Integer shopId = filter.shopId;
                String sortBy = filter.sortBy;
                String direction = filter.direction;

                if (currentOrdersLiveData != null) {
                    currentOrdersLiveData.removeObserver(ordersObserver);
                }

                currentOrdersLiveData = viewModel.getOrders(status, pm, ps, kw, shopId, sortBy, direction);
                currentOrdersLiveData.observe(getViewLifecycleOwner(), ordersObserver);

                if (currentOrdersLiveData.getValue() == null || currentOrdersLiveData.getValue().isEmpty()) {
                    progressBarOrder.setVisibility(View.VISIBLE);
                    loadOrdersWithFilters(false, pm, ps, kw, shopId, sortBy, direction);
                }
            }
        });
    }

    private void loadOrdersWithFilters(boolean isLoadMore) {
        AdminOrderViewModel.FilterState filter = viewModel.getFilterState().getValue();
        if (filter == null) return;
        loadOrdersWithFilters(isLoadMore, filter.paymentMethod, filter.paymentStatus, filter.keyword, filter.shopId, filter.sortBy, filter.direction);
    }

    private void loadOrdersWithFilters(boolean isLoadMore, String pm, String ps, String kw, Integer shopId, String sortBy, String direction) {
        viewModel.loadOrders(status, pm, ps, kw, shopId, sortBy, direction, isLoadMore);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Since we are observing getFilterState, any filter update automatically reflects here if needed.
    }
}

