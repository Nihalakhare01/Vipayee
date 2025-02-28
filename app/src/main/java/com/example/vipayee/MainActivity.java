package com.example.vipayee;

import android.content.Intent;
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

public class MainActivity extends AppCompatActivity {

    private float x1, x2;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

//        Constants.loadUserId(this);
//
//        if (Constants.USER_ID != null) {
//            Log.d("MainActivity", "User UUID: " + Constants.USER_ID.toString());
//        } else {
//            Log.e("MainActivity", "User UUID not set yet!");
//        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void speakInstructions() {
        String message = "Welcome to Visual Pay. Swipe left to go to the registration. Swipe right to open Login process.";
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
                    textToSpeech.speak("Opening registration section.", TextToSpeech.QUEUE_FLUSH, null, null);
                    Intent i = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(i);
//                    Right Slide
                } else if (x1 > x2) {
                    textToSpeech.speak("Opening Login process section.", TextToSpeech.QUEUE_FLUSH, null, null);
                    Intent i = new Intent(MainActivity.this, TapOptionActivity.class);
                    startActivity(i);
                }
                break;
        }
        return super.onTouchEvent(motionEvent);
    }

//    @Override
//    protected void onDestroy() {
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//        super.onDestroy();
//    }
}