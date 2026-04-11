package com.example.ecommerceapp.ui.fragment.seller.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerProductViewModelFactory;

public class SellerProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SellerProductAdapter adapter;
    private SellerProductViewModel viewModel;

    private int currentPage = 0;
    private final int pageSize = 10;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_product_list, container, false);

        recyclerView = view.findViewById(R.id.listProduct);

        initRecycler();
        setupViewModel();

        return view;
    }

    private void initRecycler() {

        adapter = new SellerProductAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {

                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();

                int visible = lm.getChildCount();
                int total = lm.getItemCount();
                int first = lm.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage && (visible + first) >= total) {
                    loadMore();
                }
            }
        });
    }

    private void setupViewModel() {

        SellerProductService api =
                ApiClient.getProductService(TokenManager.getInstance(requireContext()));

        SellerProductRepository repo = new SellerProductRepository(api);

        SellerProductViewModelFactory factory =
                new SellerProductViewModelFactory(repo);

        viewModel = new ViewModelProvider(requireActivity(), factory)
                .get(SellerProductViewModel.class);

        observeData();
        loadData();
    }

    private void observeData() {

        viewModel.getProducts().observe(getViewLifecycleOwner(), page -> {

            if (page == null || page.getItems() == null) return;

            if (currentPage == 0) {
                adapter.setData(page.getItems());
            } else {
                adapter.addData(page.getItems());
            }

            isLoading = false;
            isLastPage = page.getItems().size() < pageSize;
        });
    }

    public void setKeyword(String keyword) {

        viewModel.setKeyword(keyword);

        currentPage = 0;
        isLoading = false;
        isLastPage = false;

        loadData();
    }

    private void loadData() {

        String keyword = viewModel.getKeyword();

        if (keyword == null || keyword.isEmpty()) {
            viewModel.fetchProducts(currentPage, pageSize);
        } else {
            viewModel.searchProducts(keyword, currentPage, pageSize);
        }
    }

    private void loadMore() {

        if (isLoading || isLastPage) return;

        isLoading = true;
        currentPage++;

        String keyword = viewModel.getKeyword();

        if (keyword == null || keyword.isEmpty()) {
            viewModel.fetchProducts(currentPage, pageSize);
        } else {
            viewModel.searchProducts(keyword, currentPage, pageSize);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }
}