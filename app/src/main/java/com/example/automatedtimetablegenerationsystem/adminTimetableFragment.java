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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

public class adminTimetableFragment extends Fragment {

    private MultiAutoCompleteTextView multiAutoCompleteTextViewDays;
    private Spinner spinnerTime;
    private Spinner spinnersemi;
    private Spinner spinnerprogram;
    private Spinner spinnerclass;
    private EditText subjectCodeEditText, subjectNameEditText, lecturerEditText;
    private Button addTimetableButton;

    private DatabaseReference timetableRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_timetable, container, false);

        // Initialize views
        multiAutoCompleteTextViewDays = view.findViewById(R.id.days);
        spinnerTime = view.findViewById(R.id.spinnerTime);
        spinnersemi = view.findViewById(R.id.semi);
        subjectCodeEditText = view.findViewById(R.id.subjectcode);
        subjectNameEditText = view.findViewById(R.id.subjectname);
        lecturerEditText = view.findViewById(R.id.lecturer);
        addTimetableButton = view.findViewById(R.id.addtimetable);
        spinnerprogram = view.findViewById(R.id.programme);
        spinnerclass = view.findViewById(R.id.className);

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
        List<String> semiList = Arrays.asList("semi 01", "semi 02");
        ArrayAdapter<String> semiAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, semiList);
        semiAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersemi.setAdapter(semiAdapter);

        //programe
        List<String> programList = Arrays.asList("CST", "SCT","MRT","IIT");
        ArrayAdapter<String> programmeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, programList);
        programmeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerprogram.setAdapter(programmeAdapter);


        //class
        List<String> classList = Arrays.asList("A1", "C3","D4","A5");
        ArrayAdapter<String> classAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, classList);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerclass.setAdapter(classAdapter);
        // Firebase reference
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        // Set button click listener
        addTimetableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimetableToFirebase();
            }
        });

        return view;
    }

    private void addTimetableToFirebase() {
        String subjectCode = subjectCodeEditText.getText().toString().trim();
        String subjectName = subjectNameEditText.getText().toString().trim();
        String lecturer = lecturerEditText.getText().toString().trim();
        String days = multiAutoCompleteTextViewDays.getText().toString().trim();
        String time = spinnerTime.getSelectedItem().toString();
        String semi = spinnersemi.getSelectedItem().toString();
        String program = spinnerprogram.getSelectedItem().toString();
        String classname = spinnerclass.getSelectedItem().toString();
        // Validate inputs
        if (subjectCode.isEmpty() || subjectName.isEmpty() || lecturer.isEmpty() || days.isEmpty() || time.isEmpty() || semi.isEmpty()  || classname.isEmpty()  || program.isEmpty() ) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create timetable object
        Timetable timetable = new Timetable(subjectCode, subjectName, lecturer, days, time, semi,program,classname);

        // Push timetable object to Firebase
        timetableRef.push().setValue(timetable);

        // Clear input fields after adding timetable
        subjectCodeEditText.setText("");
        subjectNameEditText.setText("");
        lecturerEditText.setText("");
        multiAutoCompleteTextViewDays.setText("");

        Toast.makeText(requireContext(), "Timetable added successfully", Toast.LENGTH_SHORT).show();
    }
}
