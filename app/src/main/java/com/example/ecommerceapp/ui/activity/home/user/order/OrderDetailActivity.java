package com.example.ecommerceapp.ui.activity.home.user.order;

import android.content.DialogInterface;
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
    private TextView tvStatus, tvAddress, tvTotal, tvOrderDate, tvSubTotal;
    private RecyclerView rvItems;
    private Button btnCancelOrder;
    private View ivStepPending, ivStepProcessing, ivStepShipping, ivStepDelivered;
    private View line1, line2, line3;
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

        rvItems = findViewById(R.id.rvOrderDetailItems);
        btnCancelOrder = findViewById(R.id.btnCancelOrder);
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
                        tvSubTotal.setText(priceFormatted); // Set giá trị cho Tổng tiền hàng
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

                    // Cập nhật Stepper
                    updateTrackingStepper(order.getStatus());

                    // ==========================================
                    // LOGIC ẨN/HIỆN NÚT HỦY ĐƠN HÀNG
                    // ==========================================
                    String currentStatus = order.getStatus();

                    if (currentStatus != null && currentStatus.trim().equalsIgnoreCase("PENDING")) {
                        btnCancelOrder.setVisibility(View.VISIBLE);
                        tvStatus.setText("Chờ xác nhận");
                    } else {
                        btnCancelOrder.setVisibility(View.GONE);
                        // Dịch các trạng thái khác sang tiếng Việt cho đẹp
                        if (currentStatus != null) {
                            switch (currentStatus.toUpperCase()) {
                                case "CONFIRMED": tvStatus.setText("Đang chuẩn bị hàng"); break;
                                case "SHIPPING": tvStatus.setText("Đang giao hàng"); break;
                                case "COMPLETED": tvStatus.setText("Đã giao thành công"); break;
                                case "CANCELED": tvStatus.setText("Đã hủy"); break;
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