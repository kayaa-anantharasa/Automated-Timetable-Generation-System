package com.example.automatedtimetablegenerationsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
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
import java.util.Arrays;
import java.util.List;

public class adminTimetableFragment extends Fragment {

    private MultiAutoCompleteTextView multiAutoCompleteTextViewDays,spinnerprerequisite;
    private Spinner spinnerTime;
    private Spinner spinnersemi;
    private Spinner spinnerprogram;
    private Spinner spinnerclass, spinnersubjectCode, spinnersubjectName, spinnerclassroom ;
    private EditText lecturerEditText;
    private Button addTimetableButton;

    private DatabaseReference timetableRef;
    private DatabaseReference classesRef;
    private DatabaseReference subjectRef;
    private DatabaseReference programRef;
    private DatabaseReference classroomRef;
    private DatabaseReference prerequisiteRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_timetable, container, false);

        // Initialize views
        multiAutoCompleteTextViewDays = view.findViewById(R.id.days);
        spinnerTime = view.findViewById(R.id.spinnerTime);
        spinnersemi = view.findViewById(R.id.semi);
        spinnersubjectCode = view.findViewById(R.id.subjectcode);
        spinnersubjectName = view.findViewById(R.id.subjectname);
        lecturerEditText = view.findViewById(R.id.lecturer);
        addTimetableButton = view.findViewById(R.id.addtimetable);
        spinnerprogram = view.findViewById(R.id.programme);
        spinnerclass = view.findViewById(R.id.className);
        spinnerclassroom = view.findViewById(R.id.classroom);
        spinnerprerequisite = view.findViewById(R.id.prerequisite);

        // Set up days MultiAutoCompleteTextView
        List<String> daysList = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, daysList);
        multiAutoCompleteTextViewDays.setAdapter(daysAdapter);
        multiAutoCompleteTextViewDays.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        // Set up time Spinner
        List<String> timeList = Arrays.asList("8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM", "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM", "4:00 PM - 5:00 PM");
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, timeList);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTime.setAdapter(timeAdapter);

        // Set up semester Spinner
        List<String> semiList = Arrays.asList("S1", "S2", "S3", "S4", "S5");
        ArrayAdapter<String> semiAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semiList);
        semiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersemi.setAdapter(semiAdapter);

        // Firebase references initialization
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        classesRef = database.getReference("classes");
        subjectRef = database.getReference("subjects");
        programRef = database.getReference("program");
        classroomRef = database.getReference("classroom");
        prerequisiteRef = database.getReference("prerequisite");
        timetableRef = database.getReference("timetable");

        // Set up Firebase data listeners
        setupFirebaseListeners();

        // Set button click listener
        addTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimetableToFirebase();
            }
        });

        return view;
    }

    private void setupFirebaseListeners() {
        // Setup ValueEventListener for classes
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

        // Setup ValueEventListener for subjects
        subjectRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> subjectList = new ArrayList<>();
                List<String> subjectcodeList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("subjectName").getValue(String.class);
                    subjectList.add(name);
                    String code = snapshot.child("subjectcode").getValue(String.class);
                    subjectcodeList.add(code);
                }
                ArrayAdapter<String> subAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectList);
                subAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                ArrayAdapter<String> codeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, subjectcodeList);
                codeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnersubjectName.setAdapter(subAdapter);
                spinnersubjectCode.setAdapter(codeAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load subjects", Toast.LENGTH_SHORT).show();
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
                spinnerprogram.setAdapter(programAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load programs", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup ValueEventListener for classroom
        classroomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> classroomList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String classroomName = snapshot.child("classroomName").getValue(String.class);
                    classroomList.add(classroomName);
                }
                ArrayAdapter<String> classroomAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classroomList);
                classroomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerclassroom.setAdapter(classroomAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load classrooms", Toast.LENGTH_SHORT).show();
            }
        });

        // Setup ValueEventListener for prerequisite
        prerequisiteRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> prerequisiteList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String prerequisiteName = snapshot.child("name").getValue(String.class);
                    prerequisiteList.add(prerequisiteName);
                }



                ArrayAdapter<String> prerequisiteAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, prerequisiteList);
                prerequisiteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerprerequisite.setAdapter(prerequisiteAdapter);
                spinnerprerequisite.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(requireContext(), "Failed to load prerequisites", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void addTimetableToFirebase() {
        String subjectCode = spinnersubjectCode.getSelectedItem().toString();
        String subjectName = spinnersubjectName.getSelectedItem().toString();
        String lecturer = lecturerEditText.getText().toString().trim();
        String days = multiAutoCompleteTextViewDays.getText().toString().trim();
        String time = spinnerTime.getSelectedItem().toString();
        String semi = spinnersemi.getSelectedItem().toString();
        String program = spinnerprogram.getSelectedItem().toString();
        String classname = spinnerclass.getSelectedItem().toString();
        String prerequisite = spinnerprerequisite.getText().toString().trim();
        String classroom = spinnerclassroom.getSelectedItem().toString();

        // Validate inputs
        if (subjectCode.isEmpty() || subjectName.isEmpty() || lecturer.isEmpty() || days.isEmpty() || time.isEmpty() || semi.isEmpty() || classname.isEmpty() || program.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create timetable object
        Timetable timetable = new Timetable(subjectName, subjectCode, lecturer, days, time, semi, program, classname, classroom, prerequisite);

        // Push timetable object to Firebase
        String key = timetableRef.push().getKey(); // Generate a unique key for the new entry
        if (key != null) {
            timetableRef.child(key).setValue(timetable, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                    if (error != null) {
                        Toast.makeText(requireContext(), "Failed to add timetable: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Clear input fields after adding timetable
                        lecturerEditText.setText("");
                        multiAutoCompleteTextViewDays.setText("");

                        Toast.makeText(requireContext(), "Timetable added successfully", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
