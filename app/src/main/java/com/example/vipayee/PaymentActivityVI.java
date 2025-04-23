package com.example.vipayee;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivityVI extends AppCompatActivity {

    private TextView txtPayeeDetails;
    private EditText edtAmount;
    private Button btnMakePayment;

    private String receiverID, payeeName;
    private static final String SENDER_ID = Constants.USER_ID; // Sender ID from app

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_vi);

        // Request Notification Permission (For Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        txtPayeeDetails = findViewById(R.id.txt_payee_details);
        edtAmount = findViewById(R.id.edt_amount);
        btnMakePayment = findViewById(R.id.btn_make_payment);

        // Get ReceiverID and PayeeName from Intent
        receiverID = getIntent().getStringExtra("ReceiverID");
        payeeName = getIntent().getStringExtra("PayeeName");

        // Display only Payee Name (Hide ReceiverID)
        txtPayeeDetails.setText("Payee: " + payeeName);

        btnMakePayment.setOnClickListener(view -> makePayment());
    }

    private void makePayment() {
        String amountStr = edtAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Enter amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create Transaction Request
        TransactionRequest request = new TransactionRequest(SENDER_ID, receiverID, amount, payeeName);

        // Make API call
        ApiService apiService = RetrofitClient.getApiService();
        apiService.processTransaction(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Show notification when transaction is successful
                    NotificationHelper.showNotification(PaymentActivityVI.this,
                            "Transaction Successful",
                            "You sent ₹" + amount + " to " + payeeName + ".");

                    Toast.makeText(PaymentActivityVI.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after success
                } else {
                    Toast.makeText(PaymentActivityVI.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Transaction failed: " + t.getMessage());
                Toast.makeText(PaymentActivityVI.this, "API Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
