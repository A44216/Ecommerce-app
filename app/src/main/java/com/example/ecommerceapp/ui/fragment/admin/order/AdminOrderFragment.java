package com.example.ecommerceapp.ui.fragment.admin.order;

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
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopAutocompleteResponse;
import com.example.ecommerceapp.data.repository.admin.AdminOrderRepository;
import com.example.ecommerceapp.data.repository.admin.AdminShopRepository;
import com.example.ecommerceapp.ui.adapter.admin.order.AdminOrderPagerAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminOrderViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminOrderViewModelFactory;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private AdminOrderViewModel viewModel;
    private AdminOrderPagerAdapter pagerAdapter;

    private MaterialAutoCompleteTextView actvSearch;
    private MaterialAutoCompleteTextView actvSortTime;
    private MaterialButton btnFilter;

    private String currentPaymentMethod = null;
    private String currentPaymentStatus = null;
    private String currentKeyword = null;
    private Integer currentShopId = null;
    private String currentShopName = null;
    private String currentSortBy = "createdAt";
    private String currentDirection = "desc";

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 400;

    private ArrayAdapter<String> searchAdapter;

    public static AdminOrderFragment newInstance() {
        return new AdminOrderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_order, container, false);
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
        actvSortTime = view.findViewById(R.id.actvSortTime);
        btnFilter = view.findViewById(R.id.btnFilter);
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        AdminOrderRepository orderRepo = new AdminOrderRepository(tokenManager);
        AdminShopRepository shopRepo = new AdminShopRepository(tokenManager);
        
        viewModel = new ViewModelProvider(requireActivity(), 
                new AdminOrderViewModelFactory(orderRepo, shopRepo))
                .get(AdminOrderViewModel.class);
    }

    private void setupFilters() {
        // Setup Search
        searchAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        ) {
            @NonNull
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < getCount(); i++) {
                            list.add(getItem(i));
                        }
                        results.values = list;
                        results.count = list.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }
                };
            }
        };
        actvSearch.setAdapter(searchAdapter);

        viewModel.getOrderAutocompleteSuggestions().observe(getViewLifecycleOwner(), suggestions -> {
            if (suggestions != null && !suggestions.isEmpty()) {
                searchAdapter.clear();
                searchAdapter.addAll(suggestions);
                searchAdapter.notifyDataSetChanged();
                if (actvSearch.hasFocus()) {
                    actvSearch.showDropDown();
                }
            }
        });

        actvSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (!keyword.isEmpty()) {
                        viewModel.autocompleteOrders(keyword, currentShopId);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        actvSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                applyFiltersToChildren();
                hideKeyboard();
                return true;
            }
            return false;
        });

        actvSearch.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            actvSearch.setText(selected, false);
            actvSearch.clearFocus();
            hideKeyboard();
            applyFiltersToChildren();
        });

        actvSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && searchAdapter != null) {
                searchAdapter.clear();
                searchAdapter.notifyDataSetChanged();
            }
        });

        // Setup Sort Dropdown
        List<String> sortOptionsList = new ArrayList<>();
        sortOptionsList.add("Mới nhất");
        sortOptionsList.add("Cũ nhất");

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                sortOptionsList
        ) {
            @NonNull
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = sortOptionsList;
                        results.count = sortOptionsList.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged();
                    }
                };
            }
        };
        actvSortTime.setAdapter(sortAdapter);
        
        AdminOrderViewModel.FilterState state = viewModel.getFilterState().getValue();
        if (state != null) {
            currentPaymentMethod = state.paymentMethod;
            currentPaymentStatus = state.paymentStatus;
            currentKeyword = state.keyword;
            currentShopId = state.shopId;
            currentShopName = state.shopName;
            currentSortBy = state.sortBy;
            currentDirection = state.direction;
        }

        if (currentKeyword != null) {
            actvSearch.setText(currentKeyword, false);
        } else {
            actvSearch.setText("", false);
        }

        if ("asc".equals(currentDirection)) {
            actvSortTime.setText("Cũ nhất", false);
        } else {
            actvSortTime.setText("Mới nhất", false);
        }

        actvSortTime.setOnItemClickListener((parent, view, position, id) -> {
            currentSortBy = "createdAt";
            currentDirection = position == 0 ? "desc" : "asc";
            applyFiltersToChildren();
        });

        // Setup Filter Button (Bottom Sheet)
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_admin_order_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        MaterialAutoCompleteTextView bsPaymentMethod = bottomSheetView.findViewById(R.id.actvPaymentMethod);
        MaterialAutoCompleteTextView bsPaymentStatus = bottomSheetView.findViewById(R.id.actvPaymentStatus);
        MaterialAutoCompleteTextView bsShop = bottomSheetView.findViewById(R.id.actvShop);
        MaterialButton btnApplyFilter = bottomSheetView.findViewById(R.id.btnApplyFilter);

        // Setup Payment Method dropdown
        List<String> pmLabels = new ArrayList<>();
        pmLabels.add("Tất cả");
        for (PaymentMethod pm : PaymentMethod.values()) pmLabels.add(pm.getLabel());
        ArrayAdapter<String> pmAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                pmLabels
        ) {
            @NonNull
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = pmLabels;
                        results.count = pmLabels.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged();
                    }
                };
            }
        };
        bsPaymentMethod.setAdapter(pmAdapter);
        String currentPmLabel = "Tất cả";
        if (currentPaymentMethod != null) {
            try { currentPmLabel = PaymentMethod.valueOf(currentPaymentMethod).getLabel(); } catch (Exception ignored) {}
        }
        bsPaymentMethod.setText(currentPmLabel, false);

        // Setup Payment Status dropdown
        List<String> psLabels = new ArrayList<>();
        psLabels.add("Tất cả");
        for (PaymentStatus ps : PaymentStatus.values()) psLabels.add(ps.getLabel());
        ArrayAdapter<String> psAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                psLabels
        ) {
            @NonNull
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        results.values = psLabels;
                        results.count = psLabels.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged();
                    }
                };
            }
        };
        bsPaymentStatus.setAdapter(psAdapter);
        String currentPsLabel = "Tất cả";
        if (currentPaymentStatus != null) {
            try { currentPsLabel = PaymentStatus.valueOf(currentPaymentStatus).getLabel(); } catch (Exception ignored) {}
        }
        bsPaymentStatus.setText(currentPsLabel, false);

        // Setup Shop Autocomplete
        ArrayAdapter<AdminShopAutocompleteResponse> shopAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        bsShop.setAdapter(shopAdapter);
        
        if (currentShopName != null) {
            bsShop.setText(currentShopName, false);
        }

        // We use a separate observer in this fragment for shop suggestions specifically when bottom sheet is open
        viewModel.getShopAutocompleteSuggestions().observe(getViewLifecycleOwner(), suggestions -> {
            if (suggestions != null && !suggestions.isEmpty() && bottomSheetDialog.isShowing()) {
                shopAdapter.clear();
                shopAdapter.addAll(suggestions);
                shopAdapter.notifyDataSetChanged();
                if (bsShop.hasFocus()) bsShop.showDropDown();
            }
        });

        bsShop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    currentShopId = null;
                    currentShopName = null;
                }
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (!keyword.isEmpty() && !keyword.contains("-")) {
                        viewModel.autocompleteShops(keyword);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        bsShop.setOnItemClickListener((parent, view, position, id) -> {
            AdminShopAutocompleteResponse selectedShop = (AdminShopAutocompleteResponse) parent.getItemAtPosition(position);
            bsShop.setText(selectedShop.getName(), false);
            bsShop.clearFocus();
            hideKeyboard();
            currentShopName = selectedShop.getName();
            currentShopId = selectedShop.getId();
        });

        bsShop.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && shopAdapter != null) {
                shopAdapter.clear();
                shopAdapter.notifyDataSetChanged();
            }
        });

        btnApplyFilter.setOnClickListener(v -> {
            // Apply selections
            String selPm = bsPaymentMethod.getText().toString();
            if (selPm.equals("Tất cả")) currentPaymentMethod = null;
            else {
                for (PaymentMethod pm : PaymentMethod.values()) {
                    if (pm.getLabel().equals(selPm)) { currentPaymentMethod = pm.name(); break; }
                }
            }

            String selPs = bsPaymentStatus.getText().toString();
            if (selPs.equals("Tất cả")) currentPaymentStatus = null;
            else {
                for (PaymentStatus ps : PaymentStatus.values()) {
                    if (ps.getLabel().equals(selPs)) { currentPaymentStatus = ps.name(); break; }
                }
            }

            if (bsShop.getText().toString().trim().isEmpty()) {
                currentShopId = null;
                currentShopName = null;
            } else if (currentShopId == null) {
                // User typed something but didn't select from autocomplete
                android.widget.Toast.makeText(requireContext(), "Vui lòng chọn cửa hàng từ danh sách gợi ý", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            applyFiltersToChildren();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void setupViewPager() {
        pagerAdapter = new AdminOrderPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Preload nearby tabs to keep them alive
        viewPager.setOffscreenPageLimit(2);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            String[] titles = {
                    "Chờ xác nhận",
                    "Xác nhận",
                    "Đang giao",
                    "Hoàn tất",
                    "Huỷ",
                    "Yêu cầu trả",
                    "Tranh chấp",
                    "Trả hàng"
            };
            tab.setText(titles[position]);
        }).attach();
        
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // When tab changes, apply filters might be called by the list fragment's onResume
            }
        });
    }

    private void applyFiltersToChildren() {
        currentKeyword = actvSearch.getText().toString().trim();
        if (currentKeyword.isEmpty()) {
            currentKeyword = null;
        }

        viewModel.updateFilter(
                currentPaymentMethod,
                currentPaymentStatus,
                currentKeyword,
                currentShopId,
                currentShopName,
                currentSortBy,
                currentDirection
        );
    }

    private void hideKeyboard() {
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null && getView() != null) {
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchHandler.removeCallbacksAndMessages(null);
    }
}