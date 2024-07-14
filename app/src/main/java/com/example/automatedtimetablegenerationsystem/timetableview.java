package com.example.automatedtimetablegenerationsystem;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class timetableview extends AppCompatActivity {

    private LinearLayout repeatSubjectsContainer;
    private TableLayout timetableTable;
    private DatabaseReference databaseReference;
    private int selectedCount;
    private String selectedCurrentSemester;
    private String selectedProgram;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetableview);

        repeatSubjectsContainer = findViewById(R.id.repeatSubjectsContainer);
        timetableTable = findViewById(R.id.timetableTable);
        Button addSubjectsButton = findViewById(R.id.addSubjectsButton);

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
    }

    private void addSubjectClassSpinners() {
        databaseReference.child("subjects").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjectList = new ArrayList<>();
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

        Spinner subjectSpinner = new Spinner(this);
        subjectSpinner.setAdapter(subjectAdapter);

        Spinner classSpinner = new Spinner(this);
        databaseReference.child("classes").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> classList = new ArrayList<>();
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

    private void generateTimetable() {
        final Map<String, String> selectedSubjects = new HashMap<>();
        final Map<String, String> selectedClasses = new HashMap<>();

        // Collect selected subjects and classes
        for (int i = 0; i < repeatSubjectsContainer.getChildCount(); i++) {
            LinearLayout rowLayout = (LinearLayout) repeatSubjectsContainer.getChildAt(i);
            Spinner subjectSpinner = (Spinner) rowLayout.getChildAt(0);
            Spinner classSpinner = (Spinner) rowLayout.getChildAt(1);

            String subject = subjectSpinner.getSelectedItem().toString();
            String className = classSpinner.getSelectedItem().toString();

            selectedSubjects.put(subject, className);
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
            headerRow.addView(headerTextView);
        }
        timetableTable.addView(headerRow);

        // Define timetable timeslots
        String[] timeslots = {"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM"};

        // Load timetable from Firebase and populate the table
        databaseReference.child("timetable").child(selectedProgram).child(selectedCurrentSemester).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (String timeslot : timeslots) {
                    TableRow row = new TableRow(timetableview.this);
                    TextView timeTextView = new TextView(timetableview.this);
                    timeTextView.setText(timeslot);
                    timeTextView.setPadding(8, 8, 8, 8);
                    row.addView(timeTextView);

                    for (String day : headers) {
                        if (!day.equals("Time")) {
                            TextView dayTextView = new TextView(timetableview.this);
                            dayTextView.setPadding(8, 8, 8, 8);

                            if (dataSnapshot.hasChild(day) && dataSnapshot.child(day).hasChild(timeslot)) {
                                String subject = dataSnapshot.child(day).child(timeslot).child("subject").getValue(String.class);
                                String className = dataSnapshot.child(day).child(timeslot).child("class").getValue(String.class);

                                dayTextView.setText(subject + "\n" + className);

                                if (selectedSubjects.containsKey(subject) && selectedSubjects.get(subject).equals(className)) {
                                    dayTextView.setBackgroundColor(Color.GREEN);
                                }
                            }

                            row.addView(dayTextView);
                        }
                    }

                    timetableTable.addView(row);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
