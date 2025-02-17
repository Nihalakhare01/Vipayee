package com.example.vipayee;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashMap;
import java.util.Locale;

public class QRPaymentProcessActivity extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private EditText edtAmount;
    private String upiId = "", name = "";
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_payment_process);

        // Get the raw QR content passed from QRScanActivity
        String qrContent = getIntent().getStringExtra("QR_CONTENT");

        edtAmount = findViewById(R.id.edtAmount);
        btnPay = findViewById(R.id.btnPay);
        TextView tvUpiId = findViewById(R.id.tv_upi_id);
        TextView tvName = findViewById(R.id.tv_name);

        // Parse the UPI ID and Name from the QR content
        if (qrContent != null && qrContent.startsWith("upi://pay?")) {
            Uri uri = Uri.parse(qrContent);
            upiId = uri.getQueryParameter("pa"); // Extract UPI ID
            name = uri.getQueryParameter("pn"); // Extract Name
        }

        // Display UPI ID and Name
        tvUpiId.setText("UPI ID: " + (upiId != null ? upiId : "Not Found"));
        tvName.setText(name != null ? name : "Not Found");

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speak("Please enter the amount to transfer to " + name, null);
            }
        });

        // Auto-focus on amount field
        edtAmount.requestFocus();

        // Handling button click
        btnPay.setOnClickListener(view -> {
            String amount = edtAmount.getText().toString();

            if (amount.isEmpty() || Double.parseDouble(amount) <= 0) {
                Toast.makeText(QRPaymentProcessActivity.this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                speak("Please enter a valid amount", null);
            } else {
                String confirmationMessage = "You are transferring Rupees " + amount + " to " + name;
                Toast.makeText(QRPaymentProcessActivity.this, confirmationMessage, Toast.LENGTH_SHORT).show();

                // Speak the confirmation message and proceed after it completes
                speak(confirmationMessage, () -> {
                    Intent intent = new Intent(QRPaymentProcessActivity.this, PaymentActivity.class);
                    intent.putExtra("upiId", upiId);
                    intent.putExtra("upiName", name);
                    intent.putExtra("amount", amount);
                    startActivity(intent);
                    finish();
                });
            }
        });

        // Handle back press using dispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(QRPaymentProcessActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    // Speak method with completion callback
    private void speak(String text, Runnable onComplete) {
        if (textToSpeech != null) {
            String utteranceId = String.valueOf(System.currentTimeMillis());

            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {}

                @Override
                public void onDone(String utteranceId) {
                    runOnUiThread(() -> {
                        if (onComplete != null) {
                            onComplete.run(); // Execute after speech completes
                        }
                    });
                }

                @Override
                public void onError(String utteranceId) {}
            });

            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, params);
        }
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
