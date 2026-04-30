package com.example.ecommerceapp.ui.activity.home.admin.management.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCouponService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.AdminCouponRepository;
import com.example.ecommerceapp.ui.adapter.admin.management.coupon.AdminCouponPagerAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCouponViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminCouponViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminCouponActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialAutoCompleteTextView etSearch;
    private FloatingActionButton fabAdd;
    private androidx.appcompat.widget.Toolbar toolbar;
    private AdminCouponPagerAdapter pagerAdapter;
    private AdminCouponViewModel viewModel;

    private ArrayAdapter<String> autoCompleteAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_coupon);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupToolbar();
        setupViewPager();
        setupFab();
        setupSearch();

        fetchInitialData();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAddCoupon);
        
        // Ensure etSearch can receive focus and show dropdown
        etSearch.setFocusable(true);
        etSearch.setFocusableInTouchMode(true);
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        AdminCouponService service = ApiClient.getAdminCouponService(tokenManager);
        AdminCouponRepository repository = new AdminCouponRepository(tokenManager);

        viewModel = new ViewModelProvider(this, new AdminCouponViewModelFactory(repository))
                .get(AdminCouponViewModel.class);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupViewPager() {
        pagerAdapter = new AdminCouponPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(3);
        // Using 4 tabs based on UI design
        String[] tabTitles = new String[]{"Hoạt động", "Vô hiệu", "Hết hạn", "Đã xóa"};
        
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }

    public String getSearchKeyword() {
        return etSearch != null ? etSearch.getText().toString().trim() : "";
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminAddAndEditCouponActivity.class);
            startActivity(intent);
        });
    }

    private void fetchInitialData() {
        pagerAdapter.searchAll("");
    }

    private void setupSearch() {
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        etSearch.setAdapter(autoCompleteAdapter);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            pagerAdapter.searchAll(keyword);
            return true;
        });

        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selected = autoCompleteAdapter.getItem(position);
            if (selected != null) {
                pagerAdapter.searchAll(selected);
                etSearch.clearFocus();
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (!keyword.isEmpty() && viewModel != null) {
                        viewModel.autocomplete(keyword);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 400);
            }
        });

        viewModel.getAutocompleteSuggestions().observe(this, suggestions -> {
            if (suggestions != null && etSearch != null) {
                autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
                etSearch.setAdapter(autoCompleteAdapter);

                if (!suggestions.isEmpty() && etSearch.hasFocus()) {
                    etSearch.showDropDown();
                }
            }
        });
    }

    private void clearSearchFocus() {
        etSearch.clearFocus();
        findViewById(R.id.main).requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            clearSearchFocus();
        }
        return super.dispatchTouchEvent(ev);
    }
}
