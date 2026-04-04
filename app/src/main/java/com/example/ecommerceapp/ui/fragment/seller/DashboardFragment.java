package com.example.ecommerceapp.ui.fragment.seller;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.dashboard.DashboardResponse;
import com.example.ecommerceapp.data.model.response.dashboard.TopSellingProductResponse;
import com.example.ecommerceapp.data.repository.DashboardRepository;
import com.example.ecommerceapp.ui.adapter.seller.dashboard.TopProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.DashboardViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.DashboardViewModelFactory;
import com.example.ecommerceapp.utils.NumberUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private DashboardViewModel viewModel;
    private TextView tvRevenue, tvOrders, tvSold;
    private Spinner spFilterTopProduct;
    private RecyclerView rvTopProduct;

    private TopProductAdapter topProductAdapter;
    private final List<TopSellingProductResponse> topProductList = new ArrayList<>();
    private DashboardResponse dashboardData;

    TokenManager tokenManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = TokenManager.getInstance(requireContext());

        initViews(view);
        setInits();
        setListeners();

        topProductAdapter = new TopProductAdapter();

        rvTopProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopProduct.setAdapter(topProductAdapter);

        DashboardRepository repository =
                new DashboardRepository(ApiClient.getDashboardService(tokenManager));

        DashboardViewModelFactory factory =
                new DashboardViewModelFactory(repository);

        viewModel = new ViewModelProvider(this, factory)
                .get(DashboardViewModel.class);

        observeData();

        int shopId = (int) tokenManager.getShopId();

        viewModel.loadDashboard(shopId);
    }

    private void initViews(View view) {
        tvRevenue = view.findViewById(R.id.tvRevenue);
        tvOrders = view.findViewById(R.id.tvOrders);
        tvSold = view.findViewById(R.id.tvSold);
        spFilterTopProduct = view.findViewById(R.id.spFilterTopProduct);
        rvTopProduct = view.findViewById(R.id.rvTopProducts);
    }

    private void setInits() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.filter_top_product,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterTopProduct.setAdapter(adapter);
    }

    private void setListeners() {
        spFilterTopProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0: // Sold
                        sortBySold();
                        break;

                    case 1: // Revenue
                        sortByRevenue();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void sortByRevenue() {
        if (dashboardData == null) return;

        topProductList.clear();
        topProductList.addAll(dashboardData.getTopProductsByRevenue());

        topProductAdapter.setData(topProductList);
    }

    private void sortBySold() {
        if (dashboardData == null) return;

        topProductList.clear();
        topProductList.addAll(dashboardData.getTopProductsBySold());

        topProductAdapter.setData(topProductList);
    }

    @SuppressLint("SetTextI18n")
    private void observeData() {
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            dashboardData = data;

            tvRevenue.setText(NumberUtils.formatCompact(data.getRevenue()) + " ₫");
            tvOrders.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getOrders())));
            tvSold.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getSold())));

            // Mặc định load theo Revenue
            topProductList.clear();
            topProductList.addAll(data.getTopProductsByRevenue());
            topProductAdapter.setData(topProductList);
        });
    }
}