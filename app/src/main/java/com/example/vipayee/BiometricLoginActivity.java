package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.Locale;
import java.util.concurrent.Executor;

public class BiometricLoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101011;
    private ImageView imageView;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private TextToSpeech textToSpeech;
    private BiometricPrompt.PromptInfo promptInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_biometric_login);
        imageView = findViewById(R.id.imagView);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });



        // Initialize Biometric Manager
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {

            case BiometricManager.BIOMETRIC_SUCCESS:

                Log.d("MY_APP_TAG", "App can authenticate using biometrics");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Log.e("MY_APP_TAG", "No biometric features available on this device");
                Toast.makeText(this, "No biometric hardware available", Toast.LENGTH_SHORT).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable");
                Toast.makeText(this, "Biometric hardware unavailable", Toast.LENGTH_SHORT).show();
                return;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompt user to enroll biometrics
                Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                return;
        }


        // Initialize Executor & Biometric Prompt
        executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(BiometricLoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                textToSpeech.speak("Error while authentication. Please Try again.", TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(getApplicationContext(), "Authentication error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                textToSpeech.speak("Authentication succeessful. Opening Payement features.", TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(getApplicationContext(), "Authentication succeeded!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), OptionActivity.class);

                startActivity(i);
                finish();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                textToSpeech.speak("Authentication failed. Please Try again.", TextToSpeech.QUEUE_FLUSH, null, null);
                Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        });

        // Configure Biometric Prompt Info
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Login")
                .setSubtitle("Use your fingerprint or face to log in")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .build();


        // Set onClickListener for biometric authentication

        imageView.setOnClickListener(view -> {
            speakMessage("Touch the fingerprint sensor.");
            imageView.postDelayed(() -> biometricPrompt.authenticate(promptInfo), 2000); // 2-second delay before authentication
        });

    }
    private void speakInstructions() {
        String message = "Tap on bottom to open biometric authentication.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }


    private void speakMessage(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }


//    public boolean onTouchEvent (MotionEvent motionEvent){
//        switch (motionEvent.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                x1 = motionEvent.getX();
//                y1 = motionEvent.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = motionEvent.getX();
//                y2 = motionEvent.getY();
//
////                right slider
//                if (x1 > x2){
//                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(i);
//                }
//                break;
//        }
//        return false;
//    }

//    @Override
//    protected void onDestroy() {
//        if (textToSpeech != null) {
//            textToSpeech.stop();
//            textToSpeech.shutdown();
//        }
//        super.onDestroy();
//    }
}
