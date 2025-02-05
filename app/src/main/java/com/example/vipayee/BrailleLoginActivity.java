package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class BrailleLoginActivity extends AppCompatActivity {
    private TextView pinDisplay;
    private TextToSpeech textToSpeech;
    private boolean dot1Pressed = false;
    private boolean dot2Pressed = false;
    private boolean dot3Pressed = false;
    private boolean dot4Pressed = false;
    private Vibrator vibrator;
    private StringBuilder pin = new StringBuilder();
    private Handler handler = new Handler();
    private Runnable recognizeNumberRunnable;
    private int digitCount = 0;  // To track digit entry progress

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_braille_login);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        pinDisplay = findViewById(R.id.pinDisplay);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakMessage("Enter your four digit login pin. Enter first digit.");
            }
        });

        // Initialize the button click listeners
        findViewById(R.id.dot1).setOnClickListener(v -> onDotPressed(1));
        findViewById(R.id.dot2).setOnClickListener(v -> onDotPressed(2));
        findViewById(R.id.dot3).setOnClickListener(v -> onDotPressed(3));
        findViewById(R.id.dot4).setOnClickListener(v -> onDotPressed(4));
    }

    private void onDotPressed(int dotNumber) {
        // Toggle the pressed state of the dot
        switch (dotNumber) {
            case 1:
                dot1Pressed = !dot1Pressed;
                break;
            case 2:
                dot2Pressed = !dot2Pressed;
                break;
            case 3:
                dot3Pressed = !dot3Pressed;
                break;
            case 4:
                dot4Pressed = !dot4Pressed;
                break;
        }

        // Remove any previous pending recognition task
        if (recognizeNumberRunnable != null) {
            handler.removeCallbacks(recognizeNumberRunnable);
        }

        // Set a new recognition task after 500ms
        recognizeNumberRunnable = this::recognizeAndUpdatePin;
        handler.postDelayed(recognizeNumberRunnable, 500);
    }

    private void recognizeAndUpdatePin() {
        if (pin.length() < 7) {  // "● " counts as 2 characters per digit
            String number = getBrailleNumber(dot1Pressed, dot2Pressed, dot3Pressed, dot4Pressed);

            if (!number.isEmpty()) {
                pin.append("● ");
                pinDisplay.setText(pin.toString().trim());

                // Speak confirmation
                digitCount++;
                speakMessage("You have entered digit " + digitCount + ".");

                // Trigger vibration after every valid digit
                triggerVibration();

                // If all 4 digits are entered, proceed to the next page
                if (digitCount < 4) {
                    handler.postDelayed(() -> speakMessage("Enter digit " + (digitCount + 1) + "."), 1500);
                } else {
                    handler.postDelayed(this::goToNextPage, 1500);
                }
            }
        }

        resetGrid();
    }

    private void goToNextPage() {
        speakMessage("Authentication successful. Moving to payment feature section.");
        startActivity(new Intent(BrailleLoginActivity.this, OptionActivity.class));
        finish();
    }

    private String getBrailleNumber(boolean d1, boolean d2, boolean d3, boolean d4) {
        String pattern = "" + (d1 ? "1" : "0") + (d2 ? "1" : "0") + (d3 ? "1" : "0") + (d4 ? "1" : "0");

        switch (pattern) {
            case "1000": return "1";
            case "1100": return "2";
            case "1010": return "3";
            case "1011": return "4";
            case "1001": return "5";
            case "1110": return "6";
            case "1111": return "7";
            case "1101": return "8";
            case "0110": return "9";
            case "0101": return "0";
            default: return "";
        }
    }

    private void resetGrid() {
        dot1Pressed = false;
        dot2Pressed = false;
        dot3Pressed = false;
        dot4Pressed = false;
    }

    private void triggerVibration() {
        if (vibrator != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, 255));
            } else {
                vibrator.vibrate(100);
            }
        }
    }

    private void speakMessage(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
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