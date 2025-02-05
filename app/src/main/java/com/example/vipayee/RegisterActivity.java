package com.example.vipayee;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterActivity extends AppCompatActivity {
    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        EditText phoneInput = findViewById(R.id.editTextPhone);
        Button nextButton = findViewById(R.id.button);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneInput.getText().toString();

                if (phoneNumber.isEmpty()) {
                    phoneInput.setError("Phone number is required");
                } else if (phoneNumber.length() != 10) {
                    phoneInput.setError("Phone number must be 10 digits");
                } else {
                    Toast.makeText(RegisterActivity.this, "You'll be sent an OTP!", Toast.LENGTH_SHORT).show();
                    goToNextPage();
                    // Navigate to OTP Activity
//                    Intent intent = new Intent(RegisterActivity.this, RegisterationOTPActivity.class);
//                    intent.putExtra("PHONE_NUMBER", phoneNumber);
//                    startActivity(intent);
//                    finish();
                }
            }
            private void goToNextPage() {
                Intent intent = new Intent(RegisterActivity.this, RegisterationOTPActivity.class);
                startActivity(intent);
                finish();  // Close current activity
            }
        });
    }

    public boolean onTouchEvent (MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = motionEvent.getX();
                y1 = motionEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = motionEvent.getX();
                y2 = motionEvent.getY();

//                Left slider
                if (x1 > x2) {
                    Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                break;
        }
        return true;
    }
}
