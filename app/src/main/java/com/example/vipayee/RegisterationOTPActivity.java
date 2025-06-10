package com.example.vipayee;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterationOTPActivity extends AppCompatActivity {
    SmsBroadcastReceiver smsBroadcastReceiver;
    private TextToSpeech textToSpeech;
    TextInputEditText etOTP;
    private String userId;
    Button btnVerifyOTP;
    private ActivityResultLauncher<Intent> smsConsentLauncher;

    private static final String TAG = "RegisterationOTPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_otp);

        etOTP = findViewById(R.id.etOTP);
        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        // Retrieve phone number from intent
        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
        Log.d(TAG, "PhoneNumberRegister: " + phoneNumber);

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
        fetchUserId(phoneNumber);

        // Verify OTP button click
        btnVerifyOTP.setOnClickListener(view -> {
            String enteredOtp = etOTP.getText().toString();
            if (!enteredOtp.isEmpty()) {
                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "USER_ID after OTP verification: " + Constants.getUserUUID(this));
                // Proceed with next steps (e.g., login)
                navigateToLogin();
            } else {
                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startSmartUserConsent() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null);
    }

    private void fetchUserId(String phoneNumber) {
        OkHttpClient client = new OkHttpClient();
        String url = Constants.BASE_URL + "user/Registration/get-uuid/" + phoneNumber;

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    speakMessage("Failed to fetch User ID. Please check your connection.");
                    Toast.makeText(RegisterationOTPActivity.this, "Failed to fetch User ID", Toast.LENGTH_SHORT).show();
                });
                Log.e(TAG, "API Call Failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        Log.d(TAG, "Raw API Response: " + responseData);
                        JSONObject json = new JSONObject(responseData);
                        String userIdString = json.getString("userID");
                        Log.d(TAG, "Parsed User ID: " + userIdString);

                        if (!userIdString.isEmpty()) {
                            runOnUiThread(() -> {
                                Constants.setUserUUID(RegisterationOTPActivity.this, userIdString);
                                Log.d(TAG, "Stored User ID in Constants: " + Constants.getUserUUID(RegisterationOTPActivity.this));
                                speakMessage("User ID fetched successfully");

                            });
                        }
                    } catch (Exception e) {
                        runOnUiThread(() -> speakMessage("Error processing User ID data."));
                        Log.e(TAG, "JSON Parsing Error", e);
                    }
                } else {
                    runOnUiThread(() -> {
                        speakMessage("Failed to retrieve User ID. Response Code: " + response.code());
                        Toast.makeText(RegisterationOTPActivity.this, "Failed to retrieve User ID", Toast.LENGTH_SHORT).show();
                    });
                    Log.e(TAG, "API Response Error: " + response.code());
                }
            }
        });
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
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterationOTPActivity.this, TapOptionActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity
    }
    private String extractOtp(String message) {
        Pattern otpPattern = Pattern.compile("\\d{4,6}");
        Matcher matcher = otpPattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    private void speakMessage(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
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

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}



//package com.example.vipayee;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Bundle;
//import android.speech.tts.TextToSpeech;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.google.android.gms.auth.api.phone.SmsRetriever;
//import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
//import com.google.android.material.textfield.TextInputEditText;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import okhttp3.OkHttpClient;
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class RegisterationOTPActivity extends AppCompatActivity {
//
//    SmsBroadcastReceiver smsBroadcastReceiver;
//
//    private TextToSpeech textToSpeech;
//
//    TextInputEditText etOTP;
//
//    private String userId;
//    Button btnVerifyOTP;
//
//    private ActivityResultLauncher<Intent> smsConsentLauncher;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_register_otp);
//
//        etOTP = findViewById(R.id.etOTP);
//        btnVerifyOTP = findViewById(R.id.btnVerifyOTP);
//
//
//        // Retrieve phone number from intent
//        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");
//        Log.d("RegisterationOTPActivity", "PhoneNumberRegister" + phoneNumber);
////        fetchUserId(phoneNumber);
//        // Initialize the SMS Consent Launcher
//        smsConsentLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
//                        String message = result.getData().getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
//                        if (message != null) {
//                            String otp = extractOtp(message);
//                            etOTP.setText(otp);
//                            Toast.makeText(this, "OTP Retrieved: " + otp, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );
//
//        // Start SMS Retriever API
//        startSmartUserConsent();
//
//        // Verify OTP button click
//        btnVerifyOTP.setOnClickListener(view -> {
//            String enteredOtp = etOTP.getText().toString();
//            if (!enteredOtp.isEmpty()) {
//                Toast.makeText(this, "OTP Verified Successfully!", Toast.LENGTH_SHORT).show();
//                // Proceed with next steps (e.g., login)
//            } else {
//                Toast.makeText(this, "Please enter OTP", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    private void startSmartUserConsent() {
//        SmsRetrieverClient client = SmsRetriever.getClient(this);
//        client.startSmsUserConsent(null);
//    }
//
//
////    private void fetchUserId(String phoneNumber) {
////        OkHttpClient client = new OkHttpClient();
////        String url = Constants.BASE_URL + "user/Registration/get-uuid/" + phoneNumber;
////
////        Request request = new Request.Builder()
////                .url(url)
////                .get()
////                .build();
////
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////                runOnUiThread(() -> speakMessage("Failed to fetch User ID. Please check your connection."));
////                Log.e("FetchUserId", "API Call Failed", e);
////            }
////
////            @Override
////            public void onResponse(Call call, Response response) throws IOException {
////                if (response.isSuccessful() && response.body() != null) {
////                    try {
////                        String responseData = response.body().string();
////                        JSONObject json = new JSONObject(responseData);
////                        String userIdString = json.getString("userID");
////
////                        if (!userIdString.isEmpty()) {
////                            runOnUiThread(() -> {
////                                // Save User ID in Constants and SharedPreferences
////                                Constants.setUserId(RegisterationOTPActivity.this, userIdString);
////                                Log.d("FetchUserId", "Fetched User ID: " + userIdString);
////                                speakMessage("Your User ID is " + userIdString);
////                            });
////                        }
////
////                    } catch (Exception e) {
////                        runOnUiThread(() -> speakMessage("Error processing User ID data."));
////                        Log.e("FetchUserId", "JSON Parsing Error", e);
////                    }
////                } else {
////                    runOnUiThread(() -> speakMessage("Failed to retrieve User ID. Please try again."));
////                    Log.e("FetchUserId", "API Response Error: " + response.code());
////                }
////            }
////        });
////    }
//
//
//
//    @SuppressLint("UnspecifiedRegisterReceiverFlag")
//    private void registerBroadcastReceiver() {
//        smsBroadcastReceiver = new SmsBroadcastReceiver();
//        smsBroadcastReceiver.smsBroadcastReceiverListener = new SmsBroadcastReceiver.SmsBroadcastReceiverListener() {
//            @Override
//            public void onSuccess(Intent intent) {
//                smsConsentLauncher.launch(intent);
//            }
//
//            @Override
//            public void onFailure() {
//                Toast.makeText(RegisterationOTPActivity.this, "Failed to retrieve OTP", Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        IntentFilter intentFilter = new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION);
//        registerReceiver(smsBroadcastReceiver, intentFilter);
//    }
//
//    private String extractOtp(String message) {
//        Pattern otpPattern = Pattern.compile("\\d{4,6}");
//        Matcher matcher = otpPattern.matcher(message);
//        if (matcher.find()) {
//            return matcher.group(0);
//        }
//        return "";
//    }
//
//
//    private void speakMessage(String message) {
//        if (textToSpeech != null) {
//            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
//        }
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        registerBroadcastReceiver();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        unregisterReceiver(smsBroadcastReceiver);
//    }
//}