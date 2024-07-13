package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.automatedtimetablegenerationsystem.databinding.ActivityAdminHomeBinding;
import com.example.automatedtimetablegenerationsystem.databinding.ActivityUserMainBinding;

public class userMain extends AppCompatActivity {

    ActivityUserMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUserMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new userhomeFragment());
        binding.userbottom.setBackground(null);

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
        Intent intent = new Intent(this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
        startActivity(intent);
        finish();
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.userframelayoutid,fragment);
        fragmentTransaction.commit();
    }
}