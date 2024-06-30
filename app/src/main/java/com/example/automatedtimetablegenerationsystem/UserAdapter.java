package com.example.automatedtimetablegenerationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<signupClass> userEntries;
    private Context context;

    // Constructor to initialize the adapter with data and context
    public UserAdapter(Context context, List<signupClass> userEntries) {
        this.context = context;
        this.userEntries = userEntries;
    }

    // Create ViewHolder to hold reference to each view item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView matrixNumberTextView, nameTextView, emailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            matrixNumberTextView = itemView.findViewById(R.id.martix);
            nameTextView = itemView.findViewById(R.id.name);
            emailTextView = itemView.findViewById(R.id.email);
        }
    }

    // Inflate item layout and create ViewHolder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_useritem, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        signupClass entry = userEntries.get(position);

        holder.matrixNumberTextView.setText("Matrix Number: " + entry.getMatrixNumber());
        holder.nameTextView.setText("Name: " + entry.getName());
        holder.emailTextView.setText("Email: " + entry.getEmail());
    }

    // Return number of items in the data set
    @Override
    public int getItemCount() {
        return userEntries.size();
    }

    // Method to update adapter data
    public void updateData(List<signupClass> newEntries) {
        userEntries.clear();
        userEntries.addAll(newEntries);
        notifyDataSetChanged();
    }
}
