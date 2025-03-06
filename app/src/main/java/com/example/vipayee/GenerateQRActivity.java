package com.example.vipayee;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class GenerateQRActivity extends AppCompatActivity {
    private ImageView qrImageView;
    private TextToSpeech textToSpeech;
    private static final String USER_ID = Constants.USER_ID;
    private static final String API_URL = Constants.BASE_URL + "user/Registration/GetUserFullName/" + USER_ID;
    private String fullName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        qrImageView = findViewById(R.id.qrImageView);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeech.setLanguage(Locale.US);
                } else {
                    Log.e("TTS", "Initialization failed!");
                }
            }
        });

        // Fetch user details before generating the QR code
        fetchUserDetails();
    }

    private void fetchUserDetails() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, API_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            fullName = response.getString("fullName"); // Adjust key as per API response
                            generateQRCode();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(GenerateQRActivity.this, "Error parsing user data", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(GenerateQRActivity.this, "Failed to fetch user details", Toast.LENGTH_SHORT).show();
                        Log.e("API_ERROR", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void generateQRCode() {
        if (fullName != null && !fullName.isEmpty()) {
            Bitmap qrCodeBitmap = generateQRCodeBitmap(USER_ID, fullName);
            if (qrCodeBitmap != null) {
                qrImageView.setImageBitmap(qrCodeBitmap);
                speak("QR code generated successfully."); // ✅ Speak success message
            } else {
                Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "User details not available", Toast.LENGTH_SHORT).show();
        }
    }

    public static Bitmap generateQRCodeBitmap(String userID, String fullName) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserID", userID);
            jsonObject.put("FullName", fullName);

            String qrData = jsonObject.toString();
            BitMatrix bitMatrix = new com.google.zxing.qrcode.QRCodeWriter().encode(qrData, BarcodeFormat.QR_CODE, 300, 300);

            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException | JSONException e) {
            Log.e("QRCodeGenerator", "QR Code generation failed", e);
            return null;
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
