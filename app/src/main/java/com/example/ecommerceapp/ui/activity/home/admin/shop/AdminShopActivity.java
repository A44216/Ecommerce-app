package com.example.ecommerceapp.ui.activity.home.admin.shop;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.shop.AdminShopRepository;
import com.example.ecommerceapp.ui.adapter.admin.shop.AdminShopAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminShopViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminShopViewModelFactory;
import com.google.android.material.textfield.TextInputEditText;

public class AdminShopActivity extends AppCompatActivity {

    private AdminShopViewModel viewModel;
    private AdminShopAdapter adapter;

    private TextInputEditText edtSearch;
    private AutoCompleteTextView actvSortCreated;
    private AutoCompleteTextView actvFilterStatus;
    private RecyclerView rvShops;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ImageView ivBack;

    private ShopStatus currentStatus = null;
    private String currentKeyword = "";
    private String currentSortBy = "createdAt";
    private String currentSortDir = "desc";

    private final ActivityResultLauncher<Intent> detailActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    boolean statusChanged = result.getData().getBooleanExtra("STATUS_CHANGED", false);
                    int shopId = result.getData().getIntExtra("SHOP_ID", -1);
                    String newStatusStr = result.getData().getStringExtra("NEW_STATUS");

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
        setContentView(R.layout.activity_admin_shop);

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
            intent.putExtra("SHOP_ID", shop.getId());
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

        String[] statusOptions = {"Tất cả", "Chờ duyệt", "Đã duyệt", "Bị từ chối", "Bị khóa"};
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
                default:
                    currentStatus = null;
                    break;
            }
            viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
        });
    }

    private void setupListeners() {
        ivBack.setOnClickListener(v -> finish());

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentKeyword = s.toString().trim();
                viewModel.setFilters(currentStatus, currentKeyword, currentSortBy, currentSortDir);
            }
        });
    }

    private void observeViewModel() {
        viewModel.getShops().observe(this, shops -> {
            adapter.submitList(shops);
            tvEmpty.setVisibility(shops.isEmpty() ? View.VISIBLE : View.GONE);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
