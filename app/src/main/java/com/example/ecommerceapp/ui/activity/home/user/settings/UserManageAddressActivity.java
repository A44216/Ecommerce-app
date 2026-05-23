package com.example.ecommerceapp.ui.activity.home.user.settings;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserAddressApiService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import com.example.ecommerceapp.ui.activity.home.user.checkout.UserAddAddressActivity;
import com.example.ecommerceapp.ui.adapter.user.UserManageAddressAdapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserManageAddressActivity extends AppCompatActivity {

    private RecyclerView rvManageAddresses;
    private UserManageAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage_address);

        ImageView btnBack = findViewById(R.id.btnManageAddressBack);
        btnBack.setOnClickListener(v -> finish());

        Button btnAddAddress = findViewById(R.id.btnAddAddress);
        btnAddAddress.setOnClickListener(v -> {
            Intent intent = new Intent(UserManageAddressActivity.this, UserAddAddressActivity.class);
            startActivity(intent);
        });

        rvManageAddresses = findViewById(R.id.rvManageAddresses);
        rvManageAddresses.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new UserManageAddressAdapter(new UserManageAddressAdapter.OnAddressActionClickListener() {
            @Override
            public void onEditClick(UserAddressResponse address) {
                Intent intent = new Intent(UserManageAddressActivity.this, UserAddAddressActivity.class);
                intent.putExtra("ADDRESS_ID", address.getId());
                intent.putExtra("FULL_NAME", address.getFullName());
                intent.putExtra("PHONE", address.getPhone());
                intent.putExtra("CITY", address.getCity());
                intent.putExtra("DISTRICT", address.getDistrict());
                intent.putExtra("WARD", address.getWard());
                intent.putExtra("ADDRESS_LINE", address.getAddressLine());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(UserAddressResponse address) {
                new androidx.appcompat.app.AlertDialog.Builder(UserManageAddressActivity.this)
                        .setTitle("Xóa địa chỉ")
                        .setMessage("Bạn có chắc chắn muốn xóa địa chỉ này?")
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            TokenManager tokenManager = TokenManager.getInstance(UserManageAddressActivity.this);
                            UserAddressApiService apiService = ApiClient.getUserAddressApiService(tokenManager);
                            apiService.deleteAddress(address.getId()).enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(UserManageAddressActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
                                        loadAddresses();
                                    } else {
                                        Toast.makeText(UserManageAddressActivity.this, "Lỗi khi xóa: " + response.code(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                    Toast.makeText(UserManageAddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            }
        });
        rvManageAddresses.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        TokenManager tokenManager = TokenManager.getInstance(this);
        int userId = (int) tokenManager.getUserId();

        if (userId != -1) {
            UserAddressApiService apiService = ApiClient.getUserAddressApiService(tokenManager);
            apiService.getAddressesByUserId(userId).enqueue(new Callback<List<UserAddressResponse>>() {
                @Override
                public void onResponse(Call<List<UserAddressResponse>> call, Response<List<UserAddressResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.updateData(response.body());
                    } else {
                        Toast.makeText(UserManageAddressActivity.this, "Không thể tải danh sách địa chỉ", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<UserAddressResponse>> call, Throwable t) {
                    Toast.makeText(UserManageAddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
        }
    }
}
