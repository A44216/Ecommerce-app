package com.example.ecommerceapp.ui.fragment.seller.order;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.enums.PaymentStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.SellerOrderRepository;
import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderPagerAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerOrderViewModelFactory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SellerOrderFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private SellerOrderViewModel viewModel;
    private SellerOrderPagerAdapter pagerAdapter;

    private MaterialAutoCompleteTextView actvSearch;
    private MaterialAutoCompleteTextView actvPaymentMethod;
    private MaterialAutoCompleteTextView actvPaymentStatus;

    private String currentPaymentMethod = null;
    private String currentPaymentStatus = null;
    private String currentKeyword = null;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 500;

    private ArrayAdapter<String> searchAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViews(view);
        initViewModel();
        setupFilters();
        setupViewPager();
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabOrder);
        viewPager = view.findViewById(R.id.viewPagerOrder);
        actvSearch = view.findViewById(R.id.actvSearch);
        actvPaymentMethod = view.findViewById(R.id.actvPaymentMethod);
        actvPaymentStatus = view.findViewById(R.id.actvPaymentStatus);
    }

    private void initViewModel() {
        SellerOrderRepository repository = new SellerOrderRepository(TokenManager.getInstance(requireContext()));
        viewModel = new ViewModelProvider(requireActivity(), new SellerOrderViewModelFactory(repository))
                .get(SellerOrderViewModel.class);
    }

    private void setupFilters() {
        // Setup Search adapter first (needed by observer below)
        searchAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        );
        actvSearch.setAdapter(searchAdapter);

        // Observe autocomplete results
        viewModel.getAutocompleteResult().observe(getViewLifecycleOwner(), suggestions -> {
            if (suggestions != null && !suggestions.isEmpty()) {
                searchAdapter.clear();
                searchAdapter.addAll(suggestions);
                searchAdapter.notifyDataSetChanged();
                actvSearch.showDropDown();
            }
        });

        // Setup Payment Method dropdown
        List<String> paymentMethodLabels = new ArrayList<>();
        paymentMethodLabels.add("Tất cả");
        for (PaymentMethod method : PaymentMethod.values()) {
            paymentMethodLabels.add(method.getLabel());
        }

        ArrayAdapter<String> paymentMethodAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                paymentMethodLabels
        );
        actvPaymentMethod.setAdapter(paymentMethodAdapter);
        actvPaymentMethod.setText("Tất cả", false);

        actvPaymentMethod.setOnItemClickListener((parent, v, position, id) -> {
            if (position == 0) {
                currentPaymentMethod = null;
            } else {
                currentPaymentMethod = PaymentMethod.values()[position - 1].name();
            }
            applyFiltersToChildren();
        });

        // Setup Payment Status dropdown
        List<String> paymentStatusLabels = new ArrayList<>();
        paymentStatusLabels.add("Tất cả");
        for (PaymentStatus ps : PaymentStatus.values()) {
            paymentStatusLabels.add(ps.getLabel());
        }

        ArrayAdapter<String> paymentStatusAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                paymentStatusLabels
        );
        actvPaymentStatus.setAdapter(paymentStatusAdapter);
        actvPaymentStatus.setText("Tất cả", false);

        actvPaymentStatus.setOnItemClickListener((parent, v, position, id) -> {
            if (position == 0) {
                currentPaymentStatus = null;
            } else {
                currentPaymentStatus = PaymentStatus.values()[position - 1].name();
            }
            applyFiltersToChildren();
        });

        // Setup Search with autocomplete
        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (!keyword.isEmpty()) {
                        viewModel.autocompleteOrders(keyword);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý khi nhấn Enter/Search trên bàn phím
        actvSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applyFiltersToChildren();
                return true;
            }
            return false;
        });

        actvSearch.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            actvSearch.setText(selected, false);
            applyFiltersToChildren();
        });
    }

    private void setupViewPager() {
        pagerAdapter = new SellerOrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String[] titles = {
                    "Chờ xác nhận",
                    "Xác nhận",
                    "Đang giao",
                    "Hoàn tất",
                    "Huỷ"
            };
            tab.setText(titles[position]);
        }).attach();
    }

    private void applyFiltersToChildren() {
        currentKeyword = actvSearch.getText().toString().trim();
        if (currentKeyword.isEmpty()) {
            currentKeyword = null;
        }
        
        viewModel.updateFilter(currentPaymentMethod, currentPaymentStatus, currentKeyword);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacksAndMessages(null);
    }
}
