package com.example.automatedtimetablegenerationsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class usereditFragment extends Fragment {

    EditText newPasswordEditText, confirmPasswordEditText;
    Button changePasswordButton;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_useredit, container, false);

        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);

        preferences = requireActivity().getSharedPreferences("user_data", requireActivity().MODE_PRIVATE);
        editor = preferences.edit();

        // Initialize Firebase Database
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("users"); // Replace "users" with your desired database reference

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = newPasswordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Retrieve matrixNumber from SharedPreferences
                String matrixNumber = preferences.getString("matrixNumber", "");

                if (!matrixNumber.isEmpty()) {
                    // Query Firebase to find user with matching matrixNumber
                    reference.orderByChild("matrixNumber").equalTo(matrixNumber)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            String userId = userSnapshot.getKey();
                                            // Update password in Firebase
                                            reference.child(userId).child("password").setValue(encryptPassword(newPassword))
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            // Update password in SharedPreferences
                                                            editor.putString("password", newPassword);
                                                            editor.apply();
                                                            // Show success message
                                                            Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                                                            // Clear EditText fields
                                                            newPasswordEditText.setText("");
                                                            confirmPasswordEditText.setText("");
                                                        } else {
                                                            // Show failure message if update fails
                                                            Toast.makeText(getActivity(), "Failed to update password", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                            return; // Exit loop after updating password
                                        }
                                    } else {
                                        // Show message if user not found
                                        Toast.makeText(getActivity(), "User not found", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle database error
                                    Toast.makeText(getActivity(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(getActivity(), "Matrix number not found in SharedPreferences", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Method to encrypt password using SHA-256 hashing
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
