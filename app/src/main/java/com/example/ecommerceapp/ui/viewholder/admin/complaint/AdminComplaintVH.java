package com.example.ecommerceapp.ui.viewholder.admin.complaint;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.complaint.AdminComplaintResponse;
import java.time.format.DateTimeFormatter;

public class AdminComplaintVH extends RecyclerView.ViewHolder {

    private final TextView tvComplaintCode;
    private final TextView tvComplaintStatus;
    private final TextView tvUsername;
    private final TextView tvContent;
    private final TextView tvCreatedAt;

    public AdminComplaintVH(@NonNull View itemView) {
        super(itemView);
        tvComplaintCode = itemView.findViewById(R.id.tvComplaintCode);
        tvComplaintStatus = itemView.findViewById(R.id.tvComplaintStatus);
        tvUsername = itemView.findViewById(R.id.tvUsername);
        tvContent = itemView.findViewById(R.id.tvContent);
        tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
    }

    @SuppressLint("SetTextI18n")
    public void bind(AdminComplaintResponse complaint, OnComplaintClickListener listener) {
        tvComplaintCode.setText("Mã: " + (complaint.getComplaintCode() != null ? complaint.getComplaintCode() : "N/A"));
        tvUsername.setText("Người dùng: " + (complaint.getUsername() != null ? complaint.getUsername() : "N/A"));
        tvContent.setText("Nội dung: " + (complaint.getContent() != null ? complaint.getContent() : ""));

        if (complaint.getCreatedAt() != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            tvCreatedAt.setText("Ngày tạo: " + complaint.getCreatedAt().format(formatter));
        } else {
            tvCreatedAt.setText("Ngày tạo: N/A");
        }

        // Set status and color
        if (complaint.getStatus() != null) {
            switch (complaint.getStatus()) {
                case "PENDING":
                    tvComplaintStatus.setText("Chờ xử lý");
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.orange));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                    break;
                case "RESOLVED":
                    tvComplaintStatus.setText("Đã giải quyết");
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.green));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_approved);
                    break;
                case "REJECTED":
                    tvComplaintStatus.setText("Đã từ chối");
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.red));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_rejected);
                    break;
                default:
                    tvComplaintStatus.setText(complaint.getStatus());
                    tvComplaintStatus.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.gray));
                    tvComplaintStatus.setBackgroundResource(R.drawable.bg_shop_status_pending);
                    break;
            }
        }

        if (listener != null) {
            itemView.setOnClickListener(v -> listener.onComplaintClick(complaint));
        }
    }

    public interface OnComplaintClickListener {
        void onComplaintClick(AdminComplaintResponse complaint);
    }
}
