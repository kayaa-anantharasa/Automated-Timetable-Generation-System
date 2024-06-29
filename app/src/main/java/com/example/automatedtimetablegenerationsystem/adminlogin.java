package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class adminlogin extends AppCompatActivity {


    EditText login_username, login_password;
    Button login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminlogin);


        login_username = findViewById(R.id.username);
        login_password = findViewById(R.id.password);
        login_btn = findViewById(R.id.adminLogin);

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
        String val = login_username.getText().toString().trim();
        if (val.isEmpty()) {
            login_username.setError("Username cannot be empty");
            return false;
        } else {
            login_username.setError(null);
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
        String userName = login_username.getText().toString().trim();
        String userPassword = login_password.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("admin");
//
        Query checkUserQuery = reference.orderByChild("name").equalTo(userName);

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

                            Intent intent = new Intent(adminlogin.this, admin_home.class);
                            intent.putExtra("name", nameFromDB);
                            intent.putExtra("email", emailFromDB);
                            startActivity(intent);
                            return;
                        } else {

                            Toast.makeText(adminlogin.this, "Invalid password", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {

                    Toast.makeText(adminlogin.this, "User does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(adminlogin.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
