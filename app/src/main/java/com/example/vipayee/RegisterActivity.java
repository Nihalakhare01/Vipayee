package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextUID, editTextName, editTextPhone, editTextPin;
    private Button buttonSubmit;
    private ApiService apiService;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextUID = findViewById(R.id.editText0);
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
                .baseUrl("http://192.168.61.54:5234/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void speakInstructions() {
        String message = "Please enter the details.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void registerUser() {
        int userID = Integer.parseInt(editTextUID.getText().toString().trim());
        String fullName = editTextName.getText().toString().trim();
        String phoneNumber = editTextPhone.getText().toString().trim();
        String pin = editTextPin.getText().toString().trim();

        if (fullName.isEmpty() || phoneNumber.isEmpty() || pin.isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            textToSpeech.speak("Please fill all fields.", TextToSpeech.QUEUE_FLUSH, null, null);
            return;
        }

        User user = new User(userID, fullName, phoneNumber, pin);
        apiService.registerUser(user).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();


                    // Clear input fields
                    editTextUID.setText("");
                    editTextName.setText("");
                    editTextPhone.setText("");
                    editTextPin.setText("");

                    // Reset focus to first field (optional)
                    editTextUID.requestFocus();

                    Intent i = new Intent(RegisterActivity.this, RegisterationOTPActivity.class);
                    startActivity(i);
                    textToSpeech.speak("User Registered Successfully.Opening Verification Section. Please Verify.", TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    textToSpeech.speak("Registration Failed. ", TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                textToSpeech.speak("Error " + t.getMessage(), TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
    }
}
