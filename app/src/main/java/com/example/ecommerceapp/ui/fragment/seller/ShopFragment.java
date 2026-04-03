package com.example.ecommerceapp.ui.fragment.seller;

import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.ShopService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.ShopRepository;
import com.example.ecommerceapp.ui.activity.home.UserHomeActivity;
import com.example.ecommerceapp.ui.activity.home.seller.shop.ChatActivity;
import com.example.ecommerceapp.ui.activity.home.seller.shop.ShopInfoActivity;
import com.example.ecommerceapp.ui.viewmodel.ShopViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.ShopViewModelFactory;

public class ShopFragment extends Fragment {

    private ShopViewModel mViewModel;

    private TokenManager tokenManager;

    private View itemShopInfo;
    private View itemChat;
    private View itemLogout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setInits(view);
        setListeners();
    }

    private void setInits(View view) {
        tokenManager = TokenManager.getInstance(requireContext());

        // ánh xạ view
        itemShopInfo = view.findViewById(R.id.itemShopInfo);
        itemChat = view.findViewById(R.id.itemChat);
        itemLogout = view.findViewById(R.id.itemLogout);
    }

    private void setListeners() {

        itemShopInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ShopInfoActivity.class);
            startActivity(intent);
        });

        itemChat.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ChatActivity.class);
            startActivity(intent);
        });

        itemLogout.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), UserHomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

    }
}