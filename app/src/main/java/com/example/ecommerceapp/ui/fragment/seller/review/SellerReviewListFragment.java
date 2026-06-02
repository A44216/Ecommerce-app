package com.example.ecommerceapp.ui.fragment.seller.review;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.review.SellerReviewResponse;
import com.example.ecommerceapp.data.repository.seller.SellerReviewRepository;
import com.example.ecommerceapp.ui.adapter.seller.review.SellerReviewAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerReviewViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerReviewViewModelFactory;

import java.util.ArrayList;

public class SellerReviewListFragment extends Fragment {

    private SellerReviewViewModel viewModel;
    private SellerReviewAdapter adapter;
    private LinearLayoutManager layoutManager;
    private android.widget.ProgressBar progressBar;

    private boolean isLoadingMore = false;
    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLastPage = false;

    private int productId;
    private boolean isReplied;

    public static SellerReviewListFragment newInstance(int productId, boolean isReplied) {
        SellerReviewListFragment fragment = new SellerReviewListFragment();
        Bundle args = new Bundle();
        args.putInt("productId", productId);
        args.putBoolean("isReplied", isReplied);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_review_list, container, false);

        RecyclerView rv = view.findViewById(R.id.rvReviews);
        progressBar = view.findViewById(R.id.progressBar);

        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        adapter = new SellerReviewAdapter();
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {

                int total = layoutManager.getItemCount();
                int last = layoutManager.findLastVisibleItemPosition();

                if (!isLoadingMore && !isLastPage && last >= total - 2) {
                    loadReviews();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            productId = getArguments().getInt("productId");
            isReplied = getArguments().getBoolean("isReplied", false);
        }

        SellerReviewRepository repository = new SellerReviewRepository(TokenManager.getInstance(requireContext()));
        viewModel = new ViewModelProvider(requireActivity(), new SellerReviewViewModelFactory(repository)).get(SellerReviewViewModel.class);

        adapter.setOnItemClickListener(new SellerReviewAdapter.OnItemClickListener() {

            @Override
            public void onClick(SellerReviewResponse item) {
                // optional: mở detail
            }

            @Override
            public void onReplyClick(SellerReviewResponse item, String replyContent) {

                if (replyContent.trim().isEmpty()) {
                    // show error UI (Toast / TextInputLayout)
                    return;
                }

                viewModel.replyReview(
                        item.getReviewId(),
                        productId,
                        isReplied,
                        replyContent
                );
            }

            @Override
            public void onToggleClick(SellerReviewResponse item, int position) {
                // expand/collapse
            }
        });

        observeData();

    }

    private void observeData() {
        viewModel.getLoadingLiveData(productId, isReplied).observe(getViewLifecycleOwner(), isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading != null && isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getReviewsLiveData(productId, isReplied)
                .observe(getViewLifecycleOwner(), pageResponse -> {

                    isLoadingMore = false;

                    if (pageResponse != null) {

                        if (currentPage == 0) {
                            adapter.setData(pageResponse.getItems());
                        } else {
                            adapter.addData(pageResponse.getItems());
                        }

                        isLastPage = pageResponse.getItems() == null
                                || pageResponse.getItems().isEmpty()
                                || pageResponse.getItems().size() < PAGE_SIZE;
                    }
                });
    }

    private void loadReviews() {

        if (isLoadingMore || isLastPage) return;

        isLoadingMore = true;

        viewModel.loadReviews(productId, isReplied, currentPage, PAGE_SIZE);

        currentPage++;
    }

    public void resetAndReload() {
        currentPage = 0;
        isLastPage = false;
        isLoadingMore = false;

        adapter.setData(new ArrayList<>());

        loadReviews();
    }
}