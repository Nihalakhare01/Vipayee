package com.example.vipayee;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegisterationOTPActivity extends AppCompatActivity {

    private EditText[] otpFields = new EditText[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register_otp);
        otpFields[0] = findViewById(R.id.otp1);
        otpFields[1] = findViewById(R.id.otp2);
        otpFields[2] = findViewById(R.id.otp3);
        otpFields[3] = findViewById(R.id.otp4);
        Button submitOtp = findViewById(R.id.submitOtp);

        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});

            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    } else if (s.length() == 0 && index > 0) {
                        otpFields[index - 1].requestFocus();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        submitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder otpCode = new StringBuilder();
                for (EditText otpField : otpFields) {
                    otpCode.append(otpField.getText().toString());
                }
                if (otpCode.length() == 4) {
                    Toast.makeText(RegisterationOTPActivity.this, "OTP Entered: " + otpCode.toString(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterationOTPActivity.this, "Please enter all digits", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
