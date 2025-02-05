package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class TapOptionActivity extends AppCompatActivity {
    private GestureDetector gestureDetector;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tap_option);
        gestureDetector = new GestureDetector(this, new GestureListener());
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });
    }

    private void speakInstructions() {
        String message = "Single Tap to Biometric authentication. Double Tap to Pin Authentication.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }
        @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        // Pass touch events to GestureDetector
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }
    // Gesture Listener Class
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            textToSpeech.speak("Opening Biometric authentication", TextToSpeech.QUEUE_FLUSH, null, null);
            Toast.makeText(TapOptionActivity.this, "Single Tap - Moving to Page 1", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TapOptionActivity.this, BiometricLoginActivity.class));
            return true;
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            textToSpeech.speak("Opening Numeric authentication.", TextToSpeech.QUEUE_FLUSH, null, null);
            Toast.makeText(TapOptionActivity.this, "Double Tap - Moving to Page 2", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TapOptionActivity.this, BrailleLoginActivity.class));
            return true;
        }
    }
}
