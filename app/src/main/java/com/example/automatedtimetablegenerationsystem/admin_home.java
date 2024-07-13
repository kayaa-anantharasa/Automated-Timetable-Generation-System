package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.automatedtimetablegenerationsystem.databinding.ActivityAdminHomeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class admin_home extends AppCompatActivity {

    ActivityAdminHomeBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.activity_admin_home);

        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new adminHomeFragment());
        binding.bottom.setBackground(null);
       // BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        binding.bottom.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new adminHomeFragment());
            } else if (item.getItemId() == R.id.timetable) {
                replaceFragment(new adminTimetableFragment());
            } else if (item.getItemId() == R.id.user) {
                replaceFragment(new adminUserFragment());
            }else if (item.getItemId() == R.id.add) {
                replaceFragment(new adminAddFragment());
            }
            else if (item.getItemId() == R.id.logout) {
                logoutUser();
            }
            // Uncomment the following if statement if needed
            // else if (item.getItemId() == R.id.add) {
            //    replaceFragment(new adminHomeFragment());
            // }

            return true;
        });



//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
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
        fragmentTransaction.replace(R.id.framelayoutid,fragment);
        fragmentTransaction.commit();
    }
}