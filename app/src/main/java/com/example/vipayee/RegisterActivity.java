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

import com.example.vipayee.ApiResponse;
import com.example.vipayee.ApiService;
import com.example.vipayee.R;
import com.example.vipayee.RegisterationOTPActivity;
import com.example.vipayee.RetrofitClient;
import com.example.vipayee.User;

import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText fullNameEditText, phoneEditText, pinEditText;
    private Button registerButton;
    private TextToSpeech textToSpeech;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullNameEditText = findViewById(R.id.editTextFullName);
        phoneEditText = findViewById(R.id.editTextPhone);
        pinEditText = findViewById(R.id.editTextPin);
        registerButton = findViewById(R.id.buttonRegister);

        // Initialize TTS
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String fullName = fullNameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();
        String pin = pinEditText.getText().toString().trim();

        if (fullName.isEmpty() || phone.isEmpty() || pin.isEmpty()) {
            speakAndToast("All fields are required!");
            return;
        }

        User user = new User(fullName, phone, pin);
        ApiService apiService = RetrofitClient.getApiService();
        Call<ApiResponse> call = apiService.registerUser(user);

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String message = response.body().getMessage();
                    Log.d(TAG, "Raw Success Response: " + message);

                    if (message.contains("User registered successfully")) {
                        // Speak only the user-friendly success message
                        String successMessage = "User registered successfully.";
                        speakAndToast(successMessage);
                        moveToOTPActivity(phone);
                    } else if (message.contains("Phone number already exists")) {
                        // Speak a friendly message for duplicate phone numbers
                        String duplicateMessage = "Phone number already exists. Please enter a valid number.";
                        speakAndToast(duplicateMessage);
                        Log.d(TAG, "User-friendly message: " + duplicateMessage);
                    } else {
                        // For any other success responses, show a default error message
                        String defaultMessage = "Registration failed. Please try again.";
                        speakAndToast(defaultMessage);
                        Log.e(TAG, "Unhandled success message: " + message);
                    }
                } else {
                    // Log the response code and use a generic error message instead of the raw error body
                    Log.e(TAG, "Error Response Code: " + response.code());
                    String userFriendlyError = "Registration failed. Please try again.";
                    speakAndToast(userFriendlyError);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                String networkError = "Network error! Please try again.";
                speakAndToast(networkError);
                Log.e(TAG, "Failure: " + t.getMessage());
            }

        });
    }

    private void speakAndToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void moveToOTPActivity(String phoneNumber) {
        new android.os.Handler().postDelayed(() -> {
            Intent intent = new Intent(RegisterActivity.this, RegisterationOTPActivity.class);
//            intent.putExtra("phoneNumber", phoneNumber);
            intent.putExtra("PHONE_NUMBER", phoneNumber);
            startActivity(intent);
            finish(); // Close RegisterActivity
        }, 2000); // Delay to let TTS complete
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
