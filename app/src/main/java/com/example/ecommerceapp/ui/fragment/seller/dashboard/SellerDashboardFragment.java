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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SellerDashboardFragment extends Fragment {

    private SellerDashboardViewModel viewModel;

    private TextView tvRevenue, tvOrders, tvSold;
    private Spinner spFilterTime;

    private MaterialAutoCompleteTextView actFilterKpi, actFilterTopProduct, actFilterTopProductTime;

    private RecyclerView rvTopProduct;
    private BarChart chartRevenue;

    private SellerTopProductAdapter topProductAdapter;

    private SellerDashboardTopProductResponse topProductData;

    private TokenManager tokenManager;
    private SellerDashboardRepository dashboardRepository;

    private DateRange currentKpiRange = DateRange.THIS_MONTH;
    private DateRange currentTopRange = DateRange.THIS_MONTH;
    private ChartType currentChartType = ChartType.DAY;

    private int currentTopMode = SellerTopProductAdapter.MODE_SOLD;

    @Override
    public void onResume() {
        super.onResume();
        resetFilters();
    }

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
        setDefaultFilters();
        
        setupRecyclerView();
        setupListeners();
        initViewModel();
        setupObservers();

        viewModel.loadKpi(currentKpiRange);
        viewModel.loadTopProducts(currentTopRange);
        viewModel.loadChart(currentChartType);
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
        chartRevenue.setNoDataText("Chưa có dữ liệu thống kê");
        chartRevenue.setNoDataTextColor(android.graphics.Color.GRAY);
        
        rvTopProduct = view.findViewById(R.id.rvTopProducts);
    }

    private void setInits() {

        setupDropdown(actFilterKpi, R.array.seller_filter_kpi);
        setupDropdown(actFilterTopProduct, R.array.seller_filter_top_product);
        setupDropdown(actFilterTopProductTime, R.array.seller_filter_top_product_time);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.seller_filter_time,
                android.R.layout.simple_spinner_item
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFilterTime.setAdapter(spinnerAdapter);
    }

    private void setDefaultFilters() {

        String[] kpiItems = getResources().getStringArray(R.array.seller_filter_kpi);
        String[] topItems = getResources().getStringArray(R.array.seller_filter_top_product);
        String[] timeItems = getResources().getStringArray(R.array.seller_filter_top_product_time);

        actFilterKpi.setText(kpiItems[3], false); // THIS_MONTH
        actFilterTopProduct.setText(topItems[0], false);
        actFilterTopProductTime.setText(timeItems[0], false);

        spFilterTime.setSelection(0); // DAY
    }

    private void resetFilters() {

        setupDropdown(actFilterKpi, R.array.seller_filter_kpi);
        setupDropdown(actFilterTopProduct, R.array.seller_filter_top_product);
        setupDropdown(actFilterTopProductTime, R.array.seller_filter_top_product_time);
    }

    private void setupDropdown(MaterialAutoCompleteTextView view, int arrayRes) {
        String[] items = getResources().getStringArray(arrayRes);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                items
        );

        view.setAdapter(adapter);
    }
    
    private void setupRecyclerView() {
        topProductAdapter = new SellerTopProductAdapter();
        rvTopProduct.setLayoutManager(new LinearLayoutManager(getContext()));
        rvTopProduct.setAdapter(topProductAdapter);

        topProductAdapter.setListener(product -> {
            Intent intent = new Intent(getContext(), SellerProductDetailActivity.class);
            intent.putExtra("productId", product.getProductId());
            startActivity(intent);
        });
    }

    private void initViewModel() {
        dashboardRepository =
                new SellerDashboardRepository(ApiClient.getDashboardService(tokenManager));

        viewModel = new ViewModelProvider(
                this,
                new SellerDashboardViewModelFactory(dashboardRepository)
        ).get(SellerDashboardViewModel.class);
    }

    private void setupListeners() {
        setupKpiFilterListener();
        setupTopProductSortListener();
        setupTopProductTimeListener();
        setupChartFilterListener();
    }

    private void setupKpiFilterListener() {
        actFilterKpi.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: currentKpiRange = DateRange.TODAY; break;
                case 1: currentKpiRange = DateRange.LAST_7_DAYS; break;
                case 2: currentKpiRange = DateRange.LAST_30_DAYS; break;
                case 3: currentKpiRange = DateRange.THIS_MONTH; break;
                case 4: currentKpiRange = DateRange.THIS_YEAR; break;
            }
            viewModel.loadKpi(currentKpiRange);
        });
    }

    private void setupTopProductSortListener() {
        actFilterTopProduct.setOnItemClickListener((parent, view, position, id) -> {
            if (topProductData == null) return;

            if (position == 0) {
                currentTopMode = SellerTopProductAdapter.MODE_SOLD;
            } else {
                currentTopMode = SellerTopProductAdapter.MODE_REVENUE;
            }

            topProductAdapter.setDisplayMode(currentTopMode);

            if (currentTopMode == SellerTopProductAdapter.MODE_SOLD) {
                topProductAdapter.setData(topProductData.getTopBySold());
            } else {
                topProductAdapter.setData(topProductData.getTopByRevenue());
            }
        });
    }

    private void setupTopProductTimeListener() {
        actFilterTopProductTime.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: currentTopRange = DateRange.THIS_MONTH; break;
                case 1: currentTopRange = DateRange.LAST_MONTH; break;
                case 2: currentTopRange = DateRange.LAST_3_MONTHS; break;
                case 3: currentTopRange = DateRange.LAST_6_MONTHS; break;
                case 4: currentTopRange = DateRange.THIS_YEAR; break;
            }
            viewModel.loadTopProducts(currentTopRange);
        });
    }

    private void setupChartFilterListener() {
        spFilterTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1: currentChartType = ChartType.MONTH; break;
                    case 2: currentChartType = ChartType.YEAR; break;
                    default: currentChartType = ChartType.DAY;
                }
                viewModel.loadChart(currentChartType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupObservers() {
        observeKpiData();
        observeTopProductData();
        observeChartData();
    }

    @SuppressLint("SetTextI18n")
    private void observeKpiData() {
        viewModel.getKpiData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            tvRevenue.setText(NumberUtils.formatCompact(data.getRevenue()) + " ₫");
            tvOrders.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getOrders())));
            tvSold.setText(NumberUtils.formatCompact(BigDecimal.valueOf(data.getSold())));
        });
    }

    private void observeTopProductData() {
        viewModel.getTopProductData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            topProductData = data;
            topProductAdapter.setDisplayMode(currentTopMode);

            if (currentTopMode == SellerTopProductAdapter.MODE_SOLD) {
                topProductAdapter.setData(data.getTopBySold());
            } else {
                topProductAdapter.setData(data.getTopByRevenue());
            }
        });
    }

    private void observeChartData() {
        viewModel.getChartData().observe(getViewLifecycleOwner(), list -> {
            boolean hasData = false;
            if (list != null && !list.isEmpty()) {
                for (SellerRevenueChartResponse item : list) {
                    if (item.getRevenue() != null && item.getRevenue().floatValue() > 0) {
                        hasData = true;
                        break;
                    }
                }
            }

            if (hasData) {
                drawChart(list, currentChartType);
            } else {
                chartRevenue.clear();
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

        chartRevenue.setData(new com.github.mikephil.charting.data.BarData(dataSet));

        chartRevenue.getXAxis().setValueFormatter(
                new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels)
        );

        chartRevenue.getXAxis().setGranularity(1f);
        chartRevenue.getXAxis().setGranularityEnabled(true);
        chartRevenue.getXAxis().setLabelCount(labels.size());

        chartRevenue.invalidate();
    }
}