package com.example.automatedtimetablegenerationsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private List<Timetable> timetableEntries;
    private List<Timetable> filteredList;
    private Context context;
    private DatabaseReference timetableRef;

    // Constructor to initialize the adapter with data and context
    public TimetableAdapter(Context context, List<Timetable> timetableEntries, DatabaseReference timetableRef) {
        this.context = context;
        this.timetableEntries = timetableEntries;
        this.filteredList = new ArrayList<>(timetableEntries);
        this.timetableRef = timetableRef; // Initialize with Firebase database reference
    }

    // Create ViewHolder to hold reference to each view item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView daysTextView, lecturerTextView, subjectCodeTextView, subjectNameTextView, timeTextView, semiTextView, programTextView, classTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            daysTextView = itemView.findViewById(R.id.days);
            lecturerTextView = itemView.findViewById(R.id.lecturer);
            subjectCodeTextView = itemView.findViewById(R.id.subjectCode);
            subjectNameTextView = itemView.findViewById(R.id.subjectName);
            timeTextView = itemView.findViewById(R.id.time);
            semiTextView = itemView.findViewById(R.id.semi);
            programTextView = itemView.findViewById(R.id.programname);
            classTextView = itemView.findViewById(R.id.classname);
        }

        public void bind(Timetable timetable) {
            daysTextView.setText("Days: " + timetable.getDays());
            lecturerTextView.setText("Lecturer: " + timetable.getLecturer());
            subjectCodeTextView.setText("Subject Code: " + timetable.getSubjectCode());
            subjectNameTextView.setText("Subject Name: " + timetable.getSubjectName());
            timeTextView.setText("Time: " + timetable.getTime());
            semiTextView.setText("Semester: " + timetable.getSemi());
            programTextView.setText("Program: " + timetable.getProgram());
            classTextView.setText("Class: " + timetable.getClassname());
        }
    }

    // Inflate item layout and create ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_timetableitem, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Timetable timetable = filteredList.get(position);
        holder.bind(timetable);

        // Implement delete button functionality
        holder.itemView.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTimetableEntry(position);
            }
        });

        // Implement update button functionality
        holder.itemView.findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTimetableEntry(position);
            }
        });
    }

    // Return number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // Method to delete timetable entry from Firebase and update RecyclerView
    private void deleteTimetableEntry(int position) {
        Timetable entryToDelete = filteredList.get(position);

        // Remove from Firebase database
        String key = entryToDelete.getKey();
        timetableRef.child(key).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Remove from local list and notify adapter
                        timetableEntries.remove(entryToDelete);
                        filteredList.remove(entryToDelete);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Timetable entry deleted successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete timetable entry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Method to update timetable entry
    private void updateTimetableEntry(int position) {
        Timetable entryToUpdate = filteredList.get(position);

        // Launch a dialog with current details pre-filled
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_timetable, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        EditText editDays = dialogView.findViewById(R.id.editDays);
        EditText editLecturer = dialogView.findViewById(R.id.editLecturer);
        EditText editSubjectCode = dialogView.findViewById(R.id.editSubjectCode);
        EditText editSubjectName = dialogView.findViewById(R.id.editSubjectName);
        EditText editTime = dialogView.findViewById(R.id.editTime);
        EditText editSemi = dialogView.findViewById(R.id.editSemi);
        EditText editProgram = dialogView.findViewById(R.id.editProgram);
        EditText editClass = dialogView.findViewById(R.id.editClass);

        // Pre-fill with current details
        editDays.setText(entryToUpdate.getDays());
        editLecturer.setText(entryToUpdate.getLecturer());
        editSubjectCode.setText(entryToUpdate.getSubjectCode());
        editSubjectName.setText(entryToUpdate.getSubjectName());
        editTime.setText(entryToUpdate.getTime());
        editSemi.setText(entryToUpdate.getSemi());
        editProgram.setText(entryToUpdate.getProgram());
        editClass.setText(entryToUpdate.getClassname());

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Update the entry with new details
                entryToUpdate.setDays(editDays.getText().toString());
                entryToUpdate.setLecturer(editLecturer.getText().toString());
                entryToUpdate.setSubjectCode(editSubjectCode.getText().toString());
                entryToUpdate.setSubjectName(editSubjectName.getText().toString());
                entryToUpdate.setTime(editTime.getText().toString());
                entryToUpdate.setSemi(editSemi.getText().toString());
                entryToUpdate.setProgram(editProgram.getText().toString());
                entryToUpdate.setClassname(editClass.getText().toString());

                // Update Firebase database
                String key = entryToUpdate.getKey();
                timetableRef.child(key).setValue(entryToUpdate)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Update local list and notify adapter
                                timetableEntries.set(timetableEntries.indexOf(entryToUpdate), entryToUpdate);
                                notifyDataSetChanged();
                                Toast.makeText(context, "Timetable entry updated successfully", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "Failed to update timetable entry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();
    }

    // Method to update adapter data
    public void updateData(List<Timetable> newTimetableEntries) {
        timetableEntries.clear();
        filteredList.clear();
        timetableEntries.addAll(newTimetableEntries);
        filteredList.addAll(newTimetableEntries);
        notifyDataSetChanged();
    }

    // Method to filter data based on query
    public void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(timetableEntries);
        } else {
            query = query.toLowerCase().trim();
            for (Timetable timetable : timetableEntries) {
                if (timetable.getSubjectName().toLowerCase().contains(query)
                        || timetable.getSubjectCode().toLowerCase().contains(query)
                        || timetable.getLecturer().toLowerCase().contains(query)
                        || timetable.getDays().toLowerCase().contains(query)
                        || timetable.getTime().toLowerCase().contains(query)
                        || timetable.getSemi().toLowerCase().contains(query)
                        || timetable.getProgram().toLowerCase().contains(query)
                        || timetable.getClassname().toLowerCase().contains(query)) {
                    filteredList.add(timetable);
                }
            }
        }
        notifyDataSetChanged();
    }
}
