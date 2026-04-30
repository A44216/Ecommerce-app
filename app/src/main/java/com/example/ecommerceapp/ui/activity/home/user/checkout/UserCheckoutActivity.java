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
    
    // Tiền tệ
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal shippingFee = BigDecimal.ZERO;
    private java.util.Map<Integer, List<UserCartItem>> itemsByShop = new java.util.HashMap<>();
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal finalTotal = BigDecimal.ZERO;

    // Coupon
    private Integer appliedCouponId = null;

    // Lưu ID địa chỉ thực tế mà người dùng chọn
    private int realAddressId = -1;

    // Các View hiển thị tiền
    private TextView tvCheckoutFinalTotal;
    private TextView tvCheckoutSubtotal;
    private TextView tvCheckoutDiscount;
    private TextView tvCheckoutShippingFee;

    // Bộ khởi tạo Activity để nhận kết quả thanh toán
    private androidx.activity.result.ActivityResultLauncher<Intent> paymentLauncher;
    private List<UserOrderRequest> pendingOrders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_checkout);

        // 1. Ánh xạ View
        findViewById(R.id.btnCheckoutBack).setOnClickListener(v -> finish());
        tvCheckoutFinalTotal = findViewById(R.id.tvCheckoutFinalTotal);
        tvCheckoutSubtotal = findViewById(R.id.tvCheckoutSubtotal);
        tvCheckoutDiscount = findViewById(R.id.tvCheckoutDiscount);
        tvCheckoutShippingFee = findViewById(R.id.tvCheckoutShippingFee);
        
        android.widget.EditText edtCouponCode = findViewById(R.id.edtCouponCode);
        Button btnApplyCoupon = findViewById(R.id.btnApplyCoupon);
        Button btnPlaceOrder = findViewById(R.id.btnPlaceOrder);

        RecyclerView rvCheckoutItems = findViewById(R.id.rvCheckoutItems);
        RadioGroup rgPaymentMethod = findViewById(R.id.rgPaymentMethod);

        // --- KHỞI TẠO BỘ NHẬN KẾT QUẢ THANH TOÁN ---
        paymentLauncher = registerForActivityResult(
                new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Thanh toán thành công -> Thực hiện đặt hàng
                        if (!pendingOrders.isEmpty()) {
                            viewModel.placeMultipleOrders(pendingOrders);
                        }
                    } else {
                        Toast.makeText(this, "Thanh toán không thành công hoặc đã bị hủy", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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

        itemsByShop = new java.util.HashMap<>();
        for (UserCartItem item : selectedItems) {
            Integer shopId = item.getProduct().getShopId();
            if (shopId == null) shopId = -1;
            if (!itemsByShop.containsKey(shopId)) {
                itemsByShop.put(shopId, new ArrayList<>());
            }
            itemsByShop.get(shopId).add(item);
        }

        // Tính phí ship = 30k * số lượng shop
        shippingFee = new BigDecimal("30000").multiply(new BigDecimal(itemsByShop.size()));

        rvCheckoutItems.setLayoutManager(new LinearLayoutManager(this));
        UserCheckoutAdapter adapter = new UserCheckoutAdapter(selectedItems);
        rvCheckoutItems.setAdapter(adapter);

        subtotal = BigDecimal.ZERO;
        for (UserCartItem item : selectedItems) {
            BigDecimal price = item.getProduct().getPrice();
            if (price != null) {
                subtotal = subtotal.add(price.multiply(new BigDecimal(item.getQuantity())));
            }
        }
        
        updateTotalDisplay();

        // 3.5. XỬ LÝ MÃ GIẢM GIÁ
        com.example.ecommerceapp.api.service.UserCouponApiService couponApi = ApiClient.getUserCouponApiService(tokenManager);
        btnApplyCoupon.setOnClickListener(v -> {
            String code = edtCouponCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã giảm giá", Toast.LENGTH_SHORT).show();
                return;
            }

            couponApi.getCouponByCode(code).enqueue(new retrofit2.Callback<com.example.ecommerceapp.data.model.response.CouponResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.example.ecommerceapp.data.model.response.CouponResponse> call, retrofit2.Response<com.example.ecommerceapp.data.model.response.CouponResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        com.example.ecommerceapp.data.model.response.CouponResponse coupon = response.body();
                        
                        // Kiểm tra điều kiện đơn hàng tối thiểu
                        if (coupon.minOrderValue != null && subtotal.compareTo(coupon.minOrderValue) < 0) {
                            Toast.makeText(UserCheckoutActivity.this, "Đơn hàng chưa đạt giá trị tối thiểu để áp dụng mã này", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        appliedCouponId = coupon.id;
                        
                        if (coupon.discountAmount != null && coupon.discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                            discountAmount = coupon.discountAmount;
                        } else if (coupon.discountPercent != null && coupon.discountPercent > 0) {
                            discountAmount = subtotal.multiply(new BigDecimal(coupon.discountPercent)).divide(new BigDecimal(100));
                        }
                        
                        updateTotalDisplay();
                        Toast.makeText(UserCheckoutActivity.this, "Áp dụng mã giảm giá thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(UserCheckoutActivity.this, "Mã giảm giá không hợp lệ hoặc đã hết hạn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<com.example.ecommerceapp.data.model.response.CouponResponse> call, Throwable t) {
                    Toast.makeText(UserCheckoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        });

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
                        .setMessage("Cảm ơn bạn đã mua sắm. Đơn hàng đang chờ được xác nhận.")
                        .setCancelable(false)
                        .setPositiveButton("Về Trang chủ", (dialog, which) -> {
                            CartManager.getInstance().getCartItems().removeAll(selectedItems);
                            CartManager.getInstance().saveCart();
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

            // --- TÁCH ĐƠN HÀNG THEO SHOP ---
            List<UserOrderRequest> requests = new ArrayList<>();
            boolean isFirstShop = true;

            for (java.util.Map.Entry<Integer, List<UserCartItem>> entry : itemsByShop.entrySet()) {
                int shopId = entry.getKey();
                List<UserCartItem> shopItems = entry.getValue();

                if (shopId == -1) {
                    Toast.makeText(this, "Lỗi dữ liệu: Có sản phẩm không thuộc cửa hàng nào!", Toast.LENGTH_SHORT).show();
                    return;
                }

                BigDecimal shopSubtotal = BigDecimal.ZERO;
                List<UserOrderRequest.OrderItemRequest> itemsRequest = new ArrayList<>();
                for (UserCartItem cartItem : shopItems) {
                    itemsRequest.add(new UserOrderRequest.OrderItemRequest(
                            cartItem.getProduct().getId(),
                            cartItem.getQuantity(),
                            cartItem.getProduct().getPrice()
                    ));
                    if (cartItem.getProduct().getPrice() != null) {
                        shopSubtotal = shopSubtotal.add(cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())));
                    }
                }

                BigDecimal shopDiscount = BigDecimal.ZERO;
                Integer shopCouponId = null;

                // Áp dụng mã giảm giá chỉ cho shop đầu tiên
                if (isFirstShop) {
                    shopDiscount = discountAmount;
                    shopCouponId = appliedCouponId;
                    isFirstShop = false;
                }

                // Phí ship mỗi đơn là 30k
                BigDecimal shopShipping = new BigDecimal("30000");
                BigDecimal shopTotal = shopSubtotal.add(shopShipping).subtract(shopDiscount);
                if (shopTotal.compareTo(BigDecimal.ZERO) < 0) {
                    shopTotal = BigDecimal.ZERO;
                }

                UserOrderRequest request = new UserOrderRequest(
                        realAddressId,
                        method,
                        realUserId,
                        shopId,
                        shopTotal,
                        shopSubtotal,
                        shopDiscount,
                        shopCouponId,
                        itemsRequest
                );
                requests.add(request);
            }

            // --- LƯU TRỮ DANH SÁCH ĐƠN HÀNG CHỜ ---
            pendingOrders = requests;

            if (method == PaymentMethod.QR) {
                // Gọi API lấy URL thanh toán VNPAY
                android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(this);
                progressDialog.setMessage("Đang tạo liên kết thanh toán...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                ApiClient.getPaymentApiService().createPaymentUrl(finalTotal.longValue(), "Thanh toan don hang").enqueue(new retrofit2.Callback<com.example.ecommerceapp.data.model.response.PaymentResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.example.ecommerceapp.data.model.response.PaymentResponse> call, retrofit2.Response<com.example.ecommerceapp.data.model.response.PaymentResponse> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful() && response.body() != null) {
                            String paymentUrl = response.body().getUrl();
                            Intent intent = new Intent(UserCheckoutActivity.this, VnPayPaymentActivity.class);
                            intent.putExtra("PAYMENT_URL", paymentUrl);
                            paymentLauncher.launch(intent);
                        } else {
                            Toast.makeText(UserCheckoutActivity.this, "Không thể tạo liên kết thanh toán", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.example.ecommerceapp.data.model.response.PaymentResponse> call, Throwable t) {
                        progressDialog.dismiss();
                        Toast.makeText(UserCheckoutActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // COD -> Đặt hàng luôn
                viewModel.placeMultipleOrders(pendingOrders);
            }
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

    private void updateTotalDisplay() {
        finalTotal = subtotal.add(shippingFee).subtract(discountAmount);
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0) {
            finalTotal = BigDecimal.ZERO;
        }

        DecimalFormat df = new DecimalFormat("#,###");
        if (tvCheckoutSubtotal != null) {
            tvCheckoutSubtotal.setText(df.format(subtotal) + "đ");
        }
        if (tvCheckoutDiscount != null) {
            tvCheckoutDiscount.setText("-" + df.format(discountAmount) + "đ");
        }
        if (tvCheckoutShippingFee != null) {
            tvCheckoutShippingFee.setText(df.format(shippingFee) + "đ");
        }
        if (tvCheckoutFinalTotal != null) {
            tvCheckoutFinalTotal.setText(df.format(finalTotal) + "đ");
        }
    }
}