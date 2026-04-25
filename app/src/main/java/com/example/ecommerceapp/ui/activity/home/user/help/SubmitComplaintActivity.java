package com.example.ecommerceapp.ui.activity.home.user.help;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.ComplaintRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitComplaintActivity extends AppCompatActivity {

    private EditText edtContent;
    private Button btnSubmit;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_complaint);

        tokenManager = TokenManager.getInstance(this);
        edtContent = findViewById(R.id.edtComplaintContent);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnSubmit.setOnClickListener(v -> {
            String content = edtContent.getText().toString().trim();
            if (content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung yêu cầu", Toast.LENGTH_SHORT).show();
                return;
            }
            submitComplaint(content);
        });
    }

    private void submitComplaint(String content) {
        long userId = tokenManager.getUserId();
        if (userId == -1) return;

        ComplaintRequest request = new ComplaintRequest((int) userId, null, content);

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang gửi...");

        ApiClient.getUserService(tokenManager).submitComplaint(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(SubmitComplaintActivity.this, "Gửi yêu cầu thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setText("Gửi yêu cầu ngay");
                    Toast.makeText(SubmitComplaintActivity.this, "Gửi thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("Gửi yêu cầu ngay");
                Toast.makeText(SubmitComplaintActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
