package com.example.ecommerceapp.ui.activity.home.admin.management;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.SystemNotificationRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCreateNotificationActivity extends AppCompatActivity {

    private ImageView ivBack;
    private Spinner spinnerType;
    private EditText edtTitle, edtBody;
    private Button btnBroadcast;

    private TokenManager tokenManager;

    private final String[] notificationTypes = {
            "PROMOTION", // Khuyến mãi
            "SYSTEM",    // Cập nhật hệ thống
            "AWARDS"     // Giải thưởng
    };

    private final String[] notificationTypeDisplayNames = {
            "Khuyến mãi (PROMOTION)",
            "Hệ thống (SYSTEM)",
            "Giải thưởng (AWARDS)"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_create_notification);

        tokenManager = TokenManager.getInstance(this);

        ivBack = findViewById(R.id.ivBack);
        spinnerType = findViewById(R.id.spinnerType);
        edtTitle = findViewById(R.id.edtTitle);
        edtBody = findViewById(R.id.edtBody);
        btnBroadcast = findViewById(R.id.btnBroadcast);

        ivBack.setOnClickListener(v -> finish());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, notificationTypeDisplayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        btnBroadcast.setOnClickListener(v -> broadcastNotification());
    }

    private void broadcastNotification() {
        String title = edtTitle.getText().toString().trim();
        String body = edtBody.getText().toString().trim();
        int selectedIndex = spinnerType.getSelectedItemPosition();

        if (title.isEmpty()) {
            edtTitle.setError("Vui lòng nhập tiêu đề");
            edtTitle.requestFocus();
            return;
        }

        if (body.isEmpty()) {
            edtBody.setError("Vui lòng nhập nội dung");
            edtBody.requestFocus();
            return;
        }

        String type = notificationTypes[selectedIndex];

        SystemNotificationRequest request = new SystemNotificationRequest(title, body, type);

        btnBroadcast.setEnabled(false);
        btnBroadcast.setText("Đang gửi...");

        ApiClient.getAdminNotificationService(tokenManager).broadcastNotification(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                btnBroadcast.setEnabled(true);
                btnBroadcast.setText("Gửi thông báo (Broadcast)");

                if (response.isSuccessful()) {
                    Toast.makeText(AdminCreateNotificationActivity.this, "Đã gửi thông báo thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AdminCreateNotificationActivity.this, "Gửi thất bại. Mã lỗi: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnBroadcast.setEnabled(true);
                btnBroadcast.setText("Gửi thông báo (Broadcast)");
                Toast.makeText(AdminCreateNotificationActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
