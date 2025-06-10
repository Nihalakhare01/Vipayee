package com.example.vipayee;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TransactionHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<Transaction> transactionList;
    private TextToSpeech textToSpeech;
    private static final String API_URL = Constants.BASE_URL + "user/Transaction/get-transactions/" + Constants.USER_ID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        // Initialize Text-to-Speech with Indian English accent
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                Locale locale = Locale.forLanguageTag("en-IN"); // Indian English Accent
                int result = textToSpeech.setLanguage(locale);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(this, "TTS Language not supported!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fetchTransactions();
    }

    private void fetchTransactions() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            StringBuilder speechText = new StringBuilder();
                            speechText.append("Your last transactions are. ");

                            int limit = Math.min(response.length(), 5); // Get max 5 transactions
                            for (int i = 0; i < limit; i++) {
                                JSONObject obj = response.getJSONObject(i);
                                String transactionType = obj.getString("transactionType");
                                String payeeName = obj.getString("payeeName");
                                double amount = obj.getDouble("amount");
                                String transactionDateTime = obj.getString("transactionDate");

                                // Format Date & Time
                                String formattedDate = formatDate(transactionDateTime);
                                String formattedTime = formatTime(transactionDateTime);

                                transactionList.add(new Transaction(transactionType, payeeName, amount, formattedDate, formattedTime));

                                // Append transaction details for speech
                                speechText.append("Transaction ").append(i + 1).append(". ");
                                speechText.append(transactionType).append(". ");
                                speechText.append("To ").append(payeeName).append(". ");
                                speechText.append("Amount ").append(amount).append(" rupees. ");
                                speechText.append("On ").append(formattedDate).append(" at ").append(formattedTime).append(". ");
                            }
                            adapter.notifyDataSetChanged();

                            // Speak transactions
                            speak(speechText.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(TransactionHistoryActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TransactionHistoryActivity.this, "API Error", Toast.LENGTH_SHORT).show();
                        Log.e("API_ERROR", error.toString());
                    }
                });

        queue.add(jsonArrayRequest);
    }

    // Function to format date
    private String formatDate(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    // Function to format time
    private String formatTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
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
