package com.example.ecommerceapp.ui.activity.home.admin.management.coupon;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCouponService;
import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.admin.management.AdminCouponRequest;
import com.example.ecommerceapp.data.model.response.admin.management.coupon.AdminCouponResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAddAndEditCouponActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvTitle;
    private TextInputEditText etCode, etDiscountPercent, etDiscountAmount, etMaxDiscountAmount, etMinOrderValue, etMaxUsage, etStartDate, etEndDate;
    private TextInputLayout tilDiscountPercent, tilDiscountAmount, tilMaxDiscountAmount, tilStatus;
    private AutoCompleteTextView etStatus;
    private RadioGroup rgDiscountType;
    private RadioButton rbPercent, rbAmount;
    private MaterialButton btnAddVoucher, btnDeleteVoucher, btnRestoreVoucher;

    private AdminCouponService couponService;
    private int couponId = -1;
    private boolean isEdit = false;
    private boolean isDeletedCoupon = false;

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private CouponStatus originalStatus;
    private CouponStatus selectedStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_add_and_edit_coupon);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        couponService = ApiClient.getAdminCouponService(TokenManager.getInstance(this));

        couponId = getIntent().getIntExtra("couponId", -1);
        isEdit = couponId != -1;
        isDeletedCoupon = getIntent().getBooleanExtra("isDeleted", false);

        initViews();
        setupToolbar();
        setupActions();

        if (isEdit) {
            tilStatus.setVisibility(View.VISIBLE);
            setupStatusDropdown();
            loadCouponData();
        } else {
            tilStatus.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);

        etCode = findViewById(R.id.etCode);
        etDiscountPercent = findViewById(R.id.etDiscountPercent);
        etDiscountAmount = findViewById(R.id.etDiscountAmount);
        etMaxDiscountAmount = findViewById(R.id.etMaxDiscountAmount);
        etMinOrderValue = findViewById(R.id.etMinOrderValue);
        etMaxUsage = findViewById(R.id.etMaxUsage);
        etStartDate = findViewById(R.id.etStartDate);
        etEndDate = findViewById(R.id.etEndDate);

        tilDiscountPercent = findViewById(R.id.tilDiscountPercent);
        tilDiscountAmount = findViewById(R.id.tilDiscountAmount);
        tilMaxDiscountAmount = findViewById(R.id.tilMaxDiscountAmount);
        tilStatus = findViewById(R.id.tilStatus);
        etStatus = findViewById(R.id.etStatus);

        rgDiscountType = findViewById(R.id.rgDiscountType);
        rbPercent = findViewById(R.id.rbPercent);
        rbAmount = findViewById(R.id.rbAmount);
        btnAddVoucher = findViewById(R.id.btnAddVoucher);
        btnDeleteVoucher = findViewById(R.id.btnDeleteVoucher);
        btnRestoreVoucher = findViewById(R.id.btnRestoreVoucher);

        if (isEdit) {
            tvTitle.setText("CẬP NHẬT VOUCHER");
            btnAddVoucher.setText("Cập nhật");
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupStatusDropdown() {
        String[] statuses = new String[]{"Hoạt động", "Vô hiệu"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, statuses);
        etStatus.setAdapter(adapter);

        etStatus.setOnItemClickListener((parent, view, position, id) -> {
            if (position == 0) {
                selectedStatus = CouponStatus.ACTIVE;
            } else {
                selectedStatus = CouponStatus.DISABLED;
            }
        });
    }

    private void setupActions() {
        rgDiscountType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbPercent) {
                tilDiscountPercent.setVisibility(View.VISIBLE);
                tilDiscountAmount.setVisibility(View.GONE);
                tilMaxDiscountAmount.setVisibility(View.VISIBLE);
            } else {
                tilDiscountPercent.setVisibility(View.GONE);
                tilDiscountAmount.setVisibility(View.VISIBLE);
                tilMaxDiscountAmount.setVisibility(View.GONE);
                etMaxDiscountAmount.setText("");
            }
        });

        etStartDate.setOnClickListener(v -> showDatePicker(true));
        etEndDate.setOnClickListener(v -> showDatePicker(false));

        btnAddVoucher.setOnClickListener(v -> {
            if (validateForm()) {
                showConfirmDialog();
            }
        });

        btnDeleteVoucher.setOnClickListener(v -> showDeleteConfirmDialog());
        btnRestoreVoucher.setOnClickListener(v -> showRestoreConfirmDialog());
    }

    private void showDeleteConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn xóa coupon này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    couponService.deleteCoupon(couponId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AdminAddAndEditCouponActivity.this, "Đã xóa coupon", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi xóa coupon", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showRestoreConfirmDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc chắn muốn khôi phục coupon này?")
                .setPositiveButton("Khôi phục", (dialog, which) -> {
                    couponService.restoreCoupon(couponId).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(AdminAddAndEditCouponActivity.this, "Đã khôi phục coupon", Toast.LENGTH_SHORT).show();
                                originalStatus = CouponStatus.ACTIVE;
                                selectedStatus = CouponStatus.ACTIVE;
                                isDeletedCoupon = false;
                                etStatus.setText("Hoạt động", false);
                                enableAllInputs();
                                btnDeleteVoucher.setVisibility(View.VISIBLE);
                                btnAddVoucher.setVisibility(View.VISIBLE);
                                btnRestoreVoucher.setVisibility(View.GONE);
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi khôi phục coupon", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                            Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void disableAllInputs() {
        etCode.setEnabled(false);
        rgDiscountType.setEnabled(false);
        rbPercent.setEnabled(false);
        rbAmount.setEnabled(false);
        etDiscountPercent.setEnabled(false);
        etDiscountAmount.setEnabled(false);
        etMaxDiscountAmount.setEnabled(false);
        etMinOrderValue.setEnabled(false);
        etMaxUsage.setEnabled(false);
        etStartDate.setEnabled(false);
        etEndDate.setEnabled(false);
        tilStatus.setEnabled(false);
    }

    private void enableAllInputs() {
        etCode.setEnabled(true);
        rgDiscountType.setEnabled(true);
        rbPercent.setEnabled(true);
        rbAmount.setEnabled(true);
        etDiscountPercent.setEnabled(true);
        etDiscountAmount.setEnabled(true);
        etMaxDiscountAmount.setEnabled(true);
        etMinOrderValue.setEnabled(true);
        etMaxUsage.setEnabled(true);
        etStartDate.setEnabled(true);
        etEndDate.setEnabled(true);
        tilStatus.setEnabled(true);
    }

    private void showConfirmDialog() {
        String message = isEdit ? "Bạn có chắc chắn muốn cập nhật coupon này?" : "Bạn có chắc chắn muốn tạo coupon mới?";
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận")
                .setMessage(message)
                .setPositiveButton("Đồng ý", (dialog, which) -> submitForm())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showDatePicker(boolean isStart) {
        Calendar calendar = Calendar.getInstance();
        if (isStart && startDateTime != null) {
            calendar.set(startDateTime.getYear(), startDateTime.getMonthValue() - 1, startDateTime.getDayOfMonth());
        } else if (!isStart && endDateTime != null) {
            calendar.set(endDateTime.getYear(), endDateTime.getMonthValue() - 1, endDateTime.getDayOfMonth());
        }

        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDateTime selectedDate = LocalDateTime.of(year, month + 1, dayOfMonth, 0, 0);
            if (isStart) {
                startDateTime = selectedDate;
                etStartDate.setText(startDateTime.format(formatter));
            } else {
                endDateTime = selectedDate.withHour(23).withMinute(59).withSecond(59);
                etEndDate.setText(endDateTime.format(formatter));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void loadCouponData() {
        couponService.getCouponById(couponId, isDeletedCoupon).enqueue(new Callback<AdminCouponResponse>() {
            @Override
            public void onResponse(@NonNull Call<AdminCouponResponse> call, @NonNull Response<AdminCouponResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    bindData(response.body());
                } else {
                    Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi tải thông tin coupon", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AdminCouponResponse> call, @NonNull Throwable t) {
                Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void bindData(AdminCouponResponse coupon) {
        etCode.setText(coupon.getCode());
        
        if (coupon.getDiscountPercent() != null) {
            rbPercent.setChecked(true);
            etDiscountPercent.setText(String.valueOf(coupon.getDiscountPercent()));
        } else if (coupon.getDiscountAmount() != null) {
            rbAmount.setChecked(true);
            etDiscountAmount.setText(coupon.getDiscountAmount().toPlainString());
        }

        if (coupon.getMaxDiscountAmount() != null) {
            etMaxDiscountAmount.setText(coupon.getMaxDiscountAmount().toPlainString());
        }
        if (coupon.getMinOrderValue() != null) {
            etMinOrderValue.setText(coupon.getMinOrderValue().toPlainString());
        }
        if (coupon.getMaxUsage() != null) {
            etMaxUsage.setText(String.valueOf(coupon.getMaxUsage()));
        }

        startDateTime = coupon.getStartDate();
        endDateTime = coupon.getEndDate();
        
        if (startDateTime != null) {
            etStartDate.setText(startDateTime.format(formatter));
        }
        if (endDateTime != null) {
            etEndDate.setText(endDateTime.format(formatter));
        }

        originalStatus = coupon.getStatus();
        selectedStatus = originalStatus;

        if (isDeletedCoupon) {
            etStatus.setText("Đã xóa", false);
            disableAllInputs();
            btnDeleteVoucher.setVisibility(View.GONE);
            btnAddVoucher.setVisibility(View.GONE);
            btnRestoreVoucher.setVisibility(View.VISIBLE);
        } else {
            if (originalStatus == CouponStatus.ACTIVE) {
                etStatus.setText("Hoạt động", false);
            } else if (originalStatus == CouponStatus.DISABLED) {
                etStatus.setText("Vô hiệu", false);
            } else if (originalStatus == CouponStatus.EXPIRED) {
                etStatus.setText("Hết hạn", false);
                tilStatus.setEnabled(false);
            }

            if (isEdit) {
                btnDeleteVoucher.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean validateForm() {
        String code = etCode.getText().toString().trim();
        if (code.isEmpty()) {
            etCode.setError("Không được để trống");
            return false;
        }

        if (rbPercent.isChecked()) {
            String pctStr = etDiscountPercent.getText().toString();
            if (pctStr.isEmpty()) {
                etDiscountPercent.setError("Vui lòng nhập phần trăm");
                return false;
            }
            int pct = Integer.parseInt(pctStr);
            if (pct < 0 || pct > 100) {
                etDiscountPercent.setError("Phần trăm phải từ 0 - 100");
                return false;
            }
        } else {
            String amtStr = etDiscountAmount.getText().toString();
            if (amtStr.isEmpty()) {
                etDiscountAmount.setError("Vui lòng nhập số tiền");
                return false;
            }
            if (new BigDecimal(amtStr).compareTo(BigDecimal.ZERO) < 0) {
                etDiscountAmount.setError("Số tiền phải >= 0");
                return false;
            }
        }

        String minOrder = etMinOrderValue.getText().toString();
        if (!minOrder.isEmpty() && new BigDecimal(minOrder).compareTo(BigDecimal.ZERO) < 0) {
            etMinOrderValue.setError("Đơn tối thiểu phải >= 0");
            return false;
        }

        if (startDateTime == null) {
            etStartDate.setError("Vui lòng chọn ngày bắt đầu");
            return false;
        } else {
            etStartDate.setError(null);
        }

        if (endDateTime == null) {
            etEndDate.setError("Vui lòng chọn ngày kết thúc");
            return false;
        } else {
            etEndDate.setError(null);
        }

        if (!endDateTime.isAfter(startDateTime)) {
            etEndDate.setError("Ngày kết thúc phải sau ngày bắt đầu");
            Toast.makeText(this, "Ngày kết thúc phải sau ngày bắt đầu", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void submitForm() {
        AdminCouponRequest request = new AdminCouponRequest();
        request.setCode(etCode.getText().toString().trim());

        if (rbPercent.isChecked()) {
            request.setDiscountPercent(Integer.parseInt(etDiscountPercent.getText().toString()));
            request.setDiscountAmount(null);
            
            String maxDiscountStr = etMaxDiscountAmount.getText().toString();
            if (!maxDiscountStr.isEmpty()) {
                request.setMaxDiscountAmount(new BigDecimal(maxDiscountStr));
            }
        } else {
            request.setDiscountAmount(new BigDecimal(etDiscountAmount.getText().toString()));
            request.setDiscountPercent(null);
            request.setMaxDiscountAmount(null);
        }

        String minOrderStr = etMinOrderValue.getText().toString();
        if (!minOrderStr.isEmpty()) {
            request.setMinOrderValue(new BigDecimal(minOrderStr));
        }

        String maxUsageStr = etMaxUsage.getText().toString();
        if (!maxUsageStr.isEmpty()) {
            request.setMaxUsage(Integer.parseInt(maxUsageStr));
        }

        request.setStartDate(startDateTime);
        request.setEndDate(endDateTime);

        if (isEdit) {
            couponService.updateCoupon(couponId, request).enqueue(new Callback<AdminCouponResponse>() {
                @Override
                public void onResponse(@NonNull Call<AdminCouponResponse> call, @NonNull Response<AdminCouponResponse> response) {
                    if (response.isSuccessful()) {
                        checkAndUpdateStatus();
                    } else {
                        Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi cập nhật", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AdminCouponResponse> call, @NonNull Throwable t) {
                    Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            couponService.createCoupon(request).enqueue(new Callback<AdminCouponResponse>() {
                @Override
                public void onResponse(@NonNull Call<AdminCouponResponse> call, @NonNull Response<AdminCouponResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminAddAndEditCouponActivity.this, "Tạo thành công", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi tạo mới", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<AdminCouponResponse> call, @NonNull Throwable t) {
                    Toast.makeText(AdminAddAndEditCouponActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void checkAndUpdateStatus() {
        if (selectedStatus != null && selectedStatus != originalStatus && selectedStatus != CouponStatus.EXPIRED) {
            if (selectedStatus == CouponStatus.ACTIVE) {
                couponService.enableCoupon(couponId).enqueue(statusUpdateCallback());
            } else if (selectedStatus == CouponStatus.DISABLED) {
                couponService.disableCoupon(couponId).enqueue(statusUpdateCallback());
            }
        } else {
            Toast.makeText(AdminAddAndEditCouponActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }
    }

    private Callback<Void> statusUpdateCallback() {
        return new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Toast.makeText(AdminAddAndEditCouponActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(AdminAddAndEditCouponActivity.this, "Cập nhật trạng thái bị lỗi", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        };
    }
}
