package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import java.util.ArrayList;
import java.util.List;

public class UserAddressAdapter extends RecyclerView.Adapter<UserAddressAdapter.AddressViewHolder> {

    private List<UserAddressResponse> addressList = new ArrayList<>();
    private int selectedPosition = 0; // Mặc định chọn địa chỉ đầu tiên
    private final OnAddressSelectedListener listener;

    public interface OnAddressSelectedListener {
        void onAddressSelected(UserAddressResponse address);
    }

    public UserAddressAdapter(OnAddressSelectedListener listener) {
        this.listener = listener;
    }

    public void updateData(List<UserAddressResponse> newAddresses) {
        this.addressList = newAddresses;
        notifyDataSetChanged();
        // Nếu có dữ liệu, tự động báo cho Activity biết địa chỉ đầu tiên đang được chọn
        if (!newAddresses.isEmpty() && listener != null) {
            listener.onAddressSelected(newAddresses.get(selectedPosition));
        }
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        UserAddressResponse address = addressList.get(position);

        holder.tvReceiverName.setText(address.getFullName());
        holder.tvAddressDetail.setText(address.getFullAddress());

        holder.tvReceiverPhone.setText(address.getPhone());

        // Xử lý Checkbox chỉ chọn 1 cái
        holder.rbSelectAddress.setChecked(position == selectedPosition);

        // Xử lý sự kiện khi bấm vào thẻ
        holder.itemView.setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Cập nhật lại giao diện của thẻ cũ và thẻ mới
            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            // Báo ra ngoài Activity
            if (listener != null) {
                listener.onAddressSelected(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverName, tvReceiverPhone, tvAddressDetail;
        RadioButton rbSelectAddress;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvAddressDetail = itemView.findViewById(R.id.tvAddressDetail);
            rbSelectAddress = itemView.findViewById(R.id.rbSelectAddress);
        }
    }
}