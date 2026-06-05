package com.example.ecommerceapp.ui.activity.home.admin.management.complaint;

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
import com.example.ecommerceapp.api.service.admin.AdminComplaintService;
import com.example.ecommerceapp.data.enums.ComplaintStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.PageResponse;
import com.example.ecommerceapp.data.model.response.admin.management.complaint.AdminComplaintResponse;
import com.example.ecommerceapp.ui.adapter.admin.complaint.AdminComplaintAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminComplaintActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    
    private MaterialAutoCompleteTextView actvSearchKeyword;
    private MaterialAutoCompleteTextView actvSortTime;
    private ImageView ivBack;

    private AdminComplaintAdapter adapter;
    private List<AdminComplaintResponse> complaintList = new ArrayList<>();
    private AdminComplaintService adminComplaintService;

    // Filter states
    private int currentPage = 0;
    private final int pageSize = 10;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    private String currentKeyword = null;
    private String currentSortBy = "createdAt";
    private String currentDirection = "desc";
    private ComplaintStatus currentStatus = null;

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
        setContentView(R.layout.activity_admin_complaint);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        adminComplaintService = ApiClient.getAdminComplaintService(TokenManager.getInstance(this));

        initViews();
        setupTabs();
        setupRecyclerView();
        setupSearch();
        setupFilters();

        detailActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        fetchComplaints(true, true, false);
                    }
                }
        );

        // Initial fetch
        fetchComplaints(true, false, false);
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        
        actvSearchKeyword = findViewById(R.id.actvSearchKeyword);
        actvSortTime = findViewById(R.id.actvSortTime);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(v -> finish());
        
        swipeRefreshLayout.setOnRefreshListener(() -> fetchComplaints(true, false, false));
    }

    private void setupTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Chờ xử lý"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã giải quyết"));
        tabLayout.addTab(tabLayout.newTab().setText("Đã từ chối"));

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
                        break;
                    case 1:
                        currentStatus = ComplaintStatus.PENDING;
                        break;
                    case 2:
                        currentStatus = ComplaintStatus.RESOLVED;
                        break;
                    case 3:
                        currentStatus = ComplaintStatus.REJECTED;
                        break;
                }
                // Fetch with progress bar
                fetchComplaints(true, false, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        adapter = new AdminComplaintAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == complaintList.size() - 1) {
                    if (!isLastPage && !isLoading) {
                        fetchComplaints(false, true, false);
                    }
                }
            }
        });

        adapter.setListener(complaint -> {
            Intent intent = new Intent(AdminComplaintActivity.this, AdminComplaintDetailActivity.class);
            intent.putExtra("complaintId", complaint.getId());
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
            
            currentKeyword = selected;
            clearSearchFocus();
            fetchComplaints(true, false, true);
        });

        actvSearchKeyword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentKeyword = actvSearchKeyword.getText().toString().trim();
                fetchComplaints(true, false, true);
                return true;
            }
            return false;
        });
    }

    private void setupFilters() {
        // Setup Sort Dropdown
        String[] sortOptions = {"Mới nhất", "Cũ nhất"};
        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sortOptions);
        actvSortTime.setAdapter(sortAdapter);
        actvSortTime.setText(sortOptions[0], false);
        
        actvSortTime.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: currentSortBy = "createdAt"; currentDirection = "desc"; break;
                case 1: currentSortBy = "createdAt"; currentDirection = "asc"; break;
            }
            fetchComplaints(true, false, true);
        });
    }

    private void fetchKeywordAutocomplete(String keyword) {
        adminComplaintService.autocompleteComplaints(keyword).enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    keywordAdapter.clear();
                    keywordAdapter.addAll(response.body());
                    keywordAdapter.getFilter().filter(null, count -> {
                        if (count > 0 && actvSearchKeyword.hasFocus()) {
                            actvSearchKeyword.showDropDown();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("AdminComplaint", "Autocomplete failed: " + t.getMessage());
            }
        });
    }

    private void fetchComplaints(boolean isRefresh, boolean isSilent, boolean shouldScrollToTop) {
        if (isLoading) return;
        isLoading = true;

        if (isRefresh) {
            currentPage = 0;
            isLastPage = false;
            if (!isSilent) {
                if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }

        adminComplaintService.getComplaints(
                currentPage, pageSize, currentStatus, currentKeyword, currentSortBy, currentDirection
        ).enqueue(new Callback<PageResponse<AdminComplaintResponse>>() {
            @Override
            public void onResponse(Call<PageResponse<AdminComplaintResponse>> call, Response<PageResponse<AdminComplaintResponse>> response) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();

                    if (isRefresh) {
                        // Create a new list reference for DiffUtil
                        complaintList = new ArrayList<>();
                        recyclerView.setItemAnimator(null);
                        
                        complaintList.addAll(response.body().getItems());
                        adapter.submitList(new ArrayList<>(complaintList), () -> {
                            if (shouldScrollToTop) {
                                recyclerView.scrollToPosition(0);
                            } else if (isSilent && isRefresh) {
                                // Restore state if it exists for this tab
                                android.os.Parcelable state = tabScrollStates.get(previousTabPosition);
                                if (state != null && recyclerView.getLayoutManager() != null) {
                                    recyclerView.getLayoutManager().onRestoreInstanceState(state);
                                }
                            }
                            
                            recyclerView.post(() -> recyclerView.setItemAnimator(animator));
                        });
                        
                    } else {
                        // Pagination
                        complaintList.addAll(response.body().getItems());
                        adapter.submitList(new ArrayList<>(complaintList));
                    }
                    
                    currentPage++;
                    isLastPage = currentPage >= response.body().getTotalPages();
                } else {
                    Toast.makeText(AdminComplaintActivity.this, "Tải khiếu nại thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PageResponse<AdminComplaintResponse>> call, Throwable t) {
                isLoading = false;
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(AdminComplaintActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
    public boolean dispatchTouchEvent(android.view.MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
            android.view.View v = getCurrentFocus();
            if (v instanceof android.widget.EditText) {
                android.graphics.Rect outRect = new android.graphics.Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                    android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
