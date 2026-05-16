package com.example.ecommerceapp.ui.activity.home.admin.management.user;

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
import com.example.ecommerceapp.data.enums.Role;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.AdminUserRepository;
import com.example.ecommerceapp.ui.adapter.admin.management.user.AdminUserAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminUserViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminUserViewModelFactory;

public class AdminUserActivity extends AppCompatActivity {

    private AdminUserViewModel viewModel;
    private AdminUserAdapter adapter;

    private ImageView ivBack;
    private AutoCompleteTextView edtSearch;
    private AutoCompleteTextView actvFilterRole;
    private AutoCompleteTextView actvFilterStatus;
    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshUsers;
    private TextView tvEmpty;

    private ArrayAdapter<String> autoCompleteAdapter;
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private boolean isLoadMore = false;

    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        int userId = data.getIntExtra("userId", -1);
                        String newStatusStr = data.getStringExtra("newStatus");
                        if (userId != -1 && newStatusStr != null) {
                            UserStatus newStatus = UserStatus.valueOf(newStatusStr);
                            viewModel.updateUserStatusLocally(userId, newStatus);
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupRecyclerView();
        initViewModel();
        setupFilters();
        setupSearch();
        setupObservers();

        if (!viewModel.isDataLoaded()) {
            viewModel.fetchUsers(false, false);
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.ivBack);
        edtSearch = findViewById(R.id.edtSearch);
        actvFilterRole = findViewById(R.id.actvFilterRole);
        actvFilterStatus = findViewById(R.id.actvFilterStatus);
        rvUsers = findViewById(R.id.rvUsers);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshUsers = findViewById(R.id.swipeRefreshUsers);
        tvEmpty = findViewById(R.id.tvEmpty);

        ivBack.setOnClickListener(v -> finish());
        
        swipeRefreshUsers.setOnRefreshListener(() -> {
            viewModel.fetchUsers(false, false);
        });
        
        edtSearch.setSaveEnabled(false);
        actvFilterRole.setSaveEnabled(false);
        actvFilterStatus.setSaveEnabled(false);
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter();
        adapter.setOnItemClickListener(user -> {
            Intent intent = new Intent(this, AdminUserDetailActivity.class);
            intent.putExtra("userId", user.getId());
            detailLauncher.launch(intent);
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvUsers.setLayoutManager(layoutManager);
        rvUsers.setAdapter(adapter);

        rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { // scrolling down
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 2) {
                        isLoadMore = true;
                        viewModel.fetchUsers(true, true);
                    }
                }
            }
        });
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        AdminUserRepository repository = new AdminUserRepository(tokenManager);
        viewModel = new ViewModelProvider(this, new AdminUserViewModelFactory(repository))
                .get(AdminUserViewModel.class);
    }

    private void setupFilters() {
        // Role Filter
        String[] roleOptions = {"Tất cả", "Khách hàng", "Người bán"};
        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roleOptions);
        actvFilterRole.setAdapter(roleAdapter);
        
        // Restore role text
        String roleText = "Tất cả";
        if (viewModel.getCurrentRole() == Role.CUSTOMER) roleText = "Khách hàng";
        else if (viewModel.getCurrentRole() == Role.SELLER) roleText = "Người bán";
        actvFilterRole.setText(roleText, false);

        actvFilterRole.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: viewModel.setCurrentRole(null); break;
                case 1: viewModel.setCurrentRole(Role.CUSTOMER); break;
                case 2: viewModel.setCurrentRole(Role.SELLER); break;
            }
            viewModel.fetchUsers(false, false);
        });

        // Status Filter
        String[] statusOptions = {"Tất cả", "Hoạt động", "Vô hiệu hóa"};
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statusOptions);
        actvFilterStatus.setAdapter(statusAdapter);
        
        // Restore status text
        String statusText = "Tất cả";
        if (viewModel.getCurrentStatus() == UserStatus.ACTIVE) statusText = "Hoạt động";
        else if (viewModel.getCurrentStatus() == UserStatus.BLOCKED) statusText = "Vô hiệu hóa";
        actvFilterStatus.setText(statusText, false);

        actvFilterStatus.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: viewModel.setCurrentStatus(null); break;
                case 1: viewModel.setCurrentStatus(UserStatus.ACTIVE); break;
                case 2: viewModel.setCurrentStatus(UserStatus.BLOCKED); break;
            }
            viewModel.fetchUsers(false, false);
        });
    }

    private void setupSearch() {
        autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        edtSearch.setAdapter(autoCompleteAdapter);
        edtSearch.setText(viewModel.getCurrentKeyword());

        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getAction() == android.view.KeyEvent.ACTION_DOWN && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                viewModel.setCurrentKeyword(v.getText().toString().trim());
                viewModel.fetchUsers(false, false);
                return true;
            }
            return false;
        });

        edtSearch.setOnItemClickListener((parent, view, position, id) -> {
            String selected = autoCompleteAdapter.getItem(position);
            if (selected != null) {
                viewModel.setCurrentKeyword(selected);
                viewModel.fetchUsers(false, false);
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

    private void clearSearchFocus() {
        edtSearch.clearFocus();
        actvFilterRole.clearFocus();
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
        if (actvFilterRole != null) actvFilterRole.dismissDropDown();
        if (actvFilterStatus != null) actvFilterStatus.dismissDropDown();
        if (edtSearch != null) edtSearch.dismissDropDown();

        if (getCurrentFocus() != null) {
            clearSearchFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupObservers() {
        viewModel.getUsersLiveData().observe(this, users -> {
            boolean wasLoadMore = isLoadMore;
            isLoadMore = false;
            
            RecyclerView.ItemAnimator animator = rvUsers != null ? rvUsers.getItemAnimator() : null;
            if (!wasLoadMore && rvUsers != null) {
                rvUsers.setItemAnimator(null);
            }
            
            if (users != null && !users.isEmpty()) {
                adapter.submitList(users, () -> {
                    if (!wasLoadMore && rvUsers != null) {
                        rvUsers.scrollToPosition(0);
                        rvUsers.post(() -> rvUsers.setItemAnimator(animator));
                    }
                });
                tvEmpty.setVisibility(View.GONE);
                rvUsers.setVisibility(View.VISIBLE);
            } else {
                adapter.submitList(null, () -> {
                    if (!wasLoadMore && rvUsers != null) {
                        rvUsers.post(() -> rvUsers.setItemAnimator(animator));
                    }
                });
                tvEmpty.setVisibility(View.VISIBLE);
                rvUsers.setVisibility(View.GONE);
            }
        });

        viewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                if (swipeRefreshUsers != null && swipeRefreshUsers.isRefreshing()) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                if (swipeRefreshUsers != null) {
                    swipeRefreshUsers.setRefreshing(false);
                }
            }
        });

        viewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
