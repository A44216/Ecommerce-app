package com.example.ecommerceapp.ui.fragment.admin.user;

import android.content.Intent;
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
import com.example.ecommerceapp.ui.activity.home.admin.user.AdminUserDetailActivity;
import com.example.ecommerceapp.ui.adapter.admin.user.AdminUserAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminUserViewModel;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class AdminUserListFragment extends Fragment {

    private static final String ARG_POSITION = "arg_position";
    private int position;

    private AdminUserViewModel viewModel;
    private AdminUserAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvUsers;
    private ProgressBar progressBar;
    private TextView tvEmpty;

    private final ActivityResultLauncher<Intent> detailLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        int userId = data.getIntExtra("userId", -1);
                        String newStatusStr = data.getStringExtra("newStatus");
                        if (userId != -1 && newStatusStr != null) {
                            com.example.ecommerceapp.data.enums.UserStatus newStatus = 
                                com.example.ecommerceapp.data.enums.UserStatus.valueOf(newStatusStr);
                            viewModel.updateUserStatusLocally(userId, newStatus);
                        }
                    }
                }
            }
    );

    public static AdminUserListFragment newInstance(int position) {
        AdminUserListFragment fragment = new AdminUserListFragment();
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
        return inflater.inflate(R.layout.fragment_admin_user_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupRecyclerView();
        initViewModel();
        setupObservers();

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (getParentFragment() instanceof AdminUserFragment) {
                ((AdminUserFragment) getParentFragment()).fetchData(false);
            }
        });
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        rvUsers = view.findViewById(R.id.rvUsers);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new AdminUserAdapter();
        adapter.setOnItemClickListener(user -> {
            Intent intent = new Intent(requireContext(), AdminUserDetailActivity.class);
            intent.putExtra("userId", user.getId());
            detailLauncher.launch(intent);
        });
        rvUsers.setAdapter(adapter);

        rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) { // scrolling down
                    androidx.recyclerview.widget.LinearLayoutManager layoutManager = (androidx.recyclerview.widget.LinearLayoutManager) rvUsers.getLayoutManager();
                    if (layoutManager != null) {
                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount - 2) {
                            com.example.ecommerceapp.data.enums.Role role = (position == 0) ? 
                                    com.example.ecommerceapp.data.enums.Role.CUSTOMER : 
                                    com.example.ecommerceapp.data.enums.Role.SELLER;
                            viewModel.fetchUsers(role, true, true);
                        }
                    }
                }
            }
        });
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(AdminUserViewModel.class);
    }

    private void setupObservers() {
        if (position == 0) {
            // Customer
            viewModel.getCustomersLiveData().observe(getViewLifecycleOwner(), users -> {
                swipeRefreshLayout.setRefreshing(false);
                if (users != null && !users.isEmpty()) {
                    adapter.submitList(users);
                    tvEmpty.setVisibility(View.GONE);
                    rvUsers.setVisibility(View.VISIBLE);
                } else {
                    adapter.submitList(null);
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvUsers.setVisibility(View.GONE);
                }
            });
        } else {
            // Seller
            viewModel.getSellersLiveData().observe(getViewLifecycleOwner(), users -> {
                swipeRefreshLayout.setRefreshing(false);
                if (users != null && !users.isEmpty()) {
                    adapter.submitList(users);
                    tvEmpty.setVisibility(View.GONE);
                    rvUsers.setVisibility(View.VISIBLE);
                } else {
                    adapter.submitList(null);
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvUsers.setVisibility(View.GONE);
                }
            });
        }

        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading && !swipeRefreshLayout.isRefreshing()) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
