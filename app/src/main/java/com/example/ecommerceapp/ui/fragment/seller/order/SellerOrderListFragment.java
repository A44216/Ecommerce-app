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

        RecyclerView rv = view.findViewById(R.id.rvOrders);

        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        adapter = new SellerOrderAdapter();
        rv.setAdapter(adapter);

        // click item → detail
        adapter.setOnItemClickListener(item -> {
            Intent intent = new Intent(requireContext(), SellerOrderDetailActivity.class);
            intent.putExtra("orderId", item.getOrderId());
            startActivity(intent);
        });

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (totalItemCount == 0) return;

                int shopId = (int) TokenManager.getInstance(requireContext()).getShopId();

                // gần cuối list → load more
                if (!isLoadingMore && lastVisibleItem >= totalItemCount - 2) {

                    isLoadingMore = true;

                    viewModel.loadOrders(status, shopId, true);
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        TokenManager tokenManager = TokenManager.getInstance(requireContext());

        int shopId = (int) tokenManager.getShopId();

        viewModel = new ViewModelProvider(
                this,
                new SellerOrderViewModelFactory(tokenManager)
        ).get(SellerOrderViewModel.class);

        // OBSERVE DATA (LIST)
        viewModel.getOrders(status, shopId).observe(getViewLifecycleOwner(), data -> {

            isLoadingMore = false; // reset load state

            if (data != null) {
                adapter.setData(data);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        int shopId = (int) TokenManager.getInstance(requireContext()).getShopId();

        // 🔥 reload page 0
        viewModel.loadOrders(status, shopId, false);
    }
}