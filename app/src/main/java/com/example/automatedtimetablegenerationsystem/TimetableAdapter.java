package com.example.automatedtimetablegenerationsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private List<Timetable> timetableEntries;
    private List<Timetable> filteredList;
    private Context context;

    // Constructor to initialize the adapter with data and context
    public TimetableAdapter(Context context, List<Timetable> timetableEntries) {
        this.context = context;
        this.timetableEntries = timetableEntries;
        this.filteredList = new ArrayList<>(timetableEntries);
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
        DatabaseReference timetableRef = FirebaseDatabase.getInstance().getReference("timetable");

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

        // Pre-fill the fields with current details
        editDays.setText(entryToUpdate.getDays());
        editLecturer.setText(entryToUpdate.getLecturer());
        editSubjectCode.setText(entryToUpdate.getSubjectCode());
        editSubjectName.setText(entryToUpdate.getSubjectName());
        editTime.setText(entryToUpdate.getTime());
        editSemi.setText(entryToUpdate.getSemi());
        editProgram.setText(entryToUpdate.getProgram());
        editClass.setText(entryToUpdate.getClassname());

        builder.setTitle("Edit Timetable Entry")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Get updated values from input fields
                        String updatedDays = editDays.getText().toString();
                        String updatedLecturer = editLecturer.getText().toString();
                        String updatedSubjectCode = editSubjectCode.getText().toString();
                        String updatedSubjectName = editSubjectName.getText().toString();
                        String updatedTime = editTime.getText().toString();
                        String updatedSemi = editSemi.getText().toString();
                        String updatedProgram = editProgram.getText().toString();
                        String updatedClass = editClass.getText().toString();

                        // Update entry with new values
                        entryToUpdate.setDays(updatedDays);
                        entryToUpdate.setLecturer(updatedLecturer);
                        entryToUpdate.setSubjectCode(updatedSubjectCode);
                        entryToUpdate.setSubjectName(updatedSubjectName);
                        entryToUpdate.setTime(updatedTime);
                        entryToUpdate.setSemi(updatedSemi);
                        entryToUpdate.setProgram(updatedProgram);
                        entryToUpdate.setClassname(updatedClass);

                        // Update entry in Firebase
                        DatabaseReference timetableRef = FirebaseDatabase.getInstance().getReference("timetable");
                        String key = entryToUpdate.getKey();
                        timetableRef.child(key).setValue(entryToUpdate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Update local list and notify adapter
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
                })
                .setNegativeButton("Cancel", null);

        builder.create().show();
    }

    // Method to update data in adapter
    public void updateData(List<Timetable> newTimetableEntries) {
        this.timetableEntries = newTimetableEntries;
        this.filteredList = new ArrayList<>(newTimetableEntries);
        notifyDataSetChanged();
    }

    // Method to filter list based on query
    public void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(timetableEntries);
        } else {
            for (Timetable entry : timetableEntries) {
                if (entry.getSubjectName().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getSubjectCode().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getLecturer().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getDays().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getTime().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getProgram().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getSemi().toLowerCase().contains(query.toLowerCase()) ||
                        entry.getClassname().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(entry);
                }
            }
        }
        notifyDataSetChanged();
    }
}
