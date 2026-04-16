package com.example.ecommerceapp.ui.activity.home.admin.profile;

import android.os.Bundle;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.api.ApiClient;
import com.example.ecommerceapp.api.service.admin.AdminCategoryService;
import com.example.ecommerceapp.data.local.TokenManager;
import com.example.ecommerceapp.data.model.request.CategoryRequest;
import com.example.ecommerceapp.data.repository.admin.category.AdminCategoryRepository;
import com.example.ecommerceapp.ui.adapter.admin.category.AdminCategoryPagerAdapter;
import com.example.ecommerceapp.ui.viewmodel.admin.AdminCategoryViewModel;
import com.example.ecommerceapp.ui.viewmodel.admin.factory.AdminCategoryViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class AdminCategoryActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private EditText etSearch;
    private FloatingActionButton fabAdd;
    private AdminCategoryViewModel viewModel;

    private int currentTab = 0; // 0 = ALL, 1 = DELETED

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_category);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initViewModel();
        setupViewPager();
        setupFab();
        setupSearch();

        viewModel.fetchCategories(false);
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        etSearch = findViewById(R.id.etSearch);
        fabAdd = findViewById(R.id.fabAddCategory);
    }

    private void initViewModel() {

        AdminCategoryService service =
                ApiClient.getAdminCategoryService(TokenManager.getInstance(this));

        AdminCategoryRepository repo = new AdminCategoryRepository(service);

        viewModel = new ViewModelProvider(
                this,
                new AdminCategoryViewModelFactory(repo)
        ).get(AdminCategoryViewModel.class);
    }

    private void setupViewPager() {

        viewPager.setAdapter(new AdminCategoryPagerAdapter(this));

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position == 0) tab.setText("Tất cả");
                    else tab.setText("Đã xoá");
                }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentTab = position;
            }
        });
    }

    private void setupFab() {
        fabAdd.setOnClickListener(v -> showAddDialog());
    }

    private void setupSearch() {

        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            String keyword = etSearch.getText().toString().trim();

            viewModel.search(keyword);

            return true;
        });
    }

    private void showAddDialog() {

        final EditText inputName = new EditText(this);
        inputName.setHint("Tên danh mục");

        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(this)
                        .setTitle("Thêm danh mục")
                        .setView(inputName)
                        .setPositiveButton("Thêm", null)
                        .setNegativeButton("Huỷ", null)
                        .create();

        dialog.show();

        dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener(v -> {

                    String name = inputName.getText().toString().trim();

                    if (name.isEmpty()) {
                        inputName.setError("Không được để trống");
                        inputName.requestFocus();
                        return;
                    }

                    dialog.dismiss();

                    CategoryRequest request = new CategoryRequest(name);
                    viewModel.create(request);
                });
    }

}