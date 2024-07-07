package com.example.automatedtimetablegenerationsystem;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.graphics.pdf.PdfDocument;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // Variable to store selected program
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

                builder.setPositiveButton("Check", (dialog, which) -> {
                    // Collect selected subjects and classes
                    List<String> selectedSubjects = new ArrayList<>();
                    List<String> selectedClasses = new ArrayList<>();

                    for (int i = 0; i < subjectSpinners.size(); i++) {
                        String subject = subjectSpinners.get(i).getSelectedItem().toString();
                        String className = classSpinners.get(i).getSelectedItem().toString();

                        selectedSubjects.add(subject);
                        selectedClasses.add(className);
                    }

                    // Check timetable conflicts for current and failed semesters
                    checkTimetableConflicts(selectedSubjects, selectedClasses, selectedProgram, selectedCurrentSemester, selectedFailedSemester);
                });

                // Show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    private void checkTimetableConflicts(List<String> selectedSubjects, List<String> selectedClasses, String selectedProgram,
                                         String currentSemester, String failedSemester) {
        // Initialize query to check if there are any conflicts between current and failed semesters
        Query queryCurrentSemester = timetableRef.orderByChild("semi_program").equalTo(currentSemester + "_" + selectedProgram);
        Query queryFailedSemester = timetableRef.orderByChild("semi_program").equalTo(failedSemester + "_" + selectedProgram);

        queryCurrentSemester.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Map to store timetable entries for current semester
                Map<String, String> currentSemesterEntries = new HashMap<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String entrySubject = dataSnapshot.child("subjectName").getValue(String.class);
                    String entryClass = dataSnapshot.child("classname").getValue(String.class);

                    // Store entry with subject as key and class as value
                    if (entrySubject != null && entryClass != null) {
                        currentSemesterEntries.put(entrySubject, entryClass);
                    }
                }

                // Check for conflicts in current semester
                boolean currentSemesterConflict = checkConflicts(selectedSubjects, selectedClasses, currentSemesterEntries);

                // If there is a conflict in current semester, show message and return
                if (currentSemesterConflict) {
                    Toast.makeText(requireContext(), "Timetable conflicts found in current semester", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check for conflicts in failed semester if selected
                if (!failedSemester.equals("None")) {
                    queryFailedSemester.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            // Map to store timetable entries for failed semester
                            Map<String, String> failedSemesterEntries = new HashMap<>();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String entrySubject = dataSnapshot.child("subjectName").getValue(String.class);
                                String entryClass = dataSnapshot.child("classname").getValue(String.class);

                                // Store entry with subject as key and class as value
                                if (entrySubject != null && entryClass != null) {
                                    failedSemesterEntries.put(entrySubject, entryClass);
                                }
                            }

                            // Check for conflicts in failed semester
                            boolean failedSemesterConflict = checkConflicts(selectedSubjects, selectedClasses, failedSemesterEntries);

                            // If there is a conflict in failed semester, show message
                            if (failedSemesterConflict) {
                                Toast.makeText(requireContext(), "Timetable conflicts found in failed semester", Toast.LENGTH_SHORT).show();
                            } else {
                                // If no conflicts found, proceed to add timetable entries
                                addTimetableEntries(selectedSubjects, selectedClasses, selectedProgram, currentSemester);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Error checking failed semester entries: " + error.getMessage());
                            Toast.makeText(requireContext(), "Failed to check failed semester entries: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    // If no failed semester selected, proceed to add timetable entries
                    addTimetableEntries(selectedSubjects, selectedClasses, selectedProgram, currentSemester);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Error checking current semester entries: " + error.getMessage());
                Toast.makeText(requireContext(), "Failed to check current semester entries: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkConflicts(List<String> selectedSubjects, List<String> selectedClasses, Map<String, String> semesterEntries) {
        // Iterate through selected subjects and classes to check for conflicts
        for (int i = 0; i < selectedSubjects.size(); i++) {
            String subject = selectedSubjects.get(i);
            String className = selectedClasses.get(i);

            // Check if the subject already exists with a different class in the same semester
            if (semesterEntries.containsKey(subject) && !semesterEntries.get(subject).equals(className)) {
                return true; // Conflict found
            }
        }
        return false; // No conflicts found
    }

    private void addTimetableEntries(List<String> selectedSubjects, List<String> selectedClasses, String selectedProgram, String currentSemester) {
        // Show success message with Toast
        Toast.makeText(requireContext(), "Timetable entries added successfully for " + currentSemester, Toast.LENGTH_SHORT).show();

        // Ask user if they want to download as PDF
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Download Timetable as PDF?");
        builder.setMessage("Do you want to download the timetable as a PDF?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Generate and download PDF
            generateAndDownloadPDF(selectedSubjects, selectedClasses, currentSemester);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            // Handle if user chooses not to download
            dialog.dismiss();
        });
        builder.show();
    }

    private void generateAndDownloadPDF(List<String> selectedSubjects, List<String> selectedClasses, String currentSemester) {
        // Create a new PDF document
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Set up the text to print
        StringBuilder timetableDetails = new StringBuilder();
        timetableDetails.append("Timetable Entries for ").append(currentSemester).append("\n\n");

        // Iterate through selected subjects and classes to add details to StringBuilder
        for (int i = 0; i < selectedSubjects.size(); i++) {
            String subject = selectedSubjects.get(i);
            String className = selectedClasses.get(i);

            // Append subject and class details to StringBuilder
            timetableDetails.append("Subject: ").append(subject).append("\n");
            timetableDetails.append("Class: ").append(className).append("\n\n");

            // Draw text on the PDF canvas
            canvas.drawText("Subject: " + subject, 10, (i + 1) * 25, null);
            canvas.drawText("Class: " + className, 10, (i + 2) * 25, null);
        }

        // Finish the page
        document.finishPage(page);

        // Save the document
        try {
            File filePath = new File(Environment.getExternalStorageDirectory(), "Timetable.pdf");
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(requireContext(), "Timetable downloaded as PDF", Toast.LENGTH_SHORT).show();

            // Optionally, you can open the downloaded PDF file
            openPDF(filePath.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Error downloading timetable as PDF", Toast.LENGTH_SHORT).show();
        }

        // Close the document
        document.close();
    }

    private void openPDF(String filePath) {
        // Open the PDF file using an Intent
        File file = new File(filePath);
        Uri pdfUri = FileProvider.getUriForFile(requireContext(), requireContext().getApplicationContext().getPackageName() + ".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }
}
