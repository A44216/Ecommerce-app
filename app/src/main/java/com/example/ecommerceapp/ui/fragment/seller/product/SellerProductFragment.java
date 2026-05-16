package com.example.ecommerceapp.ui.fragment.seller.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.text.TextWatcher;
import android.text.Editable;
import java.util.ArrayList;
import java.util.List;

import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.SellerProductRepository;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerProductViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerProductViewModelFactory;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerAddAndEditProductActivity;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerProductPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

public class SellerProductFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MaterialAutoCompleteTextView etSearch;
    private SellerProductViewModel viewModel;

    private SellerProductPagerAdapter adapter;

    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private static final long SEARCH_DEBOUNCE_MS = 400;

    private ArrayAdapter<String> searchAdapter;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (getActivity() == null) return;

                        if (result.getResultCode() == getActivity().RESULT_OK) {

                            if (adapter != null) {

                                String currentKeyword = etSearch.getText() != null
                                        ? etSearch.getText().toString().trim()
                                        : "";

                                adapter.setKeywordToAll(currentKeyword);
                                adapter.reloadAll();
                            }
                        }
                    }
            );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_seller_product, container, false);

        viewPager = view.findViewById(R.id.listProduct);
        tabLayout = view.findViewById(R.id.tabLayout);
        etSearch = view.findViewById(R.id.etSearch);

        initViewModel();
        setupViewPager();
        setupSearch();
        setupFab(view);

        view.setOnTouchListener((v, event) -> {
            clearSearchFocus();
            return false;
        });

        return view;
    }

    private void initViewModel() {
        SellerProductRepository repository = new SellerProductRepository(ApiClient.getProductService(TokenManager.getInstance(requireContext())));
        viewModel = new ViewModelProvider(this, new SellerProductViewModelFactory(repository))
                .get(SellerProductViewModel.class);
    }

    private void clearSearchFocus() {
        if (etSearch != null && etSearch.hasFocus()) {
            etSearch.clearFocus();
        }
        View currentFocus = requireActivity().getCurrentFocus();
        if (currentFocus != null) {
            currentFocus.clearFocus();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }
    }

    private void setupViewPager() {

        adapter = new SellerProductPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        // FIX QUAN TRỌNG: tránh fragment bị recreate sai state
        viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Chờ duyệt");
            else if (position == 1) tab.setText("Đã duyệt");
            else if (position == 2) tab.setText("Từ chối");
            else tab.setText("Đã xoá");
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager2.SCROLL_STATE_DRAGGING) {
                    clearSearchFocus();
                }
            }
        });
    }

    private void setupSearch() {
        searchAdapter = new ArrayAdapter<String>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>()
        ) {
            @NonNull
            @Override
            public android.widget.Filter getFilter() {
                return new android.widget.Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        FilterResults results = new FilterResults();
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < getCount(); i++) {
                            list.add(getItem(i));
                        }
                        results.values = list;
                        results.count = list.size();
                        return results;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        if (results != null && results.count > 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyDataSetInvalidated();
                        }
                    }
                };
            }
        };
        etSearch.setAdapter(searchAdapter);

        viewModel.getAutocompleteResult().observe(getViewLifecycleOwner(), suggestions -> {
            if (suggestions != null && !suggestions.isEmpty()) {
                searchAdapter.clear();
                searchAdapter.addAll(suggestions);
                searchAdapter.notifyDataSetChanged();
                if (etSearch.hasFocus()) {
                    etSearch.showDropDown();
                }
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchHandler.removeCallbacks(searchRunnable);
                searchRunnable = () -> {
                    String keyword = s.toString().trim();
                    if (!keyword.isEmpty()) {
                        viewModel.autocompleteProducts(keyword);
                    }
                };
                searchHandler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_MS);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        etSearch.setOnItemClickListener((parent, v, position, id) -> {
            String selected = (String) parent.getItemAtPosition(position);
            etSearch.setText(selected, false);
            clearSearchFocus();
            triggerSearch();
        });

        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && searchAdapter != null) {
                searchAdapter.clear();
                searchAdapter.notifyDataSetChanged();
            }
        });

        etSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEARCH
                    || actionId == EditorInfo.IME_ACTION_DONE) {

                triggerSearch();
                return true;
            }

            return false;
        });
    }

    private void triggerSearch() {

        String keyword = etSearch.getText() != null
                ? etSearch.getText().toString().trim()
                : "";
                
        if (keyword.contains(" - ")) {
            keyword = keyword.split(" - ")[0].trim();
        }

        if (adapter == null) return;

        adapter.setKeywordToAll(keyword);
    }

    private void setupFab(View view) {

        FloatingActionButton fab = view.findViewById(R.id.fabAddNew);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), SellerAddAndEditProductActivity.class);
            launcher.launch(intent);
        });
    }
}