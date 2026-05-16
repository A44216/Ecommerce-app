package com.example.ecommerceapp.ui.fragment.seller.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.SellerProductRepository;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerAddAndEditProductActivity;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerProductViewModelFactory;

public class SellerProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshProducts;
    private android.widget.ProgressBar progressBar;
    private SellerProductAdapter adapter;
    private SellerProductViewModel viewModel;

    private boolean isLoadingMore = false;

    private String status = "";
    private String keyword = "";
    private Boolean isDeleted = false;

    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK) {
                            reload();
                        }
                    }
            );

    public static SellerProductListFragment newInstance(String status, Boolean isDeleted) {
        SellerProductListFragment fragment = new SellerProductListFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        args.putBoolean("isDeleted", isDeleted);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            status = getArguments().getString("status", "");
            isDeleted = getArguments().getBoolean("isDeleted", false);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_product_list, container, false);

        recyclerView = view.findViewById(R.id.listProduct);
        swipeRefreshProducts = view.findViewById(R.id.swipeRefreshProducts);
        progressBar = view.findViewById(R.id.progressBar);

        swipeRefreshProducts.setOnRefreshListener(() -> {
            if (viewModel != null) {
                reload();
            } else {
                swipeRefreshProducts.setRefreshing(false);
            }
        });

        initRecycler();
        setupViewModel();
        setupListeners();

        return view;
    }

    private void initRecycler() {

        adapter = new SellerProductAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView rv, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    android.view.View currentFocus = requireActivity().getCurrentFocus();
                    if (currentFocus != null) {
                        currentFocus.clearFocus();
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                        if (imm != null) imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {

                LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
                if (lm == null) return;

                int total = lm.getItemCount();
                int lastVisibleItem = lm.findLastVisibleItemPosition();

                if (!isLoadingMore && lastVisibleItem >= total - 2) {
                    isLoadingMore = true;
                    viewModel.fetchProducts(true);
                }
            }
        });
    }

    private void setupListeners() {

        adapter.setListener(new SellerProductAdapter.OnProductActionListener() {

            @Override
            public void onClick(SellerProductResponse product) {
                Intent intent = new Intent(requireContext(), SellerProductDetailActivity.class);
                intent.putExtra("productId", product.getId());
                startActivity(intent);
            }

            @Override
            public void onEdit(SellerProductResponse product) {
                Intent intent = new Intent(requireContext(), SellerAddAndEditProductActivity.class);
                intent.putExtra("productId", product.getId());
                editLauncher.launch(intent);
            }

            @Override
            public void onDelete(SellerProductResponse product) {

                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Xóa sản phẩm")
                        .setMessage("Bạn có chắc muốn xóa \"" + product.getName() + "\"?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            viewModel.deleteProduct(product.getId());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void onRestore(SellerProductResponse product) {

                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Khôi phục sản phẩm")
                        .setMessage("Bạn có chắc muốn khôi phục \"" + product.getName() + "\"?")
                        .setPositiveButton("Khôi phục", (dialog, which) -> {
                            viewModel.restoreProduct(product.getId());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }

            @Override
            public void onResubmit(SellerProductResponse product) {

                new android.app.AlertDialog.Builder(requireContext())
                        .setTitle("Gửi duyệt lại")
                        .setMessage("Bạn có chắc muốn gửi lại \"" + product.getName() + "\" để duyệt?")
                        .setPositiveButton("Gửi", (dialog, which) -> {
                            viewModel.submitProduct(product.getId());
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }

        });
    }

    private void setupViewModel() {

        SellerProductService api =
                ApiClient.getProductService(TokenManager.getInstance(requireContext()));

        SellerProductRepository repo = new SellerProductRepository(api);

        SellerProductViewModelFactory factory =
                new SellerProductViewModelFactory(repo);

        viewModel = new ViewModelProvider(this, factory)
                .get(SellerProductViewModel.class);

        viewModel.setStatus(status);
        viewModel.setIsDeleted(isDeleted);
        viewModel.setKeyword(keyword);

        observeData();
        if (viewModel.getProducts().getValue() == null) {
            if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        }
        viewModel.fetchProducts(false);
    }

    private void observeData() {

        viewModel.getProducts().observe(getViewLifecycleOwner(), items -> {
            boolean wasLoadingMore = isLoadingMore;
            isLoadingMore = false;
            if (swipeRefreshProducts != null) {
                swipeRefreshProducts.setRefreshing(false);
            }
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            if (items != null) {
                RecyclerView.ItemAnimator animator = recyclerView != null ? recyclerView.getItemAnimator() : null;
                if (!wasLoadingMore && recyclerView != null) {
                    recyclerView.setItemAnimator(null);
                }
                
                adapter.submitList(items, () -> {
                    if (!wasLoadingMore && recyclerView != null) {
                        recyclerView.scrollToPosition(0);
                        recyclerView.post(() -> recyclerView.setItemAnimator(animator));
                    }
                });
            }
        });
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
        if (viewModel == null) return;

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        viewModel.setKeyword(keyword);
        viewModel.fetchProducts(false);
    }

    public void reload() {
        if (viewModel == null) return;
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        viewModel.fetchProducts(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tải lại dữ liệu ngầm khi quay lại từ màn hình chi tiết
        if (viewModel != null) {
            viewModel.fetchProducts(false);
        }
    }
}