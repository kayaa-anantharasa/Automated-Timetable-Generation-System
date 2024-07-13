package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class VerifyActivity extends AppCompatActivity {

    EditText emailEditText, otpEditText;
    TextView otpTextView;
    Button sendOtpButton, verifyOtpButton;
    String generatedOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        emailEditText = findViewById(R.id.email);
        otpEditText = findViewById(R.id.otp);
        otpTextView = findViewById(R.id.otp_text);
        sendOtpButton = findViewById(R.id.send_otp);
        verifyOtpButton = findViewById(R.id.verify_otp);

        sendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    emailEditText.setError("Email cannot be empty");
                    return;
                }

                sendOtp(email);
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredOtp = otpEditText.getText().toString().trim();
                if (TextUtils.isEmpty(enteredOtp)) {
                    otpEditText.setError("OTP cannot be empty");
                    return;
                }

                if (enteredOtp.equals("1234")) {
                    Intent intent = new Intent(VerifyActivity.this, ChangePassword.class);
                    intent.putExtra("email", emailEditText.getText().toString().trim());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(VerifyActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendOtp(String email) {
        // Generate a random 6-digit OTP
        Random random = new Random();
        generatedOtp = String.format("%06d", random.nextInt(1000000));

        // Send the OTP to the email (Here you need to implement email sending)
        // This is a placeholder for sending the email
        Toast.makeText(this, "OTP sent to " + email, Toast.LENGTH_SHORT).show();

        // Make OTP fields visible
        otpTextView.setVisibility(View.VISIBLE);
        otpEditText.setVisibility(View.VISIBLE);
        verifyOtpButton.setVisibility(View.VISIBLE);
    }
}
