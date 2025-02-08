package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class PaymentOptionActivity extends AppCompatActivity {

    float x1,x2;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payement_option);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });
    }

    private void speakInstructions() {
        String message = "Make Transaction here. Swipe Right for Scan the QR.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = motionEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = motionEvent.getX();


                //Left slide
                if (x1 < x2) {
                    textToSpeech.speak("Opening the QR Scan Page.", TextToSpeech.QUEUE_FLUSH, null, null);
                    Intent i = new Intent(PaymentOptionActivity.this, QRScanActivity.class);
                    startActivity(i);
//                    Right Slide
                } else if (x1 > x2) {
                    textToSpeech.speak("Opening the pay by number.", TextToSpeech.QUEUE_FLUSH, null, null);
                    Intent i = new Intent(PaymentOptionActivity.this, UPIActivity.class);
                    startActivity(i);
                }

                break;
        }
        return super.onTouchEvent(motionEvent);
    }
}
