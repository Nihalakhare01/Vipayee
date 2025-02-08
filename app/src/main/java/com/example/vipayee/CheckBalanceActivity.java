package com.example.vipayee;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class CheckBalanceActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_check_balance);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });
    }

    private void speakInstructions() {
        String message = "Check Balance here.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
