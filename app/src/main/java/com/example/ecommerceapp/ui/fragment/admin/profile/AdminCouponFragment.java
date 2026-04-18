package com.example.ecommerceapp.ui.fragment.admin.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCouponService;
import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.profile.AdminCouponRepository;
import com.example.ecommerceapp.ui.adapter.admin.profile.AdminCouponAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCouponViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminCouponViewModelFactory;

public class AdminCouponFragment extends Fragment {

    private static final String ARG_POSITION = "arg_position";

    private AdminCouponViewModel viewModel;
    private AdminCouponAdapter adapter;
    private int position;

    private RecyclerView rvCoupons;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private String currentKeyword = "";
    private boolean isFirstLoad = true;

    public static AdminCouponFragment newInstance(int position) {
        AdminCouponFragment fragment = new AdminCouponFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_coupon, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        initViewModel();
        setupObservers();

        swipeRefreshLayout.setOnRefreshListener(() -> fetchData(false));
    }

    private void initViews(View view) {
        rvCoupons = view.findViewById(R.id.rvCoupons);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new AdminCouponAdapter(requireContext());
        adapter.setOnItemClickListener(new AdminCouponAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(com.example.ecommerceapp.data.model.response.admin.profile.AdminCouponResponse coupon) {
                android.content.Intent intent = new android.content.Intent(requireContext(), com.example.ecommerceapp.ui.activity.home.admin.profile.AdminAddAndEditCouponActivity.class);
                intent.putExtra("couponId", coupon.getId());
                intent.putExtra("isDeleted", position == 3);
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(com.example.ecommerceapp.data.model.response.admin.profile.AdminCouponResponse coupon, View view) {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(requireContext(), view);
                
                if (position == 3) {
                    popup.getMenu().add("Khôi phục");
                } else {
                    popup.getMenu().add("Bật (Active)");
                    popup.getMenu().add("Tắt (Disable)");
                    popup.getMenu().add("Xóa");
                }
                
                popup.setOnMenuItemClickListener(item -> {
                    if (item.getTitle().equals("Bật (Active)")) {
                        viewModel.enableCoupon(coupon.getId());
                    } else if (item.getTitle().equals("Tắt (Disable)")) {
                        viewModel.disableCoupon(coupon.getId());
                    } else if (item.getTitle().equals("Xóa")) {
                        viewModel.deleteCoupon(coupon.getId());
                    } else if (item.getTitle().equals("Khôi phục")) {
                        viewModel.restoreCoupon(coupon.getId());
                    }
                    return true;
                });
                popup.show();
            }
        });
        rvCoupons.setAdapter(adapter);
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        AdminCouponService service = ApiClient.getAdminCouponService(tokenManager);
        AdminCouponRepository repository = new AdminCouponRepository(tokenManager); // hoặc new AdminCouponRepository(service) tuỳ theo code của repo

        viewModel = new ViewModelProvider(this, new AdminCouponViewModelFactory(repository))
                .get(AdminCouponViewModel.class);
    }

    private void setupObservers() {
        viewModel.getCouponsLiveData().observe(getViewLifecycleOwner(), pageResponse -> {
            swipeRefreshLayout.setRefreshing(false);
            if (pageResponse != null && pageResponse.getItems() != null && !pageResponse.getItems().isEmpty()) {
                adapter.submitList(pageResponse.getItems());
                tvEmpty.setVisibility(View.GONE);
                rvCoupons.setVisibility(View.VISIBLE);
            } else {
                adapter.submitList(null);
                tvEmpty.setVisibility(View.VISIBLE);
                rvCoupons.setVisibility(View.GONE);
            }
        });

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading && !swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getActionSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success != null && success) {
                fetchData(true);
            }
        });
    }

    private void fetchData(boolean isSilent) {
        CouponStatus status = getStatusByPosition(position);
        Boolean isDeleted = false;
        if (position == 3) {
            status = null;
            isDeleted = true;
        }
        viewModel.getCoupons(0, 100, status, currentKeyword, isDeleted, isSilent);
    }

    public void search(String keyword) {
        this.currentKeyword = keyword;
        fetchData(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof com.example.ecommerceapp.ui.activity.home.admin.profile.AdminCouponActivity) {
            this.currentKeyword = ((com.example.ecommerceapp.ui.activity.home.admin.profile.AdminCouponActivity) getActivity()).getSearchKeyword();
        }
        fetchData(!isFirstLoad);
        isFirstLoad = false;
    }

    private CouponStatus getStatusByPosition(int position) {
        switch (position) {
            case 0: return CouponStatus.ACTIVE;
            case 1: return CouponStatus.DISABLED;
            case 2: return CouponStatus.EXPIRED;
            default: return null; // Tab "Đã xóa"
        }
    }
}
