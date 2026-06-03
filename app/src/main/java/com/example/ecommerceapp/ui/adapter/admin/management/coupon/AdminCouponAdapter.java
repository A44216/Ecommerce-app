package com.example.ecommerceapp.ui.adapter.admin.management.coupon;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.enums.CouponStatus;
import com.example.ecommerceapp.data.model.response.admin.management.coupon.AdminCouponResponse;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class AdminCouponAdapter extends ListAdapter<AdminCouponResponse, AdminCouponAdapter.CouponViewHolder> {

    private OnItemClickListener listener;
    private boolean isDeletedTab = false;

    public interface OnItemClickListener {
        void onItemClick(AdminCouponResponse coupon);
        void onItemLongClick(AdminCouponResponse coupon, View view);
    }

    public AdminCouponAdapter() {
        super(new DiffUtil.ItemCallback<AdminCouponResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminCouponResponse oldItem, @NonNull AdminCouponResponse newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull AdminCouponResponse oldItem, @NonNull AdminCouponResponse newItem) {
                return Objects.equals(oldItem.getCode(), newItem.getCode()) &&
                       oldItem.getStatus() == newItem.getStatus() &&
                       Objects.equals(oldItem.getUsedCount(), newItem.getUsedCount()) &&
                       Objects.equals(oldItem.getMaxUsage(), newItem.getMaxUsage()) &&
                       Objects.equals(oldItem.getDiscountPercent(), newItem.getDiscountPercent()) &&
                       Objects.equals(oldItem.getDiscountAmount(), newItem.getDiscountAmount()) &&
                       Objects.equals(oldItem.getMinOrderValue(), newItem.getMinOrderValue()) &&
                       Objects.equals(oldItem.getStartDate(), newItem.getStartDate()) &&
                       Objects.equals(oldItem.getEndDate(), newItem.getEndDate());
            }
        });
    }

    public void setDeletedTab(boolean deletedTab) {
        this.isDeletedTab = deletedTab;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_coupon, parent, false);
        return new CouponViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        AdminCouponResponse item = getItem(position);
        holder.bind(item, isDeletedTab);
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
                    listener.onItemClick(getItem(pos));
                }
            });

            itemView.setOnLongClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemLongClick(getItem(pos), v);
                    return true;
                }
                return false;
            });
        }

        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        public void bind(AdminCouponResponse item, boolean deletedTab) {
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
                tvDateRange.setText("");
            }

            int used = item.getUsedCount() != null ? item.getUsedCount() : 0;
            if (item.getMaxUsage() == null) {
                tvUsageCount.setText(used + " / Không giới hạn");
                pbUsage.setVisibility(View.GONE);
            } else {
                int max = item.getMaxUsage();
                tvUsageCount.setText(used + "/" + max);
                pbUsage.setVisibility(View.VISIBLE);
                pbUsage.setMax(max);
                pbUsage.setProgress(used);
            }

            String minOrder = "0";
            if (item.getMinOrderValue() != null) {
                minOrder = String.format("%,d", item.getMinOrderValue().longValue()).replace(',', '.');
            }
            tvMinOrder.setText("Đơn tối thiểu: " + minOrder + " đ");

            if (deletedTab) {
                tvStatus.setText("Đã xóa");
                tvStatus.setTextColor(Color.parseColor("#9E9E9E"));
                tvStatus.setBackgroundColor(Color.parseColor("#F5F5F5"));
            } else {
                setupStatus(item.getStatus());
            }
        }

        @SuppressLint("SetTextI18n")
        private void setupStatus(CouponStatus status) {
            if (status == null) return;
            
            switch (status) {
                case ACTIVE:
                    tvStatus.setText("Hoạt động");
                    tvStatus.setTextColor(Color.parseColor("#4CAF50"));
                    tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    break;
                case DISABLED:
                    tvStatus.setText("Vô hiệu");
                    tvStatus.setTextColor(Color.parseColor("#FF9800"));
                    tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
                    break;
                case EXPIRED:
                    tvStatus.setText("Hết hạn");
                    tvStatus.setTextColor(Color.parseColor("#F44336"));
                    tvStatus.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    break;
                default:
                    tvStatus.setText("Đã xóa");
                    tvStatus.setTextColor(Color.parseColor("#9E9E9E"));
                    tvStatus.setBackgroundColor(Color.parseColor("#F5F5F5"));
                    break;
            }
        }
    }
}
