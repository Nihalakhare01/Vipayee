package com.example.vipayee;

import static androidx.fragment.app.FragmentManager.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginIfUserExist extends AppCompatActivity {

    EditText editTextPhone, editTextPin;
    private TextToSpeech textToSpeech;
    Button buttonVerify;
    private static final String TAG = "LoginIfUserExist";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_if_user_exist);

        editTextPhone = findViewById(R.id.editTextPhone);
        editTextPin = findViewById(R.id.editTextPin);
        buttonVerify = findViewById(R.id.buttonVerify);

        buttonVerify.setOnClickListener(this::onClick);
    }

    private void onClick(View v) {
        String phone = editTextPhone.getText().toString().trim();
        String enteredPin = editTextPin.getText().toString().trim();

        if (phone.isEmpty() || enteredPin.isEmpty()) {
            Toast.makeText(this, "Please enter both phone and PIN", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        apiService.getUserByPhone(phone).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String actualPin = response.body().getPin();
                    if (enteredPin.equals(actualPin)) {
                        Toast.makeText(LoginIfUserExist.this, "PIN matched!", Toast.LENGTH_SHORT).show();
                       fetchUserId(phone);
                        Intent intent = new Intent(LoginIfUserExist.this, TapOptionActivity.class);
                        startActivity(intent);
                        // You can navigate to next activity here if needed
                    } else {
                        Toast.makeText(LoginIfUserExist.this, "Incorrect PIN!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginIfUserExist.this, "User not found!", Toast.LENGTH_SHORT).show();
                }
            }

            private void fetchUserId(String phoneNumber) {
                OkHttpClient client = new OkHttpClient();
                String url = Constants.BASE_URL + "user/Registration/get-uuid/" + phone;

                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                client.newCall(request).enqueue(new okhttp3.Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        runOnUiThread(() -> {
                            speakMessage("Failed to fetch User ID. Please check your connection.");
                            Toast.makeText(LoginIfUserExist.this, "Failed to fetch User ID", Toast.LENGTH_SHORT).show();
                        });
                        Log.e(TAG, "API Call Failed", e);
                    }

                    @Override
                    public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String responseData = response.body().string();
                                Log.d(TAG, "Raw API Response: " + responseData);
                                JSONObject json = new JSONObject(responseData);
                                String userIdString = json.getString("userID");
                                Log.d(TAG, "Parsed User ID: " + userIdString);

                                if (!userIdString.isEmpty()) {
                                    runOnUiThread(() -> {
                                        Constants.setUserUUID(LoginIfUserExist.this, userIdString);
                                        Log.d(TAG, "Stored User ID in Constants: " + Constants.getUserUUID(LoginIfUserExist.this));
                                        speakMessage("User ID fetched successfully");

                                    });
                                }
                            } catch (Exception e) {
                                runOnUiThread(() -> speakMessage("Error processing User ID data."));
                                Log.e(TAG, "JSON Parsing Error", e);
                            }
                        } else {
                            runOnUiThread(() -> {
                                speakMessage("Failed to retrieve User ID. Response Code: " + response.code());
                                Toast.makeText(LoginIfUserExist.this, "Failed to retrieve User ID", Toast.LENGTH_SHORT).show();
                            });
                            Log.e(TAG, "API Response Error: " + response.code());
                        }
                    }
                });
            }

            private void speakMessage(String message) {
                if (textToSpeech != null) {
                    textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }


            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginIfUserExist.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
