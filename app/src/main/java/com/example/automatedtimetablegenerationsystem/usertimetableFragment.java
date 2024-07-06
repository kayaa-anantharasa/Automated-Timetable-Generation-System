package com.example.automatedtimetablegenerationsystem;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class usertimetableFragment extends Fragment {

    private Spinner spinnerCount;
    private Spinner spinnerSemester;
    private Spinner spinnerProgram;
    private Spinner spinnerFailedSemester;
    private Button addButton;
    private LinearLayout containerLayout;

    private DatabaseReference timetableRef;

    private String[] subjects = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private String[] semesters = {"semi 01", "semi 02"};
    private String[] programs = {"CST", "IIT", "EAG"};
    private String[] subjectNames = {"AI", "Data Science", "EAG"};
    private String[] classNames = {"C1", "A1", "B3"};

    // Variables to store selected subject, class, program, and time
    private String selectedProgram;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usertimetable, container, false);

        spinnerCount = view.findViewById(R.id.subject);
        spinnerSemester = view.findViewById(R.id.semi);
        spinnerProgram = view.findViewById(R.id.currentprogram);
        spinnerFailedSemester = view.findViewById(R.id.failedsemester);
        addButton = view.findViewById(R.id.addtimetable);
        containerLayout = view.findViewById(R.id.containerLayout);

        // Initialize Firebase Realtime Database reference
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        // Create adapters for spinners
        ArrayAdapter<String> countAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjects);
        countAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCount.setAdapter(countAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        ArrayAdapter<String> failedSemesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        failedSemesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFailedSemester.setAdapter(failedSemesterAdapter);

        ArrayAdapter<String> programAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, programs);
        programAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProgram.setAdapter(programAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected count, semester, program, and failed semester
                int selectedCount = Integer.parseInt(spinnerCount.getSelectedItem().toString());
                String selectedCurrentSemester = spinnerSemester.getSelectedItem().toString();
                selectedProgram = spinnerProgram.getSelectedItem().toString();
                String selectedFailedSemester = spinnerFailedSemester.getSelectedItem().toString();

                // Create AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = getLayoutInflater().inflate(R.layout.popup_layout, null);
                builder.setView(dialogView);
                builder.setTitle("Check Timetable");

                LinearLayout popupContainerLayout = dialogView.findViewById(R.id.popupContainerLayout);

                // Clear existing views if any
                popupContainerLayout.removeAllViews();

                // Add spinners for subject and class in the same line
                List<Spinner> subjectSpinners = new ArrayList<>();
                List<Spinner> classSpinners = new ArrayList<>();

                for (int i = 0; i < selectedCount; i++) {
                    View lineLayout = getLayoutInflater().inflate(R.layout.popup_edittext_fields, null);
                    Spinner spinnerSubject = lineLayout.findViewById(R.id.spinnerSubject);
                    Spinner spinnerClass = lineLayout.findViewById(R.id.spinnerClass);

                    ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectNames);
                    subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSubject.setAdapter(subjectAdapter);

                    ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classNames);
                    classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerClass.setAdapter(classAdapter);

                    popupContainerLayout.addView(lineLayout);

                    // Add spinners to lists
                    subjectSpinners.add(spinnerSubject);
                    classSpinners.add(spinnerClass);
                }

                // Add negative button (cancel)
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle cancel button click
                    Toast.makeText(requireContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Dismiss the dialog when cancel is clicked
                });

                // Add positive button (Check)
                builder.setPositiveButton("Check", (dialog, which) -> {
                    // Construct timetable entry key based on selected values


                    dialog.dismiss(); // Dismiss the dialog when check is clicked
                });

                // Show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }
}
