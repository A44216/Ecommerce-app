package com.example.ecommerceapp.ui.fragment.admin.user;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.UserStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.user.AdminUserRepository;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminUserViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminUserViewModelFactory;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

public class AdminUserFragment extends Fragment {

    private AdminUserViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private TextInputEditText edtSearch;
    private AutoCompleteTextView actvFilter;

    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    public static AdminUserFragment newInstance() {
        return new AdminUserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        initViews(view);
        setupFilterDropdown();
        setupSearch();
        restoreState();
        setupViewPager();
        
        if (!viewModel.isDataLoaded()) {
            fetchData(false);
        }
    }

    private void initViewModel() {
        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        AdminUserRepository repository = new AdminUserRepository(tokenManager);
        viewModel = new ViewModelProvider(requireActivity(), new AdminUserViewModelFactory(repository))
                .get(AdminUserViewModel.class);
    }

    private void initViews(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager);
        edtSearch = view.findViewById(R.id.edtSearch);
        actvFilter = view.findViewById(R.id.actvFilter);
        
        edtSearch.setSaveEnabled(false);
        actvFilter.setSaveEnabled(false);
    }

    private void restoreState() {
        edtSearch.setText(viewModel.getCurrentKeyword());
        
        String statusText = "Tất cả";
        if (viewModel.getCurrentStatus() == UserStatus.ACTIVE) {
            statusText = "Hoạt động";
        } else if (viewModel.getCurrentStatus() == UserStatus.BLOCKED) {
            statusText = "Vô hiệu hóa";
        }
        actvFilter.setText(statusText, false);
    }

    private void setupFilterDropdown() {
        String[] statusOptions = {"Tất cả", "Hoạt động", "Vô hiệu hóa"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, statusOptions);
        actvFilter.setAdapter(adapter);

        actvFilter.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0: viewModel.setCurrentStatus(null); break;
                case 1: viewModel.setCurrentStatus(UserStatus.ACTIVE); break;
                case 2: viewModel.setCurrentStatus(UserStatus.BLOCKED); break;
            }
            fetchData(false);
        });
    }

    private void setupSearch() {
        edtSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getAction() == android.view.KeyEvent.ACTION_DOWN && event.getKeyCode() == android.view.KeyEvent.KEYCODE_ENTER)) {
                viewModel.setCurrentKeyword(v.getText().toString().trim());
                fetchData(false);
                
                android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
            }
            return false;
        });
    }

    private void setupViewPager() {
        AdminUserPagerAdapter adapter = new AdminUserPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("KHÁCH HÀNG");
            } else {
                tab.setText("NGƯỜI BÁN");
            }
        }).attach();
    }

    public void fetchData(boolean isSilent) {
        viewModel.fetchUsers(com.example.ecommerceapp.data.enums.Role.CUSTOMER, false, isSilent);
        viewModel.fetchUsers(com.example.ecommerceapp.data.enums.Role.SELLER, false, isSilent);
    }

    private static class AdminUserPagerAdapter extends FragmentStateAdapter {
        public AdminUserPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return AdminUserListFragment.newInstance(position);
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
}