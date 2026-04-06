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
import com.example.ecommerceapp.api.service.UserOrderApiService;
import com.example.ecommerceapp.data.enums.PaymentMethod;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.UserOrderRequest;
import com.example.ecommerceapp.data.model.ui.UserCartItem;
import com.example.ecommerceapp.data.repository.UserOrderRepository;
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

        // 2. Setup MVVM (Khởi tạo ViewModel)
        TokenManager tokenManager = TokenManager.getInstance(this);
        UserOrderApiService apiService = ApiClient.getUserOrderApiService(tokenManager);
        UserOrderRepository repository = new UserOrderRepository(apiService);
        UserCheckoutViewModelFactory factory = new UserCheckoutViewModelFactory(repository);
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
        BigDecimal shippingFee = new BigDecimal("30000"); // Phí ship giả định
        finalTotal = total.add(shippingFee);

        DecimalFormat df = new DecimalFormat("#,###");
        tvCheckoutFinalTotal.setText(df.format(finalTotal) + "đ");

        // ==========================================
        // 4. LẮNG NGHE KẾT QUẢ TỪ SERVER (MVVM)
        // ==========================================
        viewModel.getOrderSuccess().observe(this, isSuccess -> {
            if (isSuccess) {
                // Hiện Dialog chúc mừng KHI VÀ CHỈ KHI API trả về thành công
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

        // ==========================================
        // 5. SỰ KIỆN BẤM NÚT ĐẶT HÀNG
        // ==========================================
        btnPlaceOrder.setOnClickListener(v -> {
            // Lấy phương thức thanh toán
            int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
            PaymentMethod method = (selectedId == R.id.rbVNPay) ? PaymentMethod.QR : PaymentMethod.COD;

            // lấy ID thật từ TokenManager và ép kiểu sang int ---
            int realUserId = (int) tokenManager.getUserId();

            // Chặn lại nếu người dùng chưa đăng nhập (ID = -1)
            if (realUserId == -1) {
                Toast.makeText(this, "Vui lòng đăng nhập để thực hiện đặt hàng!", Toast.LENGTH_SHORT).show();
                return; // Dừng lại, không cho gọi API
            }

            int dummyAddressId = 1;
            int dummyShopId = 2;

            // Truyền realUserId vào Request
            UserOrderRequest request = new UserOrderRequest(dummyAddressId, method, realUserId, dummyShopId, finalTotal);

            // Ra lệnh cho ViewModel bắn API
            viewModel.placeOrder(request);
        });
    }
}