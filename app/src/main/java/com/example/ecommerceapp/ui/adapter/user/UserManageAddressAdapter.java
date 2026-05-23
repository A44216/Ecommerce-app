package com.example.ecommerceapp.ui.adapter.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.UserAddressResponse;
import java.util.ArrayList;
import java.util.List;

public class UserManageAddressAdapter extends RecyclerView.Adapter<UserManageAddressAdapter.ManageAddressViewHolder> {
    public interface OnAddressActionClickListener {
        void onEditClick(UserAddressResponse address);
        void onDeleteClick(UserAddressResponse address);
    }

    private List<UserAddressResponse> addressList = new ArrayList<>();
    private final OnAddressActionClickListener actionListener;

    public UserManageAddressAdapter(OnAddressActionClickListener actionListener) {
        this.actionListener = actionListener;
    }

    public void updateData(List<UserAddressResponse> newAddresses) {
        this.addressList = newAddresses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ManageAddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_address, parent, false);
        return new ManageAddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageAddressViewHolder holder, int position) {
        UserAddressResponse address = addressList.get(position);

        holder.tvReceiverName.setText(address.getFullName());
        holder.tvAddressDetail.setText(address.getFullAddress());
        holder.tvReceiverPhone.setText(address.getPhone());

        holder.tvEditAddress.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onEditClick(address);
            }
        });

        holder.tvDeleteAddress.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDeleteClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public static class ManageAddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvReceiverName, tvReceiverPhone, tvAddressDetail, tvEditAddress, tvDeleteAddress;

        public ManageAddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverName = itemView.findViewById(R.id.tvReceiverName);
            tvReceiverPhone = itemView.findViewById(R.id.tvReceiverPhone);
            tvAddressDetail = itemView.findViewById(R.id.tvAddressDetail);
            tvEditAddress = itemView.findViewById(R.id.tvEditAddress);
            tvDeleteAddress = itemView.findViewById(R.id.tvDeleteAddress);
        }
    }
}
