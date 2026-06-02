package com.example.ecommerceapp.ui.fragment.seller.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.order.SellerOrderResponse;
import com.example.ecommerceapp.data.repository.seller.SellerOrderRepository;
import com.example.ecommerceapp.ui.activity.home.seller.order.SellerOrderDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerOrderViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderListFragment extends Fragment {

    public interface FilterCallback {
        void onAutocompleteResult(List<String> suggestions);
    }

    private static final String ARG_STATUS = "status";

    private String status;
    private SellerOrderViewModel viewModel;
    private SellerOrderAdapter adapter;
    private LinearLayoutManager layoutManager;
    private boolean isLoadingMore = false;

    private RecyclerView rvOrders;
    private SwipeRefreshLayout swipeRefreshOrders;
    private android.widget.ProgressBar progressBar;

    private LiveData<List<SellerOrderResponse>> currentOrdersLiveData;
    private final Observer<List<SellerOrderResponse>> ordersObserver = data -> {
        boolean wasLoadingMore = isLoadingMore;
        isLoadingMore = false;
        if (swipeRefreshOrders != null) {
            swipeRefreshOrders.setRefreshing(false);
        }
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (data != null) {
            RecyclerView.ItemAnimator animator = rvOrders != null ? rvOrders.getItemAnimator() : null;
            if (!wasLoadingMore && rvOrders != null) {
                rvOrders.setItemAnimator(null);
            }
            
            adapter.submitList(new ArrayList<>(data), () -> {
                if (!wasLoadingMore && rvOrders != null) {
                    rvOrders.scrollToPosition(0);
                    rvOrders.post(() -> rvOrders.setItemAnimator(animator));
                }
            });
        }
    };

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
        return inflater.inflate(R.layout.fragment_seller_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupRecyclerView();
        initViewModel();
        setupObservers();
    }

    private void initViews(View view) {
        rvOrders = view.findViewById(R.id.rvOrders);
        swipeRefreshOrders = view.findViewById(R.id.swipeRefreshOrders);
        progressBar = view.findViewById(R.id.progressBar);
        swipeRefreshOrders.setOnRefreshListener(() -> loadOrdersWithFilters(false));
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
                    loadOrdersWithFilters(true);
                }
            }
        });
    }

    private void initViewModel() {
        SellerOrderRepository repository = new SellerOrderRepository(TokenManager.getInstance(requireContext()));
        viewModel = new ViewModelProvider(requireActivity(), new SellerOrderViewModelFactory(repository))
                .get(SellerOrderViewModel.class);
    }

    private void setupObservers() {
        // Observe filter changes từ parent (qua ViewModel)
        viewModel.getFilter().observe(getViewLifecycleOwner(), filter -> {
            if (filter != null) {
                String pm = filter.paymentMethod;
                String ps = filter.paymentStatus;
                String kw = filter.keyword;

                if (currentOrdersLiveData != null) {
                    currentOrdersLiveData.removeObserver(ordersObserver);
                }

                currentOrdersLiveData = viewModel.getOrders(status, pm, ps, kw);
                currentOrdersLiveData.observe(getViewLifecycleOwner(), ordersObserver);

                if (currentOrdersLiveData.getValue() == null) {
                    loadOrdersWithFilters(false, pm, ps, kw);
                }
            }
        });

        // Observe autocomplete results
        viewModel.getAutocompleteResult().observe(getViewLifecycleOwner(), suggestions -> {
            if (getActivity() instanceof FilterCallback) {
                ((FilterCallback) getActivity()).onAutocompleteResult(suggestions);
            }
        });
    }

    private void loadOrdersWithFilters(boolean isLoadMore) {
        SellerOrderViewModel.FilterState filter = viewModel.getFilter().getValue();
        String pm = filter != null ? filter.paymentMethod : null;
        String ps = filter != null ? filter.paymentStatus : null;
        String kw = filter != null ? filter.keyword : null;
        loadOrdersWithFilters(isLoadMore, pm, ps, kw);
    }

    private void loadOrdersWithFilters(boolean isLoadMore, String paymentMethod, String paymentStatus, String keyword) {
        if (!isLoadMore) {
            if (progressBar != null && (swipeRefreshOrders == null || !swipeRefreshOrders.isRefreshing())) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
        if (viewModel != null) {
            viewModel.loadOrders(status, paymentMethod, paymentStatus, keyword, isLoadMore);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu ngầm khi quay lại từ màn hình chi tiết
        // Không bật progress bar để tránh hiện tượng nháy hay che màn hình
        if (viewModel != null) {
            SellerOrderViewModel.FilterState filter = viewModel.getFilter().getValue();
            String pm = filter != null ? filter.paymentMethod : null;
            String ps = filter != null ? filter.paymentStatus : null;
            String kw = filter != null ? filter.keyword : null;
            viewModel.loadOrders(status, pm, ps, kw, false);
        }
    }
}
