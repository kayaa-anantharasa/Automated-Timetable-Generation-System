package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class signup extends AppCompatActivity {

 //   @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_signup);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
 EditText signup_name, signup_email, signup_password,signup_cpassword;
    EditText signup_matrixnumber;
    //TextView loginRedirectText;
    Button signup_Button;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signup_name = findViewById(R.id.signupname);
        signup_email = findViewById(R.id.signupemail);
        signup_matrixnumber = findViewById(R.id.signupmatrixnumber);
        signup_password = findViewById(R.id.loginpassword);
        signup_cpassword = findViewById(R.id.signupcpassword);
        signup_Button = findViewById(R.id.signupButton);
        signup_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateInputs()) {
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");
                    String name = signup_name.getText().toString();
                    String email = signup_email.getText().toString();
                    String matrixnumber = signup_matrixnumber.getText().toString();
                    ;
                    String password = signup_password.getText().toString();
                    String cpassword = signup_cpassword.getText().toString();

                    int matrixNumber = 0;
                    try {
                        matrixNumber = Integer.parseInt(signup_matrixnumber.getText().toString());
                    } catch (NumberFormatException e) {

                        Toast.makeText(signup.this, "Invalid matrix number", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String encryptedPassword = encryptPassword(password);

                    signupClass signupData = new signupClass(name, email, encryptedPassword, matrixNumber);
                    reference.child(name).setValue(signupData);
                    Toast.makeText(signup.this, "You have signup successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(signup.this, login.class);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean validateInputs() {
        String name = signup_name.getText().toString().trim();
        String email = signup_email.getText().toString().trim();
        String matrixNumberStr = signup_matrixnumber.getText().toString().trim();
        String password = signup_password.getText().toString().trim();
        String confirmPassword = signup_cpassword.getText().toString().trim();

        if (name.isEmpty()) {
            signup_name.setError("Name is required");
            return false;
        }

        if (email.isEmpty()) {
            signup_email.setError("Email is required");
            return false;
        }

        if (matrixNumberStr.isEmpty()) {
            signup_matrixnumber.setError("Matrix number is required");
            return false;
        }

        if (password.isEmpty()) {
            signup_password.setError("Password is required");
            return false;
        }

        if (confirmPassword.isEmpty()) {
            signup_cpassword.setError("Confirm password is required");
            return false;
        }

        if (!password.equals(confirmPassword)) {
            signup_cpassword.setError("Passwords do not match");
            return false;
        }

        // Additional validation can be added here as per requirements

        return true;
    }

    private String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] digest = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // fallback to plain password (not recommended)
        }
    }
}
