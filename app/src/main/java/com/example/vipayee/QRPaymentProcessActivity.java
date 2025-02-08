package com.example.vipayee;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class QRPaymentProcessActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_payment_process);

        // Get the raw QR content passed from MainActivity
        String qrContent = getIntent().getStringExtra("QR_CONTENT");

        // Parse the UPI ID and Name from the QR content
        String upiId = "";
        String name = "";
        EditText edtAmount = findViewById(R.id.edtAmount);
        Button btnPay = findViewById(R.id.btnPay);


        if (qrContent != null && qrContent.startsWith("upi://pay?")) {
            Uri uri = Uri.parse(qrContent);
            upiId = uri.getQueryParameter("pa"); // Extract UPI ID
            name = uri.getQueryParameter("pn"); // Extract Name
        }

        // Display the parsed UPI ID and Name in TextViews
        TextView tvUpiId = findViewById(R.id.tv_upi_id);
        TextView tvName = findViewById(R.id.tv_name);

        tvUpiId.setText("UPI ID:      " + (upiId != null ? upiId : "Not Found"));
        tvName.setText( (name != null ? name : "Not Found"));



        // Handling button click
        btnPay.setOnClickListener(view -> {
            String amount = edtAmount.getText().toString();

            if (amount.isEmpty() || Double.parseDouble(amount) <= 0) {
                Toast.makeText(QRPaymentProcessActivity.this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(QRPaymentProcessActivity.this, "Processing Payment of ₹" + amount, Toast.LENGTH_SHORT).show();
                // Add actual payment processing logic here
            }
        });
    }



    @Override
    public void onBackPressed() {
        // Navigate back to MainActivity when the back button is pressed
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
