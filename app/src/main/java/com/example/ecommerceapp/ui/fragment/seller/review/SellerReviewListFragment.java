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
import com.example.ecommerceapp.ui.adapter.seller.review.SellerReviewAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerReviewViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerReviewViewModelFactory;

public class SellerReviewListFragment extends Fragment {

    private SellerReviewViewModel viewModel;
    private SellerReviewAdapter adapter;
    private LinearLayoutManager layoutManager;

    private boolean isLoadingMore = false;
    private int currentPage = 0;
    private final int PAGE_SIZE = 10;
    private boolean isLastPage = false;

    private Integer productId;

    public static SellerReviewListFragment newInstance(Integer productId) {
        SellerReviewListFragment fragment = new SellerReviewListFragment();
        Bundle args = new Bundle();
        args.putInt("productId", productId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_seller_review_list, container, false);

        RecyclerView rv = view.findViewById(R.id.rvReviews);
        layoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(layoutManager);

        adapter = new SellerReviewAdapter();
        rv.setAdapter(adapter);

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = layoutManager.getItemCount();
                int lastVisible = layoutManager.findLastVisibleItemPosition();

                if (!isLoadingMore && !isLastPage && lastVisible >= totalItemCount - 2) {
                    isLoadingMore = true;
                    loadReviews();
                }
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        productId = getArguments().getInt("productId");
        viewModel = new ViewModelProvider(this,
                new SellerReviewViewModelFactory(TokenManager.getInstance(requireContext())))
                .get(SellerReviewViewModel.class);

        loadReviews();
    }

    private void loadReviews() {
        viewModel.getReviews(productId, null, currentPage, PAGE_SIZE)
                .observe(getViewLifecycleOwner(), pageResponse -> {
                    isLoadingMore = false;
                    if (pageResponse != null) {
                        if (currentPage == 0) adapter.setData(pageResponse.getContent());
                        else adapter.addData(pageResponse.getContent());

                        isLastPage = pageResponse.isLast();
                        currentPage = pageResponse.getNumber() + 1;
                    }
                });
    }
}
