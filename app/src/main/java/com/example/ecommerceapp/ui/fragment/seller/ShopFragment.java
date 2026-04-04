package com.example.ecommerceapp.ui.fragment.seller;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.example.ecommerceapp.utils.ImageLoader;

public class ShopFragment extends Fragment {

    private ShopViewModel mViewModel;

    private ImageView imgShopAvatar;
    private TextView tvShopName;
    private TextView tvShopAddress;
    private TextView tvShopRating;

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
        setUpViewModel();
        setListeners();
        observeData();

        loadShop(); // gọi lần đầu
    }

    @Override
    public void onResume() {
        super.onResume();
        loadShop(); // refresh khi quay lại từ Activity
    }

    private void loadShop() {
        int userId = (int) tokenManager.getUserId();
        mViewModel.fetchShopByUser(userId);
    }

    private void setUpViewModel() {
        ShopService api = ApiClient.getShopService(tokenManager);
        ShopRepository repository = new ShopRepository(api);

        mViewModel = new ViewModelProvider(
                requireActivity(),
                new ShopViewModelFactory(repository)
        ).get(ShopViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    private void observeData() {
        mViewModel.getShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop == null) return;

            tvShopName.setText(shop.getShopName());
            tvShopAddress.setText(shop.getAddress());
            tvShopRating.setText(
                    shop.getRatingAvg() + " ⭐ (" + shop.getRatingCount() + ")"
            );

            String avatar = shop.getAvatar();

            if (avatar == null || avatar.trim().isEmpty()) {
                imgShopAvatar.setImageResource(R.drawable.ic_avatar);
            } else {
                ImageLoader.load(requireContext(), imgShopAvatar, avatar);
            }
        });
    }

    private void setInits(View view) {
        tokenManager = TokenManager.getInstance(requireContext());

        // ánh xạ view
        imgShopAvatar = view.findViewById(R.id.imgShopAvatar);
        tvShopName = view.findViewById(R.id.tvShopName);
        tvShopAddress = view.findViewById(R.id.tvShopAddress);
        tvShopRating = view.findViewById(R.id.tvShopRating);

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