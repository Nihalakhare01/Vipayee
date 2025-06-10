package com.example.vipayee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class PaymentOptionActivity extends AppCompatActivity {

    float x1,x2,y1,y2;
    private TextToSpeech textToSpeech;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payement_option);

//        // 🔹 Retrieve USER_ID from SharedPreferences
//        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
//        userId = prefs.getString("USER_ID", null);
//
//        if (userId != null) {
//            Log.d("PaymentOptionActivity", "Retrieved USER_ID: " + userId);
//
//        } else {
//            Log.e("PaymentOptionActivity", "USER_ID not found in SharedPreferences.");
//        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });
    }

    // 🔹 Common method to navigate & pass USER_ID
    private void navigateToActivity(Class<?> targetActivity, String speechMessage) {
        Log.d("PaymentOptionActivity","called me");
        Intent intent = new Intent(PaymentOptionActivity.this, targetActivity);
        startActivity(intent);
//        speakMessage("User ID found.");


//        if (userId != null) {
//
//            intent.putExtra("USER_ID", userId); // Pass USER_ID
//            Log.d("PaymentOptionActivity", "PaymentOptionActivity Sending USER_ID: " + userId);
//        } else {
//        }
    }


    private void speakInstructions() {
        String message = "Make Transaction here. Swipe right for Scan the QR. Swipe left for Pay By Phone Number. Swipe Up to generate QR.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                x1 = motionEvent.getX();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = motionEvent.getX();
//
//
//                //Left slide
//                if (x1 < x2) {
//                    navigateToActivity(QRScanActivity.class, "Opening Check Balance.");
////                    Right Slide
//                } else if (x1 > x2) {
//                    navigateToActivity(UPIActivity.class, "Opening Check Balance.");
//                }
//
//                break;



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
                        navigateToActivity(QRScannerActivityVI.class, "Opening VI Scan.");
                    } else {
                        navigateToActivity(UPIActivity.class, "Opening Upi Mode.");
                    }
                } else {
                    // 🔹 Vertical Swipe (Up/Down)
                    if (deltaY > 0) {
                        navigateToActivity(GenerateQRActivity.class, "TO Get QR");
                    }
                    else {

                        navigateToActivity(GenerateQRActivity.class, "TO Get QR");
                    }
                }
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

 private void speakMessage(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
}
