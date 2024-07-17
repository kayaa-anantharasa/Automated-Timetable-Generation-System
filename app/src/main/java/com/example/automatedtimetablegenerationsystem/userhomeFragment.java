package com.example.automatedtimetablegenerationsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    private TextView fname,fletter,ftime; // TextView to display username

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userhome, container, false);

        // Initialize Views
        fname = view.findViewById(R.id.name);
        fletter= view.findViewById(R.id.firstletter);
        ftime= view.findViewById(R.id.time);
        spinnerSemester = view.findViewById(R.id.semesters);
        spinnerProgram = view.findViewById(R.id.program);
        spinnerclass = view.findViewById(R.id.classname);
        progressBar = view.findViewById(R.id.progressBar);
        showTimetableButton = view.findViewById(R.id.showtimetable);

        // Load username from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", requireContext().MODE_PRIVATE);
        String username = preferences.getString("username", "Default Name");
        // Get reference to "logout_times" table in Firebase
        DatabaseReference logoutTimesRef = FirebaseDatabase.getInstance().getReference().child("logout_times").child(username);

        logoutTimesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if dataSnapshot exists and has a value
                if (dataSnapshot.exists()) {
                    String logoutTime = dataSnapshot.getValue(String.class);
                    ftime.setText(logoutTime);

                } else {
                    // Handle case where no data exists for the username (should not happen if user has logged out properly)
                  //  Log.d("LogoutTime", "No logout time found for " + username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle potential errors
               // Log.e("Firebase", "Error fetching logout time for " + username + ": " + databaseError.getMessage());
            }
        });


        fname.setText(username); // Set username to fname TextView
        String firstLetter = username.substring(0, 1);
        fletter.setText(firstLetter);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDateTime = sdf.format(calendar.getTime());

        // Initialize Spinners with adapters
        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(semesterAdapter);

        // Firebase Database References
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classesRef = database.getReference("classes");
        DatabaseReference programRef = database.getReference("program");

        // Load classes from Firebase
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

        // Set click listener for showTimetableButton
        showTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSemester = spinnerSemester.getSelectedItem().toString();
                String selectedProgram = spinnerProgram.getSelectedItem().toString();
                String selectedClass = spinnerclass.getSelectedItem().toString();
                fetchTimetable(selectedSemester, selectedProgram, selectedClass);
            }
        });

        return view;
    }

    private void fetchTimetable(String semester, String program, String classname) {
        progressBar.setVisibility(View.VISIBLE);
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");
        timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    timetableData.clear(); // Clear previous data
                    for (DataSnapshot timetableSnapshot : snapshot.getChildren()) {
                        String entrySemester = timetableSnapshot.child("semi").getValue(String.class);
                        String entryProgram = timetableSnapshot.child("program").getValue(String.class);
                        String entryclass = timetableSnapshot.child("classname").getValue(String.class);
                        if (entrySemester != null && entryProgram != null && entryclass != null &&
                                entrySemester.equals(semester) && entryProgram.equals(program) && entryclass.equals(classname)) {
                            TimetableEntry timetableEntry = timetableSnapshot.getValue(TimetableEntry.class);
                            if (timetableEntry != null) {
                                timetableData.add(timetableEntry);
                            }
                        }
                    }
                    if (!timetableData.isEmpty()) {
                        Toast.makeText(requireContext(), "Timetable loaded for " + semester + " - " + program, Toast.LENGTH_SHORT).show();
                        showTimetableDialog(timetableData);
                    } else {
                        Toast.makeText(requireContext(), "No timetable found for " + semester + " - " + program, Toast.LENGTH_SHORT).show();
                    }
                } else {
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
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_timetable, null);
        builder.setView(dialogView);

        TableLayout tableLayout = dialogView.findViewById(R.id.timetableTable);
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

        String[] timeSlots = {"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM",
                "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM",
                "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM"};

        Map<String, Map<String, TimetableEntry>> timetableMap = new HashMap<>();
        for (String day : headers) {
            if (!day.equals("Time")) {
                timetableMap.put(day, new HashMap<>());
            }
        }
        for (TimetableEntry entry : timetableEntries) {
            String[] days = entry.getDays().split(",\\s*");
            for (String day : days) {
                if (timetableMap.containsKey(day)) {
                    timetableMap.get(day).put(entry.getTime(), entry);
                }
            }
        }

        for (String timeSlot : timeSlots) {
            TableRow row = new TableRow(requireContext());
            TextView timeTextView = new TextView(requireContext());
            timeTextView.setText(timeSlot);
            timeTextView.setPadding(16, 16, 16, 16);
            timeTextView.setTextSize(6);
            timeTextView.setTypeface(null, Typeface.BOLD);
            timeTextView.setBackgroundColor(getResources().getColor(R.color.blue));
            timeTextView.setTextColor(getResources().getColor(R.color.white));
            row.addView(timeTextView);

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
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
