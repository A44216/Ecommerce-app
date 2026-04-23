package com.example.ecommerceapp.ui.activity.home.admin.management.product;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCategoryService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.repository.admin.AdminCategoryRepository;
import com.example.ecommerceapp.ui.adapter.admin.management.product.AdminCategoryPagerAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCategoryViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminCategoryViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminCategoryActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private AutoCompleteTextView etSearch;
    private FloatingActionButton fabAdd;
    private AdminCategoryViewModel viewModel;

    private ArrayAdapter<String> autoCompleteAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private int currentTab = 0; // 0 = ALL, 1 = DELETED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupViewPager();
        setupFab();
        setupSearch();

        viewModel.fetchCategories(false);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAddCategory);
    }

    private void initViewModel() {

        AdminCategoryService service =
                ApiClient.getAdminCategoryService(TokenManager.getInstance(this));

        AdminCategoryRepository repo = new AdminCategoryRepository(service);

        viewModel = new ViewModelProvider(
                this,
                new AdminCategoryViewModelFactory(repo)
        ).get(AdminCategoryViewModel.class);
    }

    private void setupViewPager() {

        viewPager.setAdapter(new AdminCategoryPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Tất cả");
                    else tab.setText("Đã xoá");
                }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentTab = position;
            }
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void setupSearch() {

        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        etSearch.setAdapter(autoCompleteAdapter);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = etSearch.getText().toString().trim();
            viewModel.search(keyword);
            return true;
        });

        etSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selected = autoCompleteAdapter.getItem(position);
            if (selected != null) {
                viewModel.search(selected);
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
                    if (!keyword.isEmpty()) {
                        viewModel.autocomplete(keyword);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 400);
            }
        });

        viewModel.getAutocompleteSuggestions().observe(this, suggestions -> {
            if (suggestions != null) {
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

        InputMethodManager imm =
                (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearch.getWindowToken(), 0);
        }
    }

    private void showAddDialog() {

        final EditText inputName = new EditText(this);
        inputName.setHint("Tên danh mục");

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Thêm danh mục")
                        .setView(inputName)
                        .setPositiveButton("Thêm", null)
                        .setNegativeButton("Huỷ", null)
                        .create();

        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String name = inputName.getText().toString().trim();

                    if (name.isEmpty()) {
                        inputName.setError("Không được để trống");
                        inputName.requestFocus();
                        return;
                    }

                    dialog.dismiss();

                    CategoryRequest request = new CategoryRequest(name);
                    viewModel.create(request);
                });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            clearSearchFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

}