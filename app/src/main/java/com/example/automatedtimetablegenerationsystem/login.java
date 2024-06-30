package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class login extends AppCompatActivity {

    EditText login_matrix, login_password;
    Button login_btn;
    TextView signup_txt;
    TextView adminlogin_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signup_txt = findViewById(R.id.signuptxt);
        adminlogin_txt = findViewById(R.id.adminlogin);
        login_matrix = findViewById(R.id.loginmatrix);
        login_password = findViewById(R.id.loginpassword);
        login_btn = findViewById(R.id.loginbtn);

        signup_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);
            }
        });

        adminlogin_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, adminlogin.class);
                startActivity(intent);
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateUsername() && validatePassword()) {
                    checkUser();
                }
            }
        });
    }

    public Boolean validateUsername() {
        String val = login_matrix.getText().toString().trim();
        if (val.isEmpty()) {
            login_matrix.setError("Username cannot be empty");
            return false;
        } else {
            login_matrix.setError(null);
            return true;
        }
    }

    public Boolean validatePassword() {
        String val = login_password.getText().toString().trim();
        if (val.isEmpty()) {
            login_password.setError("Password cannot be empty");
            return false;
        } else {
            login_password.setError(null);
            return true;
        }
    }

    public void checkUser() {
        String userMatrixNumber = login_matrix.getText().toString().trim();
        String userPassword = login_password.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserQuery = reference.orderByChild("matrixNumber").equalTo(userMatrixNumber);

        checkUserQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String encryptedPasswordFromDB = userSnapshot.child("password").getValue(String.class);
                        String encryptedEnteredPassword = encryptPassword(userPassword);

                        if (encryptedPasswordFromDB != null && encryptedPasswordFromDB.equals(encryptedEnteredPassword)) {
                            String nameFromDB = userSnapshot.child("name").getValue(String.class);
                            String emailFromDB = userSnapshot.child("email").getValue(String.class);

                            Intent intent = new Intent(login.this, userMain.class);
                            intent.putExtra("name", nameFromDB);
                            intent.putExtra("email", emailFromDB);
                            startActivity(intent);
                            finish(); // Close login activity
                            return;
                        } else {
                            Toast.makeText(login.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(login.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(login.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                //Log.e("Database Error", error.getMessage()); // Log error for debugging
            }
        });
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
