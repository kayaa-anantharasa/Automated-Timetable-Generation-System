package com.example.automatedtimetablegenerationsystem;

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

import java.util.ArrayList;
import java.util.List;

public class usertimetableFragment extends Fragment {

    private Spinner spinnerSubject, spinnerSemester;
    private Button addButton;
    private LinearLayout containerLayout;

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

                // Clear existing views if any
                containerLayout.removeAllViews();

                // Add EditText fields for both subject and class in the same line
                for (int i = 0; i < selectedSubjectCount; i++) {
                    // Create a horizontal LinearLayout to hold subject and class EditText fields
                    LinearLayout lineLayout = new LinearLayout(requireContext());
                    lineLayout.setOrientation(LinearLayout.HORIZONTAL);

                    // Create EditText for subject
                    EditText editTextSubject = new EditText(requireContext());
                    editTextSubject.setHint("Subject " + (i + 1) + " in " + selectedSemester);
                    LinearLayout.LayoutParams subjectParams = new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    editTextSubject.setLayoutParams(subjectParams);

                    // Create EditText for class
                    EditText editTextClass = new EditText(requireContext());
                    editTextClass.setHint("Class for Subject " + (i + 1));
                    LinearLayout.LayoutParams classParams = new LinearLayout.LayoutParams(
                            0,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            1.0f
                    );
                    editTextClass.setLayoutParams(classParams);

                    // Add EditText fields to the horizontal LinearLayout
                    lineLayout.addView(editTextSubject);
                    lineLayout.addView(editTextClass);

                    // Add the horizontal LinearLayout to the main container layout
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 20, 0, 0); // Adjust margins as needed
                    lineLayout.setLayoutParams(params);
                    containerLayout.addView(lineLayout);
                }

                // Show a toast message with the selected options
                String message = "Added " + selectedSubjectCount + " pairs of EditText fields for " + selectedSemester;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
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

}
