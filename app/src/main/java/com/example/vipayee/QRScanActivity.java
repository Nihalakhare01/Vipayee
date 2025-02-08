package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
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

public class QRScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private boolean isQrCodeProcessed = false; // Flag to track QR code processing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

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
                Log.e("QRSacnActivity", "Camera initialization failed.", e);
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

                                // Navigate to ResultActivity with the raw QR content
                                Intent intent = new Intent(QRScanActivity.this, QRPaymentProcessActivity.class);
                                intent.putExtra("QR_CONTENT", qrContent);
                                startActivity(intent);

                                // Stop analyzing further frames
                                imageProxy.close();
                                finish(); // Close the current activity
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("QRSacnActivity", "QR code scan failed.", e);
                        })
                        .addOnCompleteListener(task -> imageProxy.close());
            } catch (Exception e) {
                Log.e("QRSacnActivity", "Failed to process image.", e);
                imageProxy.close();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isQrCodeProcessed = false; // Reset the flag when the activity resumes
    }
}
