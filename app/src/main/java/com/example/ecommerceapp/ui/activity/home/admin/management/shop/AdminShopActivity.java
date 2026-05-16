package com.example.ecommerceapp.ui.activity.home.admin.management.shop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopAutocompleteResponse;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.AdminShopRepository;
import com.example.ecommerceapp.ui.adapter.admin.management.shop.AdminShopAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminShopViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminShopViewModelFactory;

public class AdminShopActivity extends AppCompatActivity {

    private AdminShopViewModel viewModel;
    private AdminShopAdapter adapter;

    private AutoCompleteTextView edtSearch;
    private AutoCompleteTextView actvSortCreated;
    private AutoCompleteTextView actvFilterStatus;
    private RecyclerView rvShops;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshShops;
    private TextView tvEmpty;
    private ImageView ivBack;

    private ArrayAdapter<AdminShopAutocompleteResponse> autoCompleteAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private ShopStatus currentStatus = null;
    private String currentKeyword = "";
    private String currentSortBy = "createdAt";
    private String currentSortDir = "desc";
    
    private boolean isLoadMore = false;

    private final ActivityResultLauncher<Intent> detailActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean statusChanged = result.getData().getBooleanExtra("statusChanged", false);
                    int shopId = result.getData().getIntExtra("shopId", -1);
                    String newStatusStr = result.getData().getStringExtra("newStatus");

                    if (statusChanged && shopId != -1 && newStatusStr != null) {
                        try {
                            ShopStatus newStatus = ShopStatus.valueOf(newStatusStr);
                            // Cập nhật UI ngay lập tức dựa trên data trả về từ Detail để ko phải load lại list
                            viewModel.updateShopStatus(shopId, newStatus);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupViewModel();
        setupRecyclerView();
        setupFilters();
        setupListeners();
        observeViewModel();

        viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
    }

    private void initViews() {
        edtSearch = findViewById(R.id.edtSearch);
        actvSortCreated = findViewById(R.id.actvSortCreated);
        actvFilterStatus = findViewById(R.id.actvFilterStatus);
        rvShops = findViewById(R.id.rvShops);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshShops = findViewById(R.id.swipeRefreshShops);
        tvEmpty = findViewById(R.id.tvEmpty);
        ivBack = findViewById(R.id.ivBack);
    }

    private void setupViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        AdminShopRepository repository = new AdminShopRepository(tokenManager);
        AdminShopViewModelFactory factory = new AdminShopViewModelFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(AdminShopViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new AdminShopAdapter(shop -> {
            Intent intent = new Intent(this, AdminShopDetailActivity.class);
            intent.putExtra("shopId", shop.getId());
            detailActivityLauncher.launch(intent);
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvShops.setLayoutManager(layoutManager);
        rvShops.setAdapter(adapter);

        rvShops.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) { // Scroll down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        isLoadMore = true;
                        viewModel.loadNextPage();
                    }
                }
            }
        });
    }

    private void setupFilters() {
        String[] sortOptions = {"Mới nhất", "Cũ nhất"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sortOptions);
        actvSortCreated.setAdapter(sortAdapter);

        actvSortCreated.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                currentSortDir = "desc";
            } else {
                currentSortDir = "asc";
            }
            viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
        });

        String[] statusOptions = {"Tất cả", "Chờ duyệt", "Đã duyệt", "Bị từ chối", "Bị khóa", "Đã hủy"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusOptions);
        actvFilterStatus.setAdapter(statusAdapter);

        actvFilterStatus.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 1:
                    currentStatus = ShopStatus.PENDING;
                    break;
                case 2:
                    currentStatus = ShopStatus.APPROVED;
                    break;
                case 3:
                    currentStatus = ShopStatus.REJECTED;
                    break;
                case 4:
                    currentStatus = ShopStatus.BLOCKED;
                    break;
                case 5:
                    currentStatus = ShopStatus.CANCELED;
                    break;
                default:
                    currentStatus = null;
                    break;
            }
            viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
        });
    }

    private void setupSearch() {
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        edtSearch.setAdapter(autoCompleteAdapter);

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = edtSearch.getText().toString().trim();
            currentKeyword = keyword;
            viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
            return true;
        });

        edtSearch.setOnItemClickListener((parent, view, position, id) -> {
            AdminShopAutocompleteResponse selected = autoCompleteAdapter.getItem(position);
            if (selected != null) {
                currentKeyword = selected.getName();
                viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
                edtSearch.clearFocus();
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
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
                edtSearch.setAdapter(autoCompleteAdapter);

                if (!suggestions.isEmpty() && edtSearch.hasFocus()) {
                    edtSearch.showDropDown();
                }
            }
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());
        setupSearch();

        swipeRefreshShops.setOnRefreshListener(() -> {
            viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
        });
    }

    private void clearSearchFocus() {
        edtSearch.clearFocus();
        actvSortCreated.clearFocus();
        actvFilterStatus.clearFocus();
        findViewById(R.id.main).requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // Dismiss all dropdowns when touching outside
        if (actvSortCreated != null) actvSortCreated.dismissDropDown();
        if (actvFilterStatus != null) actvFilterStatus.dismissDropDown();
        if (edtSearch != null) edtSearch.dismissDropDown();

        if (getCurrentFocus() != null) {
            clearSearchFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void observeViewModel() {
        viewModel.getShops().observe(this, shops -> {
            boolean wasLoadMore = isLoadMore;
            isLoadMore = false;
            
            RecyclerView.ItemAnimator animator = rvShops != null ? rvShops.getItemAnimator() : null;
            if (!wasLoadMore && rvShops != null) {
                rvShops.setItemAnimator(null);
            }
            
            adapter.submitList(shops, () -> {
                if (!wasLoadMore && rvShops != null) {
                    rvShops.scrollToPosition(0);
                    rvShops.post(() -> rvShops.setItemAnimator(animator));
                }
            });
            tvEmpty.setVisibility(shops.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                if (swipeRefreshShops != null && swipeRefreshShops.isRefreshing()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                if (swipeRefreshShops != null) {
                    swipeRefreshShops.setRefreshing(false);
                }
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
