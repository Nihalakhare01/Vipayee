package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UPIActivity extends AppCompatActivity {

    private EditText phoneNumberField;
    private Button fetchUserButton;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi);

        phoneNumberField = findViewById(R.id.phoneNumberField);
        fetchUserButton = findViewById(R.id.fetchUserButton);

        // Initialize Text-to-Speech with Indian English accent
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = Locale.forLanguageTag("en-IN"); // Indian English
                int result = textToSpeech.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Indian English language not supported!");
                } else {
                    speak("Enter the mobile number to proceed with the transaction.");
                }
            } else {
                Log.e("TTS", "Text-to-Speech initialization failed!");
            }
        });

        fetchUserButton.setOnClickListener(v -> {
            String phoneNumber = phoneNumberField.getText().toString().trim();
            if (phoneNumber.length() == 10) {
                fetchUserDetails(phoneNumber);
            } else {
                speak("Please enter a 10-digit number to make a transaction.");
                Toast.makeText(UPIActivity.this, "Enter a valid phone number", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUserDetails(String phoneNumber) {
        ApiService apiService = RetrofitClient.getApiService();

        Call<UserResponse> call = apiService.getUserByPhoneNumber(phoneNumber);
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse user = response.body();
                    speak("User fetched successfully. Moving to the next page.");
                    Intent intent = new Intent(UPIActivity.this, UPITransactionActivity.class);
                    intent.putExtra("userId", user.getId());
                    intent.putExtra("payeeName", user.getName());
                    startActivity(intent);
                } else {
                    speak("User not found. Please enter a valid phone number.");
                    Toast.makeText(UPIActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch user details", t);
                speak("API error. Please try again.");
                Toast.makeText(UPIActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speak(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
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
