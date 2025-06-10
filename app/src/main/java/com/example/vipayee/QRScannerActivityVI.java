package com.example.vipayee;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.view.PreviewView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerActivityVI extends AppCompatActivity {

    private TextView txtScannedData;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private ProcessCameraProvider cameraProvider;
    private String receiverID, payeeName;
    private TextToSpeech textToSpeech;  // 🔹 TTS object

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner_vi);

        previewView = findViewById(R.id.previewView);
        txtScannedData = findViewById(R.id.txtScannedData); // Ensure TextView is initialized

        // 🔹 Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            startCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                barcodeScanner = BarcodeScanning.getClient();
                cameraExecutor = Executors.newSingleThreadExecutor();

                imageAnalysis.setAnalyzer(cameraExecutor, this::scanBarcode);

                Camera camera = cameraProvider.bindToLifecycle(
                        this, cameraSelector, preview, imageAnalysis);

            } catch (Exception e) {
                Log.e("QRScanner", "Camera binding failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void scanBarcode(ImageProxy image) {
        if (image.getImage() == null) {
            image.close();
            return;
        }

        @SuppressWarnings("UnsafeOptInUsageError")
        InputImage inputImage = InputImage.fromMediaImage(image.getImage(), image.getImageInfo().getRotationDegrees());

        barcodeScanner.process(inputImage)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String rawValue = barcode.getRawValue();
                        if (rawValue != null) {
                            handleScannedData(rawValue);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("QRScanner", "QR Scan Failed", e))
                .addOnCompleteListener(task -> image.close());
    }

    private void handleScannedData(String qrData) {
        try {
            JSONObject jsonObject = new JSONObject(qrData);
            receiverID = jsonObject.optString("UserID", null);
            payeeName = jsonObject.optString("FullName", null);

            if (receiverID == null || payeeName == null) {
                runOnUiThread(() -> Toast.makeText(this, "Invalid QR Code!", Toast.LENGTH_SHORT).show());
                return;
            }

            // 🔹 Speak out details fetched successfully
            String message = "Details fetched successfully. Moving to payment page.";
            speak(message);

            runOnUiThread(() -> {
                txtScannedData.setText("Receiver ID: " + receiverID + "\nPayee Name: " + payeeName);
                moveToPaymentPage();
            });

        } catch (JSONException e) {
            Log.e("QRScanner", "Error parsing QR data: " + e.getMessage());
            runOnUiThread(() -> Toast.makeText(this, "Invalid QR Code", Toast.LENGTH_SHORT).show());
        }
    }

    private void moveToPaymentPage() {
        stopCamera();

        Intent intent = new Intent(QRScannerActivityVI.this, PaymentActivityVI.class);
        intent.putExtra("ReceiverID", receiverID);
        intent.putExtra("PayeeName", payeeName);
        startActivity(intent);
        finish();
    }

    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }

    // 🔹 Speak text using Text-to-Speech
    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopCamera();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
