package com.example.automatedtimetablegenerationsystem;

import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class timetableview extends AppCompatActivity {

    private LinearLayout repeatSubjectsContainer;
    private LinearLayout currentSubjectsContainer;
    private TableLayout timetableTable;
    private DatabaseReference databaseReference;
    private int selectedCount;
    private String selectedCurrentSemester;
    private String selectedProgram;
    private Button addSubjectsButton;
    private ImageView addCurrentSubjectsButton;
    private TextView totalHoursTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetableview);

        repeatSubjectsContainer = findViewById(R.id.repeatSubjectsContainer);
        currentSubjectsContainer = findViewById(R.id.currentSubjectsContainer);
        timetableTable = findViewById(R.id.timetableTable);
        totalHoursTextView = findViewById(R.id.totalHoursTextView);
        addSubjectsButton = findViewById(R.id.addSubjectsButton);
        addCurrentSubjectsButton = findViewById(R.id.addCurrentSubjectsButton);

        selectedCount = getIntent().getIntExtra("selectedCount", 0);
        selectedCurrentSemester = getIntent().getStringExtra("selectedCurrentSemester");
        selectedProgram = getIntent().getStringExtra("selectedProgram");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Add spinners for selecting subjects and classes based on the selected count
        addSubjectClassSpinners();

        // Handle add subjects button click
        addSubjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateTimetable();
            }
        });

        // Handle add current subjects button click
        addCurrentSubjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCurrentSubjectClassRow(selectedCurrentSemester);
            }
        });
    }

    private void addSubjectClassSpinners() {
        databaseReference.child("subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjectList = new ArrayList<>();
                subjectList.add("Please Select");// Add default item
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String subjectName = snapshot.child("subjectName").getValue(String.class);

                    subjectList.add(subjectName);
                }

                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(timetableview.this, android.R.layout.simple_spinner_item, subjectList);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                for (int i = 0; i < selectedCount; i++) {
                    addSubjectClassRow(subjectAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void addSubjectClassRow(ArrayAdapter<String> subjectAdapter) {
        LinearLayout rowLayout = new LinearLayout(this);
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);
        rowLayout.setPadding(0, 8, 0, 8);

        Spinner subjectSpinner = new Spinner(this);
        // Set initial selection to the first item ("Please select")
        subjectSpinner.setSelection(0);
        subjectSpinner.setAdapter(subjectAdapter);

        Spinner classSpinner = new Spinner(this);
        databaseReference.child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> classList = new ArrayList<>();
                classList.add("Please Select class ");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String className = snapshot.child("className").getValue(String.class);

                    classList.add(className);
                }

                ArrayAdapter<String> classAdapter = new ArrayAdapter<>(timetableview.this, android.R.layout.simple_spinner_item, classList);
                classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classSpinner.setAdapter(classAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });

        rowLayout.addView(subjectSpinner);
        rowLayout.addView(classSpinner);

        repeatSubjectsContainer.addView(rowLayout);
    }

    private void addCurrentSubjectClassRow(String currentSemester) {
        databaseReference.child("subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjectList = new ArrayList<>();
                subjectList.add("Please Select");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String subjectName = snapshot.child("subjectName").getValue(String.class);
                    String subjectSemester = snapshot.child("semester").getValue(String.class); // Assuming 'semester' is stored in your database
                    // Filter subjects by current semester
                    if (subjectSemester != null && subjectSemester.equals(currentSemester)) {
                        subjectList.add(subjectName);
                    }
                }

                ArrayAdapter<String> subjectAdapter = new ArrayAdapter<>(timetableview.this, android.R.layout.simple_spinner_item, subjectList);
                subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                LinearLayout rowLayout = new LinearLayout(timetableview.this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setPadding(0, 8, 0, 8);

                Spinner subjectSpinner = new Spinner(timetableview.this);
                subjectSpinner.setAdapter(subjectAdapter);

                Spinner classSpinner = new Spinner(timetableview.this);
                databaseReference.child("classes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> classList = new ArrayList<>();
                        classList.add("Please Select class ");
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String className = snapshot.child("className").getValue(String.class);
                            classList.add(className);
                        }

                        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(timetableview.this, android.R.layout.simple_spinner_item, classList);
                        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        classSpinner.setAdapter(classAdapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error
                    }
                });

                rowLayout.addView(subjectSpinner);
                rowLayout.addView(classSpinner);

                currentSubjectsContainer.addView(rowLayout);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void generateTimetable() {
        final Map<String, String> selectedSubjects = new HashMap<>();
        final List<String> conflicts = new ArrayList<>();
        final Set<String> conflictSet = new HashSet<>(); // Set to track unique conflicts

        // Collect selected subjects and classes
        for (int i = 0; i < repeatSubjectsContainer.getChildCount(); i++) {
            LinearLayout rowLayout = (LinearLayout) repeatSubjectsContainer.getChildAt(i);
            Spinner subjectSpinner = (Spinner) rowLayout.getChildAt(0);
            Spinner classSpinner = (Spinner) rowLayout.getChildAt(1);

            String subject = subjectSpinner.getSelectedItem().toString();
            String className = classSpinner.getSelectedItem().toString();

            if (!subject.equals("Please Select") && !className.equals("Please Select class ")) {
                String key = subject + "_" + className;
                if (selectedSubjects.containsKey(key)) {
                    conflicts.add("Conflict detected: " + subject + " in " + className);
                } else {
                    selectedSubjects.put(key, subject);
                }
            }
        }

        if (!conflicts.isEmpty()) {
            showAlert(String.join("\n", conflicts));
            return;
        }

        // Clear existing timetable
        timetableTable.removeAllViews();

        // Create table headers
        TableRow headerRow = new TableRow(this);
        String[] headers = {"Time", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        for (String header : headers) {
            TextView headerTextView = new TextView(this);
            headerTextView.setText(header);
            headerTextView.setPadding(8, 8, 8, 8);
            headerTextView.setGravity(Gravity.CENTER);
            headerTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            headerTextView.setBackgroundColor(Color.LTGRAY);
            headerRow.addView(headerTextView);
        }
        timetableTable.addView(headerRow);

        // Define timetable timeslots
        String[] timeslots = {"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM"};

        // Load timetable from Firebase and populate the table
        databaseReference.child("timetable").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String timeslot : timeslots) {
                    TableRow row = new TableRow(timetableview.this);
                    TextView timeTextView = new TextView(timetableview.this);
                    timeTextView.setText(timeslot);
                    timeTextView.setPadding(8, 8, 8, 8);
                    timeTextView.setGravity(Gravity.CENTER);
                    timeTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                    row.addView(timeTextView);

                    for (String day : headers) {
                        if (!day.equals("Time")) {
                            TextView dayTextView = new TextView(timetableview.this);
                            dayTextView.setPadding(8, 8, 8, 8);
                            dayTextView.setGravity(Gravity.CENTER);
                            dayTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                            dayTextView.setBackgroundColor(Color.WHITE);
                            dayTextView.setText(""); // Empty cell, can be updated later

                            boolean matchFound = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String dbClassName = snapshot.child("classname").getValue(String.class);
                                String dbSubjectName = snapshot.child("subjectName").getValue(String.class);
                                String dbTime = snapshot.child("time").getValue(String.class);
                                String dbDays = snapshot.child("days").getValue(String.class);

                                if (selectedSubjects.containsKey(dbSubjectName + "_" + dbClassName)) {
                                    if (dbDays != null && dbDays.contains(day) && dbTime != null && dbTime.equals(timeslot)) {
                                        matchFound = true;
                                        dayTextView.setText(dbSubjectName + "\n" + dbClassName);
                                        break;
                                    }
                                }
                            }

                            if (matchFound) {
                                dayTextView.setBackgroundColor(Color.RED);
                            }

                            row.addView(dayTextView);
                        }
                    }

                    timetableTable.addView(row);
                }

                // Check for conflicts based on time and days for selected subjects
                for (int i = 0; i < repeatSubjectsContainer.getChildCount(); i++) {
                    LinearLayout rowLayout = (LinearLayout) repeatSubjectsContainer.getChildAt(i);
                    Spinner subjectSpinner = (Spinner) rowLayout.getChildAt(0);
                    Spinner classSpinner = (Spinner) rowLayout.getChildAt(1);

                    String subject = subjectSpinner.getSelectedItem().toString();
                    String className = classSpinner.getSelectedItem().toString();

                    if (!subject.equals("Please Select") && !className.equals("Please Select class ")) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String dbSubjectName = snapshot.child("subjectName").getValue(String.class);
                            String dbClassName = snapshot.child("classname").getValue(String.class);
                            String dbTime = snapshot.child("time").getValue(String.class);
                            String dbDays = snapshot.child("days").getValue(String.class);

                            if (subject.equals(dbSubjectName) && className.equals(dbClassName)) {
                                String conflictMessage = "Conflict detected: " + dbTime + " on " + dbDays;
                                if (conflictSet.contains(conflictMessage)) {
                                    conflicts.add("Duplicate conflict: " + conflictMessage);
                                } else {
                                    conflictSet.add(conflictMessage);
                                }
                            }
                        }
                    }
                }

                if (!conflicts.isEmpty()) {
                    showAlert(String.join("\n", conflicts));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Conflict Detected");
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
