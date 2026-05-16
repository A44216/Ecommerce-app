package com.example.ecommerceapp.ui.fragment.admin.management.product;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.model.response.admin.management.product.AdminCategoryResponse;
import com.example.ecommerceapp.ui.adapter.admin.management.product.AdminCategoryAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCategoryViewModel;

import java.util.ArrayList;

public class AdminCategoryListFragment extends Fragment {

    public static final String ARG_TYPE = "type";

    public static final int TYPE_ALL = 0;
    public static final int TYPE_DELETED = 1;

    private int type;

    private RecyclerView rv;
    private SwipeRefreshLayout swipeRefreshCategory;
    private android.widget.ProgressBar progressBarCategory;
    private AdminCategoryAdapter adapter;
    private AdminCategoryViewModel viewModel;

    private AlertDialog editDialog;

    public static AdminCategoryListFragment newInstance(int type) {
        AdminCategoryListFragment f = new AdminCategoryListFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_TYPE, type);
        f.setArguments(b);
        return f;
    }

    // Đã bỏ onResume để không bị fetch lại khi chuyển tab

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments() != null ? getArguments().getInt(ARG_TYPE) : 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_admin_category_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rv = view.findViewById(R.id.rvCategory);
        swipeRefreshCategory = view.findViewById(R.id.swipeRefreshCategory);
        progressBarCategory = view.findViewById(R.id.progressBarCategory);

        viewModel = new ViewModelProvider(requireActivity())
                .get(AdminCategoryViewModel.class);

        swipeRefreshCategory.setOnRefreshListener(() -> {
            viewModel.fetchCategories(type == TYPE_DELETED);
        });

        setupRecycler();
        observeData();
        observeError();

        // LOAD THEO TAB
        if (swipeRefreshCategory != null && swipeRefreshCategory.isRefreshing()) {
            progressBarCategory.setVisibility(View.GONE);
        } else {
            progressBarCategory.setVisibility(View.VISIBLE);
        }
        
        if (type == TYPE_DELETED) {
            viewModel.fetchCategories(true);
        } else {
            viewModel.fetchCategories(false);
        }
    }

    private void setupRecycler() {
        adapter = new AdminCategoryAdapter(
                new AdminCategoryAdapter.OnActionListener() {

                    @Override
                    public void onEdit(AdminCategoryResponse category) {
                        showEditDialog(category);
                    }

                    @Override
                    public void onDelete(AdminCategoryResponse category) {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận xoá")
                                .setMessage("Bạn có chắc muốn xoá danh mục: " + category.getName())
                                .setPositiveButton("Xoá", (dialog, which) -> {
                                    viewModel.delete(category.getId(), type == TYPE_DELETED);
                                })
                                .setNegativeButton("Huỷ", null)
                                .show();
                    }

                    @Override
                    public void onRestore(AdminCategoryResponse category) {

                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("Xác nhận khôi phục")
                                .setMessage("Bạn có chắc muốn khôi phục danh mục: " + category.getName() + " ?")
                                .setPositiveButton("Khôi phục", (dialog, which) -> {
                                    viewModel.restore(category.getId());
                                })
                                .setNegativeButton("Huỷ", null)
                                .show();
                    }

                },
                type == TYPE_DELETED
        );

        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);
    }

    private void observeData() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null && isLoading) {
                if (swipeRefreshCategory != null && swipeRefreshCategory.isRefreshing()) {
                    if (progressBarCategory != null) progressBarCategory.setVisibility(View.GONE);
                } else {
                    if (progressBarCategory != null) progressBarCategory.setVisibility(View.VISIBLE);
                }
            } else {
                if (progressBarCategory != null) progressBarCategory.setVisibility(View.GONE);
                if (swipeRefreshCategory != null) swipeRefreshCategory.setRefreshing(false);
            }
        });

        if (type == TYPE_DELETED) {
            viewModel.getDeleted().observe(getViewLifecycleOwner(), list -> {
                if (swipeRefreshCategory != null) swipeRefreshCategory.setRefreshing(false);
                if (progressBarCategory != null) progressBarCategory.setVisibility(View.GONE);
                if (list == null) return;
                
                RecyclerView.ItemAnimator animator = rv != null ? rv.getItemAnimator() : null;
                if (rv != null) rv.setItemAnimator(null);
                
                adapter.submitList(new java.util.ArrayList<>(list), () -> {
                    if (rv != null) {
                        rv.scrollToPosition(0);
                        rv.post(() -> rv.setItemAnimator(animator));
                    }
                });
            });
        } else {
            viewModel.getAll().observe(getViewLifecycleOwner(), list -> {
                if (swipeRefreshCategory != null) swipeRefreshCategory.setRefreshing(false);
                if (progressBarCategory != null) progressBarCategory.setVisibility(View.GONE);
                if (list == null) return;
                
                RecyclerView.ItemAnimator animator = rv != null ? rv.getItemAnimator() : null;
                if (rv != null) rv.setItemAnimator(null);
                
                adapter.submitList(new java.util.ArrayList<>(list), () -> {
                    if (rv != null) {
                        rv.scrollToPosition(0);
                        rv.post(() -> rv.setItemAnimator(animator));
                    }
                });
            });
        }
    }

    private void showEditDialog(AdminCategoryResponse category) {

        EditText inputName = new EditText(requireContext());
        inputName.setText(category.getName());
        inputName.setHint("Tên danh mục");

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Sửa danh mục");
        builder.setView(inputName);
        builder.setPositiveButton("Cập nhật", null);
        builder.setNegativeButton("Huỷ", (d, w) -> d.dismiss());

        editDialog = builder.create();
        editDialog.show();

        editDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String name = inputName.getText().toString().trim();

                    if (name.isEmpty()) {
                        inputName.setError("Không được để trống");
                        inputName.requestFocus();
                        return;
                    }

                    // chỉ đóng khi hợp lệ
                    editDialog.dismiss();
                    editDialog = null;

                    viewModel.update(category.getId(), new CategoryRequest(name));
                });
    }

    private void observeError() {

        viewModel.getError().observe(getViewLifecycleOwner(), msg -> {

            if (msg == null) return;

            String message;

            switch (msg) {
                case "CATEGORY_ALREADY_EXISTS":
                    message = "Tên danh mục đã tồn tại";
                    break;

                case "CATEGORY_NOT_FOUND":
                    message = "Không tìm thấy danh mục";
                    break;

                case "CATEGORY_ALREADY_DELETED":
                    message = "Danh mục đã bị xoá";
                    break;

                case "CATEGORY_NOT_DELETED":
                    message = "Danh mục chưa bị xoá";
                    break;

                case "SERVER_ERROR":
                    message = "Lỗi hệ thống";
                    break;

                case "NETWORK_ERROR":
                    message = "Mất kết nối mạng";
                    break;

                default:
                    message = msg;
            }

            showErrorDialog(message);

            viewModel.clearError();
        });
    }

    private void showErrorDialog(String message) {

        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Thông báo lỗi")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

}