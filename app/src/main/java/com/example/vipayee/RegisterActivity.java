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
                .baseUrl(Constants.BASE_URL+"user/")  // Ensure this is correct
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

        // Create a new user with a generated UUID
        User user = new User(fullName, phoneNumber, pin);

        apiService.registerUser(user).enqueue(new Callback<Void>() {

            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
               editTextName.setText("");
                 editTextPhone.setText("");
                editTextPin.setText("");


                if (response.isSuccessful()) {
                    textToSpeech.speak("User Registered Successfully", TextToSpeech.QUEUE_FLUSH, null, null);
                    Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(RegisterActivity.this, RegisterationOTPActivity.class);
                    startActivity(i);
                } else {
                    try {
                        textToSpeech.speak("Registration Failed: ", TextToSpeech.QUEUE_FLUSH, null, null);
                        String errorMessage = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Toast.makeText(RegisterActivity.this, "Registration Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                        textToSpeech.speak("Registration Failed: " + errorMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
