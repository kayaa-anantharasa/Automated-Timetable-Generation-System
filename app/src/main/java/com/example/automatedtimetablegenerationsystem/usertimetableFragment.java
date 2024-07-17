package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class usertimetableFragment extends Fragment {

    private Spinner spinnerCount;
    private Spinner spinnerSemester;
    private Spinner spinnerProgram;
    private Button addButton;
    private DatabaseReference timetableRef;
    private String selectedProgram;
    private static final int PERMISSION_REQUEST_CODE = 100;

    private String[] subjects = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private String[] semesters = {"S1", "S2", "S3", "S4", "S5"};
    private String[] programs = {"CST", "IIT", "EAG"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usertimetable, container, false);

        spinnerCount = view.findViewById(R.id.subject);
        spinnerSemester = view.findViewById(R.id.semi);
        spinnerProgram = view.findViewById(R.id.currentprogram);
        addButton = view.findViewById(R.id.addtimetable);

        // Initialize Firebase Realtime Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference programRef = database.getReference("program");
        timetableRef = database.getReference().child("timetable");

        // Create adapters for spinners
        ArrayAdapter<String> countAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjects);
        countAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCount.setAdapter(countAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        // Load programs from Firebase
        programRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> programList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String programName = snapshot.child("name").getValue(String.class);
                    if (programName != null) {
                        programList.add(programName);
                    }
                }
                ArrayAdapter<String> programAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, programList);
                programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerProgram.setAdapter(programAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load programs", Toast.LENGTH_SHORT).show();
            }
        });


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedCount = Integer.parseInt(spinnerCount.getSelectedItem().toString());
                String selectedCurrentSemester = spinnerSemester.getSelectedItem().toString();
                selectedProgram = spinnerProgram.getSelectedItem().toString();
                SharedPreferences preferences = requireContext().getSharedPreferences("user_data", requireContext().MODE_PRIVATE);
                String username = preferences.getString("username", "Default Name");
                Intent intent = new Intent(requireContext(), timetableview.class);
                intent.putExtra("selectedCount", selectedCount);
                intent.putExtra("selectedCurrentSemester", selectedCurrentSemester);
                intent.putExtra("selectedProgram", selectedProgram);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });

        return view;
    }
}
