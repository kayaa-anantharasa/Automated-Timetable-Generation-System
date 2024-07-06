package com.example.automatedtimetablegenerationsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimetableUserAdapter extends RecyclerView.Adapter<TimetableUserAdapter.ViewHolder> {
    private List<TimetableEntry> timetableData;

    public TimetableUserAdapter(List<TimetableEntry> timetableData) {
        this.timetableData = timetableData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timetable, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimetableEntry entry = timetableData.get(position);

        holder.textClassName.setText(entry.getClassname());
        holder.textSubjectName.setText(entry.getSubjectName());
        holder.textLecturer.setText(entry.getLecturer());
        holder.textDays.setText(entry.getDays());
        // You can bind more fields as per your TimetableEntry class
    }

    @Override
    public int getItemCount() {
        return timetableData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textClassName;
        TextView textSubjectName;
        TextView textLecturer;
        TextView textDays;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textClassName = itemView.findViewById(R.id.textClassName);
            textSubjectName = itemView.findViewById(R.id.textSubjectName);
            textLecturer = itemView.findViewById(R.id.textLecturer);
            textDays = itemView.findViewById(R.id.textDays);
        }
    }
}
