package com.example.automatedtimetablegenerationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

public class TimetableAdapter extends RecyclerView.Adapter<TimetableAdapter.ViewHolder> {

    private List<Timetable> timetableEntries;
    private Context context;

    // Constructor to initialize the adapter with data and context
    public TimetableAdapter(Context context, List<Timetable> timetableEntries) {
        this.context = context;
        this.timetableEntries = timetableEntries;
    }

    // Create ViewHolder to hold reference to each view item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView daysTextView, lecturerTextView, subjectCodeTextView, subjectNameTextView, timeTextView,semiTextView,programTextView,classTextView;
        Button deleteButton;

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
            deleteButton = itemView.findViewById(R.id.delete);
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
        Timetable entry = timetableEntries.get(position);

        holder.daysTextView.setText("Days: " + entry.getDays());
        holder.lecturerTextView.setText("Lecturer: " + entry.getLecturer());
        holder.subjectCodeTextView.setText("Subject Code: " + entry.getSubjectCode());
        holder.subjectNameTextView.setText("Subject Name: " + entry.getSubjectName());
        holder.timeTextView.setText("Time: " + entry.getTime());
        holder.semiTextView.setText("Semester: " + entry.getSemi());
        holder.programTextView.setText("Program: " + entry.getProgram());
        holder.classTextView.setText("Class: " + entry.getClassname());
        // Implement delete button functionality here if needed
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle delete button click action here
                // Example: Notify adapter of item removal and update data source
                // timetableEntries.remove(position);
                // notifyItemRemoved(position);
                timetableEntries.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, timetableEntries.size());

            }
        });
    }

    // Return number of items in the data set
    @Override
    public int getItemCount() {
        return timetableEntries.size();
    }

    // Method to update adapter data
    public void updateData(List<Timetable> newTimetableEntries) {
        timetableEntries.clear();
        timetableEntries.addAll(newTimetableEntries);
        notifyDataSetChanged();
    }
}
