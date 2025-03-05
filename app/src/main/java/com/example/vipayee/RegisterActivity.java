package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextName, editTextPhone, editTextPin;
    private Button buttonSubmit;
    private ApiService apiService;
    private TextToSpeech textToSpeech;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextName = findViewById(R.id.editText1);
        editTextPhone = findViewById(R.id.editText2);
        editTextPin = findViewById(R.id.editText3);
        buttonSubmit = findViewById(R.id.buttonSubmit);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL + "user/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        buttonSubmit.setOnClickListener(v -> registerUser());
    }

    private void speakInstructions() {
        String message = "Please enter the details.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void registerUser() {
        String fullName = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhone.getText().toString().trim();
        String pin = editTextPin.getText().toString().trim();

        if (fullName.isEmpty() || phoneNumber.isEmpty() || pin.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(fullName, phoneNumber, pin);

        Log.d(TAG, "Registering user: " + fullName + ", Phone: " + phoneNumber);

        apiService.registerUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "User registered successfully");
                    speak("User Registered Successfully");
                    Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();

                    // Start OTP activity and pass the phone number
                    Intent intent = new Intent(RegisterActivity.this, RegisterationOTPActivity.class);
                    intent.putExtra("PHONE_NUMBER", phoneNumber);
                    startActivity(intent);

                    // Clear fields
                    editTextName.setText("");
                    editTextPhone.setText("");
                    editTextPin.setText("");
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Registration failed", t);
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleErrorResponse(Response<?> response) {
        try {
            String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
            Log.e(TAG, "Registration failed: " + errorMessage);
            speak("Registration Failed: " + errorMessage);
            Toast.makeText(RegisterActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error reading error response", e);
            e.printStackTrace();
        }
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
