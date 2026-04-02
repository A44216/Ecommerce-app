package com.example.ecommerceapp.ui.fragment.seller;

import androidx.lifecycle.ViewModelProvider;

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
import com.example.ecommerceapp.ui.viewmodel.ShopViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.ShopViewModelFactory;

public class ShopFragment extends Fragment {

    private ShopViewModel mViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = TokenManager.getInstance(requireContext());
        ShopService apiService = ApiClient.getShopService(tokenManager);        ShopRepository repository = new ShopRepository(apiService);

        ShopViewModelFactory factory = new ShopViewModelFactory(repository);

        mViewModel = new ViewModelProvider(this, factory)
                .get(ShopViewModel.class);
    }
}