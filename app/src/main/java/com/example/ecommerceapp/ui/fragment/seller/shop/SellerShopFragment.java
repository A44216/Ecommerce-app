package com.example.ecommerceapp.ui.fragment.seller.shop;

import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.seller.SellerShopService;
import com.example.ecommerceapp.data.enums.ShopStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.seller.SellerShopRepository;
import com.example.ecommerceapp.ui.activity.home.UserHomeActivity;
import com.example.ecommerceapp.ui.activity.home.seller.shop.SellerChatActivity;
import com.example.ecommerceapp.ui.activity.home.seller.shop.SellerShopInfoActivity;
import com.example.ecommerceapp.ui.viewmodel.seller.SellerShopViewModel;
import com.example.ecommerceapp.ui.viewmodel.seller.factory.SellerShopViewModelFactory;
import com.example.ecommerceapp.utils.ImageLoader;

public class SellerShopFragment extends Fragment {

    private SellerShopViewModel mViewModel;

    private ImageView imgShopAvatar;
    private TextView tvShopName, tvShopStatus, tvShopAddress, tvShopRating;

    private TokenManager tokenManager;

    private View itemShopInfo;
    private View itemChat;
    private View itemLogout;
    private com.google.android.material.button.MaterialButton btnCancelRegistration;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_seller_shop, container, false);
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
        mViewModel.fetchMyShop();
    }

    private void setUpViewModel() {
        SellerShopService api = ApiClient.getShopService(tokenManager);
        SellerShopRepository repository = new SellerShopRepository(api);

        mViewModel = new ViewModelProvider(
                requireActivity(),
                new SellerShopViewModelFactory(repository)
        ).get(SellerShopViewModel.class);
    }

    @SuppressLint("SetTextI18n")
    private void observeData() {
        mViewModel.getShop().observe(getViewLifecycleOwner(), shop -> {
            if (shop == null) return;

            Log.e("SHOP_NAME = ", shop.getShopName());

            tvShopName.setText(shop.getShopName());
            tvShopAddress.setText(shop.getAddress());
            tvShopRating.setText(
                    shop.getRatingAvg() + " ⭐ (" + shop.getRatingCount() + " đánh giá)"
            );

            setStatusUI(shop.getStatus());

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
        tvShopStatus = view.findViewById(R.id.tvShopStatus);
        tvShopName = view.findViewById(R.id.tvShopName);
        tvShopAddress = view.findViewById(R.id.tvShopAddress);
        tvShopRating = view.findViewById(R.id.tvShopRating);

        itemShopInfo = view.findViewById(R.id.itemShopInfo);
        itemChat = view.findViewById(R.id.itemChat);
        itemLogout = view.findViewById(R.id.itemLogout);
        btnCancelRegistration = view.findViewById(R.id.btnCancelRegistration);
    }

    private void setListeners() {

        itemShopInfo.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SellerShopInfoActivity.class);
            startActivity(intent);
        });

        itemChat.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SellerChatActivity.class);
            startActivity(intent);
        });

        itemLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Xác nhận đăng xuất")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {

                        TokenManager.getInstance(requireContext()).logout();
                        Intent intent = new Intent(requireContext(), com.example.ecommerceapp.ui.activity.login.LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    })
                    .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        btnCancelRegistration.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Hủy yêu cầu đăng ký")
                    .setMessage("Bạn có chắc chắn muốn hủy yêu cầu đăng ký mở Shop này không?")
                    .setPositiveButton("Hủy yêu cầu", (dialog, which) -> {
                        mViewModel.cancelRegistration();
                    })
                    .setNegativeButton("Quay lại", null)
                    .show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void setStatusUI(ShopStatus status) {
        
        btnCancelRegistration.setVisibility(View.GONE);

        switch (status) {

            case APPROVED:
                tvShopStatus.setText("Hoạt động");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_approved);
                tvShopStatus.setTextColor(requireContext().getColor(R.color.green));
                break;

            case BLOCKED:
                tvShopStatus.setText("Bị khóa");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_blocked);
                tvShopStatus.setTextColor(requireContext().getColor(R.color.red));
                break;

            case REJECTED:
                tvShopStatus.setText("Từ chối");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_rejected);
                tvShopStatus.setTextColor(requireContext().getColor(R.color.orange));
                break;

            case PENDING:
                tvShopStatus.setText("Chờ duyệt");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                tvShopStatus.setTextColor(requireContext().getColor(R.color.orange));
                btnCancelRegistration.setVisibility(View.VISIBLE);
                break;
                
            case CANCELED:
                tvShopStatus.setText("Đã hủy");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_rejected);
                tvShopStatus.setTextColor(requireContext().getColor(R.color.gray));
                btnCancelRegistration.setVisibility(View.GONE);
                break;

            default:
                tvShopStatus.setText("UNKNOWN");
                tvShopStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                tvShopStatus.setTextColor(requireContext().getColor(R.color.gray));
                break;
        }
    }

}