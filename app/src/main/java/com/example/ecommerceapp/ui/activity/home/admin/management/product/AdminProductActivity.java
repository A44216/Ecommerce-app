package com.example.ecommerceapp.ui.activity.home.admin.management.product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCategoryService;
import com.example.ecommerceapp.api.service.admin.AdminProductService;
import com.example.ecommerceapp.api.service.admin.AdminShopService;
import com.example.ecommerceapp.data.enums.ProductStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ProductAutocompleteResponse;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminCategoryResponse;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminProductResponse;
import com.example.ecommerceapp.data.model.response.admin.management.shop.AdminShopAutocompleteResponse;
import com.example.ecommerceapp.data.model.response.seller.PageResponse;
import com.example.ecommerceapp.ui.adapter.admin.product.AdminProductAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProductActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    
    private MaterialAutoCompleteTextView actvSearchKeyword;
    private MaterialAutoCompleteTextView actvSortTime;
    private MaterialButton btnFilter;
    private ImageView ivBack;

    private AdminProductAdapter adapter;
    private List<AdminProductResponse> productList = new ArrayList<>();
    private AdminProductService adminProductService;
    private AdminShopService adminShopService;
    private AdminCategoryService adminCategoryService;

    // Filter states
    private int currentPage = 0;
    private final int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private String currentKeyword = null;
    private String currentSortBy = "createdAt";
    private String currentDirection = "desc";
    private ProductStatus currentStatus = null;
    private Boolean currentIsDeleted = false;
    
    // Additional bottom sheet filters
    private Integer currentShopId = null;
    private Integer currentCategoryId = null;
    private String currentShopName = "";
    private String currentCategoryName = "";

    // Autocomplete
    private ArrayAdapter<String> keywordAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private ActivityResultLauncher<Intent> detailActivityLauncher;
    
    // Tab State Persistence
    private java.util.Map<Integer, android.os.Parcelable> tabScrollStates = new java.util.HashMap<>();
    private int previousTabPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_product);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adminProductService = ApiClient.getAdminProductService(TokenManager.getInstance(this));
        adminShopService = ApiClient.getAdminShopService(TokenManager.getInstance(this));
        adminCategoryService = ApiClient.getAdminCategoryService(TokenManager.getInstance(this));

        initViews();
        setupTabs();
        setupRecyclerView();
        setupSearch();
        setupFilters();
        
        // Launcher for Detail Activity to quietly update list locally on return
        detailActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            int returnedId = data.getIntExtra("productId", -1);
                            String action = data.getStringExtra("action");
                            String newStatusStr = data.getStringExtra("newStatus");

                            if (returnedId != -1 && action != null && productList != null) {
                                int index = -1;
                                for (int i = 0; i < productList.size(); i++) {
                                    if (productList.get(i).getId() == returnedId) {
                                        index = i;
                                        break;
                                    }
                                }

                                if (index != -1) {
                                    boolean shouldRemove = false;
                                    AdminProductResponse item = productList.get(index);

                                    if ("DELETE".equals(action)) {
                                        if (currentIsDeleted == null || !currentIsDeleted) {
                                            shouldRemove = true;
                                        }
                                    } else if ("RESTORE".equals(action)) {
                                        if (Boolean.TRUE.equals(currentIsDeleted) || currentStatus == ProductStatus.REJECTED) {
                                            shouldRemove = true;
                                        }
                                    } else if ("UPDATE_STATUS".equals(action) && newStatusStr != null) {
                                        ProductStatus newStatus = ProductStatus.valueOf(newStatusStr);
                                        item.setStatus(newStatus);
                                        if (currentStatus != null && currentStatus != newStatus) {
                                            shouldRemove = true;
                                        }
                                    }

                                    if (shouldRemove) {
                                        productList.remove(index);
                                        adapter.submitList(new ArrayList<>(productList));
                                    } else {
                                        adapter.submitList(new ArrayList<>(productList));
                                        adapter.notifyItemChanged(index);
                                    }
                                }
                            } else {
                                fetchProducts(true, true, false);
                            }
                        }
                    }
                }
        );

        // Initial fetch
        fetchProducts(true, false, false);
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        
        actvSearchKeyword = findViewById(R.id.actvSearchKeyword);
        actvSortTime = findViewById(R.id.actvSortTime);
        btnFilter = findViewById(R.id.btnFilter);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());
        
        swipeRefreshLayout.setOnRefreshListener(() -> fetchProducts(true, false, false));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ duyệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã duyệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Từ chối"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã xoá"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Save current state BEFORE switching
                if (recyclerView.getLayoutManager() != null) {
                    tabScrollStates.put(previousTabPosition, recyclerView.getLayoutManager().onSaveInstanceState());
                }
                previousTabPosition = tab.getPosition();

                switch (tab.getPosition()) {
                    case 0:
                        currentStatus = null;
                        currentIsDeleted = false;
                        break;
                    case 1:
                        currentStatus = ProductStatus.PENDING;
                        currentIsDeleted = false;
                        break;
                    case 2:
                        currentStatus = ProductStatus.APPROVED;
                        currentIsDeleted = false;
                        break;
                    case 3:
                        currentStatus = ProductStatus.REJECTED;
                        currentIsDeleted = false;
                        break;
                    case 4:
                        currentStatus = null;
                        currentIsDeleted = true;
                        break;
                }
                // Silent fetch without showing swipe refresh spinner to prevent flashing
                fetchProducts(true, true, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == productList.size() - 1) {
                    if (!isLastPage && !isLoading) {
                        fetchProducts(false, true, false);
                    }
                }
            }
        });

        adapter.setListener(product -> {
            Intent intent = new Intent(AdminProductActivity.this, AdminProductDetailActivity.class);
            intent.putExtra("productId", product.getId());
            intent.putExtra("isDeletedTab", currentIsDeleted != null && currentIsDeleted);
            detailActivityLauncher.launch(intent);
        });
    }

    private void setupSearch() {
        keywordAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvSearchKeyword.setAdapter(keywordAdapter);
        actvSearchKeyword.setThreshold(1);

        actvSearchKeyword.addTextChangedListener(new TextWatcher() {
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
                        fetchKeywordAutocomplete(keyword);
                    }
                };

                searchHandler.postDelayed(searchRunnable, 400);
            }
        });

        actvSearchKeyword.setOnItemClickListener((parent, view, position, id) -> {
            String selected = keywordAdapter.getItem(position);
            actvSearchKeyword.setText(selected, false);
            
            if (selected != null && selected.contains(" - ")) {
                currentKeyword = selected.split(" - ")[0].trim();
            } else {
                currentKeyword = selected;
            }
            clearSearchFocus();
            fetchProducts(true, false, true);
        });

        actvSearchKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String typed = actvSearchKeyword.getText().toString().trim();
                if (typed.contains(" - ")) {
                    currentKeyword = typed.split(" - ")[0].trim();
                } else {
                    currentKeyword = typed;
                }
                fetchProducts(true, false, true);
                return true;
            }
            return false;
        });
    }

    private void setupFilters() {
        // Setup Sort Dropdown
        String[] sortOptions = {"Mới nhất", "Cũ nhất", "Bán chạy", "Giá cao đến thấp", "Giá thấp đến cao"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sortOptions);
        actvSortTime.setAdapter(sortAdapter);
        actvSortTime.setText(sortOptions[0], false);
        
        actvSortTime.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: currentSortBy = "createdAt"; currentDirection = "desc"; break;
                case 1: currentSortBy = "createdAt"; currentDirection = "asc"; break;
                case 2: currentSortBy = "soldCount"; currentDirection = "desc"; break;
                case 3: currentSortBy = "price"; currentDirection = "desc"; break;
                case 4: currentSortBy = "price"; currentDirection = "asc"; break;
            }
            fetchProducts(true, true, true);
        });
        
        // Setup Filter Button for Bottom Sheet
        btnFilter.setOnClickListener(v -> showFilterBottomSheet());
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_admin_product_filter, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        MaterialAutoCompleteTextView actvShopFilter = bottomSheetView.findViewById(R.id.actvShopFilter);
        MaterialAutoCompleteTextView actvCategoryFilter = bottomSheetView.findViewById(R.id.actvCategoryFilter);
        MaterialButton btnReset = bottomSheetView.findViewById(R.id.btnReset);
        MaterialButton btnApply = bottomSheetView.findViewById(R.id.btnApply);
        ImageView ivClose = bottomSheetView.findViewById(R.id.ivClose);

        ivClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        
        actvShopFilter.setText(currentShopName, false);
        actvCategoryFilter.setText(currentCategoryName, false);

        // Setup Shop Autocomplete
        ArrayAdapter<AdminShopAutocompleteResponse> shopAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvShopFilter.setAdapter(shopAdapter);
        actvShopFilter.setThreshold(1);

        actvShopFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    adminShopService.autocompleteShops(keyword).enqueue(new Callback<List<AdminShopAutocompleteResponse>>() {
                        @Override
                        public void onResponse(Call<List<AdminShopAutocompleteResponse>> call, Response<List<AdminShopAutocompleteResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                shopAdapter.clear();
                                shopAdapter.addAll(response.body());
                                shopAdapter.getFilter().filter(null, count -> {
                                    if (count > 0 && actvShopFilter.hasFocus()) {
                                        actvShopFilter.showDropDown();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onFailure(Call<List<AdminShopAutocompleteResponse>> call, Throwable t) {}
                    });
                }
            }
        });

        actvShopFilter.setOnItemClickListener((parent, view, position, id) -> {
            AdminShopAutocompleteResponse selectedShop = shopAdapter.getItem(position);
            if (selectedShop != null) {
                currentShopId = selectedShop.getId();
            }
            actvShopFilter.clearFocus();
            actvShopFilter.dismissDropDown();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(actvShopFilter.getWindowToken(), 0);
            }
        });

        // Setup Category Autocomplete
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        actvCategoryFilter.setAdapter(categoryAdapter);
        actvCategoryFilter.setThreshold(1);
        List<AdminCategoryResponse> currentCategories = new ArrayList<>();

        actvCategoryFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    adminCategoryService.autocompleteCategories(keyword).enqueue(new Callback<List<AdminCategoryResponse>>() {
                        @Override
                        public void onResponse(Call<List<AdminCategoryResponse>> call, Response<List<AdminCategoryResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                currentCategories.clear();
                                currentCategories.addAll(response.body());
                                categoryAdapter.clear();
                                for (AdminCategoryResponse cat : response.body()) {
                                    categoryAdapter.add(cat.getName());
                                }
                                categoryAdapter.getFilter().filter(null, count -> {
                                    if (count > 0 && actvCategoryFilter.hasFocus()) {
                                        actvCategoryFilter.showDropDown();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onFailure(Call<List<AdminCategoryResponse>> call, Throwable t) {}
                    });
                }
            }
        });

        actvCategoryFilter.setOnItemClickListener((parent, view, position, id) -> {
            if (position < currentCategories.size()) {
                currentCategoryId = currentCategories.get(position).getId();
            }
            actvCategoryFilter.clearFocus();
            actvCategoryFilter.dismissDropDown();
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(actvCategoryFilter.getWindowToken(), 0);
            }
        });

        btnReset.setOnClickListener(v -> {
            actvShopFilter.setText("", false);
            actvCategoryFilter.setText("", false);
            currentShopId = null;
            currentCategoryId = null;
            currentShopName = "";
            currentCategoryName = "";
            bottomSheetDialog.dismiss();
            fetchProducts(true, false, true);
        });

        btnApply.setOnClickListener(v -> {
            currentShopName = actvShopFilter.getText().toString().trim();
            currentCategoryName = actvCategoryFilter.getText().toString().trim();
            if (currentShopName.isEmpty()) currentShopId = null;
            if (currentCategoryName.isEmpty()) currentCategoryId = null;
            bottomSheetDialog.dismiss();
            fetchProducts(true, false, true);
        });

        bottomSheetDialog.show();
    }

    private void fetchKeywordAutocomplete(String keyword) {
        adminProductService.autocomplete(keyword, null).enqueue(new Callback<List<ProductAutocompleteResponse>>() {
            @Override
            public void onResponse(Call<List<ProductAutocompleteResponse>> call, Response<List<ProductAutocompleteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    keywordAdapter.clear();
                    for (ProductAutocompleteResponse item : response.body()) {
                        keywordAdapter.add(item.getProductCode() + " - " + item.getName());
                    }
                    keywordAdapter.getFilter().filter(null, count -> {
                        if (count > 0 && actvSearchKeyword.hasFocus()) {
                            actvSearchKeyword.showDropDown();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ProductAutocompleteResponse>> call, Throwable t) {
                Log.e("AdminProduct", "Autocomplete failed: " + t.getMessage());
            }
        });
    }

    private void fetchProducts(boolean isRefresh, boolean isSilent, boolean shouldScrollToTop) {
        if (isLoading) return;
        isLoading = true;

        if (isRefresh) {
            currentPage = 0;
            isLastPage = false;
            if (!isSilent) {
                swipeRefreshLayout.setRefreshing(true);
            }
        }

        adminProductService.getProducts(
                currentPage, pageSize, currentShopId, currentCategoryId, 
                currentStatus, currentIsDeleted, currentKeyword, currentSortBy, currentDirection
        ).enqueue(new Callback<PageResponse<AdminProductResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<AdminProductResponse>> call, Response<PageResponse<AdminProductResponse>> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
                    if (isRefresh) {
                        // Create a new list reference for DiffUtil to properly calculate changes
                        productList = new ArrayList<>();
                        // Temporarily disable ItemAnimator to prevent the messy add/remove flash
                        recyclerView.setItemAnimator(null);
                        
                        productList.addAll(response.body().getItems());
                        adapter.submitList(new ArrayList<>(productList), () -> {
                            if (shouldScrollToTop) {
                                recyclerView.smoothScrollToPosition(0);
                            } else if (isSilent && isRefresh) {
                                // Restore state if it exists for this tab
                                android.os.Parcelable state = tabScrollStates.get(previousTabPosition);
                                if (state != null && recyclerView.getLayoutManager() != null) {
                                    recyclerView.getLayoutManager().onRestoreInstanceState(state);
                                }
                            }
                            
                            // Restore the animator
                            recyclerView.post(() -> recyclerView.setItemAnimator(animator));
                        });
                        
                    } else {
                        // Pagination (isRefresh == false), just add to the list
                        productList.addAll(response.body().getItems());
                        adapter.submitList(new ArrayList<>(productList));
                    }
                    
                    currentPage++;
                    isLastPage = currentPage >= response.body().getTotalPages();
                } else {
                    Toast.makeText(AdminProductActivity.this, "Failed to load products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<AdminProductResponse>> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AdminProductActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearSearchFocus() {
        actvSearchKeyword.clearFocus();
        findViewById(R.id.main).requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(actvSearchKeyword.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (actvSearchKeyword != null) actvSearchKeyword.dismissDropDown();
        if (actvSortTime != null) actvSortTime.dismissDropDown();

        if (getCurrentFocus() != null) {
            clearSearchFocus();
        }
        return super.dispatchTouchEvent(ev);
    }
}
