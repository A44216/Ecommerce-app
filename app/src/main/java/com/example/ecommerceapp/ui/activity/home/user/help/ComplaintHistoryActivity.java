package com.example.ecommerceapp.ui.activity.home.user.help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.UserService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.response.ComplaintResponse;
import com.example.ecommerceapp.ui.adapter.user.ComplaintAdapter;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComplaintHistoryActivity extends AppCompatActivity {

    private RecyclerView rvComplaints;
    private LinearLayout layoutEmpty;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_history);

        rvComplaints = findViewById(R.id.rvComplaints);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        rvComplaints.setLayoutManager(new LinearLayoutManager(this));
        tokenManager = TokenManager.getInstance(this);

        loadComplaints();
    }

    private void loadComplaints() {
        long userId = tokenManager.getUserId();
        if (userId == -1) return;

        UserService apiService = ApiClient.getUserService(tokenManager);
        apiService.getMyComplaints(userId).enqueue(new Callback<List<ComplaintResponse>>() {
            @Override
            public void onResponse(Call<List<ComplaintResponse>> call, Response<List<ComplaintResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ComplaintResponse> list = response.body();
                    if (list.isEmpty()) {
                        rvComplaints.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    } else {
                        rvComplaints.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                        ComplaintAdapter adapter = new ComplaintAdapter(list, item -> {
                            String message = "Nội dung khiếu nại:\n" + item.getContent() + "\n\n";
                            if (item.getAdminResponse() != null && !item.getAdminResponse().isEmpty()) {
                                message += "Phản hồi từ Admin:\n" + item.getAdminResponse();
                            } else {
                                message += "Chưa có phản hồi từ Admin.";
                            }
                            
                            new androidx.appcompat.app.AlertDialog.Builder(ComplaintHistoryActivity.this)
                                    .setTitle("Chi tiết khiếu nại #" + item.getId())
                                    .setMessage(message)
                                    .setPositiveButton("Đóng", null)
                                    .show();
                        });
                        rvComplaints.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ComplaintResponse>> call, Throwable t) {
                // Xử lý lỗi
            }
        });
    }
}
