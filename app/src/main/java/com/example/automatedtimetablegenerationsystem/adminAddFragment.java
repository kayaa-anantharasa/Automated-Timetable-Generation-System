package com.example.automatedtimetablegenerationsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class adminAddFragment extends Fragment {

    private DatabaseReference databaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admin_add, container, false);


        databaseReference = FirebaseDatabase.getInstance().getReference();

        //class
        View addButton = rootView.findViewById(R.id.classButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddClassDialog();
            }
        });


        //suject
        View subButton = rootView.findViewById(R.id.subjectButton);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddSubjectDialog();
            }
        });

        //classroom
        View classroomButton = rootView.findViewById(R.id.classroomButton);
        classroomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddclassroomDialog();
            }
        });
        //pre
        View prerequisiteButton = rootView.findViewById(R.id.addPrerequisite);
        prerequisiteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddpreDialog();
            }
        });
        //program
        View programButton = rootView.findViewById(R.id.addProgram);
        programButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddprogramDialog();
            }
        });
        return rootView;
    }

    private void showAddClassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Class");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classesRef = database.getReference("classes");

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String className = input.getText().toString().trim();
                if (!className.isEmpty()) {
                    // Save className to Firebase Database
                    String classId = classesRef.push().getKey(); // Generate a unique key for the new class
                    classModel newClass = new classModel(classId, className);
                    classesRef.child(classId).setValue(newClass);

                    Toast.makeText(requireContext(), "Class added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Please enter a class name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void showAddSubjectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Subject");

        // Set up the input fields
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputSubjectName = new EditText(requireContext());
        inputSubjectName.setInputType(InputType.TYPE_CLASS_TEXT);
        inputSubjectName.setHint("Subject Name");
        layout.addView(inputSubjectName);

        final EditText inputSubjectCode = new EditText(requireContext());
        inputSubjectCode.setInputType(InputType.TYPE_CLASS_TEXT);
        inputSubjectCode.setHint("Subject Code");
        layout.addView(inputSubjectCode);
        // Spinner for Semester selection
        Spinner spinnerSemester = new Spinner(requireContext());
        String[] semesters = {"S1", "S2", "S3", "S4", "S5"};
        ArrayAdapter<String> adapterSemester = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, semesters);
        adapterSemester.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(adapterSemester);
        layout.addView(spinnerSemester);

        final EditText pre = new EditText(requireContext());
        pre.setInputType(InputType.TYPE_CLASS_TEXT);
        pre.setHint("prerequisite Subject");
        layout.addView(pre);
        builder.setView(layout);

        // Initialize Firebase Database reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference subjectsRef = database.getReference("subjects");

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String subjectName = inputSubjectName.getText().toString().trim();
                String subjectCode = inputSubjectCode.getText().toString().trim();
                String semester = spinnerSemester.getSelectedItem().toString();
                String presub = pre.getText().toString().trim();
                if (!subjectName.isEmpty() && !subjectCode.isEmpty()) {
                    // Save subject to Firebase Database
                    String subjectId = subjectsRef.push().getKey(); // Generate a unique key for the new subject
                    subjectModel newSubject = new subjectModel(subjectId, subjectName, subjectCode,semester,presub);
                    subjectsRef.child(subjectId).setValue(newSubject);

                    Toast.makeText(requireContext(), "Subject added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Please enter both subject name and code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    // Method to show dialog for adding a new classroom
    private void showAddclassroomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Classroom");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference classroomRef = database.getReference("classroom");

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String classroomName = input.getText().toString().trim();
                if (!classroomName.isEmpty()) {
                    // Save className to Firebase Database
                    String classroomId = classroomRef.push().getKey(); // Generate a unique key for the new class
                    classroomModel newClass = new classroomModel(classroomId, classroomName);
                    classroomRef.child(classroomId).setValue(newClass);

                    Toast.makeText(requireContext(), "Classroom added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Please enter a classroom name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Method to show dialog for adding a new prere
    private void showAddpreDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Prerequisite");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference Ref = database.getReference("prerequisite");

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Name = input.getText().toString().trim();
                if (!Name.isEmpty()) {
                    // Save className to Firebase Database
                    String Id = Ref.push().getKey(); // Generate a unique key for the new class
                    prerequisitemodel newClass = new prerequisitemodel(Id, Name);
                    Ref.child(Id).setValue(newClass);

                    Toast.makeText(requireContext(), "Subject added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Please enter a subject name", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void showAddprogramDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Program");

        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference Ref = database.getReference("program");

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String Name = input.getText().toString().trim();
                if (!Name.isEmpty()) {
                    // Save className to Firebase Database
                    String Id = Ref.push().getKey(); // Generate a unique key for the new class
                    prerequisitemodel newClass = new prerequisitemodel(Id, Name);
                    Ref.child(Id).setValue(newClass);

                    Toast.makeText(requireContext(), "Program added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "Please enter a Program ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
