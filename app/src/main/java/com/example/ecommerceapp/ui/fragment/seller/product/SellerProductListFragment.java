package com.example.ecommerceapp.ui.fragment.seller.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerProductService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.seller.product.SellerProductResponse;
import com.example.ecommerceapp.data.repository.seller.product.SellerProductRepository;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerAddAndEditProductActivity;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerProductDetailActivity;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerProductAdapter;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerProductViewModelFactory;

import java.util.ArrayList;

public class SellerProductListFragment extends Fragment {

    private RecyclerView recyclerView;
    private SellerProductAdapter adapter;
    private SellerProductViewModel viewModel;

    private int currentPage = 0;
    private final int pageSize = 10;

    private boolean isLoading = false;
    private boolean isLastPage = false;

    private String status = "";
    private String keyword = "";

    private final ActivityResultLauncher<Intent> editLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == getActivity().RESULT_OK) {
                            reload();
                        }
                    }
            );

    public static SellerProductListFragment newInstance(String status) {
        SellerProductListFragment fragment = new SellerProductListFragment();
        Bundle args = new Bundle();
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            status = getArguments().getString("status", "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_product_list, container, false);

        recyclerView = view.findViewById(R.id.listProduct);

        initRecycler();
        setupViewModel();
        setupListeners();

        return view;
    }

    private void initRecycler() {

        adapter = new SellerProductAdapter();

        adapter.setCurrentStatus(status);

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
                            adapter.removeItem(product);

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

                            viewModel.getRestoreResult().observe(getViewLifecycleOwner(), success -> {
                                if (success != null && success) {
                                    reload();
                                }
                            });

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

                            viewModel.getSubmitResult().observe(getViewLifecycleOwner(), success -> {
                                if (success != null && success) {
                                    reload();
                                }
                            });

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
        viewModel.setKeyword(keyword);

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

        this.keyword = keyword;

        if (viewModel == null) return;

        viewModel.setKeyword(keyword);

        currentPage = 0;
        isLoading = false;
        isLastPage = false;

        if (adapter != null) {
            adapter.setData(new ArrayList<>());
        }

        loadData();
    }

    private void loadData() {
        isLoading = true;
        viewModel.fetchProducts(currentPage, pageSize);
    }

    private void loadMore() {

        if (isLoading || isLastPage) return;

        isLoading = true;
        currentPage++;

        viewModel.fetchProducts(currentPage, pageSize);
    }

    public void reload() {

        if (adapter == null || viewModel == null) return;

        currentPage = 0;
        isLoading = false;
        isLastPage = false;

        adapter.setData(new ArrayList<>());
        loadData();
    }
}