package com.example.automatedtimetablegenerationsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class userhomeFragment extends Fragment {
    private Spinner spinnerSemester;
    private Spinner spinnerProgram;
    private Spinner spinnerclass;
    private String[] semesters = {"S1", "S2", "S3", "S4", "S5"};

    private Button showTimetableButton;
    private ProgressBar progressBar;
    private DatabaseReference timetableRef;
    private List<TimetableEntry> timetableData = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userhome, container, false);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
         DatabaseReference  classesRef = database.getReference("classes");
         DatabaseReference programRef = database.getReference("program");

        // Initialize Spinners
        spinnerSemester = view.findViewById(R.id.semesters);
        spinnerProgram = view.findViewById(R.id.program);
        spinnerclass = view.findViewById(R.id.classname);
        progressBar = view.findViewById(R.id.progressBar);

        // Initialize Spinners with adapters
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        classesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> classList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String className = snapshot.child("className").getValue(String.class);
                    classList.add(className);
                }
                ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classList);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerclass.setAdapter(classAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load classes", Toast.LENGTH_SHORT).show();
            }
        });
        // Setup ValueEventListener for program
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

        // Initialize Firebase
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        // Initialize button and set click listener
        showTimetableButton = view.findViewById(R.id.showtimetable);
        showTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSemester = spinnerSemester.getSelectedItem().toString();
                String selectedProgram = spinnerProgram.getSelectedItem().toString();
                String selectedClass= spinnerclass.getSelectedItem().toString();
                fetchTimetable(selectedSemester, selectedProgram,selectedClass);
            }
        });

        return view;
    }

    private void fetchTimetable(String semester, String program,String classname) {
        progressBar.setVisibility(View.VISIBLE);
        timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    // Clear previous data
                    timetableData.clear();

                    // Iterate through all children
                    for (DataSnapshot timetableSnapshot : snapshot.getChildren()) {
                        // Get the timetable entry data
                        String entrySemester = timetableSnapshot.child("semi").getValue(String.class);
                        String entryProgram = timetableSnapshot.child("program").getValue(String.class);
                        String entryclass = timetableSnapshot.child("classname").getValue(String.class);
                        // Check if the semester and program match the selected ones
                        if (entrySemester != null && entryProgram != null && entryclass != null &&
                                entrySemester.equals(semester) && entryProgram.equals(program) && entryclass.equals(classname)) {
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
                        showTimetableDialog(timetableData);
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
                progressBar.setVisibility(View.GONE);
                Toast.makeText(requireContext(), "Failed to load timetable: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showTimetableDialog(List<TimetableEntry> timetableEntries) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        //builder.setTitle("Timetable Details");

        // Inflate the custom layout for the dialog
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_timetable, null);
        builder.setView(dialogView);

        // Initialize the TableLayout in the dialog
        TableLayout tableLayout = dialogView.findViewById(R.id.timetableTable);

        // Add table headers
        TableRow headerRow = new TableRow(requireContext());
        String[] headers = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String header : headers) {
            TextView headerTextView = new TextView(requireContext());
            headerTextView.setText(header);
            headerTextView.setPadding(16, 16, 16, 16);
            headerTextView.setTextSize(10);
            headerTextView.setTypeface(null, Typeface.BOLD);
            headerTextView.setBackgroundColor(getResources().getColor(R.color.white));
            headerTextView.setTextColor(getResources().getColor(R.color.black));
            headerRow.addView(headerTextView);
        }
        tableLayout.addView(headerRow);

        // Define time slots
        String[] timeSlots = {"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM",
                "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM",
                "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM"};

        // Create a map to hold timetable entries by day and time slot
        Map<String, Map<String, TimetableEntry>> timetableMap = new HashMap<>();
        for (String day : headers) {
            if (!day.equals("Time")) {
                timetableMap.put(day, new HashMap<>());
            }
        }
        for (TimetableEntry entry : timetableEntries) {
            String[] days = entry.getDays().split(",\\s*"); // Split days string into individual days
            for (String day : days) {
                if (timetableMap.containsKey(day)) {
                    timetableMap.get(day).put(entry.getTime(), entry);
                }
            }
        }

        // Populate the table rows
        for (String timeSlot : timeSlots) {
            TableRow row = new TableRow(requireContext());

            // Time column
            TextView timeTextView = new TextView(requireContext());
            timeTextView.setText(timeSlot);
            timeTextView.setPadding(16, 16, 16, 16);
            timeTextView.setTextSize(6);
            timeTextView.setTypeface(null, Typeface.BOLD);
            timeTextView.setBackgroundColor(getResources().getColor(R.color.blue));
            timeTextView.setTextColor(getResources().getColor(R.color.white));
            row.addView(timeTextView);

            // Day columns
            for (String day : headers) {
                if (!day.equals("Time")) {
                    TextView cellTextView = new TextView(requireContext());
                    cellTextView.setPadding(16, 16, 16, 16);
                    cellTextView.setTextSize(10);
                    cellTextView.setBackgroundColor(getResources().getColor(R.color.grey));
                    cellTextView.setTextColor(getResources().getColor(R.color.black));
                    TimetableEntry entry = timetableMap.get(day).get(timeSlot);
                    if (entry != null) {
                        String cellText = entry.getSubjectName() + "\n" + entry.getLecturer() + "\n" + entry.getClassname();
                        cellTextView.setText(cellText);
                    }
                    row.addView(cellTextView);
                }
            }
            tableLayout.addView(row);
        }

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss(); // Dismiss dialog on OK button click
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
