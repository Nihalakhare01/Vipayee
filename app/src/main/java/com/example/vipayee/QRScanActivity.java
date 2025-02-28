package com.example.vipayee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import android.content.pm.PackageManager;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import androidx.camera.view.PreviewView;
import java.util.Locale;

public class QRScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private String userId;
    private TextToSpeech textToSpeech;
    private boolean isQrCodeProcessed = false; // Flag to track QR code processing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.ENGLISH);
                speakText("You can scan QR now"); // Speak when the activity starts
            }
        });

        // Retrieve USER_ID from SharedPreferences
//        SharedPreferences prefs = getSharedPreferences(Constants.PREF_NAME, MODE_PRIVATE);
//        userId = prefs.getString("USER_ID", null);
//
//        if (userId != null) {
//            Log.d("PaymentOptionActivity", "Retrieved USER_ID: " + userId);
//        } else {
//            Log.e("PaymentOptionActivity", "USER_ID not found in SharedPreferences.");
//        }

        // Initialize ML Kit Barcode Scanner
        barcodeScanner = BarcodeScanning.getClient();
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Check for camera permission and request it if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // Start the camera directly
            startCamera();
        }
    }

    // Method to start the camera and set up the preview
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview use case
                Preview preview = new Preview.Builder().build();

                // Image analysis use case
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, new QRCodeAnalyzer());

                // Select the back camera
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Bind use cases to the lifecycle
                cameraProvider.unbindAll();
                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

                // Set up the PreviewView to display the camera feed
                PreviewView previewView = findViewById(R.id.previewView);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            } catch (Exception e) {
                Log.e("QRScanActivity", "Camera initialization failed.", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // QR Code analyzer to process frames from the camera
    private class QRCodeAnalyzer implements ImageAnalysis.Analyzer {
        @OptIn(markerClass = ExperimentalGetImage.class)
        @Override
        public void analyze(ImageProxy imageProxy) {
            try {
                if (imageProxy.getImage() == null) return;

                // Skip processing if a QR code has already been detected
                if (isQrCodeProcessed) {
                    imageProxy.close();
                    return;
                }

                // Convert the ImageProxy to InputImage for ML Kit
                InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());

                // Process the image with ML Kit Barcode Scanner
                barcodeScanner.process(image)
                        .addOnSuccessListener(barcodes -> {
                            for (Barcode barcode : barcodes) {
                                // Extract QR code content
                                String qrContent = barcode.getRawValue();
                                Log.d("QR Code", "QR Content: " + qrContent);

                                // Set the flag to true to prevent further processing
                                isQrCodeProcessed = true;

                                // Speak success message
                                speakText("QR fetched successfully");

                                // Delay finishing the activity to let TTS complete
                                new android.os.Handler().postDelayed(() -> {
                                    // Navigate to QRPaymentProcessActivity with the QR content
                                    Intent intent = new Intent(QRScanActivity.this, QRPaymentProcessActivity.class);
                                    intent.putExtra("QR_CONTENT", qrContent);
                                    startActivity(intent);

                                    finish(); // Close the current activity
                                }, 2300); // 2-second delay (adjust as needed)
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("QRScanActivity", "QR code scan failed.", e);
                            speakText("Unable to fetch QR");
                        })
                        .addOnCompleteListener(task -> imageProxy.close());

            } catch (Exception e) {
                Log.e("QRScanActivity", "Failed to process image.", e);
                imageProxy.close();
            }
        }
    }

    // Function to speak text
    private void speakText(String message) {
        if (textToSpeech != null) {
            textToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isQrCodeProcessed = false; // Reset the flag when the activity resumes
    }
}
