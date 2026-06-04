package com.example.ecommerceapp.ui.activity.home.user.checkout;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserAddressApiService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.UserAddressRequest;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import com.example.ecommerceapp.data.repository.UserAddressRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAddAddressActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etCity, etDistrict, etWard, etAddressLine;
    private Button btnSaveAddress;
    private UserAddressRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_add_address);

        // 1. Ánh xạ
        ImageView btnBack = findViewById(R.id.btnAddAddressBack);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etCity = findViewById(R.id.etCity);
        etDistrict = findViewById(R.id.etDistrict);
        etWard = findViewById(R.id.etWard);
        etAddressLine = findViewById(R.id.etAddressLine);
        btnSaveAddress = findViewById(R.id.btnSaveAddress);

        btnBack.setOnClickListener(v -> finish());

        // 2. Khởi tạo API
        TokenManager tokenManager = TokenManager.getInstance(this);
        UserAddressApiService apiService = ApiClient.getUserAddressApiService(tokenManager);
        repository = new UserAddressRepository(apiService);

        // 3. Xử lý logic Edit (Nếu có truyền data từ Intent)
        int editAddressId = getIntent().getIntExtra("ADDRESS_ID", -1);
        boolean isEditMode = (editAddressId != -1);

        if (isEditMode) {
            etFullName.setText(getIntent().getStringExtra("FULL_NAME"));
            etPhone.setText(getIntent().getStringExtra("PHONE"));
            etCity.setText(getIntent().getStringExtra("CITY"));
            etDistrict.setText(getIntent().getStringExtra("DISTRICT"));
            etWard.setText(getIntent().getStringExtra("WARD"));
            etAddressLine.setText(getIntent().getStringExtra("ADDRESS_LINE"));
            btnSaveAddress.setText("Cập nhật địa chỉ");
        }

        // 4. Sự kiện Lưu / Cập Nhật Địa Chỉ
        btnSaveAddress.setOnClickListener(v -> {
            String name = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String district = etDistrict.getText().toString().trim();
            String ward = etWard.getText().toString().trim();
            String line = etAddressLine.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(line)) {
                Toast.makeText(this, "Vui lòng nhập đủ Tên, SĐT và Số nhà!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại không hợp lệ (Phải gồm 10 số và bắt đầu bằng số 0)!", Toast.LENGTH_SHORT).show();
                return;
            }

            int userId = (int) tokenManager.getUserId();
            if (userId == -1) {
                Toast.makeText(this, "Lỗi xác thực người dùng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Gói thành Request (mặc định cho isDefault = false)
            UserAddressRequest request = new UserAddressRequest(userId, name, phone, line, city, district, ward, false);

            if (isEditMode) {
                // Gọi API Update
                repository.updateAddress(editAddressId, request).enqueue(new Callback<UserAddressResponse>() {
                    @Override
                    public void onResponse(Call<UserAddressResponse> call, Response<UserAddressResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UserAddAddressActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UserAddAddressActivity.this, "Cập nhật thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserAddressResponse> call, Throwable t) {
                        Toast.makeText(UserAddAddressActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                // Bắn API Create
                repository.createAddress(request).enqueue(new Callback<UserAddressResponse>() {
                    @Override
                    public void onResponse(Call<UserAddressResponse> call, Response<UserAddressResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UserAddAddressActivity.this, "Thêm địa chỉ thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(UserAddAddressActivity.this, "Thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<UserAddressResponse> call, Throwable t) {
                        Toast.makeText(UserAddAddressActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}