package com.example.ecommerceapp.ui.fragment.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.ui.Category;
import com.example.ecommerceapp.data.model.ui.Product;
import com.example.ecommerceapp.ui.activity.home.user.cart.UserCartActivity;
import com.example.ecommerceapp.ui.adapter.user.BannerAdapter;
import com.example.ecommerceapp.ui.adapter.user.CategoryAdapter;
import com.example.ecommerceapp.ui.adapter.user.ProductAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

public class HomeFragment extends Fragment {

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. Ánh xạ View
        rvCategories = view.findViewById(R.id.rvCategories);

        // 2. Cài đặt RecyclerView lướt ngang (HORIZONTAL)
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false);
        rvCategories.setLayoutManager(linearLayoutManager);

        // 3. Tạo dữ liệu giả
        List<Category> dummyCategories = getDummyCategories();

        // 4. Gắn Adapter
        categoryAdapter = new CategoryAdapter(dummyCategories);
        rvCategories.setAdapter(categoryAdapter);

        ViewPager2 viewPagerBanner = view.findViewById(R.id.viewPagerBanner);

        // 1. Tạo danh sách ảnh giả để test
        List<Integer> dummyBanners = new ArrayList<>();
        dummyBanners.add(android.R.drawable.sym_def_app_icon); // Ảnh 1
        dummyBanners.add(android.R.drawable.ic_dialog_map);    // Ảnh 2
        dummyBanners.add(android.R.drawable.ic_dialog_email);  // Ảnh 3

        // 2. Gắn Adapter vào ViewPager2
        BannerAdapter bannerAdapter = new BannerAdapter(dummyBanners);
        viewPagerBanner.setAdapter(bannerAdapter);

        RecyclerView rvProducts = view.findViewById(R.id.rvProducts);

        // Cài đặt hiển thị dạng Lưới (Grid) với 2 cột
        // Tính năng lướt (scroll) của nó đã bị tắt trong XML (nestedScrollingEnabled="false")
        // để nhường quyền lướt cho cái NestedScrollView bọc ngoài cùng.
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        rvProducts.setLayoutManager(gridLayoutManager);

        // Tạo dữ liệu giả và gắn Adapter
        List<Product> dummyProducts = getDummyProducts();
        ProductAdapter productAdapter = new ProductAdapter(dummyProducts);
        rvProducts.setAdapter(productAdapter);

        ImageView ivCartHome = view.findViewById(R.id.ivCartHome);
        ivCartHome.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), UserCartActivity.class);
            startActivity(intent);
        });

        return view;
    }

    // Hàm tạo dữ liệu giả để test giao diện
    private List<Category> getDummyCategories() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(android.R.drawable.ic_menu_camera, "ShopeeFood"));
        list.add(new Category(android.R.drawable.ic_menu_gallery, "Shopee Mart"));
        list.add(new Category(android.R.drawable.ic_menu_compass, "Shopee VIP"));
        list.add(new Category(android.R.drawable.ic_menu_agenda, "Deal 1K"));
        list.add(new Category(android.R.drawable.ic_menu_call, "Nạp thẻ"));
        list.add(new Category(android.R.drawable.ic_menu_day, "Quốc tế"));
        list.add(new Category(android.R.drawable.ic_menu_help, "Bắt trend"));
        list.add(new Category(android.R.drawable.ic_menu_mapmode, "FreeShip"));
        return list;
    }

    private List<Product> getDummyProducts() {
        List<Product> list = new ArrayList<>();

        // Sản phẩm 1
        Product p1 = new Product();
        // Nhớ thêm các hàm Setter này vào class Product.java của bạn nhé!
        p1.setName("Bàn phím cơ AULA F87 Pro V2 Pin 12K");
        p1.setPrice(new BigDecimal("750000"));
        p1.setImages(Collections.singletonList("https://link-anh-giagia.com/anh1.jpg"));
        list.add(p1);

        // Sản phẩm 2
        Product p2 = new Product();
        p2.setName("Chuột không dây Logitech G304 LightSpeed");
        p2.setPrice(new BigDecimal("600000"));
        p2.setImages(Collections.singletonList("https://link-anh-giagia.com/anh2.jpg"));
        list.add(p2);

        // Sản phẩm 3
        Product p3 = new Product();
        p3.setName("Tai nghe Bluetooth Hổ Vằn Pro 2");
        p3.setPrice(new BigDecimal("350000"));
        p3.setImages(Collections.singletonList("https://link-anh-giagia.com/anh3.jpg"));
        list.add(p3);

        return list;
    }

}