package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class AmountActivity extends AppCompatActivity {

    TextView upiDetails;
    EditText amountField;
    Button proceedButton;
    String upiId, upiName;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amount);

        upiDetails = findViewById(R.id.upiDetails);
        amountField = findViewById(R.id.amountField);
        proceedButton = findViewById(R.id.proceedButton);

        upiId = getIntent().getStringExtra("upiId");
        upiName = getIntent().getStringExtra("upiName");

        upiDetails.setText("Pay to: " + upiName + "\nUPI ID: " + upiId);

        // Initialize Text-to-Speech with Indian English accent
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = Locale.forLanguageTag("en-IN"); // Indian English Accent
                int result = textToSpeech.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS Language not supported!", Toast.LENGTH_SHORT).show();
                } else {
                    // Speak on entering the page
                    speak("Please enter the amount. You are paying to " + upiName);
                }
            }
        });

        proceedButton.setOnClickListener(v -> {
            String amount = amountField.getText().toString().trim();
            if (!amount.isEmpty()) {
                // Speak transaction confirmation
                speak("Transferring rupees " + amount + " to " + upiName);

                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("upiId", upiId);
                intent.putExtra("upiName", upiName);
                intent.putExtra("amount", amount);
                startActivity(intent);
            } else {
                speak("Please enter a valid amount.");
                Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
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
