package com.example.ecommerceapp.ui.fragment.seller.product;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) tab.setText("Chờ duyệt");
            else if (position == 1) tab.setText("Đã duyệt");
            else tab.setText("Từ chối");
        }).attach();
    }

    private void setupSearch() {

        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            String keyword = etSearch.getText() != null
                    ? etSearch.getText().toString().trim()
                    : "";

            adapter.setKeywordToAll(keyword);

            return true;
        });
    }

    private void setupFab(View view) {

        FloatingActionButton fab = view.findViewById(R.id.fabAddNew);

        fab.setOnClickListener(v ->
                startActivity(new Intent(getContext(),
                        SellerAddAndEditProductActivity.class)));
    }
}