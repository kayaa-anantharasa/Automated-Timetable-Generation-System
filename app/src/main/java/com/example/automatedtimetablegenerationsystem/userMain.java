package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.automatedtimetablegenerationsystem.databinding.ActivityUserMainBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class userMain extends AppCompatActivity {

    ActivityUserMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        // Inflate layout and set content view
        binding = ActivityUserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Replace initial fragment with userhomeFragment
        replaceFragment(new userhomeFragment());
        binding.userbottom.setBackground(null);

        // Handle bottom navigation item selection
        binding.userbottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.userhome) {
                replaceFragment(new userhomeFragment());
            } else if (item.getItemId() == R.id.usertimetable) {
                replaceFragment(new usertimetableFragment());
            }  else if (item.getItemId() == R.id.userlogout) {
                logoutUser();
            }
            return true;
        });
    }

    private void logoutUser() {
        // Retrieve username (name) from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("user_data", MODE_PRIVATE);
        String username = preferences.getString("username", "");

        // Check if username is available
        if (!username.isEmpty()) {
            // Get current date and time formatted as string
            String currentTime = getCurrentDateTime(); // Ensure this method returns the current timestamp as needed

            // Update Firebase Realtime Database
            DatabaseReference logoutTimesRef = FirebaseDatabase.getInstance().getReference().child("logout_times").child(username);
            logoutTimesRef.setValue(currentTime)
                    .addOnSuccessListener(aVoid -> {
                        // Logout successful, navigate to login activity
                        Intent intent = new Intent(userMain.this, login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                        Toast.makeText(userMain.this, "Failed to update logout time: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Handle case where username is empty (logically shouldn't happen if user is logged in properly)
            Toast.makeText(userMain.this, "Username not found", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentDateTime() {
        // Get current date and time formatted
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userframelayoutid, fragment);
        fragmentTransaction.commit();
    }
}
