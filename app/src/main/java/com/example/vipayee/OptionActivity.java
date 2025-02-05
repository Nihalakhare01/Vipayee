package com.example.vipayee;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OptionActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_option);
        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });
    }

    private void speakInstructions() {
        String message = "Payment feature's are available. Swipe right for transaction. Swipe left to check balance. Swipe up for past transaction.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }


}
