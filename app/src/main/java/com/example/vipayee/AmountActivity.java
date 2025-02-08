package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AmountActivity extends AppCompatActivity {

    TextView upiDetails;
    EditText amountField;
    Button proceedButton;
    String upiId, upiName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        upiDetails = findViewById(R.id.upiDetails);
        amountField = findViewById(R.id.amountField);
        proceedButton = findViewById(R.id.proceedButton);

        upiId = getIntent().getStringExtra("upiId");
        upiName = getIntent().getStringExtra("upiName");

        upiDetails.setText("Pay to: " + upiName + "\nUPI ID: " + upiId);

        proceedButton.setOnClickListener(v -> {
            String amount = amountField.getText().toString().trim();
            if (!amount.isEmpty()) {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("upiId", upiId);
                intent.putExtra("upiName", upiName);
                intent.putExtra("amount", amount);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
            }
        });
    }
}