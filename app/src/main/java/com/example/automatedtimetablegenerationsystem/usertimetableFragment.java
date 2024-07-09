package com.example.automatedtimetablegenerationsystem;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
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
import java.util.List;

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
                    List<String> selectedSubjects = new ArrayList<>();
                    List<String> selectedClasses = new ArrayList<>();

                    for (int i = 0; i < subjectSpinners.size(); i++) {
                        String subject = subjectSpinners.get(i).getSelectedItem().toString();
                        String className = classSpinners.get(i).getSelectedItem().toString();

                        selectedSubjects.add(subject);
                        selectedClasses.add(className);
                    }

                    // Check for timetable conflicts
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
                                         String selectedCurrentSemester, String selectedFailedSemester) {
        // Initialize Firebase Realtime Database reference
        DatabaseReference timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        // Retrieve timetable data for current semester and program
        List<String> currenttimetableEntries = new ArrayList<>();
        Query currentSemesterQuery = timetableRef.orderByChild("program").equalTo(selectedProgram);
        currentSemesterQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String time = snapshot.child("time").getValue(String.class);
                    String day = snapshot.child("days").getValue(String.class);
                    String semester = snapshot.child("semi").getValue(String.class);
                    String program = snapshot.child("program").getValue(String.class);

                    // Filter by current semester and selected program
                    if (semester != null && semester.equals(selectedCurrentSemester) && program.equals(selectedProgram)) {
                        currenttimetableEntries.add(program + " - " + day + " at " + time);
                    }
                }

                // Retrieve timetable data for failed semester and program
                List<String> timetableEntries = new ArrayList<>();
                for (String subject : selectedSubjects) {
                    timetableRef.orderByChild("subjectName").equalTo(subject)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String time = snapshot.child("time").getValue(String.class);
                                        String day = snapshot.child("days").getValue(String.class);
                                        String semester = snapshot.child("semi").getValue(String.class);
                                        String program = snapshot.child("program").getValue(String.class);
                                        String className = snapshot.child("classname").getValue(String.class);
                                        // Filter by failed semester and program
                                        if (semester.equals(selectedFailedSemester) && program.equals(selectedProgram) && selectedClasses.contains(className)) {
                                            timetableEntries.add(subject + " - " + day + " at " + time + " (" + className + ")");
                                        }
                                    }

                                    // Check for overlaps between current and failed semester timetables
                                    boolean overlapFound = checkForOverlaps(currenttimetableEntries, timetableEntries);

                                    // Display result based on overlap
                                    if (overlapFound) {
                                        Toast.makeText(requireContext(), "Overlap found between current and failed semester timetables.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(requireContext(), "No overlap found between current and failed semester timetables.", Toast.LENGTH_SHORT).show();
                                        // Generate and download PDF for failed semester timetable view
                                        PdfDocument document = generatePDF(timetableEntries);
                                        showDownloadDialog(document);
                                      // generateAndDownloadPDF(selectedSubjects, selectedProgram,selectedClasses,selectedFailedSemester);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Handle database error
                                    Log.e(TAG, "Error fetching timetable data", databaseError.toException());
                                    Toast.makeText(requireContext(), "Error fetching timetable data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle database error
                Log.e(TAG, "Error fetching timetable data", databaseError.toException());
                Toast.makeText(requireContext(), "Error fetching timetable data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkForOverlaps(List<String> currenttimetableEntries, List<String> timetableEntries) {
        // Logic to check for overlaps between current and failed semester timetables
        // Implement your logic here based on the structure of timetable entries
        for (String currentEntry : currenttimetableEntries) {
            String[] currentParts = currentEntry.split(" - ");
            String currentDayTime = currentParts[1]; // Extract day and time part from current entry
            String[] currentDayTimeParts = currentDayTime.split(" at ");
            String currentDays = currentDayTimeParts[0]; // Extract days part
            String currentTime = currentDayTimeParts[1]; // Extract time part

            for (String failedEntry : timetableEntries) {
                String[] failedParts = failedEntry.split(" - ");
                String failedDayTime = failedParts[1]; // Extract day and time part from failed entry
                String[] failedDayTimeParts = failedDayTime.split(" at ");
                String failedDays = failedDayTimeParts[0]; // Extract days part
                String failedTime = failedDayTimeParts[1]; // Extract time part

                // Check for overlapping days and time
                if (currentDays.equals(failedDays) && currentTime.equals(failedTime)) {
                    return true; // Overlap found
                }
            }
        }
        return false; // No overlap found
    }

    private void generateAndDownloadPDF(List<String> selectedSubjects,String selectedProgram, List<String> selectedClasses, String selectedFailedSemester) {
        // Retrieve timetable entries for failed semester and program
        List<String> timetableEntries = new ArrayList<>();
        for (String subject : selectedSubjects) {
            timetableRef.orderByChild("subjectName").equalTo(subject)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String time = snapshot.child("time").getValue(String.class);
                                String day = snapshot.child("days").getValue(String.class);
                                String semester = snapshot.child("semi").getValue(String.class);
                                String program = snapshot.child("program").getValue(String.class);

                                // Filter by failed semester and program
                                if (semester.equals(selectedFailedSemester) && program.equals(selectedProgram)) {
                                    timetableEntries.add(subject + " - " + day + " at " + time);
                                }
                            }

                            // Generate PDF document
                            PdfDocument document = generatePDF(timetableEntries);

                            // Show dialog to ask user to download
                            showDownloadDialog(document);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle database error
                            Log.e(TAG, "Error fetching timetable data", databaseError.toException());
                            Toast.makeText(requireContext(), "Error fetching timetable data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private PdfDocument generatePDF(List<String> timetableEntries) {
        PdfDocument document = new PdfDocument();
        int pageNum = 1;

        for (String entry : timetableEntries) {
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, pageNum).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            canvas.drawText(entry, 10, 50, null);

            document.finishPage(page);
            pageNum++;
        }

        return document;
    }

    private void showDownloadDialog(PdfDocument document) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Download PDF");
        builder.setMessage("Do you want to download the timetable as PDF?");
        builder.setPositiveButton("Download", (dialog, which) -> {
            // Save PDF to external storage
            try {
                File filePath = new File(Environment.getExternalStorageDirectory(), "Failed_Timetable.pdf");
                document.writeTo(new FileOutputStream(filePath));
                document.close();

                Toast.makeText(requireContext(), "Failed semester timetable downloaded as PDF", Toast.LENGTH_SHORT).show();

                // Open the PDF file
                openPdfFile(filePath);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(requireContext(), "Error downloading failed semester timetable as PDF", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openPdfFile(File pdfFile) {
        Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(requireContext(), "No application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }


}
