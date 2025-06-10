package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UPITransactionActivity extends AppCompatActivity {

    private TextView payeeNameText;
    private EditText amountField;
    private Button payButton;
    private String receiverId;
    private String payeeName;
    private TextToSpeech textToSpeech;
    private String senderId;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_transaction);

        payeeNameText = findViewById(R.id.payeeNameText);
        amountField = findViewById(R.id.amountField);
        payButton = findViewById(R.id.payButton);

        receiverId = getIntent().getStringExtra("userId"); // Receiver ID from API
        payeeName = getIntent().getStringExtra("payeeName");

        payeeNameText.setText("Payee Name: " + payeeName);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                    speak("Enter the amount to proceed with the transaction.");
                } else {
                    Log.e("TTS", "Text-to-Speech initialization failed!");
                }
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                senderId = Constants.USER_ID;
                amount = amountField.getText().toString().trim();

                if (amount.isEmpty()) {
                    speak("Please enter a valid amount.");
                    Toast.makeText(UPITransactionActivity.this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Speak before making the transaction, then process the transaction after speech completes
                speakWithCallback("Transferring " + amount + " rupees to " + payeeName, new Runnable() {
                    @Override
                    public void run() {
                        processTransaction(senderId, receiverId, amount, payeeName);
                    }
                });
            }
        });
    }

    private void processTransaction(String senderId, String receiverId, String amount, String payeeName) {
        ApiService apiService = RetrofitClient.getApiService();

        TransactionRequest transaction = new TransactionRequest(senderId, receiverId, Double.parseDouble(amount), payeeName);

        Call<Void> call = apiService.processTransaction(transaction);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    speak("Transaction successful.");

                    NotificationHelper.showNotification(UPITransactionActivity.this,
                            "Transaction Successful",
                            "You sent ₹" + amount + " to " + payeeName + ".");

                    Toast.makeText(UPITransactionActivity.this, "Transaction Successful", Toast.LENGTH_SHORT).show();

                    amountField.setText(""); // Clear input field after transaction

                    // ✅ Redirect to PaymentOptionActivity
                    Intent intent = new Intent(UPITransactionActivity.this, PaymentOptionActivity.class);
                    startActivity(intent);
                    finish(); // optional: close this screen

                } else {
                    speak("Transaction failed. Please try again.");
                    Toast.makeText(UPITransactionActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Transaction failed", t);
                NotificationHelper.showNotification(UPITransactionActivity.this,
                        "Transaction UnSuccessful",
                        "Try again to pay ₹" + amount + " to " + payeeName + ".");
                speak("Transaction failed. Please try again.");
                Toast.makeText(UPITransactionActivity.this, "API Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speak(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void speakWithCallback(String message, Runnable onComplete) {
        if (textToSpeech != null) {
            HashMap<String, String> params = new HashMap<>();
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TTS_FINISHED");

            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, params);

            textToSpeech.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    if ("TTS_FINISHED".equals(utteranceId)) {
                        runOnUiThread(onComplete);
                    }
                }
            });
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
