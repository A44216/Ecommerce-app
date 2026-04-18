package com.example.ecommerceapp.ui.adapter.admin.profile;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.model.response.admin.profile.AdminCouponResponse;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminCouponAdapter extends RecyclerView.Adapter<AdminCouponAdapter.CouponViewHolder> {

    private final Context context;
    private final List<AdminCouponResponse> list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AdminCouponResponse coupon);
        void onItemLongClick(AdminCouponResponse coupon, View view);
    }

    public AdminCouponAdapter(Context context) {
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<AdminCouponResponse> newList) {
        list.clear();
        if (newList != null) {
            list.addAll(newList);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_coupon, parent, false);
        return new CouponViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        AdminCouponResponse item = list.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class CouponViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCouponCode, tvStatus, tvDiscountPercent, tvDateRange, tvUsageCount, tvMinOrder;
        private final ProgressBar pbUsage;

        public CouponViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDiscountPercent = itemView.findViewById(R.id.tvDiscountPercent);
            tvDateRange = itemView.findViewById(R.id.tvDateRange);
            tvUsageCount = itemView.findViewById(R.id.tvUsageCount);
            tvMinOrder = itemView.findViewById(R.id.tvMinOrder);
            pbUsage = itemView.findViewById(R.id.pbUsage);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(list.get(pos));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(list.get(pos), v);
                    return true;
                }
                return false;
            });
        }

        public void bind(AdminCouponResponse item) {
            tvCouponCode.setText(item.getCode());

            if (item.getDiscountPercent() != null) {
                tvDiscountPercent.setText("-" + item.getDiscountPercent() + "%");
            } else if (item.getDiscountAmount() != null) {
                String amount = String.format("%,d", item.getDiscountAmount().longValue()).replace(',', '.');
                tvDiscountPercent.setText("-" + amount + "đ");
            } else {
                tvDiscountPercent.setText("N/A");
            }

            DateTimeFormatter formatter = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String start = item.getStartDate() != null ? item.getStartDate().format(formatter) : "";
                String end = item.getEndDate() != null ? item.getEndDate().format(formatter) : "";
                tvDateRange.setText(start + " - " + end);
            } else {
                tvDateRange.setText(""); // Fallback cho API thấp
            }

            int used = item.getUsedCount() != null ? item.getUsedCount() : 0;
            int max = item.getMaxUsage() != null ? item.getMaxUsage() : 1;
            tvUsageCount.setText(used + "/" + max);

            pbUsage.setMax(max);
            pbUsage.setProgress(used);

            String minOrder = "0";
            if (item.getMinOrderValue() != null) {
                minOrder = String.format("%,d", item.getMinOrderValue().longValue()).replace(',', '.');
            }
            tvMinOrder.setText("Đơn tối thiểu: " + minOrder + " đ");

            setupStatus(item.getStatus(), item);
        }

        private void setupStatus(CouponStatus status, AdminCouponResponse item) {
            if (status == null) return;
            
            switch (status) {
                case ACTIVE:
                    tvStatus.setText("Hoạt động");
                    tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
                    tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    break;
                case DISABLED:
                    tvStatus.setText("Vô hiệu");
                    tvStatus.setTextColor(Color.parseColor("#FF9800")); // Orange
                    tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    break;
                case EXPIRED:
                    tvStatus.setText("Hết hạn");
                    tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
                    tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    break;
                default:
                    tvStatus.setText("Đã xóa");
                    tvStatus.setTextColor(Color.parseColor("#9E9E9E")); // Gray
                    tvStatus.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    break;
            }
        }
    }
}
