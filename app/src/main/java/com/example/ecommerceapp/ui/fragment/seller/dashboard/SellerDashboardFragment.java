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
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerRevenueChartResponse;
import com.example.ecommerceapp.data.repository.seller.dashboard.SellerDashboardRepository;
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

    private TokenManager tokenManager;
    private SellerDashboardRepository dashboardRepository;

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

        setInits();
        setListeners();

        // Adapter
        topProductAdapter = new SellerTopProductAdapter();
        rvTopProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopProduct.setAdapter(topProductAdapter);

        topProductAdapter.setListener(product -> {
            Intent intent = new Intent(getContext(), SellerProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            startActivity(intent);
        });

        // Repository + ViewModel
        dashboardRepository =
                new SellerDashboardRepository(ApiClient.getDashboardService(tokenManager));

        SellerDashboardViewModelFactory factory =
                new SellerDashboardViewModelFactory(dashboardRepository);

        viewModel = new ViewModelProvider(this, factory)
                .get(SellerDashboardViewModel.class);

        observeData();

        // ✅ CALL API (NO shopId)
        viewModel.loadDashboard();

        loadChart(ChartType.DAY);
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

        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.seller_filter_top_product,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterTopProduct.setAdapter(adapter);

        ArrayAdapter<CharSequence> timeAdapter =
                ArrayAdapter.createFromResource(
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

                if (dashboardData == null) return;

                if (position == 0) {
                    topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_SOLD);
                    topProductAdapter.setData(dashboardData.getTopProductsBySold());
                } else {
                    topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_REVENUE);
                    topProductAdapter.setData(dashboardData.getTopProductsByRevenue());
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

                loadChart(type);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    // ✅ FIX: no shopId
    private void loadChart(ChartType type) {

        dashboardRepository.getRevenueChart(type)
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
                        t.printStackTrace();
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

            if (type == ChartType.DAY) {
                label = label.substring(5);
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

    @SuppressLint("SetTextI18n")
    private void observeData() {

        viewModel.getDashboardData().observe(getViewLifecycleOwner(), data -> {

            if (data == null) return;

            dashboardData = data;

            tvRevenue.setText(NumberUtils.formatCompact(data.getRevenue()) + " ₫");
            tvOrders.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getOrders())));
            tvSold.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getSold())));

            spFilterTopProduct.setSelection(0, false);

            topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_SOLD);
            topProductAdapter.setData(data.getTopProductsBySold());
        });
    }
}