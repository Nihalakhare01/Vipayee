package com.example.vipayee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
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
    private String userId = Constants.USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_balance);

        balanceTextView = findViewById(R.id.balanceTextView);

        Log.d("CheckBalanceActivity", "Using USER_ID: " + userId);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                fetchBalance(userId);
            }
        });
    }

    private void fetchBalance(String userId) {
        OkHttpClient client = new OkHttpClient();
        String url = Constants.BASE_URL+"user/CheckBalance/get-balance/" + userId;

        Log.d("CheckBalanceActivity", "Fetching balance from: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> speakMessage("Failed to fetch balance. Please check your connection."));
                Log.e("CheckBalance", "API Call Failed: " + e.getMessage(), e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", "Response Data: " + responseData);

                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (json.has("balance")) {
                            double balance = json.optDouble("balance", 0); // Correct way to parse integer values

                            runOnUiThread(() -> {
                                balanceTextView.setText("₹" + balance);
                                speakMessage("Your available balance is " + balance + " rupees.");
                            });

                        } else {
                            runOnUiThread(() -> speakMessage("Balance information is missing. Please try again."));
                            Log.e("JSON_ERROR", "Balance key not found in response.");
                        }

                    } catch (Exception e) {
                        runOnUiThread(() -> speakMessage("Error processing balance data."));
                        Log.e("JSON_PARSE_ERROR", "Error parsing JSON: " + e.getMessage(), e);
                    }
                } else {
                    runOnUiThread(() -> speakMessage("Failed to retrieve balance. Please try again."));
                    Log.e("CheckBalance", "API Response Error: Code " + response.code());
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
