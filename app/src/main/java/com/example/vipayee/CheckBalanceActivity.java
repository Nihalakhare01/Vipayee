package com.example.vipayee;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckBalanceActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private TextView balanceTextView;
    private Button buttonRefresh;
    private String userId; // User ID from SharedPreferences
//    private static final String PREF_NAME = "UserPref"; // SharedPreferences Name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);

        balanceTextView = findViewById(R.id.balanceTextView);
        buttonRefresh = findViewById(R.id.buttonRefresh);

        // 🔹 Retrieve User ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);

        if (userId != null) {
            Log.d("CheckBalanceActivity", " CheckBalanceActivity Retrieved USER_ID: " + userId);

        } else {
            Log.e("CheckBalanceActivity", "USER_ID not found in SharedPreferences.");
        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                if (userId != null) {
                    fetchBalance(userId);
                } else {
                    speakMessage("User ID not found. Please re-login.");
                }
            }
        });

        // Refresh balance when button is clicked
        buttonRefresh.setOnClickListener(v -> {
            if (userId != null) {
                fetchBalance(userId);
            } else {
                speakMessage("User ID missing. Please re-login.");
            }
        });
    }

    private void fetchBalance(String userId) {
        OkHttpClient client = new OkHttpClient();
        String url = Constants.BASE_URL + "user/Checkbalance/get-balance/" + userId; // Modify based on your API

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> speakMessage("Failed to fetch balance. Please check your connection."));
                Log.e("CheckBalance", "API Call Failed", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONObject json = new JSONObject(responseData);
                        String balance = json.getString("balance");

                        runOnUiThread(() -> {
                            balanceTextView.setText("Your Balance: ₹" + balance);
                            speakMessage("Your available balance is " + balance + " rupees.");
                        });

                    } catch (Exception e) {
                        runOnUiThread(() -> speakMessage("Error processing balance data."));
                        Log.e("CheckBalance", "JSON Parsing Error", e);
                    }
                } else {
                    runOnUiThread(() -> speakMessage("Failed to retrieve balance. Please try again."));
                    Log.e("CheckBalance", "API Response Error: " + response.code());
                }
            }
        });
    }

    private void speakMessage(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
