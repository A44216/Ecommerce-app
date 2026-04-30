package com.example.ecommerceapp.ui.fragment.admin.dashboard;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.app.Activity;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminCategorySalesChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminOrderStatusChartResponse;
import com.example.ecommerceapp.data.model.response.admin.dashboard.AdminRevenueChartResponse;
import com.example.ecommerceapp.data.repository.admin.AdminDashboardRepository;
import com.example.ecommerceapp.ui.activity.home.admin.management.shop.AdminShopDetailActivity;
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

    private SwipeRefreshLayout swipeRefreshAdminDashboard;

    private TextView kpiGMV, kpiOrders, kpiRevenue, kpiShops, kpiProducts, kpiComplaints, kpiCoupons;

    public static AdminDashboardFragment newInstance() {
        return new AdminDashboardFragment();
    }

    private ActivityResultLauncher<Intent> shopDetailLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        shopDetailLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        boolean statusChanged = result.getData().getBooleanExtra("statusChanged", false);
                        if (statusChanged && viewModel != null) {
                            viewModel.fetchAllDataWithCurrentRange();
                        }
                    }
                }
        );
    }


    @Override
    public void onResume() {
        super.onResume();
        resetFilters();
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

    private void resetFilters() {
        String[] timeRangeValues = getResources().getStringArray(R.array.admin_filter_global_time);
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, timeRangeValues);
        actFilterGlobalTime.setAdapter(timeAdapter);

        String[] chartTypeValues = getResources().getStringArray(R.array.admin_filter_chart_type);
        ArrayAdapter<String> chartTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, chartTypeValues);
        actFilterChartType.setAdapter(chartTypeAdapter);

        String[] productTypeValues = getResources().getStringArray(R.array.admin_filter_top_product_type);
        ArrayAdapter<String> productTypeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, productTypeValues);
        actFilterTopProductType.setAdapter(productTypeAdapter);
    }

    @SuppressLint("SetTextI18n")
    private void initViews(View view) {
        actFilterGlobalTime = view.findViewById(R.id.actFilterGlobalTime);
        actFilterChartType = view.findViewById(R.id.actFilterChartType);
        actFilterTopProductType = view.findViewById(R.id.actFilterTopProductType);

        chartRevenue = view.findViewById(R.id.chartRevenue);
        chartOrderStatus = view.findViewById(R.id.chartOrderStatus);
        chartCategorySales = view.findViewById(R.id.chartCategorySales);

        swipeRefreshAdminDashboard = view.findViewById(R.id.swipeRefreshAdminDashboard);
        swipeRefreshAdminDashboard.setOnRefreshListener(() -> {
            if (viewModel != null) {
                viewModel.fetchAllDataWithCurrentRange();
            }
        });

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
        chartRevenue.setNoDataText("Chưa có dữ liệu thống kê");
        chartRevenue.setNoDataTextColor(Color.GRAY);

        chartOrderStatus.getDescription().setEnabled(false);
        chartOrderStatus.setUsePercentValues(true);
        chartOrderStatus.setDrawHoleEnabled(true);
        chartOrderStatus.setHoleColor(Color.WHITE);
        chartOrderStatus.setRotationEnabled(false);
        chartOrderStatus.setNoDataText("Chưa có đơn hàng");
        chartOrderStatus.setNoDataTextColor(Color.GRAY);

        chartCategorySales.getDescription().setEnabled(false);
        chartCategorySales.setDrawGridBackground(false);
        chartCategorySales.setNoDataText("Chưa có dữ liệu danh mục");
        chartCategorySales.setNoDataTextColor(Color.GRAY);
    }

    private void setupRecyclerViews(View view) {
        RecyclerView rvTopShops = view.findViewById(R.id.rvTopShops);
        rvTopShops.setLayoutManager(new LinearLayoutManager(getContext()));
        topShopAdapter = new AdminTopShopAdapter();
        topShopAdapter.setOnItemClickListener(shopId -> {
            Intent intent = new Intent(requireContext(), AdminShopDetailActivity.class);
            intent.putExtra("shopId", shopId);
            shopDetailLauncher.launch(intent);
        });
        rvTopShops.setAdapter(topShopAdapter);


        RecyclerView rvTopProducts = view.findViewById(R.id.rvTopProducts);
        rvTopProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        topProductAdapter = new AdminTopProductAdapter();
        topProductAdapter.setListener(product -> {
            if (product.getId() != null) {
                Intent intent = new Intent(requireContext(), com.example.ecommerceapp.ui.activity.home.admin.management.product.AdminProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }
        });
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

    @SuppressLint("SetTextI18n")
    private void observeViewModel() {
        viewModel.getKpiLiveData().observe(getViewLifecycleOwner(), kpi -> {
            swipeRefreshAdminDashboard.setRefreshing(false);
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
            swipeRefreshAdminDashboard.setRefreshing(false);
            boolean hasData = false;
            if (data != null && !data.isEmpty()) {
                for (AdminRevenueChartResponse item : data) {
                    if (item.getRevenue() != null && item.getRevenue().floatValue() > 0) {
                        hasData = true;
                        break;
                    }
                }
            }

            if (hasData) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                String currentType = viewModel.getCurrentChartType();
                for (int i = 0; i < data.size(); i++) {
                    AdminRevenueChartResponse item = data.get(i);
                    float rev = item.getRevenue() != null ? item.getRevenue().floatValue() : 0f;
                    entries.add(new BarEntry(i, rev));

                    String label = item.getLabel() != null ? item.getLabel() : "";
                    if ("DAY".equals(currentType) && label.length() >= 5) {
                        label = label.substring(5);
                    } else if ("MONTH".equals(currentType) && label.contains("-")) {
                        String month = label.split("-")[1];
                        label = "T" + Integer.parseInt(month);
                    }
                    labels.add(label);
                }
                BarDataSet dataSet = new BarDataSet(entries, "Doanh thu");
                chartRevenue.setData(new BarData(dataSet));

                chartRevenue.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
                chartRevenue.getXAxis().setGranularity(1f);
                chartRevenue.getXAxis().setGranularityEnabled(true);
                chartRevenue.getXAxis().setLabelCount(labels.size());

                chartRevenue.invalidate();
            } else {
                chartRevenue.clear();
            }
        });

        viewModel.getOrderStatusChartLiveData().observe(getViewLifecycleOwner(), data -> {
            swipeRefreshAdminDashboard.setRefreshing(false);
            boolean hasData = false;
            if (data != null && !data.isEmpty()) {
                for (AdminOrderStatusChartResponse item : data) {
                    if (item.getOrderCount() > 0) {
                        hasData = true;
                        break;
                    }
                }
            }

            if (hasData) {
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
            swipeRefreshAdminDashboard.setRefreshing(false);
            boolean hasData = false;
            if (data != null && !data.isEmpty()) {
                for (AdminCategorySalesChartResponse item : data) {
                    if (item.getTotalSales() != null && item.getTotalSales().floatValue() > 0) {
                        hasData = true;
                        break;
                    }
                }
            }

            if (hasData) {
                ArrayList<BarEntry> entries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                
                if (data.size() == 1) {
                    // Cột ẩn bên trái
                    entries.add(new BarEntry(0, 0f));
                    labels.add("");
                    
                    // Cột thật ở giữa
                    AdminCategorySalesChartResponse item = data.get(0);
                    entries.add(new BarEntry(1, item.getTotalSales().floatValue()));
                    labels.add(item.getCategoryName() != null ? item.getCategoryName() : "Khác");
                    
                    // Cột ẩn bên phải
                    entries.add(new BarEntry(2, 0f));
                    labels.add("");
                } else {
                    for (int i = 0; i < data.size(); i++) {
                        AdminCategorySalesChartResponse item = data.get(i);
                        entries.add(new BarEntry(i, item.getTotalSales().floatValue()));
                        labels.add(item.getCategoryName() != null ? item.getCategoryName() : "Khác");
                    }
                }
                BarDataSet dataSet = new BarDataSet(entries, "Doanh số");
                BarData barData = new BarData(dataSet);
                chartCategorySales.setData(barData);

                XAxis xAxis = chartCategorySales.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                xAxis.setLabelCount(labels.size());

                xAxis.resetAxisMinimum();
                xAxis.resetAxisMaximum();
                chartCategorySales.fitScreen();

                chartCategorySales.invalidate();
            } else {
                chartCategorySales.clear();
            }
        });

        viewModel.getTopShopsLiveData().observe(getViewLifecycleOwner(), data -> {
            swipeRefreshAdminDashboard.setRefreshing(false);
            if (data != null) {
                topShopAdapter.setShopList(data);
            }
        });

        viewModel.getTopProductsLiveData().observe(getViewLifecycleOwner(), response -> {
            swipeRefreshAdminDashboard.setRefreshing(false);
            if (response != null) {
                updateTopProductList(viewModel.getCurrentTopProductType());
            }
        });

        viewModel.getTopProductTypeLiveData().observe(getViewLifecycleOwner(), type -> {
            if (viewModel.getTopProductsLiveData().getValue() != null) {
                updateTopProductList(type);
            }
        });

        // Trigger initial fetch only if data is empty
        if (viewModel.getKpiLiveData().getValue() == null) {
            viewModel.fetchAllDataWithCurrentRange();
        }
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