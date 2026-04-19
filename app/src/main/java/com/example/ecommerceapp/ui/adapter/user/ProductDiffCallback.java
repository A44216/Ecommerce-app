package com.example.ecommerceapp.ui.adapter.user;

import androidx.recyclerview.widget.DiffUtil;
import com.example.ecommerceapp.data.model.response.UserProductResponse;
import java.util.List;
import java.util.Objects;

public class ProductDiffCallback extends DiffUtil.Callback {

    private final List<UserProductResponse> oldList;
    private final List<UserProductResponse> newList;

    public ProductDiffCallback(List<UserProductResponse> oldList, List<UserProductResponse> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return Objects.equals(oldList.get(oldItemPosition).getId(), newList.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        UserProductResponse oldItem = oldList.get(oldItemPosition);
        UserProductResponse newItem = newList.get(newItemPosition);
        
        return Objects.equals(oldItem.getName(), newItem.getName()) &&
               Objects.equals(oldItem.getPrice(), newItem.getPrice()) &&
               Objects.equals(oldItem.getRatingAvg(), newItem.getRatingAvg()) &&
               Objects.equals(oldItem.getSoldCount(), newItem.getSoldCount());
    }
}
