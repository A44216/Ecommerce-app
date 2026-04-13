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
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.activity.home.seller.product.SellerAddAndEditProductActivity;
import com.example.ecommerceapp.ui.adapter.seller.product.SellerProductPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;

public class SellerProductFragment extends Fragment {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TextInputEditText etSearch;

    private SellerProductPagerAdapter adapter;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (getActivity() == null) return;

                        if (result.getResultCode() == getActivity().RESULT_OK) {

                            // chỉ reload tab 0 (PENDING)
                            if (adapter != null) {
                                adapter.setKeywordToAll(""); // giữ search đồng bộ (nếu có)
                            }

                            // gọi reload tab đầu tiên thông qua FragmentManager
                            Fragment fragment = getChildFragmentManager()
                                    .findFragmentByTag("f0");

                            if (fragment instanceof SellerProductListFragment) {
                                ((SellerProductListFragment) fragment).reload();
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

        setupViewPager();
        setupSearch();
        setupFab(view);

        return view;
    }

    private void setupViewPager() {

        adapter = new SellerProductPagerAdapter(requireActivity());
        viewPager.setAdapter(adapter);

        // FIX QUAN TRỌNG: tránh fragment bị recreate sai state
        viewPager.setOffscreenPageLimit(3);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Chờ duyệt");
            else if (position == 1) tab.setText("Đã duyệt");
            else tab.setText("Từ chối");
        }).attach();
    }

    private void setupSearch() {

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