package com.example.ecommerceapp.ui.fragment.admin.profile;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.repository.admin.profile.AdminProfileRepository;
import com.example.ecommerceapp.ui.activity.home.admin.profile.AdminChangePasswordActivity;
import com.example.ecommerceapp.ui.activity.home.admin.profile.AdminProfileInfoActivity;
import com.example.ecommerceapp.ui.activity.login.LoginActivity;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminProfileViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminProfileViewModelFactory;
import com.example.ecommerceapp.utils.ImageLoader;

public class AdminProfileFragment extends Fragment {

    private View rootView;
    private LinearLayout itemProfileInfo, itemChangePassword, itemLogout;
    private TextView tvFullName, tvPhone;
    private ImageView imgAvatar;

    private AdminProfileViewModel viewModel;

    private final ActivityResultLauncher<Intent> launcher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            viewModel.loadProfile();
                        }
                    }
            );

    public static AdminProfileFragment newInstance() {
        return new AdminProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_admin_profile, container, false);

        initViews();
        initViewModel();
        observeData();

        viewModel.loadProfile();

        setListeners();

        return rootView;
    }

    private void initViewModel() {

        TokenManager tm = TokenManager.getInstance(requireContext());
        AdminProfileRepository repo = new AdminProfileRepository(tm);

        AdminProfileViewModelFactory factory = new AdminProfileViewModelFactory(repo);

        viewModel = new ViewModelProvider(requireActivity(), factory).get(AdminProfileViewModel.class);
    }

    private void observeData() {

        viewModel.getProfileData().observe(getViewLifecycleOwner(), data -> {
            if (data == null) return;

            tvFullName.setText(data.getFullName());
            tvPhone.setText(data.getPhone());

            ImageLoader.load(requireContext(), imgAvatar, data.getAvatar());
        });
    }

    private void initViews() {
        itemProfileInfo = rootView.findViewById(R.id.itemProfileInfo);
        itemChangePassword = rootView.findViewById(R.id.itemChangePassword);
        itemLogout = rootView.findViewById(R.id.itemLogout);
        tvFullName = rootView.findViewById(R.id.tvFullName);
        tvPhone = rootView.findViewById(R.id.tvPhone);
        imgAvatar = rootView.findViewById(R.id.imgAvatar);
    }

    private void setListeners() {

        itemProfileInfo.setOnClickListener(v -> openProfileInfo());

        itemChangePassword.setOnClickListener(v -> openChangePassword());

        itemLogout.setOnClickListener(v -> logout());
    }

    private void openProfileInfo() {
        launcher.launch(new Intent(requireContext(), AdminProfileInfoActivity.class));
    }



    private void openChangePassword() {
        startActivity(new Intent(requireContext(), AdminChangePasswordActivity.class));
    }

    private void logout() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {

                    // Clear toàn bộ session
                    TokenManager.getInstance(requireContext()).logout();

                    // Chuyển về Login và clear stack
                    Intent intent = new Intent(requireContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }
}