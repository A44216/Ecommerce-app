package com.example.ecommerceapp.ui.activity.home.user.checkout;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ecommerceapp.R;
import java.text.DecimalFormat;

public class PaymentSimulationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_simulation);

        TextView tvAmount = findViewById(R.id.tvPaymentAmount);
        Button btnConfirm = findViewById(R.id.btnConfirmPayment);
        TextView btnCancel = findViewById(R.id.btnCancelPayment);

        double amount = getIntent().getDoubleExtra("PAYMENT_AMOUNT", 0);
        DecimalFormat df = new DecimalFormat("#,###");
        tvAmount.setText(df.format(amount) + "đ");

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        btnConfirm.setOnClickListener(v -> {
            simulatePaymentVerification();
        });
    }

    private void simulatePaymentVerification() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Đang xác thực giao dịch...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Giả lập độ trễ 2 giây
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressDialog.dismiss();
            Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        }, 2000);
    }
}
