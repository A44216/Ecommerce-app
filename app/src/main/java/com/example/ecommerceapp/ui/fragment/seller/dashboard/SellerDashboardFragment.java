package com.example.ecommerceapp.ui.fragment.seller.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.ecommerceapp.data.enums.DateRange;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardKPIResponse;
import com.example.ecommerceapp.data.model.response.seller.dashboard.SellerDashboardTopProductResponse;
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
    Spinner spFilterTime;
    private AutoCompleteTextView actFilterKpi, actFilterTopProduct, actFilterTopProductTime;
    private RecyclerView rvTopProduct;
    private BarChart chartRevenue;

    private SellerTopProductAdapter topProductAdapter;
    private SellerDashboardKPIResponse kpiData;
    private SellerDashboardTopProductResponse topProductData;
    private TokenManager tokenManager;
    private SellerDashboardRepository dashboardRepository;

    private DateRange currentRange = DateRange.THIS_MONTH;
    private ChartType currentChartType = ChartType.DAY;

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

        // CALL API
        viewModel.loadDashboard(currentRange, currentChartType);
    }

    private void initViews(View view) {
        tvRevenue = view.findViewById(R.id.tvRevenue);
        tvOrders = view.findViewById(R.id.tvOrders);
        tvSold = view.findViewById(R.id.tvSold);

        actFilterTopProduct = view.findViewById(R.id.actFilterTopProduct);
        actFilterTopProductTime = view.findViewById(R.id.actFilterTopProductTime);
        actFilterKpi = view.findViewById(R.id.actFilterKpi);

        spFilterTime = view.findViewById(R.id.spFilterTime);

        chartRevenue = view.findViewById(R.id.chartRevenue);
        rvTopProduct = view.findViewById(R.id.rvTopProducts);
    }

    private void setInits() {

        // FILTER TOP PRODUCT BY SOLD AND REVENUE
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.seller_filter_top_product,
                        android.R.layout.simple_spinner_item
                );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actFilterTopProduct.setAdapter(adapter);

        // FILTER CHART TIME
        ArrayAdapter<CharSequence> timeAdapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.seller_filter_time,
                        android.R.layout.simple_spinner_item
                );

        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterTime.setAdapter(timeAdapter);

        // FILTER KPI
        ArrayAdapter<CharSequence> kpiAdapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.seller_filter_kpi,
                        android.R.layout.simple_spinner_item
                );

        kpiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actFilterKpi.setAdapter(kpiAdapter);

        // FILTER TOP PRODUCT TIME
        ArrayAdapter<CharSequence> topProductTimeAdapter =
                ArrayAdapter.createFromResource(
                        requireContext(),
                        R.array.seller_filter_top_product_time,
                        android.R.layout.simple_spinner_item
                );

        topProductTimeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actFilterTopProductTime.setAdapter(topProductTimeAdapter);

        // DEFAULT VALUE
        actFilterTopProduct.setText(actFilterTopProduct.getAdapter().getItem(0).toString(), false);
        actFilterKpi.setText(actFilterKpi.getAdapter().getItem(0).toString(), false);
        actFilterTopProductTime.setText(actFilterTopProductTime.getAdapter().getItem(0).toString(), false);

    }

    private void setListeners() {

        // FILTER TOP PRODUCT
        actFilterTopProduct.setOnItemClickListener((parent, view, position, id) -> {

            if (topProductData == null) return;

            if (position == 0) {
                topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_SOLD);
                topProductAdapter.setData(topProductData.getTopBySold());
            } else {
                topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_REVENUE);
                topProductAdapter.setData(topProductData.getTopByRevenue());
            }
        });

        // FILTER CHART TIME
        spFilterTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 1: currentChartType = ChartType.MONTH; break;
                    case 2: currentChartType = ChartType.YEAR; break;
                    default: currentChartType = ChartType.DAY;
                }

                viewModel.loadDashboard(currentRange, currentChartType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // FILTER KPI
        actFilterKpi.setOnItemClickListener((parent, view, position, id) -> {

            switch (position) {
                case 0: currentRange = DateRange.TODAY; break;
                case 1: currentRange = DateRange.LAST_7_DAYS; break;
                case 2: currentRange = DateRange.LAST_30_DAYS; break;
                case 3: currentRange = DateRange.THIS_MONTH; break;
                case 4: currentRange = DateRange.THIS_YEAR; break;
            }

            viewModel.loadDashboard(currentRange, currentChartType);
        });

        // FILTER TOP PRODUCT TIME
        actFilterTopProductTime.setOnItemClickListener((parent, view, position, id) -> {

            switch (position) {
                case 0: currentRange = DateRange.THIS_MONTH; break;
                case 1: currentRange = DateRange.LAST_MONTH; break;
                case 2: currentRange = DateRange.LAST_3_MONTHS; break;
                case 3: currentRange = DateRange.LAST_6_MONTHS; break;
                case 4: currentRange = DateRange.THIS_YEAR; break;
            }

            viewModel.loadDashboard(currentRange, currentChartType);
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

        // KPI
        viewModel.getKpiData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            tvRevenue.setText(NumberUtils.formatCompact(data.getRevenue()) + " ₫");
            tvOrders.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getOrders())));
            tvSold.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getSold())));
        });

        // TOP PRODUCT
        viewModel.getTopProductData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            topProductData = data;

            topProductAdapter.setDisplayMode(SellerTopProductAdapter.MODE_SOLD);
            topProductAdapter.setData(data.getTopBySold());
        });

        // CHART
        viewModel.getChartData().observe(getViewLifecycleOwner(), list -> {
            if (list == null) return;

            drawChart(list, currentChartType);
        });
    }
}