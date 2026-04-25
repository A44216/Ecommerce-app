package com.example.ecommerceapp.ui.activity.home.user.search;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserProductService;
import com.example.ecommerceapp.data.repository.UserProductRepository;
import com.example.ecommerceapp.ui.adapter.user.SearchHistoryAdapter;
import com.example.ecommerceapp.ui.adapter.user.UserProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.UserSearchViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.UserSearchViewModelFactory;
import com.example.ecommerceapp.utils.SearchHistoryManager;

public class UserSearchActivity extends AppCompatActivity {

    private EditText etSearchInput;
    private RecyclerView rvSearchResults;
    private RecyclerView rvSuggestions;
    private LinearLayout layoutEmptyState;
    private ProgressBar progressBar;
    private android.widget.TextView tvTrendingTitle;

    private UserProductAdapter productAdapter;
    private com.example.ecommerceapp.ui.adapter.user.UserSuggestionAdapter suggestionAdapter;
    private SearchHistoryAdapter historyAdapter;
    private UserSearchViewModel viewModel;
    private SearchHistoryManager historyManager;

    private LinearLayout layoutSearchHistory;
    private RecyclerView rvSearchHistory;
    private android.widget.TextView tvClearHistory;
    
    // Debounce cho tìm kiếm
    private final android.os.Handler searchHandler = new android.os.Handler(android.os.Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        etSearchInput = findViewById(R.id.etSearchInput);
        rvSearchResults = findViewById(R.id.rvSearchResults);
        rvSuggestions = findViewById(R.id.rvSuggestions);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        progressBar = findViewById(R.id.progressBar);
        tvTrendingTitle = findViewById(R.id.tvTrendingTitle);
        ImageView ivBack = findViewById(R.id.ivBack);

        layoutSearchHistory = findViewById(R.id.layoutSearchHistory);
        rvSearchHistory = findViewById(R.id.rvSearchHistory);
        tvClearHistory = findViewById(R.id.tvClearHistory);

        historyManager = new SearchHistoryManager(this);

        // 1. Cài đặt nút Quay lại
        ivBack.setOnClickListener(v -> finish());

        // 2. Setup RecyclerView
        rvSearchResults.setLayoutManager(new GridLayoutManager(this, 2));
        productAdapter = new UserProductAdapter(this);
        rvSearchResults.setAdapter(productAdapter);

        rvSuggestions.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        suggestionAdapter = new com.example.ecommerceapp.ui.adapter.user.UserSuggestionAdapter(suggestion -> {
            etSearchInput.setText(suggestion);
            etSearchInput.setSelection(suggestion.length());
            rvSuggestions.setVisibility(View.GONE);
            hideKeyboard();
            
            // Lưu vào lịch sử tìm kiếm
            historyManager.addSearchKeyword(suggestion);
            
            viewModel.searchProducts(suggestion, true);
        });
        rvSuggestions.setAdapter(suggestionAdapter);

        rvSearchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@androidx.annotation.NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            String keyword = etSearchInput.getText().toString().trim();
                            if (!keyword.isEmpty()) {
                                viewModel.searchProducts(keyword, false);
                            }
                        }
                    }
                }
            }
        });

        // Setup RecyclerView Lịch sử
        rvSearchHistory.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        historyAdapter = new SearchHistoryAdapter(new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onKeywordClick(String keyword) {
                etSearchInput.setText(keyword);
                etSearchInput.setSelection(keyword.length());
                layoutSearchHistory.setVisibility(View.GONE);
                hideKeyboard();
                viewModel.searchProducts(keyword, true);
                historyManager.addSearchKeyword(keyword); // Đưa lên đầu
            }

            @Override
            public void onDeleteClick(String keyword) {
                historyManager.removeSearchKeyword(keyword);
                showSearchHistory();
            }
        });
        rvSearchHistory.setAdapter(historyAdapter);

        tvClearHistory.setOnClickListener(v -> {
            historyManager.clearHistory();
            layoutSearchHistory.setVisibility(View.GONE);
        });

        // 3. Khởi tạo ViewModel
        UserProductService productService = ApiClient.getUserProductService();
        UserProductRepository productRepository = new UserProductRepository(productService);
        UserSearchViewModelFactory factory = new UserSearchViewModelFactory(productRepository);
        viewModel = new ViewModelProvider(this, factory).get(UserSearchViewModel.class);

        // 4. Lắng nghe dữ liệu
        viewModel.getSearchResults().observe(this, products -> {
            tvTrendingTitle.setVisibility(View.GONE);
            if (products != null && !products.isEmpty()) {
                productAdapter.updateData(products);
                rvSearchResults.setVisibility(View.VISIBLE);
                layoutEmptyState.setVisibility(View.GONE);
            } else {
                rvSearchResults.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getTrendingProducts().observe(this, products -> {
            if (etSearchInput.getText().toString().trim().isEmpty()) {
                if (products != null && !products.isEmpty()) {
                    tvTrendingTitle.setVisibility(View.VISIBLE);
                    productAdapter.updateData(products);
                    rvSearchResults.setVisibility(View.VISIBLE);
                    layoutEmptyState.setVisibility(View.GONE);
                }
            }
        });

        viewModel.getSuggestions().observe(this, suggestions -> {
            if (suggestions != null && !suggestions.isEmpty() && !etSearchInput.getText().toString().trim().isEmpty()) {
                suggestionAdapter.updateData(suggestions);
                rvSuggestions.setVisibility(View.VISIBLE);
            } else {
                rvSuggestions.setVisibility(View.GONE);
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                rvSearchResults.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.GONE);
                tvTrendingTitle.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // 5. Bắt sự kiện Gõ phím
        etSearchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearchInput.getText().toString().trim();
                rvSuggestions.setVisibility(View.GONE);
                if (!keyword.isEmpty()) {
                    historyManager.addSearchKeyword(keyword);
                    viewModel.searchProducts(keyword, true);
                    layoutSearchHistory.setVisibility(View.GONE);
                    hideKeyboard();
                } else {
                    Toast.makeText(this, "Vui lòng nhập từ khóa", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });

        etSearchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (keyword.isEmpty()) {
                    rvSuggestions.setVisibility(View.GONE);
                    showSearchHistory();
                    viewModel.fetchTrendingProducts(); // Lấy lại trending nếu xóa trắng
                } else {
                    layoutSearchHistory.setVisibility(View.GONE);
                    
                    // --- TRIỂN KHAI DEBOUNCE (300ms) ---
                    if (searchRunnable != null) {
                        searchHandler.removeCallbacks(searchRunnable);
                    }
                    searchRunnable = () -> viewModel.fetchSuggestions(keyword);
                    searchHandler.postDelayed(searchRunnable, 300);
                }
            }
            @Override public void afterTextChanged(android.text.Editable s) {}
        });

        // 6. Tự động bật bàn phím và gọi Trending ban đầu
        etSearchInput.requestFocus();
        showKeyboard();
        showSearchHistory();
        viewModel.fetchTrendingProducts();
    }

    private void showSearchHistory() {
        java.util.List<String> history = historyManager.getSearchHistory();
        if (etSearchInput.getText().toString().trim().isEmpty() && !history.isEmpty()) {
            historyAdapter.setData(history);
            layoutSearchHistory.setVisibility(View.VISIBLE);
            rvSuggestions.setVisibility(View.GONE);
        } else {
            layoutSearchHistory.setVisibility(View.GONE);
        }
    }

    private void showKeyboard() {
        etSearchInput.postDelayed(() -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInput(etSearchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        }, 100);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(etSearchInput.getWindowToken(), 0);
        }
    }
}
