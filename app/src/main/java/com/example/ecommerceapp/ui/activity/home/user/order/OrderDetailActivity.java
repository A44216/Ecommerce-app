package com.example.ecommerceapp.ui.activity.home.user.order;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.UserOrderResponse;
import com.example.ecommerceapp.ui.adapter.user.UserOrderItemAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    // Đã khai báo thêm tvOrderDate và tvSubTotal
    private TextView tvStatus, tvAddress, tvTotal, tvOrderDate, tvSubTotal, tvDiscountAmount, tvShippingFee, tvPaymentMethod, tvPaymentStatus;
    private RecyclerView rvItems;
    private Button btnCancelOrder, btnReceiveOrder, btnReturnOrder;
    private View ivStepPending, ivStepProcessing, ivStepShipping, ivStepDelivered;
    private View line1, line2, line3;
    private View rlDiscount;
    private int orderId;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        tokenManager = TokenManager.getInstance(this);

        // 1. Ánh xạ View
        tvStatus = findViewById(R.id.tvDetailStatus);
        tvAddress = findViewById(R.id.tvDetailAddress);
        tvTotal = findViewById(R.id.tvTotalPayment);
        tvOrderDate = findViewById(R.id.tvOrderDate); // Ánh xạ Ngày đặt
        tvSubTotal = findViewById(R.id.tvSubTotal);   // Ánh xạ Tổng tiền hàng
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount); // Ánh xạ Giảm giá
        rlDiscount = findViewById(R.id.rlDiscount); // Box giảm giá
        tvShippingFee = findViewById(R.id.tvShippingFee); // Ánh xạ Phí vận chuyển
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod);
        tvPaymentStatus = findViewById(R.id.tvPaymentStatus);

        rvItems = findViewById(R.id.rvOrderDetailItems);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
        btnReceiveOrder = findViewById(R.id.btnReceiveOrder);
        btnReturnOrder = findViewById(R.id.btnReturnOrder);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Ánh xạ Stepper
        ivStepPending = findViewById(R.id.ivStepPending);
        ivStepProcessing = findViewById(R.id.ivStepProcessing);
        ivStepShipping = findViewById(R.id.ivStepShipping);
        ivStepDelivered = findViewById(R.id.ivStepDelivered);
        line1 = findViewById(R.id.linePendingToProcessing);
        line2 = findViewById(R.id.lineProcessingToShipping);
        line3 = findViewById(R.id.lineShippingToDelivered);

        // 2. Lấy ID từ Intent
        orderId = getIntent().getIntExtra("ORDER_ID", -1);

        if (orderId != -1) {
            loadOrderDetail();
        } else {
            Toast.makeText(this, "Không tìm thấy mã đơn hàng!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 3. Sự kiện bấm nút Hủy
        btnCancelOrder.setOnClickListener(v -> showCancelConfirmDialog());
        btnReceiveOrder.setOnClickListener(v -> showReceiveConfirmDialog());
        btnReturnOrder.setOnClickListener(v -> showReturnDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (orderId != -1) {
            loadOrderDetail();
        }
    }

    private void loadOrderDetail() {
        ApiClient.getUserOrderApiService(tokenManager).getOrderById(orderId).enqueue(new Callback<UserOrderResponse>() {
            @Override
            public void onResponse(Call<UserOrderResponse> call, Response<UserOrderResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserOrderResponse order = response.body();

                    // ==========================================
                    // HIỂN THỊ DỮ LIỆU CƠ BẢN
                    // ==========================================
                    tvStatus.setText(order.getStatus());

                    if (order.getTotalPrice() != null) {
                        String priceFormatted = String.format("%,.0fđ", order.getTotalPrice());
                        tvTotal.setText(priceFormatted);
                    }
                    
                    if (tvShippingFee != null) {
                        tvShippingFee.setText("30,000đ");
                    }
                    
                    if (order.getOrderItems() != null) {
                        double subTotalValue = 0;
                        for (com.example.ecommerceapp.data.model.response.UserOrderItemResponse item : order.getOrderItems()) {
                            if (item.getPrice() != null && item.getQuantity() != null) {
                                subTotalValue += item.getPrice().doubleValue() * item.getQuantity();
                            }
                        }
                        tvSubTotal.setText(String.format("%,.0fđ", subTotalValue));
                    }

                    if (order.getDiscountAmount() != null && order.getDiscountAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                        rlDiscount.setVisibility(View.VISIBLE);
                        tvDiscountAmount.setText(String.format("-%,.0fđ", order.getDiscountAmount()));
                    } else {
                        rlDiscount.setVisibility(View.GONE);
                    }

                    if (order.getCreatedAt() != null) {
                        // Cắt chuỗi để lấy phần ngày yyyy-mm-dd
                        String date = order.getCreatedAt().split("T")[0];
                        tvOrderDate.setText("Ngày đặt: " + date);
                    }

                    // Hiển thị địa chỉ
                    if (order.getAddressLine() != null) {
                        String fullAddress = order.getShippingName() + " - " + order.getShippingPhone() + "\n" + order.getAddressLine();
                        tvAddress.setText(fullAddress);
                    } else {
                        tvAddress.setText("Chưa cập nhật địa chỉ");
                    }

                    // Setup danh sách sản phẩm
                    rvItems.setLayoutManager(new LinearLayoutManager(OrderDetailActivity.this));
                    if (order.getOrderItems() != null) {
                        rvItems.setAdapter(new UserOrderItemAdapter(order.getOrderItems()));
                    }

                    // --- HIỂN THỊ PHƯƠNG THỨC THANH TOÁN ---
                    if (order.getPaymentMethod() != null) {
                        if ("QR".equalsIgnoreCase(order.getPaymentMethod())) {
                            tvPaymentMethod.setText("VNPay");
                        } else {
                            tvPaymentMethod.setText("Thanh toán khi nhận hàng (COD)");
                        }
                    }

                    // --- HIỂN THỊ TRẠNG THÁI THANH TOÁN ---
                    if (order.getPaymentStatus() != null) {
                        if ("PAID".equalsIgnoreCase(order.getPaymentStatus())) {
                            tvPaymentStatus.setText("Đã thanh toán");
                            tvPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        } else {
                            tvPaymentStatus.setText("Chưa thanh toán");
                            tvPaymentStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        }
                    }

                    // Cập nhật Stepper
                    updateTrackingStepper(order.getStatus());

                    // ==========================================
                    // LOGIC ẨN/HIỆN NÚT HỦY ĐƠN HÀNG VÀ CÁC NÚT KHÁC
                    // ==========================================
                    String currentStatus = order.getStatus();

                    btnCancelOrder.setVisibility(View.GONE);
                    btnReceiveOrder.setVisibility(View.GONE);
                    btnReturnOrder.setVisibility(View.GONE);

                    if (currentStatus != null) {
                        if (currentStatus.trim().equalsIgnoreCase("PENDING")) {
                            btnCancelOrder.setVisibility(View.VISIBLE);
                            tvStatus.setText("Chờ xác nhận");
                        } else if (currentStatus.trim().equalsIgnoreCase("SHIPPING")) {
                            btnReceiveOrder.setVisibility(View.VISIBLE);
                            btnReturnOrder.setVisibility(View.VISIBLE);
                            tvStatus.setText("Đang giao hàng");
                        } else if (currentStatus.trim().equalsIgnoreCase("COMPLETED")) {
                            btnReturnOrder.setVisibility(View.VISIBLE);
                            tvStatus.setText("Đã giao thành công");
                        } else {
                            switch (currentStatus.toUpperCase()) {
                                case "CONFIRMED": tvStatus.setText("Đang chuẩn bị hàng"); break;
                                case "CANCELED": tvStatus.setText("Đã hủy"); break;
                                case "RETURN_REQUESTED": tvStatus.setText("Đang yêu cầu trả hàng"); break;
                                case "DISPUTED": tvStatus.setText("Đang tranh chấp (Admin xử lý)"); break;
                                case "RETURNED": tvStatus.setText("Đã trả hàng/Hoàn tiền"); break;
                                default: tvStatus.setText(currentStatus); break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<UserOrderResponse> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ==========================================
    // HÀM XỬ LÝ HỦY ĐƠN HÀNG KÈM POPUP HỎI ĐÁP
    // ==========================================
    private void showCancelConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận hủy đơn")
                .setMessage("Bạn có chắc chắn muốn hủy đơn hàng này không? Quá trình này không thể hoàn tác.")
                .setPositiveButton("Đồng ý hủy", (dialog, which) -> executeCancelOrder())
                .setNegativeButton("Quay lại", null)
                .show();
    }

    private void executeCancelOrder() {
        // Tạm thời vô hiệu hóa nút để tránh khách bấm nhiều lần
        btnCancelOrder.setEnabled(false);
        btnCancelOrder.setText("Đang xử lý...");

        ApiClient.getUserOrderApiService(tokenManager).cancelOrder(orderId).enqueue(new Callback<UserOrderResponse>() {
            @Override
            public void onResponse(Call<UserOrderResponse> call, Response<UserOrderResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Đã hủy đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                    // Tải lại dữ liệu trang để nó tự ẩn nút Hủy và đổi chữ thành "Đã hủy"
                    loadOrderDetail();
                } else {
                    btnCancelOrder.setEnabled(true);
                    btnCancelOrder.setText("Hủy Đơn Hàng");
                    Toast.makeText(OrderDetailActivity.this, "Lỗi khi hủy đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserOrderResponse> call, Throwable t) {
                btnCancelOrder.setEnabled(true);
                btnCancelOrder.setText("Hủy Đơn Hàng");
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReceiveConfirmDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận nhận hàng")
                .setMessage("Bạn xác nhận đã nhận được hàng và sản phẩm không có vấn đề gì?")
                .setPositiveButton("Đồng ý", (dialog, which) -> executeReceiveOrder())
                .setNegativeButton("Quay lại", null)
                .show();
    }

    private void executeReceiveOrder() {
        btnReceiveOrder.setEnabled(false);
        btnReceiveOrder.setText("Đang xử lý...");

        ApiClient.getUserOrderApiService(tokenManager).receiveOrder(orderId).enqueue(new Callback<UserOrderResponse>() {
            @Override
            public void onResponse(Call<UserOrderResponse> call, Response<UserOrderResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Xác nhận nhận hàng thành công!", Toast.LENGTH_SHORT).show();
                    loadOrderDetail();
                } else {
                    btnReceiveOrder.setEnabled(true);
                    btnReceiveOrder.setText("Đã Nhận Được Hàng");
                    Toast.makeText(OrderDetailActivity.this, "Lỗi khi xác nhận", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserOrderResponse> call, Throwable t) {
                btnReceiveOrder.setEnabled(true);
                btnReceiveOrder.setText("Đã Nhận Được Hàng");
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showReturnDialog() {
        android.widget.EditText input = new android.widget.EditText(this);
        input.setHint("Nhập lý do trả hàng...");
        input.setPadding(48, 32, 48, 32);

        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu trả hàng / Hoàn tiền")
                .setMessage("Vui lòng cho biết lý do bạn muốn trả hàng:")
                .setView(input)
                .setPositiveButton("Gửi yêu cầu", (dialog, which) -> {
                    String reason = input.getText().toString().trim();
                    if (reason.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập lý do trả hàng!", Toast.LENGTH_SHORT).show();
                    } else {
                        executeReturnOrder(reason);
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void executeReturnOrder(String reason) {
        btnReturnOrder.setEnabled(false);
        btnReturnOrder.setText("Đang xử lý...");

        ApiClient.getUserOrderApiService(tokenManager).requestReturn(orderId, reason).enqueue(new Callback<UserOrderResponse>() {
            @Override
            public void onResponse(Call<UserOrderResponse> call, Response<UserOrderResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Gửi yêu cầu trả hàng thành công!", Toast.LENGTH_LONG).show();
                    loadOrderDetail();
                } else {
                    btnReturnOrder.setEnabled(true);
                    btnReturnOrder.setText("Yêu Cầu Trả Hàng / Hoàn Tiền");
                    Toast.makeText(OrderDetailActivity.this, "Lỗi khi gửi yêu cầu", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserOrderResponse> call, Throwable t) {
                btnReturnOrder.setEnabled(true);
                btnReturnOrder.setText("Yêu Cầu Trả Hàng / Hoàn Tiền");
                Toast.makeText(OrderDetailActivity.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTrackingStepper(String status) {
        if (status == null) return;

        int activeColor = getResources().getColor(R.color.primary_blue);
        int inactiveColor = android.graphics.Color.parseColor("#EEEEEE");

        // Reset
        ivStepPending.setAlpha(0.2f);
        ivStepProcessing.setAlpha(0.2f);
        ivStepShipping.setAlpha(0.2f);
        ivStepDelivered.setAlpha(0.2f);
        line1.setBackgroundColor(inactiveColor);
        line2.setBackgroundColor(inactiveColor);
        line3.setBackgroundColor(inactiveColor);

        switch (status.toUpperCase()) {
            case "PENDING":
                ivStepPending.setAlpha(1.0f);
                break;
            case "CONFIRMED":
                ivStepPending.setAlpha(1.0f);
                ivStepProcessing.setAlpha(1.0f);
                line1.setBackgroundColor(activeColor);
                break;
            case "SHIPPING":
                ivStepPending.setAlpha(1.0f);
                ivStepProcessing.setAlpha(1.0f);
                ivStepShipping.setAlpha(1.0f);
                line1.setBackgroundColor(activeColor);
                line2.setBackgroundColor(activeColor);
                break;
            case "COMPLETED":
                ivStepPending.setAlpha(1.0f);
                ivStepProcessing.setAlpha(1.0f);
                ivStepShipping.setAlpha(1.0f);
                ivStepDelivered.setAlpha(1.0f);
                line1.setBackgroundColor(activeColor);
                line2.setBackgroundColor(activeColor);
                line3.setBackgroundColor(activeColor);
                break;
        }
    }
}