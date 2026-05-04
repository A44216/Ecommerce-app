package com.example.ecommerceapp.ui.activity.home.user.suggestion;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.ui.SuggestionDisplayItem;
import com.example.ecommerceapp.data.repository.ProductEvaluationRepository;
import com.example.ecommerceapp.data.repository.RecommendationsRepository;
import com.example.ecommerceapp.ui.adapter.user.SuggestionListAdapter;
import com.example.ecommerceapp.ui.view.FuzzyTriangleView;
import com.example.ecommerceapp.ui.viewmodel.factory.SuggestionViewModelFactory;
import com.example.ecommerceapp.ui.viewmodel.user.SuggestionViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.ChipGroup;

public class SuggestionCenterActivity extends AppCompatActivity {

    private SuggestionViewModel viewModel;
    private SuggestionListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private SuggestionViewModel.SuggestionType currentType = SuggestionViewModel.SuggestionType.PERSONALIZED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion_center);

        initViews();
        initViewModel();
        observeViewModel();

        // Load default data
        viewModel.fetchSuggestions(currentType);
    }

    private void initViews() {
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        RecyclerView rvSuggestions = findViewById(R.id.rvSuggestions);

        rvSuggestions.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new SuggestionListAdapter(this::showExplanationBottomSheet);
        rvSuggestions.setAdapter(adapter);

        ChipGroup chipGroup = findViewById(R.id.chipGroupFilters);
        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_personalized) {
                currentType = SuggestionViewModel.SuggestionType.PERSONALIZED;
            } else if (checkedId == R.id.chip_trending) {
                currentType = SuggestionViewModel.SuggestionType.TRENDING;
            } else if (checkedId == R.id.chip_fuzzy) {
                currentType = SuggestionViewModel.SuggestionType.FUZZY;
            }
            viewModel.fetchSuggestions(currentType);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.fetchSuggestions(currentType));
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        RecommendationsRepository recRepo = new RecommendationsRepository(ApiClient.getRecommendationService(tokenManager));
        ProductEvaluationRepository evalRepo = new ProductEvaluationRepository(ApiClient.getProductEvaluationService(tokenManager));

        SuggestionViewModelFactory factory = new SuggestionViewModelFactory(recRepo, evalRepo);
        viewModel = new ViewModelProvider(this, factory).get(SuggestionViewModel.class);
    }

    private void observeViewModel() {
        viewModel.getSuggestionList().observe(this, items -> {
            adapter.submitList(items);
            swipeRefreshLayout.setRefreshing(false);
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading && !swipeRefreshLayout.isRefreshing() ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showExplanationBottomSheet(SuggestionDisplayItem item) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View view = getLayoutInflater().inflate(R.layout.layout_fuzzy_explanation_bottom_sheet, null);

        TextView tvFullReason = view.findViewById(R.id.tvFullReason);
        FuzzyTriangleView triangleView = view.findViewById(R.id.fuzzyTriangleView);
        View btnClose = view.findViewById(R.id.btnClose);

        tvFullReason.setText(item.getReason());
        
        // Cập nhật biểu đồ tam giác
        triangleView.setScores(
                item.getRatingScore().floatValue(),
                item.getSoldScore().floatValue(),
                item.getPriceScore().floatValue()
        );

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}
