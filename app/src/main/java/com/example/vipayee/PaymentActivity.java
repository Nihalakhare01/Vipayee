package com.example.vipayee;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class PaymentActivity extends AppCompatActivity {

    TextView paymentDetails;
    Button payButton;
    String upiId, upiName, amount;
    private static final int UPI_PAYMENT = 102; // Request Code for UPI Payment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentDetails = findViewById(R.id.paymentDetails);
        payButton = findViewById(R.id.payButton);

        // Retrieve data from previous activity
        upiId = getIntent().getStringExtra("upiId");
        upiName = getIntent().getStringExtra("upiName");
        amount = getIntent().getStringExtra("amount");

        // Display payment details
        paymentDetails.setText("Paying: ₹" + amount + "\nTo: " + upiName + "\nUPI ID: " + upiId);

        // Click listener to start payment
        payButton.setOnClickListener(v -> initiatePayment());
    }

    private void initiatePayment() {
        Uri uri = Uri.parse("upi://pay")
                .buildUpon()
                .appendQueryParameter("pa", upiId) // Payee UPI ID
                .appendQueryParameter("pn", upiName) // Payee Name
                .appendQueryParameter("mc", "") // Merchant Code (if applicable)
                .appendQueryParameter("tid", "TXN123456") // Transaction ID
                .appendQueryParameter("tr", "TXNREF123456") // Transaction Reference ID
                .appendQueryParameter("tn", "UPI Payment") // Transaction Note
                .appendQueryParameter("am", amount) // Payment Amount
                .appendQueryParameter("cu", "INR") // Currency
                .appendQueryParameter("url", "") // URL (if applicable)
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        try {
            startActivityForResult(intent, UPI_PAYMENT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No UPI app found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_PAYMENT) {
            if (data != null) {
                String response = data.getStringExtra("response");
                if (response != null) {
                    handlePaymentResponse(response);
                } else {
                    Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No response from UPI app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handlePaymentResponse(String response) {
        if (response.toLowerCase().contains("success")) {
            Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Payment Failed!", Toast.LENGTH_LONG).show();
        }
    }
}
