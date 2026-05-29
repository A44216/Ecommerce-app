package com.example.ecommerceapp.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.adapter.AssistantChatAdapter;
import com.example.ecommerceapp.ui.viewmodel.AssistantViewModel;

import java.util.ArrayList;

public class AssistantChatActivity extends AppCompatActivity {

    private AssistantViewModel viewModel;
    private AssistantChatAdapter adapter;
    private RecyclerView rvChatHistory;
    private EditText etMessage;
    private ImageButton btnSend;
    private ProgressBar pbLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assistant_chat);

        Toolbar toolbar = findViewById(R.id.toolbarAssistant);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        rvChatHistory = findViewById(R.id.rvChatHistory);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        pbLoading = findViewById(R.id.pbLoading);

        adapter = new AssistantChatAdapter(this, new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatHistory.setLayoutManager(layoutManager);
        rvChatHistory.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(AssistantViewModel.class);

        viewModel.getChatItems().observe(this, items -> {
            adapter.setChatItems(items);
            if (items != null && !items.isEmpty()) {
                rvChatHistory.smoothScrollToPosition(items.size() - 1);
            }
        });

        viewModel.getIsLoading().observe(this, isLoading -> {
            pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSend.setEnabled(!isLoading);
        });

        btnSend.setOnClickListener(v -> {
            String message = etMessage.getText().toString();
            if (!message.trim().isEmpty()) {
                viewModel.sendMessage(message);
                etMessage.setText("");
            }
        });
    }
}
