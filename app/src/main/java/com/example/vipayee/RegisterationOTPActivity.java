package com.example.vipayee;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterationOTPActivity extends AppCompatActivity {

    SmsBroadcastReceiver smsBroadcastReceiver;
    TextInputEditText etOTP;
    Button btnVerifyOTP;

    private ActivityResultLauncher<Intent> smsConsentLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otp);

        etOTP = findViewById(R.id.etOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        // Initialize the SMS Consent Launcher
        smsConsentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
                        if (message != null) {
                            String otp = extractOtp(message);
                            etOTP.setText(otp);
                            Toast.makeText(this, "OTP Retrieved: " + otp, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Start SMS Retriever API
        startSmartUserConsent();

        // Verify OTP button click
        btnVerifyOTP.setOnClickListener(view -> {
            String enteredOtp = etOTP.getText().toString();
            if (!enteredOtp.isEmpty()) {
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                // Proceed with next steps (e.g., login)
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void registerBroadcastReceiver() {
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
            @Override
            public void onSuccess(Intent intent) {
                smsConsentLauncher.launch(intent);
            }

            @Override
            public void onFailure() {
                Toast.makeText(RegisterationOTPActivity.this, "Failed to retrieve OTP", Toast.LENGTH_SHORT).show();
            }
        };

        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
        registerReceiver(smsBroadcastReceiver, intentFilter);
    }

    private String extractOtp(String message) {
        Pattern otpPattern = Pattern.compile("\\d{4,6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcastReceiver();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(smsBroadcastReceiver);
    }
}