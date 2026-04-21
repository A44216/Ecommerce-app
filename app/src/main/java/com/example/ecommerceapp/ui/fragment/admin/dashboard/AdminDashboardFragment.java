package com.example.ecommerceapp.ui.fragment.admin.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminCategorySalesChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminOrderStatusChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminRevenueChartResponse;
import com.example.ecommerceapp.data.repository.admin.dashboard.AdminDashboardRepository;
import com.example.ecommerceapp.ui.adapter.admin.dashboard.AdminTopProductAdapter;
import com.example.ecommerceapp.ui.adapter.admin.dashboard.AdminTopShopAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminDashboardViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminDashboardViewModelFactory;
import com.example.ecommerceapp.utils.NumberUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;

public class AdminDashboardFragment extends Fragment {

    private AdminDashboardViewModel viewModel;
    private AdminTopShopAdapter topShopAdapter;
    private AdminTopProductAdapter topProductAdapter;

    private MaterialAutoCompleteTextView actFilterGlobalTime;
    private MaterialAutoCompleteTextView actFilterChartType;
    private MaterialAutoCompleteTextView actFilterTopProductType;

    private BarChart chartRevenue, chartCategorySales;
    private PieChart chartOrderStatus;

    private TextView kpiGMV, kpiOrders, kpiRevenue, kpiShops, kpiProducts, kpiComplaints, kpiCoupons;

    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_dashboard, container, false);
        initViews(view);
        setupCharts();
        setupRecyclerViews(view);
        setupViewModel();
        setupFilters();
        observeViewModel();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void initViews(View view) {
        actFilterGlobalTime = view.findViewById(R.id.actFilterGlobalTime);
        actFilterChartType = view.findViewById(R.id.actFilterChartType);
        actFilterTopProductType = view.findViewById(R.id.actFilterTopProductType);

        chartRevenue = view.findViewById(R.id.chartRevenue);
        chartOrderStatus = view.findViewById(R.id.chartOrderStatus);
        chartCategorySales = view.findViewById(R.id.chartCategorySales);

        View vGMV = view.findViewById(R.id.kpiGMV);
        ((TextView) vGMV.findViewById(R.id.tvLabel)).setText("Tổng GMV");
        kpiGMV = vGMV.findViewById(R.id.tvValue);

        View vOrders = view.findViewById(R.id.kpiOrders);
        ((TextView) vOrders.findViewById(R.id.tvLabel)).setText("Tổng đơn");
        kpiOrders = vOrders.findViewById(R.id.tvValue);

        View vRevenue = view.findViewById(R.id.kpiRevenue);
        ((TextView) vRevenue.findViewById(R.id.tvLabel)).setText("Doanh thu sàn");
        kpiRevenue = vRevenue.findViewById(R.id.tvValue);

        View vShops = view.findViewById(R.id.kpiShops);
        ((TextView) vShops.findViewById(R.id.tvLabel)).setText("Shop chờ duyệt");
        kpiShops = vShops.findViewById(R.id.tvValue);

        View vProducts = view.findViewById(R.id.kpiProducts);
        ((TextView) vProducts.findViewById(R.id.tvLabel)).setText("SP chờ duyệt");
        kpiProducts = vProducts.findViewById(R.id.tvValue);

        View vComplaints = view.findViewById(R.id.kpiComplaints);
        ((TextView) vComplaints.findViewById(R.id.tvLabel)).setText("Khiếu nại");
        kpiComplaints = vComplaints.findViewById(R.id.tvValue);

        View vCoupons = view.findViewById(R.id.kpiCoupons);
        ((TextView) vCoupons.findViewById(R.id.tvLabel)).setText("Mã giảm giá");
        kpiCoupons = vCoupons.findViewById(R.id.tvValue);
    }

    private void setupCharts() {
        chartRevenue.getDescription().setEnabled(false);
        chartRevenue.setDrawGridBackground(false);

        chartOrderStatus.getDescription().setEnabled(false);
        chartOrderStatus.setUsePercentValues(true);
        chartOrderStatus.setDrawHoleEnabled(true);
        chartOrderStatus.setHoleColor(Color.WHITE);

        chartCategorySales.getDescription().setEnabled(false);
        chartCategorySales.setDrawGridBackground(false);
    }

    private void setupRecyclerViews(View view) {
        RecyclerView rvTopShops = view.findViewById(R.id.rvTopShops);
        rvTopShops.setLayoutManager(new LinearLayoutManager(getContext()));
        topShopAdapter = new AdminTopShopAdapter();
        rvTopShops.setAdapter(topShopAdapter);

        RecyclerView rvTopProducts = view.findViewById(R.id.rvTopProducts);
        rvTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        topProductAdapter = new AdminTopProductAdapter();
        rvTopProducts.setAdapter(topProductAdapter);
    }

    private void setupViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        AdminDashboardRepository repository = new AdminDashboardRepository(tokenManager);
        AdminDashboardViewModelFactory factory = new AdminDashboardViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(AdminDashboardViewModel.class);
    }

    private void setupFilters() {
        String[] timeRangeKeys = {"TODAY", "YESTERDAY", "LAST_7_DAYS", "THIS_MONTH", "LAST_MONTH", "THIS_YEAR"};
        String[] timeRangeValues = getResources().getStringArray(R.array.admin_filter_global_time);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, timeRangeValues);
        actFilterGlobalTime.setAdapter(timeAdapter);
        int timeIndex = java.util.Arrays.asList(timeRangeKeys).indexOf(viewModel.getCurrentGlobalDateRange());
        if (timeIndex >= 0) {
            actFilterGlobalTime.setText(timeRangeValues[timeIndex], false);
        } else {
            actFilterGlobalTime.setText(viewModel.getCurrentGlobalDateRange(), false);
        }
        actFilterGlobalTime.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.setGlobalDateRange(timeRangeKeys[position]);
        });

        String[] chartTypeKeys = {"DAY", "MONTH", "YEAR"};
        String[] chartTypeValues = getResources().getStringArray(R.array.admin_filter_chart_type);
        ArrayAdapter<String> chartTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, chartTypeValues);
        actFilterChartType.setAdapter(chartTypeAdapter);
        int chartIndex = java.util.Arrays.asList(chartTypeKeys).indexOf(viewModel.getCurrentChartType());
        if (chartIndex >= 0) {
            actFilterChartType.setText(chartTypeValues[chartIndex], false);
        } else {
            actFilterChartType.setText(viewModel.getCurrentChartType(), false);
        }
        actFilterChartType.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.setChartType(chartTypeKeys[position]);
        });

        String[] productTypeKeys = {"SOLD", "REVENUE"};
        String[] productTypeValues = getResources().getStringArray(R.array.admin_filter_top_product_type);
        ArrayAdapter<String> productTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, productTypeValues);
        actFilterTopProductType.setAdapter(productTypeAdapter);
        int productTypeIndex = java.util.Arrays.asList(productTypeKeys).indexOf(viewModel.getCurrentTopProductType());
        if (productTypeIndex >= 0) {
            actFilterTopProductType.setText(productTypeValues[productTypeIndex], false);
        } else {
            actFilterTopProductType.setText(viewModel.getCurrentTopProductType(), false);
        }
        actFilterTopProductType.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.setTopProductType(productTypeKeys[position]);
        });
    }

    private void observeViewModel() {
        viewModel.getKpiLiveData().observe(getViewLifecycleOwner(), kpi -> {
            if (kpi != null) {
                kpiGMV.setText(NumberUtils.formatCompact(kpi.getTotalGMV()) + " ₫");
                kpiOrders.setText(NumberUtils.formatCompact(java.math.BigDecimal.valueOf(kpi.getTotalOrders())));
                kpiRevenue.setText(NumberUtils.formatCompact(kpi.getTotalPlatformRevenue()) + " ₫");
                kpiShops.setText(NumberUtils.formatCompact(java.math.BigDecimal.valueOf(kpi.getPendingShops())));
                kpiProducts.setText(NumberUtils.formatCompact(java.math.BigDecimal.valueOf(kpi.getPendingProducts())));
                kpiComplaints.setText(NumberUtils.formatCompact(java.math.BigDecimal.valueOf(kpi.getPendingComplaints())));
                kpiCoupons.setText(NumberUtils.formatCompact(java.math.BigDecimal.valueOf(kpi.getActiveCoupons())));
            }
        });

        viewModel.getRevenueChartLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && !data.isEmpty()) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    AdminRevenueChartResponse item = data.get(i);
                    entries.add(new BarEntry(i, item.getRevenue().floatValue()));
                    labels.add(item.getLabel());
                }
                BarDataSet dataSet = new BarDataSet(entries, "Revenue");
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                BarData barData = new BarData(dataSet);
                chartRevenue.setData(barData);
                XAxis xAxis = chartRevenue.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                chartRevenue.invalidate();
            } else {
                chartRevenue.clear();
            }
        });

        viewModel.getOrderStatusChartLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && !data.isEmpty()) {
                ArrayList<PieEntry> entries = new ArrayList<>();
                for (AdminOrderStatusChartResponse item : data) {
                    entries.add(new PieEntry(item.getOrderCount(), item.getStatus()));
                }
                PieDataSet dataSet = new PieDataSet(entries, "Order Status");
                dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
                PieData pieData = new PieData(dataSet);
                chartOrderStatus.setData(pieData);
                chartOrderStatus.invalidate();
            } else {
                chartOrderStatus.clear();
            }
        });

        viewModel.getCategorySalesChartLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null && !data.isEmpty()) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                for (int i = 0; i < data.size(); i++) {
                    AdminCategorySalesChartResponse item = data.get(i);
                    entries.add(new BarEntry(i, item.getTotalSales().floatValue()));
                    labels.add(item.getCategoryName());
                }
                BarDataSet dataSet = new BarDataSet(entries, "Category Sales");
                dataSet.setColors(ColorTemplate.LIBERTY_COLORS);
                BarData barData = new BarData(dataSet);
                chartCategorySales.setData(barData);
                XAxis xAxis = chartCategorySales.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                chartCategorySales.invalidate();
            } else {
                chartCategorySales.clear();
            }
        });

        viewModel.getTopShopsLiveData().observe(getViewLifecycleOwner(), data -> {
            if (data != null) {
                topShopAdapter.setShopList(data);
            }
        });

        viewModel.getTopProductsLiveData().observe(getViewLifecycleOwner(), response -> {
            if (response != null) {
                updateTopProductList(viewModel.getCurrentTopProductType());
            }
        });

        viewModel.getTopProductTypeLiveData().observe(getViewLifecycleOwner(), type -> {
            if (viewModel.getTopProductsLiveData().getValue() != null) {
                updateTopProductList(type);
            }
        });

        // Trigger initial fetch
        viewModel.fetchAllDataWithCurrentRange();
    }

    private void updateTopProductList(String type) {
        if (viewModel.getTopProductsLiveData().getValue() != null) {
            if ("REVENUE".equals(type)) {
                topProductAdapter.setProductList(viewModel.getTopProductsLiveData().getValue().getTopByRevenue(), type);
            } else {
                topProductAdapter.setProductList(viewModel.getTopProductsLiveData().getValue().getTopBySold(), type);
            }
        }
    }
}