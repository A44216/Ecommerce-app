package com.example.ecommerceapp.ui.adapter.admin.order;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.ecommerceapp.data.enums.OrderStatus;
import com.example.ecommerceapp.ui.fragment.admin.order.AdminOrderListFragment;

public class AdminOrderPagerAdapter extends FragmentStateAdapter {

    public AdminOrderPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String status = null;
        switch (position) {
            case 0: status = OrderStatus.PENDING.name(); break;
            case 1: status = OrderStatus.CONFIRMED.name(); break;
            case 2: status = OrderStatus.SHIPPING.name(); break;
            case 3: status = OrderStatus.COMPLETED.name(); break;
            case 4: status = OrderStatus.CANCELED.name(); break;
            case 5: status = OrderStatus.RETURN_REQUESTED.name(); break;
            case 6: status = OrderStatus.DISPUTED.name(); break;
            case 7: status = OrderStatus.RETURNED.name(); break;
        }
        return AdminOrderListFragment.newInstance(status);
    }

    @Override
    public int getItemCount() {
        return 8;
    }
}
