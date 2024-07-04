package com.example.automatedtimetablegenerationsystem;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class usertimetableFragment extends Fragment {

    private Spinner spinnerSubject, spinnerSemester;
    private Button addButton;
    private LinearLayout containerLayout;

    private DatabaseReference timetableRef;

    private int[] subjects = {1, 2, 3, 4, 5, 6, 7, 8}; // Example subjects as integers
    private String[] semesters = {"Semester 1", "Semester 2", "Semester 3"}; // Example semesters

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usertimetable, container, false);

        spinnerSubject = view.findViewById(R.id.subject);
        spinnerSemester = view.findViewById(R.id.semi);
        addButton = view.findViewById(R.id.addtimetable);
        containerLayout = view.findViewById(R.id.containerLayout);

        // Initialize Firebase Realtime Database reference
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        // Create adapter for subject spinner
        ArrayAdapter<Integer> subjectAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, toIntegerList(subjects));
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubject.setAdapter(subjectAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        // Button click listener
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get selected subject count and semester
                int selectedSubjectCount = (int) spinnerSubject.getSelectedItem();
                String selectedSemester = spinnerSemester.getSelectedItem().toString();

                // Create AlertDialog builder
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                View dialogView = getLayoutInflater().inflate(R.layout.popup_layout, null);
                builder.setView(dialogView);
                builder.setTitle("Add Timetable");

                LinearLayout popupContainerLayout = dialogView.findViewById(R.id.popupContainerLayout);

                // Clear existing views if any
                popupContainerLayout.removeAllViews();

                // Add EditText fields for both subject and class in the same line
                for (int i = 0; i < selectedSubjectCount; i++) {
                    // Inflate EditText fields for subject and class
                    View lineLayout = getLayoutInflater().inflate(R.layout.popup_edittext_fields, null);

                    EditText editTextSubject = lineLayout.findViewById(R.id.editTextSubject);
                    EditText editTextClass = lineLayout.findViewById(R.id.editTextClass);

                    editTextSubject.setHint("Subject name :");
                    editTextClass.setHint("Class :");

                    // Add inflated view to the container layout
                    popupContainerLayout.addView(lineLayout);
                }

                // After the for loop, add one additional EditText for the current program
                View lineLayout = getLayoutInflater().inflate(R.layout.popup_edittext_fields, null);
                EditText editTextSubject = lineLayout.findViewById(R.id.editTextSubject);
                EditText editTextClass = lineLayout.findViewById(R.id.editTextClass);

                editTextSubject.setHint("Current Program");
                editTextClass.setVisibility(View.GONE); // Hide the class EditText

                // Add inflated view to the container layout
                popupContainerLayout.addView(lineLayout);

                // Add positive button (optional)
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Validate and save data
                    if (saveDataToDatabase()) {
                        Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                    }
                });

                // Add negative button (optional)
                builder.setNegativeButton("Cancel", (dialog, which) -> {
                    // Handle cancel button click
                    Toast.makeText(requireContext(), "Cancel clicked", Toast.LENGTH_SHORT).show();
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private List<Integer> toIntegerList(int[] array) {
        List<Integer> list = new ArrayList<>();
        for (int value : array) {
            list.add(value);
        }
        return list;
    }

    private boolean saveDataToDatabase() {
        // Retrieve entered data and perform validation
        String day = "Friday"; // Example day
        String time = "9:00 AM"; // Example time

        LinearLayout popupContainerLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.popup_layout, null)
                .findViewById(R.id.popupContainerLayout);

        List<Timetable> entriesToSave = new ArrayList<>();
        for (int i = 0; i < popupContainerLayout.getChildCount(); i++) {
            View lineLayout = popupContainerLayout.getChildAt(i);
            EditText editTextSubject = lineLayout.findViewById(R.id.editTextSubject);
            EditText editTextClass = lineLayout.findViewById(R.id.editTextClass);

            String subjectName = editTextSubject.getText().toString().trim();
            String className = editTextClass.getText().toString().trim();

            // Perform validation if needed

            // Check for schedule conflict
            if (checkForScheduleConflict(day, time, subjectName)) {
                Toast.makeText(requireContext(), "There's already a class scheduled at this time for " + subjectName, Toast.LENGTH_SHORT).show();
                return false; // Data not saved due to conflict
            }

            // Generate a unique key for the new timetable entry
            String timetableId = timetableRef.push().getKey();

            // Create a TimetableEntry object
            Timetable entry = new Timetable(
                    timetableId,
                    "A1",
                    day,
                    "Ramanan",
                    "CST",
                    "semi 01",
                    subjectName,
                    "CSC001"
            );

            entriesToSave.add(entry);
        }

        // Save data to Firebase Realtime Database
        for (Timetable entry : entriesToSave) {
            timetableRef.child(entry.getSubjectName()).setValue(entry)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Data successfully saved
                            Toast.makeText(requireContext(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to save data
                            Toast.makeText(requireContext(), "Failed to save data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        return true; // Data saved successfully
    }

    private boolean checkForScheduleConflict(String day, String time, String subjectName) {
        // Query Firebase to check if there's already a class scheduled with the same subject, day, and time
        Query query = timetableRef.orderByChild("days").equalTo(day);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timetable entry = snapshot.getValue(Timetable.class);
                    if (entry != null && entry.getSubjectName().equals(subjectName)) {
                        // Check for time conflict (not implemented in this example)
                      //  return false; // Conflict found
                    }
                }
                // No conflict found
                // Proceed with saving data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
            }
        });

        return false; // Assuming no conflict for demonstration
    }
}
