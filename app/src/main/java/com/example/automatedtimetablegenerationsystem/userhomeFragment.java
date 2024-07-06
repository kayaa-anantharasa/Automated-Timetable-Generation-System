package com.example.automatedtimetablegenerationsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class userhomeFragment extends Fragment {
    private Spinner spinnerSemester;
    private Spinner spinnerProgram;
    private String[] semesters = {"semi 01", "semi 02"};
    private String[] programs = {"CST", "IIT", "EAG"};
    private Button showTimetableButton;
    private DatabaseReference timetableRef;
    private List<TimetableEntry> timetableData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userhome, container, false);

        // Initialize Spinners
        spinnerSemester = view.findViewById(R.id.semesters);
        spinnerProgram = view.findViewById(R.id.program);

        // Initialize Spinners with adapters
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        ArrayAdapter<String> programAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, programs);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgram.setAdapter(programAdapter);

        // Initialize Firebase
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        // Initialize button and set click listener
        showTimetableButton = view.findViewById(R.id.showtimetable);
        showTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSemester = spinnerSemester.getSelectedItem().toString();
                String selectedProgram = spinnerProgram.getSelectedItem().toString();
                fetchTimetable(selectedSemester, selectedProgram);
            }
        });

        return view;
    }
    private void fetchTimetable(String semester, String program) {
        timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Clear previous data
                    timetableData.clear();

                    // Iterate through all children
                    for (DataSnapshot timetableSnapshot : snapshot.getChildren()) {
                        // Get the timetable entry data
                        String entrySemester = timetableSnapshot.child("semi").getValue(String.class);
                        String entryProgram = timetableSnapshot.child("program").getValue(String.class);

                        // Check if the semester and program match the selected ones
                        if (entrySemester != null && entryProgram != null &&
                                entrySemester.equals(semester) && entryProgram.equals(program)) {
                            // Convert snapshot to TimetableEntry object
                            TimetableEntry timetableEntry = timetableSnapshot.getValue(TimetableEntry.class);
                            if (timetableEntry != null) {
                                timetableData.add(timetableEntry);
                            }
                        }
                    }

                    // Update UI with timetableData
                    if (!timetableData.isEmpty()) {
                        // Display Toast message
                        Toast.makeText(requireContext(), "Timetable loaded for " + semester + " - " + program, Toast.LENGTH_SHORT).show();


                    } else {
                        // If no timetable data found for the selected semester and program
                        Toast.makeText(requireContext(), "No timetable found for " + semester + " - " + program, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // If no timetable data available at all
                    Toast.makeText(requireContext(), "No timetable data available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load timetable: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
