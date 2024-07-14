package com.example.automatedtimetablegenerationsystem;

import android.content.Intent;
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
    private LinearLayout containerLayout;
    private DatabaseReference timetableRef;

    private String[] subjects = {"1", "2", "3", "4", "5", "6", "7", "8"};
    private String[] semesters = {"S1", "S2", "S3", "S4", "S5"};
    private String[] programs = {"CST", "IIT", "EAG"};
    private String[] subjectNames = {"AI", "Data Science", "EAG"};
    private String[] classNames = {"C1", "A1", "B3"};

    // Variable to store selected program
    private String selectedProgram;

    private static final int PERMISSION_REQUEST_CODE = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usertimetable, container, false);

        spinnerCount = view.findViewById(R.id.subject);
        spinnerSemester = view.findViewById(R.id.semi);
        spinnerProgram = view.findViewById(R.id.currentprogram);

        addButton = view.findViewById(R.id.addtimetable);
        containerLayout = view.findViewById(R.id.containerLayout);

        // Initialize Firebase Realtime Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classesRef = database.getReference("classes");
        DatabaseReference subjectRef = database.getReference("subjects");
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
                    programList.add(programName);
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
                // Get selected count, semester, program, and failed semester
                int selectedCount = Integer.parseInt(spinnerCount.getSelectedItem().toString());
                String selectedCurrentSemester = spinnerSemester.getSelectedItem().toString();
                selectedProgram = spinnerProgram.getSelectedItem().toString();

                // Navigate to timetableview activity
                Intent intent = new Intent(requireContext(), timetableview.class);
                startActivity(intent);
            }
        });

        return view;
    }
}
