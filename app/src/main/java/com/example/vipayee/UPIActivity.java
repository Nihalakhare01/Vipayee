package com.example.vipayee;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UPIActivity extends AppCompatActivity {

    EditText upiIdField;
    Button verifyUpiButton;
    TextView nameDisplay;

    private String userId;

    private static final int UPI_VALIDATION = 101; // Request Code for UPI Validation

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        upiIdField = findViewById(R.id.upiIdField);
        verifyUpiButton = findViewById(R.id.verifyUpiButton);
        nameDisplay = findViewById(R.id.nameDisplay);


        // 🔹 Retrieve USER_ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);

        if (userId != null) {
            Log.d("PaymentOptionActivity", "Retrieved USER_ID: " + userId);

        } else {
            Log.e("PaymentOptionActivity", "USER_ID not found in SharedPreferences.");
        }

        verifyUpiButton.setOnClickListener(v -> {
            String upiId = upiIdField.getText().toString().trim();
            if (!upiId.isEmpty()) {
                validateUpiId(upiId);
            } else {
                Toast.makeText(this, "Enter a valid UPI ID", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void validateUpiId(String upiId) {
        Uri uri = Uri.parse("upi://pay")
                .buildUpon()
                .appendQueryParameter("pa", upiId) // Payee UPI ID
                .appendQueryParameter("pn", "Validation Check") // Payee Name
                .appendQueryParameter("am", "0.01") // Small amount for validation
                .appendQueryParameter("cu", "INR") // Currency
                .appendQueryParameter("tn", "UPI ID Verification") // Transaction Note
                .build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        try {
            startActivityForResult(intent, UPI_VALIDATION);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No UPI app found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_VALIDATION) {
            if (data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("success")) {
                    String accountHolderName = extractNameFromResponse(response);
                    nameDisplay.setText("Account Holder: " + accountHolderName);
                    nameDisplay.setVisibility(TextView.VISIBLE);

                    // Move to EnterAmountActivity
                    Intent intent = new Intent(this, AmountActivity.class);
                    intent.putExtra("upiId", upiIdField.getText().toString());
                    intent.putExtra("upiName", accountHolderName);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Invalid UPI ID", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String extractNameFromResponse(String response) {
        return "User Name"; // Normally, you'd parse the response to extract the name
    }
}
