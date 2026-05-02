package com.example.ecommerceapp.ui.adapter.admin.complaint;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.response.admin.management.complaint.AdminComplaintResponse;
import com.example.ecommerceapp.ui.viewholder.admin.complaint.AdminComplaintVH;

import java.util.Objects;

public class AdminComplaintAdapter extends ListAdapter<AdminComplaintResponse, AdminComplaintVH> {

    private AdminComplaintVH.OnComplaintClickListener listener;

    public AdminComplaintAdapter() {
        super(new DiffUtil.ItemCallback<AdminComplaintResponse>() {
            @Override
            public boolean areItemsTheSame(@NonNull AdminComplaintResponse oldItem, @NonNull AdminComplaintResponse newItem) {
                return Objects.equals(oldItem.getId(), newItem.getId());
            }

            @Override
            public boolean areContentsTheSame(@NonNull AdminComplaintResponse oldItem, @NonNull AdminComplaintResponse newItem) {
                return Objects.equals(oldItem.getStatus(), newItem.getStatus()) &&
                       Objects.equals(oldItem.getContent(), newItem.getContent()) &&
                       Objects.equals(oldItem.getUsername(), newItem.getUsername()) &&
                       Objects.equals(oldItem.getComplaintCode(), newItem.getComplaintCode());
            }
        });
    }

    public void setListener(AdminComplaintVH.OnComplaintClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminComplaintVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_complaint, parent, false);
        return new AdminComplaintVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminComplaintVH holder, int position) {
        holder.bind(getItem(position), listener);
    }
}
