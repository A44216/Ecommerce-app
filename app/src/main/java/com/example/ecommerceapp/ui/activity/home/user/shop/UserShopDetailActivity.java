package com.example.ecommerceapp.ui.activity.home.user.shop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ConversationRequest;
import com.example.ecommerceapp.data.model.response.ConversationResponse;
import com.example.ecommerceapp.data.model.response.ShopResponse;
import com.example.ecommerceapp.ui.activity.home.user.chat.ChatActivity;
import com.example.ecommerceapp.utils.ImageLoader;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserShopDetailActivity extends AppCompatActivity {

    private int shopId;
    private TokenManager tokenManager;
    private ShopResponse currentShop;

    private ShapeableImageView ivShopAvatar;
    private TextView tvShopName, tvShopRating;
    private EditText edtSearchShop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_shop_detail);

        tokenManager = TokenManager.getInstance(this);
        shopId = getIntent().getIntExtra("SHOP_ID", -1);

        if (shopId == -1) {
            Toast.makeText(this, "Không tìm thấy Shop", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        loadShopDetails();
        setupTabs();
    }

    private void initViews() {
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(v -> finish());

        edtSearchShop = findViewById(R.id.edtSearchShop);
        edtSearchShop.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = edtSearchShop.getText().toString().trim();
            if (!keyword.isEmpty()) {
                Intent intent = new Intent(this, com.example.ecommerceapp.ui.activity.home.user.search.UserSearchActivity.class);
                intent.putExtra("keyword", keyword);
                intent.putExtra("shopId", shopId);
                startActivity(intent);
            }
            return false;
        });

        ivShopAvatar = findViewById(R.id.ivShopAvatar);
        tvShopName = findViewById(R.id.tvShopName);
        tvShopRating = findViewById(R.id.tvShopRating);

        findViewById(R.id.btnChatShop).setOnClickListener(v -> handleChatAction());
    }

    private void loadShopDetails() {
        ApiClient.getPublicShopService(tokenManager).getShopById(shopId).enqueue(new Callback<ShopResponse>() {
            @Override
            public void onResponse(Call<ShopResponse> call, Response<ShopResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentShop = response.body();
                    displayShopInfo();
                } else {
                    Toast.makeText(UserShopDetailActivity.this, "Không tải được thông tin shop", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ShopResponse> call, Throwable t) {
                Toast.makeText(UserShopDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayShopInfo() {
        if (currentShop.getShopName() != null) {
            tvShopName.setText(currentShop.getShopName());
        }
        if (currentShop.getRatingAvg() != null) {
            tvShopRating.setText(String.format("%.1f", currentShop.getRatingAvg()));
        }
        if (currentShop.getAvatar() != null && !currentShop.getAvatar().isEmpty()) {
            ImageLoader.load(this, ivShopAvatar, currentShop.getAvatar());
        }
    }

    private void handleChatAction() {
        if (tokenManager.getUserId() == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để chat", Toast.LENGTH_SHORT).show();
            return;
        }

        ConversationRequest req = new ConversationRequest(shopId, (int) tokenManager.getUserId());
        ApiClient.getChatApiService(tokenManager).createConversation(req).enqueue(new Callback<ConversationResponse>() {
            @Override
            public void onResponse(Call<ConversationResponse> call, Response<ConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Intent intent = new Intent(UserShopDetailActivity.this, ChatActivity.class);
                    intent.putExtra("CONVERSATION_ID", response.body().getId());
                    intent.putExtra("SHOP_NAME", currentShop != null ? currentShop.getShopName() : "Shop");
                    startActivity(intent);
                } else {
                    Toast.makeText(UserShopDetailActivity.this, "Không thể mở chat", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ConversationResponse> call, Throwable t) {
                Toast.makeText(UserShopDetailActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTabs() {
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                if (position == 0) {
                    return ShopProductsFragment.newInstance(shopId);
                }
                return ShopCategoriesFragment.newInstance(shopId);
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Sản phẩm");
            } else {
                tab.setText("Danh mục");
            }
        }).attach();
    }
}
