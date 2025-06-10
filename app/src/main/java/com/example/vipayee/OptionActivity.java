package com.example.vipayee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class OptionActivity extends AppCompatActivity {
    float x1, x2, y1, y2;
    private TextToSpeech textToSpeech;
     // Store USER_ID

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
                    // 🔹 Horizontal Swipe (Left/Right)
                    if (deltaX > 0) {
                        navigateToActivity(CheckBalanceBrailleLoginActivity.class, "Enter your 4 digit Login Pin to Know balance.");
                    } else {
                        navigateToActivity(PaymentOptionActivity.class, "Opening Payment Mode.");
                    }
                } else {
                    // 🔹 Vertical Swipe (Up/Down)
                    if (deltaY < 0) {

                        navigateToActivity(TransactionHistoryBrailleLoginActivity.class, "Enter your 4 digit Login Pin to Know Past Transaction.");
                    }
                }
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

    // 🔹 Common method to navigate & pass USER_ID
    private void navigateToActivity(Class<?> targetActivity, String speechMessage) {
        Log.d("OptionActivity","called me");
        Intent intent = new Intent(OptionActivity.this, targetActivity);
//        intent.putExtra("USER_ID", userId); // Pass USER_ID
//        Log.d("OptionActivity", "OptionActivity Sending USER_ID: " + userId);
        startActivity(intent);
//        if (userId != null) {
//        } else {
//            speakMessage("User ID not found. Please re-login.");
//        }
    }

    private void speakInstructions() {
        String message = "Payment features available. Swipe left for transaction. Swipe right to check balance. Swipe up for past transactions.";
        speakMessage(message);
    }

    private void speakMessage(String message) {
//        textToSpeech.speak(userId, TextToSpeech.QUEUE_FLUSH, null, null);
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
