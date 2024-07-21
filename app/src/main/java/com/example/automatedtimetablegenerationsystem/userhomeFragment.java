package com.example.automatedtimetablegenerationsystem;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class userhomeFragment extends Fragment {
    private List<String> subjects; // Define these appropriately
    private List<String> classes;  // Define these appropriately

    private Spinner spinnerSemester;
    private Spinner spinnerProgram;
    private Spinner spinnerclass;
    private String[] semesters = {"S1", "S2", "S3", "S4", "S5"};
    String selectedSemester;
    private Button showTimetableButton;
    private Button ViewTimetable;
    private Button PdfTimetable;
    private ProgressBar progressBar;
    private DatabaseReference timetableRef;
    private List<TimetableEntry> timetableData = new ArrayList<>();
    private TextView fname, fletter, ftime; // TextView to display username
    String username ;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_userhome, container, false);

        // Initialize Views
        fname = view.findViewById(R.id.name);
        fletter = view.findViewById(R.id.firstletter);
        ftime = view.findViewById(R.id.time);
        spinnerSemester = view.findViewById(R.id.semesters);
        spinnerProgram = view.findViewById(R.id.program);
        spinnerclass = view.findViewById(R.id.classname);
        progressBar = view.findViewById(R.id.progressBar);
        showTimetableButton = view.findViewById(R.id.showtimetable);
        ViewTimetable = view.findViewById(R.id.ViewTimetable);
        PdfTimetable = view.findViewById(R.id.savePdf);
        // Load username from SharedPreferences
        SharedPreferences preferences = requireContext().getSharedPreferences("user_data", requireContext().MODE_PRIVATE);
         username = preferences.getString("username", "Default Name");

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
                    // Log.d("LogoutTime", "No logout time found for " + username);
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
                 selectedSemester = spinnerSemester.getSelectedItem().toString();
                String selectedProgram = spinnerProgram.getSelectedItem().toString();
                String selectedClass = spinnerclass.getSelectedItem().toString();
                fetchTimetable(selectedSemester, selectedProgram, selectedClass);
            }
        });
        ViewTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String semester = "S3"; // Example semester, adjust as needed

                // Fetch data from viewsubject based on username
                DatabaseReference viewSubjectRef = FirebaseDatabase.getInstance().getReference()
                        .child("viewsubject").child(username);

                viewSubjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<String> subjects = new ArrayList<>();
                            List<String> classes = new ArrayList<>();

                            // Collect all subjects and classes associated with the username
                            for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                                String subjectName = subjectSnapshot.getKey(); // Get the subject name (e.g., BUSINESS INTELLIGENCE)
                                String className = subjectSnapshot.child("class").getValue(String.class);

                                if (className != null) {
                                    subjects.add(subjectName);
                                    classes.add(className);
                                }
                            }

                            if (!subjects.isEmpty()) {
                                // Data found, fetch timetable data for the semester and display together
                                fetchTimetableForAllSubjects(semester, subjects, classes);
                            } else {
                                Toast.makeText(requireContext(), "No subjects found for " + username, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "No data found for " + username, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        PdfTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = fname.getText().toString().trim(); // Assuming fname is an EditText for username
                String semester = "S3"; // Example semester, adjust as needed

                // Fetch data from viewsubject based on username
                DatabaseReference viewSubjectRef = FirebaseDatabase.getInstance().getReference()
                        .child("viewsubject").child(username);

                viewSubjectRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<String> subjects = new ArrayList<>();
                            List<String> classes = new ArrayList<>();

                            // Collect all subjects and classes associated with the username
                            for (DataSnapshot subjectSnapshot : dataSnapshot.getChildren()) {
                                String subjectName = subjectSnapshot.getKey(); // Get the subject name
                                String className = subjectSnapshot.child("class").getValue(String.class);

                                if (className != null) {
                                    subjects.add(subjectName);
                                    classes.add(className);
                                }
                            }

                            if (!subjects.isEmpty()) {
                                // Data found, generate PDF timetable
                                generatePdf(username, semester, subjects, classes);
                            } else {
                                Toast.makeText(requireContext(), "No subjects found for " + username, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "No data found for " + username, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(requireContext(), "Failed to fetch data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });




        return view;
    }
    private void generatePdf(String username, String semester, List<String> subjects, List<String> classes) {
        // Create a new PdfDocument
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 600, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();

        // Fetch timetable data based on subjects and classes
        fetchTimetableData(subjects, classes, new TimetableDataCallback() {
            @Override
            public void onTimetableDataReceived(List<TimetableEntry> timetableData) {
                // Create timetable content for PDF
                Map<String, Map<String, List<TimetableEntry>>> timetableMap = createTimetableContent(timetableData, subjects, classes);
                // Draw timetable content on PDF canvas
                drawTimetable(canvas, timetableMap);
                // Finish the page
                document.finishPage(page);

                // Save the PDF file
                File pdfFile = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), username + "_timetable.pdf");
                try {
                    // Create parent directories if they don't exist
                    pdfFile.getParentFile().mkdirs();
                    // Write the document content to the file
                    document.writeTo(new FileOutputStream(pdfFile));
                    Toast.makeText(requireContext(), "PDF saved successfully: " + pdfFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                    // View the PDF using an Intent
                    Uri pdfUri = FileProvider.getUriForFile(requireContext(),
                            requireContext().getApplicationContext().getPackageName() + ".provider", pdfFile);
                    Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                    pdfIntent.setDataAndType(pdfUri, "application/pdf");
                    pdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(pdfIntent);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(requireContext(), "Failed to save PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    // Close the document
                    document.close();
                }
            }

            @Override
            public void onTimetableDataError(String errorMessage) {
                Toast.makeText(requireContext(), "Failed to fetch timetable data: " + errorMessage, Toast.LENGTH_SHORT).show();
                // Close the document in case of error
                document.close();
            }
        });
    }
    private void drawTimetable(Canvas canvas, Map<String, Map<String, List<TimetableEntry>>> timetableMap) {
        Paint paint = new Paint();
        paint.setTextSize(30); // Text size for timetable entries
        int startX = 50; // Starting X coordinate
        int startY = 100; // Starting Y coordinate
        int columnWidth = 300; // Width of each column (for days)
        int rowHeight = 100; // Height of each row (for time slots)

        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] timeSlots = getTimeSlots();

        // Draw headers for days
        int x = startX + columnWidth;
        int y = startY + rowHeight;
        for (String day : days) {
            // Draw day header
            paint.setColor(Color.WHITE);
            canvas.drawRect(x, startY, x + columnWidth, startY + rowHeight, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(day, x + 20, startY + 70, paint);
            x += columnWidth;
        }

        // Draw headers for time slots
        x = startX;
        y = startY + rowHeight;
        for (String timeSlot : timeSlots) {
            // Draw time slot header
            paint.setColor(Color.WHITE);
            canvas.drawRect(startX, y, startX + columnWidth, y + rowHeight, paint);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(timeSlot, startX + 20, y + 70, paint);
            y += rowHeight;
        }

        // Draw timetable entries
        x = startX + columnWidth;
        for (String timeSlot : timeSlots) {
            y = startY + rowHeight;

            for (String day : days) {
                paint.setColor(Color.WHITE);
                canvas.drawRect(x, y, x + columnWidth, y + rowHeight, paint);
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.FILL);

                // Get timetable entries for the current day and time slot
                List<TimetableEntry> entries = timetableMap.get(day).get(timeSlot);

                // Calculate text height for multiline entries
                int textHeight = calculateTextHeight(paint, entries);

                // Draw each timetable entry under the respective day and time slot
                if (entries != null && !entries.isEmpty()) {
                    StringBuilder entryText = new StringBuilder();
                    for (TimetableEntry entry : entries) {
                        entryText.append(String.format("%s\n%s\n%s\n\n", entry.getSubjectName(), entry.getLecturer(), entry.getClassname()));
                    }
                    canvas.drawText(entryText.toString(), x + 20, y + 70 + textHeight, paint);
                } else {
                    // If no class, indicate "No Class" under the respective day and time slot
                    canvas.drawText("No Class", x + 20, y + 70 + textHeight, paint);
                }

                x += columnWidth; // Move to the next column for the next day
            }

            x = startX + columnWidth; // Reset X coordinate for the next time slot row
            y += rowHeight; // Move to the next row for the next time slot
        }
    }

    private int calculateTextHeight(Paint paint, List<TimetableEntry> entries) {
        if (entries == null || entries.isEmpty()) {
            return 0;
        }

        // Calculate height for multiline text
        String sampleText = entries.get(0).getSubjectName() + "\n" + entries.get(0).getLecturer() + "\n" + entries.get(0).getClassname();
        Rect bounds = new Rect();
        paint.getTextBounds(sampleText, 0, sampleText.length(), bounds);
        return bounds.height();
    }

    private Map<String, Map<String, List<TimetableEntry>>> createTimetableContent(List<TimetableEntry> timetableData, List<String> subjects, List<String> classes) {
        // Initialize timetable map with empty slots for each day and time slot
        Map<String, Map<String, List<TimetableEntry>>> timetableMap = new HashMap<>();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

        // Initialize the timetable map with empty lists for each day and time slot
        for (String day : days) {
            timetableMap.put(day, new HashMap<>());
            for (String timeSlot : getTimeSlots()) {
                timetableMap.get(day).put(timeSlot, new ArrayList<>());
            }
        }

        // Populate timetable map with timetable entries
        for (TimetableEntry entry : timetableData) {
            if (subjects.contains(entry.getSubjectName()) && classes.contains(entry.getClassname())) {
                String[] entryDays = entry.getDays().split(",\\s*");
                for (String day : entryDays) {
                    if (timetableMap.containsKey(day)) {
                        timetableMap.get(day).get(entry.getTime()).add(entry);
                    }
                }
            }
        }
        return timetableMap;
    }

    private String[] getTimeSlots() {
        return new String[]{"8:00 AM - 9:00 AM", "9:00 AM - 10:00 AM", "10:00 AM - 11:00 AM", "11:00 AM - 12:00 PM",
                "12:00 PM - 1:00 PM", "1:00 PM - 2:00 PM", "2:00 PM - 3:00 PM", "3:00 PM - 4:00 PM",
                "4:00 PM - 5:00 PM", "5:00 PM - 6:00 PM"};
    }



    private void fetchTimetableData(List<String> subjects, List<String> classes, TimetableDataCallback callback) {
        DatabaseReference timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<TimetableEntry> timetableData = new ArrayList<>();

                    for (DataSnapshot timetableSnapshot : snapshot.getChildren()) {
                        TimetableEntry timetableEntry = timetableSnapshot.getValue(TimetableEntry.class);

                        if (timetableEntry != null &&
                                subjects.contains(timetableEntry.getSubjectName()) &&
                                classes.contains(timetableEntry.getClassname())) {
                            timetableData.add(timetableEntry);
                        }
                    }

                    callback.onTimetableDataReceived(timetableData);
                } else {
                    callback.onTimetableDataError("No timetable data available");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onTimetableDataError("Failed to fetch timetable data: " + error.getMessage());
            }
        });
    }



    // Callback interface for timetable data
    interface TimetableDataCallback {
        void onTimetableDataReceived(List<TimetableEntry> timetableData);
        void onTimetableDataError(String errorMessage);
    }

    private void fetchTimetableForAllSubjects(String semester, List<String> subjects, List<String> classes) {
        progressBar.setVisibility(View.VISIBLE);
        timetableRef = FirebaseDatabase.getInstance().getReference().child("timetable");

        timetableRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                if (snapshot.exists()) {
                    List<TimetableEntry> timetableData = new ArrayList<>();

                    for (DataSnapshot timetableSnapshot : snapshot.getChildren()) {
                     //   String entrySemester = timetableSnapshot.child("semi").getValue(String.class);
                        String entrySubject = timetableSnapshot.child("subjectName").getValue(String.class);
                        String entryClassname = timetableSnapshot.child("classname").getValue(String.class);

                        if (entrySubject != null && entryClassname != null &&
                                subjects.contains(entrySubject) && classes.contains(entryClassname)) {
                            TimetableEntry timetableEntry = timetableSnapshot.getValue(TimetableEntry.class);
                            if (timetableEntry != null) {
                                timetableData.add(timetableEntry);
                            }
                        }
                    }

                    if (!timetableData.isEmpty()) {
                        Toast.makeText(requireContext(), "Timetable loaded" , Toast.LENGTH_SHORT).show();
                        showTimetableDialog(timetableData);
                    } else {
                        Toast.makeText(requireContext(), "No timetable found", Toast.LENGTH_SHORT).show();
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
