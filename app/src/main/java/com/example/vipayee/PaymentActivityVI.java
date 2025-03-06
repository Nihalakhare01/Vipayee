package com.example.vipayee;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivityVI extends AppCompatActivity {

    private TextView txtPayeeDetails;
    private EditText edtAmount;
    private Button btnMakePayment;

    private String receiverID, payeeName;
    private static final String SENDER_ID = Constants.USER_ID; // Sender ID from app
    private TextToSpeech textToSpeech; // TTS instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_vi);

        // Request Notification Permission (For Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }

        txtPayeeDetails = findViewById(R.id.txt_payee_details);
        edtAmount = findViewById(R.id.edt_amount);
        btnMakePayment = findViewById(R.id.btn_make_payment);

        // Get ReceiverID and PayeeName from Intent
        receiverID = getIntent().getStringExtra("ReceiverID");
        payeeName = getIntent().getStringExtra("PayeeName");

        // Display only Payee Name (Hide ReceiverID)
        txtPayeeDetails.setText("Payee: " + payeeName);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
                speak("Enter the amount to proceed with the transaction.");
            } else {
                Log.e("TTS", "Initialization failed!");
            }
        });

        btnMakePayment.setOnClickListener(view -> makePayment());
    }

    private void makePayment() {
        String amountStr = edtAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            speak("Please enter the amount.");
            Toast.makeText(this, "Enter amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            speak("Invalid amount. Please enter a valid number.");
            Toast.makeText(this, "Invalid amount!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Speak before transaction
        speak("Transferring ₹" + amount + " to " + payeeName);

        // Create Transaction Request
        TransactionRequest request = new TransactionRequest(SENDER_ID, receiverID, amount, payeeName);

        // Make API call
        ApiService apiService = RetrofitClient.getApiService();
        apiService.processTransaction(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    speak("Transaction successful. You sent ₹" + amount + " to " + payeeName);
                    NotificationHelper.showNotification(PaymentActivityVI.this,
                            "Transaction Successful",
                            "You sent ₹" + amount + " to " + payeeName + ".");
                    Toast.makeText(PaymentActivityVI.this, "Transaction Successful!", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity after success
                } else {
                    speak("Transaction failed. Please try again.");
                    Toast.makeText(PaymentActivityVI.this, "Transaction Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("API_ERROR", "Transaction failed: " + t.getMessage());
                speak("Transaction failed due to an API error. Please try again.");
                Toast.makeText(PaymentActivityVI.this, "API Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void speak(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
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
