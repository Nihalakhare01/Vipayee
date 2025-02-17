package com.example.vipayee;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {

    private TextToSpeech textToSpeech;
    private String userId;
    private RecyclerView recyclerView;
    private TransactionAdapter transactionAdapter;
    private List<TransactionModel> transactionList;

    private static final String API_URL = Constants.BASE_URL + "user/Transaction/get-transactions/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(transactionAdapter);

        // Retrieve USER_ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
        userId = prefs.getString("USER_ID", null);

        if (userId != null) {
            Log.d("TransactionActivity", "Retrieved USER_ID: " + userId);
            fetchTransactionHistory();
        } else {
            Log.e("TransactionActivity", "USER_ID not found in SharedPreferences.");
            Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
        }

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakInstructions();
            }
        });
    }

    private void fetchTransactionHistory() {
        String url = API_URL + userId; // Append user ID to API URL

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        transactionList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                String transactionType = jsonObject.getString("transactionType");
                                String amount = jsonObject.getString("amount");
                                String payeeName = jsonObject.getString("payeeName");
                                String transactionDate = jsonObject.getString("transactionDate");

                                transactionList.add(new TransactionModel(transactionType, amount, payeeName, transactionDate));
                            }

                            transactionAdapter.notifyDataSetChanged();
                            speakFirstFiveTransactions(); // Call TTS after fetching transactions

                        } catch (JSONException e) {
                            Log.e("TransactionActivity", "JSON Parsing error: " + e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TransactionActivity", "API Error: " + error.getMessage());
                        Toast.makeText(TransactionHistoryActivity.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                    }
                });

        Volley.newRequestQueue(this).add(jsonArrayRequest);
    }

    private void speakFirstFiveTransactions() {
        if (transactionList.isEmpty()) {
            textToSpeech.speak("No transactions found", TextToSpeech.QUEUE_FLUSH, null, null);
            return;
        }

        StringBuilder transactionDetails = new StringBuilder("Your first 5 transactions are: ");

        int count = Math.min(5, transactionList.size()); // Get only first 5 transactions
        for (int i = 0; i < count; i++) {
            TransactionModel transaction = transactionList.get(i);
            String type = transaction.getTransactionType().equalsIgnoreCase("Credit") ? "Received from" : "Sent to";

            // Format date and time
            String formattedDate = formatDate(transaction.getTransactionDate());
            String formattedTime = formatTime(transaction.getTransactionDate());

            String detail = type + " " + transaction.getPayeeName() +
                    ", amount " + transaction.getAmount() +
                    ", on " + formattedDate +
                    " at " + formattedTime + ". ";

            transactionDetails.append(detail);
        }

        textToSpeech.speak(transactionDetails.toString(), TextToSpeech.QUEUE_FLUSH, null, null);
    }

    // Function to format Date (dd-MM-yyyy)
    private String formatDate(String transactionDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date date = inputFormat.parse(transactionDate);
            return (date != null) ? outputFormat.format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    // Function to format Time (HH:mm)
    private String formatTime(String transactionDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = inputFormat.parse(transactionDate);
            return (date != null) ? outputFormat.format(date) : "";
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }


    private void speakInstructions() {
        String message = "Check Transaction History here.";
        textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}