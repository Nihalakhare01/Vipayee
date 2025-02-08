package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OptionActivity extends AppCompatActivity {

    float x1,x2,y1,y2;
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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = motionEvent.getX();
                y1 = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = motionEvent.getX();
                y2 = motionEvent.getY();

                float deltaX = x2 - x1;
                float deltaY = y2 - y1;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    // Horizontal Swipe
                    if (deltaX > 0) {
                        textToSpeech.speak("Opening Check Balance options.", TextToSpeech.QUEUE_FLUSH, null, null);
                        startActivity(new Intent(OptionActivity.this, CheckBalanceActivity.class));
                    } else {
                        textToSpeech.speak("Opening Payment Mode options.", TextToSpeech.QUEUE_FLUSH, null, null);
                        startActivity(new Intent(OptionActivity.this, PaymentOptionActivity.class));
                    }
                } else {
                    // Vertical Swipe
                    if (deltaY > 0) {
                        textToSpeech.speak("Swipe up for transaction history.", TextToSpeech.QUEUE_FLUSH, null, null);
                    } else {
                        textToSpeech.speak("Opening Transaction History.", TextToSpeech.QUEUE_FLUSH, null, null);
                        startActivity(new Intent(OptionActivity.this, TransactionHistoryActivity.class));
                    }
                }
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    private void speakInstructions() {
        String message = "Payment feature's are available. Swipe right for transaction. Swipe left to check balance. Swipe up for past transaction.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }


}
