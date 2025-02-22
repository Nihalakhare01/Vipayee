package com.example.vipayee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CheckBalanceBrailleLoginActivity extends AppCompatActivity {
    private TextView pinDisplay;
    private TextToSpeech textToSpeech;
    private boolean dot1Pressed = false, dot2Pressed = false, dot3Pressed = false, dot4Pressed = false;
    private Vibrator vibrator;
    private StringBuilder pin = new StringBuilder();
    private Handler handler = new Handler();
    private int digitCount = 0;
    private String userPin = ""; // User PIN fetched from API
    private static final String TAG = "BrailleLoginActivity";

    // 🔹 Your User ID (Replace with actual dynamic user ID if needed)
    private static final String USER_ID = Constants.USER_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braille_login);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        pinDisplay = findViewById(R.id.pinDisplay);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakMessage("Fetching PIN. Please wait.");
            }
        });

        // 🔹 Fetch PIN from API
        fetchUserPin(USER_ID);

        // 🔹 Initialize the button click listeners
        findViewById(R.id.dot1).setOnClickListener(v -> onDotPressed(1));
        findViewById(R.id.dot2).setOnClickListener(v -> onDotPressed(2));
        findViewById(R.id.dot3).setOnClickListener(v -> onDotPressed(3));
        findViewById(R.id.dot4).setOnClickListener(v -> onDotPressed(4));
    }

    private void fetchUserPin(String userId) {
        OkHttpClient client = new OkHttpClient();
        String url = Constants.BASE_URL+"user/Registration/get-pin/" + userId;
        Log.d("API_CALL", "Fetching PIN from: " + url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "API Call Failed: " + e.getMessage());
                runOnUiThread(() -> speakMessage("Failed to fetch PIN. Please check your connection."));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Log.d("API_RESPONSE", "Response Data: " + responseData);
                    try {
                        JSONObject json = new JSONObject(responseData);
                        if (json.has("pin")) {
                            userPin = json.getString("pin").trim();
                            Log.d("PIN_FETCHED", "User PIN: " + userPin);
                            runOnUiThread(() -> speakMessage("PIN fetched successfully. Enter your four-digit PIN."));
                        } else {
                            Log.e("JSON_ERROR", "PIN key missing in response: " + responseData);
                            runOnUiThread(() -> speakMessage("PIN not found. Please try again."));
                        }
                    } catch (Exception e) {
                        Log.e("JSON_PARSE_ERROR", "Error parsing JSON: " + e.getMessage());
                        runOnUiThread(() -> speakMessage("Error retrieving PIN."));
                    }
                } else {
                    Log.e("API_ERROR", "Failed to retrieve PIN. Response Code: " + response.code());
                    runOnUiThread(() -> speakMessage("Failed to retrieve PIN. Try again."));
                }
            }
        });
    }


    private void onDotPressed(int dotNumber) {
        switch (dotNumber) {
            case 1: dot1Pressed = !dot1Pressed; break;
            case 2: dot2Pressed = !dot2Pressed; break;
            case 3: dot3Pressed = !dot3Pressed; break;
            case 4: dot4Pressed = !dot4Pressed; break;
        }
        handler.postDelayed(this::recognizeAndUpdatePin, 500);
    }

    private void recognizeAndUpdatePin() {
        if (pin.length() < 4) {
            String number = getBrailleNumber(dot1Pressed, dot2Pressed, dot3Pressed, dot4Pressed);
            if (!number.isEmpty()) {
                pin.append(number);
                pinDisplay.setText("● ".repeat(pin.length()).trim());

                digitCount++;
                speakMessage("You have entered digit " + digitCount + ".");
                triggerVibration();

                if (digitCount == 4) {
                    handler.postDelayed(this::authenticatePin, 1500);
                } else {
                    handler.postDelayed(() -> speakMessage("Enter digit " + (digitCount + 1) + "."), 1500);
                }
            }
        }
        resetGrid();
    }

    private void authenticatePin() {
        if (userPin.isEmpty()) {
            speakMessage("PIN is not loaded yet. Please wait.");
            return;
        }

        if (pin.toString().trim().equals(userPin.trim())) {
            speakMessage("Authentication successful. Moving to the payment section.");

            handler.postDelayed(() -> {
                startActivity(new Intent(CheckBalanceBrailleLoginActivity.this, CheckBalanceActivity.class));
                finish();
            }, 2000);
        } else {
            speakMessage("Incorrect PIN. Please try again.");
            pin.setLength(0);
            digitCount = 0;
            handler.postDelayed(() -> speakMessage("Enter your four-digit login PIN."), 2000);
            pinDisplay.setText("");
        }
    }

    private void resetGrid() {
        dot1Pressed = false;
        dot2Pressed = false;
        dot3Pressed = false;
        dot4Pressed = false;
    }

    private void triggerVibration() {
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    private void speakMessage(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private String getBrailleNumber(boolean d1, boolean d2, boolean d3, boolean d4) {
        String pattern = "" + (d1 ? "1" : "0") + (d2 ? "1" : "0") + (d3 ? "1" : "0") + (d4 ? "1" : "0");

        switch (pattern) {
            case "1000": return "1";
            case "1100": return "2";
            case "1010": return "3";
            case "1011": return "4";
            case "1001": return "5";
            case "1110": return "6";
            case "1111": return "7";
            case "1101": return "8";
            case "0110": return "9";
            case "0111": return "0";
            default: return "";
        }
    }
}
