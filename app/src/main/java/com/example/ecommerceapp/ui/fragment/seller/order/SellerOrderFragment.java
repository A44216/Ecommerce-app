package com.example.ecommerceapp.ui.fragment.seller.order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.ui.adapter.seller.order.SellerOrderPagerAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class SellerOrderFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        tabLayout = view.findViewById(R.id.tabOrder);
        viewPager = view.findViewById(R.id.viewPagerOrder);

        SellerOrderPagerAdapter adapter = new SellerOrderPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            String[] titles = {
                    "Chờ xác nhận",
                    "Xác nhận",
                    "Đang giao",
                    "Hoàn tất",
                    "Huỷ"
            };

            tab.setText(titles[position]);

        }).attach();
    }
}