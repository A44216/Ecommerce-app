package com.example.ecommerceapp.ui.fragment.seller.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.ecommerceapp.data.enums.ChartType;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.SellerRevenueChartResponse;
import com.example.ecommerceapp.data.repository.DashboardRepository;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.dashboard.SellerTopProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerDashboardViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerDashboardViewModelFactory;
import com.example.ecommerceapp.utils.NumberUtils;
import com.github.mikephil.charting.charts.BarChart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SellerDashboardFragment extends Fragment {

    private SellerDashboardViewModel viewModel;
    private TextView tvRevenue, tvOrders, tvSold;
    private Spinner spFilterTopProduct, spFilterTime;
    private RecyclerView rvTopProduct;
    private BarChart chartRevenue;

    private SellerTopProductAdapter topProductAdapter;
    private SellerDashboardResponse dashboardData;

    TokenManager tokenManager;

    DashboardRepository dashboardRepository;

    private int shopId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);

        tokenManager = TokenManager.getInstance(requireContext());
        shopId = (int) tokenManager.getShopId();

        setInits();
        setListeners();

        topProductAdapter = new SellerTopProductAdapter();

        rvTopProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopProduct.setAdapter(topProductAdapter);

        topProductAdapter.setListener(product -> {
            Intent intent = new Intent(getContext(), SellerProductDetailActivity.class);

            intent.putExtra("productId", product.getProductId());

            startActivity(intent);
        });

        dashboardRepository = new DashboardRepository(ApiClient.getDashboardService(tokenManager));

        SellerDashboardViewModelFactory factory =
                new SellerDashboardViewModelFactory(dashboardRepository);

        viewModel = new ViewModelProvider(this, factory)
                .get(SellerDashboardViewModel.class);

        observeData();

        viewModel.loadDashboard(shopId);

        loadChart(shopId, ChartType.DAY);
    }

    private void initViews(View view) {
        tvRevenue = view.findViewById(R.id.tvRevenue);
        tvOrders = view.findViewById(R.id.tvOrders);
        tvSold = view.findViewById(R.id.tvSoldAndRevenue);
        spFilterTopProduct = view.findViewById(R.id.spFilterTopProduct);
        spFilterTime = view.findViewById(R.id.spFilterTime);
        chartRevenue = view.findViewById(R.id.chartRevenue);
        rvTopProduct = view.findViewById(R.id.rvTopProducts);
    }

    private void setInits() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.seller_filter_top_product,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterTopProduct.setAdapter(adapter);

        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.seller_filter_time,
                android.R.layout.simple_spinner_item
        );

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterTime.setAdapter(timeAdapter);
    }

    private void setListeners() {
        spFilterTopProduct.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_SOLD);
                    sortBySold();
                } else {
                    topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_REVENUE);
                    sortByRevenue();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spFilterTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ChartType type;

                switch (position) {
                    case 1: type = ChartType.MONTH; break;
                    case 2: type = ChartType.YEAR; break;
                    default: type = ChartType.DAY;
                }

                loadChart(shopId, type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void loadChart(int shopId, ChartType type) {

        dashboardRepository.getRevenueChart(shopId, type)
                .enqueue(new retrofit2.Callback<List<SellerRevenueChartResponse>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<SellerRevenueChartResponse>> call,
                                           retrofit2.Response<List<SellerRevenueChartResponse>> response) {

                        if (response.isSuccessful() && response.body() != null) {
                            drawChart(response.body(), type);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<SellerRevenueChartResponse>> call, Throwable t) {

                    }
                });
    }

    private void drawChart(List<SellerRevenueChartResponse> list, ChartType type) {

        List<com.github.mikephil.charting.data.BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            SellerRevenueChartResponse item = list.get(i);

            entries.add(new com.github.mikephil.charting.data.BarEntry(
                    i,
                    item.getRevenue().floatValue()
            ));

            String label = item.getLabel();

            // format đẹp
            if (type == ChartType.DAY) {
                label = label.substring(5); // MM-dd
            } else if (type == ChartType.MONTH) {
                String month = label.split("-")[1];
                label = "T" + Integer.parseInt(month);
            }

            labels.add(label);
        }

        com.github.mikephil.charting.data.BarDataSet dataSet =
                new com.github.mikephil.charting.data.BarDataSet(entries, "Doanh thu");

        com.github.mikephil.charting.data.BarData barData =
                new com.github.mikephil.charting.data.BarData(dataSet);

        chartRevenue.setData(barData);

        chartRevenue.getXAxis().setValueFormatter(
                new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
        );

        chartRevenue.getXAxis().setGranularity(1f);
        chartRevenue.getXAxis().setGranularityEnabled(true);
        chartRevenue.getXAxis().setLabelCount(labels.size());

        chartRevenue.invalidate();
    }

    private void sortByRevenue() {
        if (dashboardData == null) return;

        topProductAdapter.setData(dashboardData.getTopProductsByRevenue());
    }

    private void sortBySold() {
        if (dashboardData == null) return;

        topProductAdapter.setData(dashboardData.getTopProductsBySold());
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
            spFilterTopProduct.setSelection(0, false);
            topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_SOLD);
            topProductAdapter.setData(data.getTopProductsBySold());
        });
    }
}