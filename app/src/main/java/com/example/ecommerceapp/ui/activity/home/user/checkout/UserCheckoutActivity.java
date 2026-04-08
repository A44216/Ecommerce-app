package com.example.ecommerceapp.ui.activity.home.user.checkout;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserAddressApiService;
import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.example.ecommerceapp.data.repository.UserAddressRepository;
import com.example.ecommerceapp.data.repository.UserOrderRepository;
import com.example.ecommerceapp.ui.adapter.user.UserAddressAdapter;
import com.example.ecommerceapp.ui.adapter.user.UserCheckoutAdapter;
import com.example.ecommerceapp.ui.viewmodel.UserCheckoutViewModel;
import com.example.ecommerceapp.ui.viewmodel.factory.UserCheckoutViewModelFactory;
import com.example.ecommerceapp.utils.CartManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class UserCheckoutActivity extends AppCompatActivity {

    private List<UserCartItem> selectedItems;
    private UserCheckoutViewModel viewModel;
    private BigDecimal finalTotal = BigDecimal.ZERO;

    // Lưu ID địa chỉ thực tế mà người dùng chọn
    private int realAddressId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkout);

        // 1. Ánh xạ View
        findViewById(R.id.btnCheckoutBack).setOnClickListener(v -> finish());
        TextView tvCheckoutFinalTotal = findViewById(R.id.tvCheckoutFinalTotal);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        RecyclerView rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);

        // --- SETUP RECYCLERVIEW ĐỊA CHỈ ---
        RecyclerView rvCheckoutAddresses = findViewById(R.id.rvCheckoutAddresses);
        rvCheckoutAddresses.setLayoutManager(new LinearLayoutManager(this));

        UserAddressAdapter addressAdapter = new UserAddressAdapter(address -> {
            realAddressId = address.getId(); // Bắt ID khi người dùng click
        });
        rvCheckoutAddresses.setAdapter(addressAdapter);
        // ----------------------------------

        // --- NÚT THÊM ĐỊA CHỈ MỚI ---
        TextView tvAddNewAddress = findViewById(R.id.tvAddNewAddress);
        tvAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(UserCheckoutActivity.this, UserAddAddressActivity.class);
            startActivity(intent);
        });

        // 2. Setup MVVM (Khởi tạo ViewModel)
        TokenManager tokenManager = TokenManager.getInstance(this);
        int realUserId = (int) tokenManager.getUserId();

        UserOrderApiService orderApi = ApiClient.getUserOrderApiService(tokenManager);
        UserOrderRepository orderRepo = new UserOrderRepository(orderApi);

        UserAddressApiService addressApi = ApiClient.getUserAddressApiService(tokenManager);
        UserAddressRepository addressRepo = new UserAddressRepository(addressApi);

        UserCheckoutViewModelFactory factory = new UserCheckoutViewModelFactory(orderRepo, addressRepo);
        viewModel = new ViewModelProvider(this, factory).get(UserCheckoutViewModel.class);

        // 3. Setup dữ liệu sản phẩm & Tính tiền
        selectedItems = new ArrayList<>();
        List<UserCartItem> allCartItems = CartManager.getInstance().getCartItems();
        for (UserCartItem item : allCartItems) {
            if (item.isChecked()) {
                selectedItems.add(item);
            }
        }

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm để thanh toán", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        UserCheckoutAdapter adapter = new UserCheckoutAdapter(selectedItems);
        rvCheckoutItems.setAdapter(adapter);

        BigDecimal total = BigDecimal.ZERO;
        for (UserCartItem item : selectedItems) {
            BigDecimal price = item.getProduct().getPrice();
            if (price != null) {
                total = total.add(price.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        BigDecimal shippingFee = new BigDecimal("30000"); // Phí ship
        finalTotal = total.add(shippingFee);

        DecimalFormat df = new DecimalFormat("#,###");
        tvCheckoutFinalTotal.setText(df.format(finalTotal) + "đ");

        // 4. LẮNG NGHE DỮ LIỆU ĐỊA CHỈ & KẾT QUẢ ĐẶT HÀNG
        viewModel.getAddressList().observe(this, addresses -> {
            if (addresses != null && !addresses.isEmpty()) {
                addressAdapter.updateData(addresses);
            } else {
                Toast.makeText(this, "Bạn chưa có địa chỉ giao hàng nào!", Toast.LENGTH_SHORT).show();
            }
        });

        // Gọi API tải địa chỉ ngay khi vào trang
//        if (realUserId != -1) {
//            viewModel.fetchAddresses(realUserId);
//        }

        viewModel.getOrderSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                new AlertDialog.Builder(this)
                        .setTitle("Đặt hàng thành công!")
                        .setMessage("Cảm ơn bạn đã mua sắm. Đơn hàng sẽ sớm được giao đến bạn.")
                        .setCancelable(false)
                        .setPositiveButton("Về Trang chủ", (dialog, which) -> {
                            CartManager.getInstance().getCartItems().removeAll(selectedItems);
                            Intent intent = new Intent(UserCheckoutActivity.this, com.example.ecommerceapp.ui.activity.home.UserHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        })
                        .show();
            }
        });

        viewModel.getOrderError().observe(this, errorMessage -> {
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        });

        // 5. SỰ KIỆN BẤM NÚT ĐẶT HÀNG
        btnPlaceOrder.setOnClickListener(v -> {
            int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
            PaymentMethod method = (selectedId == R.id.rbVNPay) ? PaymentMethod.QR : PaymentMethod.COD;

            if (realUserId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để thực hiện đặt hàng!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (realAddressId == -1) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng!", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- BƯỚC MỚI: GÓI CÁC SẢN PHẨM TRONG GIỎ HÀNG LẠI ---
            List<UserOrderRequest.OrderItemRequest> itemsRequest = new ArrayList<>();
            for (UserCartItem cartItem : selectedItems) {
                itemsRequest.add(new UserOrderRequest.OrderItemRequest(
                        cartItem.getProduct().getId(),
                        cartItem.getQuantity(),
                        cartItem.getProduct().getPrice()
                ));
            }

            int dummyShopId = 2;

            // Truyền itemsRequest vào OrderRequest
            UserOrderRequest request = new UserOrderRequest(realAddressId, method, realUserId, dummyShopId, finalTotal, itemsRequest);

            viewModel.placeOrder(request);
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Lấy lại ID và gọi lại API mỗi khi màn hình này hiện lên
        TokenManager tokenManager = TokenManager.getInstance(this);
        int realUserId = (int) tokenManager.getUserId();
        if (realUserId != -1 && viewModel != null) {
            viewModel.fetchAddresses(realUserId);
        }
    }
}