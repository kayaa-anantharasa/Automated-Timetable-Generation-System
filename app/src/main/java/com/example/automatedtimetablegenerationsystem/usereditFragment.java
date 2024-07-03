package com.example.automatedtimetablegenerationsystem;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class usereditFragment extends Fragment {

    EditText newPasswordEditText, confirmPasswordEditText;
    Button changePasswordButton;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_useredit, container, false);

        newPasswordEditText = view.findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = view.findViewById(R.id.confirmPasswordEditText);
        changePasswordButton = view.findViewById(R.id.changePasswordButton);

        preferences = requireActivity().getSharedPreferences("user_data", requireActivity().MODE_PRIVATE);
        editor = preferences.edit();

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

                // Update password in SharedPreferences
                editor.putString("password", newPassword);
                editor.apply();

                Toast.makeText(getActivity(), "Password updated successfully", Toast.LENGTH_SHORT).show();
                newPasswordEditText.setText("");
                confirmPasswordEditText.setText("");
            }
        });

        return view;
    }
}
