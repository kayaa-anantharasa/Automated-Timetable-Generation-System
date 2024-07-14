package com.example.automatedtimetablegenerationsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<signupClass> userEntries;
    private List<signupClass> originalList; // Original list to hold all data
    private List<signupClass> filteredList; // Filtered list based on search query
    private Context context;

    // Constructor to initialize the adapter with data and context
    public UserAdapter(Context context, List<signupClass> userEntries) {
        this.context = context;
        this.originalList = new ArrayList<>(userEntries); // Initialize original list
        this.userEntries = new ArrayList<>(userEntries); // Initialize userEntries (for all data)
        this.filteredList = new ArrayList<>(userEntries); // Initially, filtered list is same as original
    }

    // ViewHolder class to hold reference to each view item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView matrixNumberTextView, nameTextView, emailTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            matrixNumberTextView = itemView.findViewById(R.id.martix);
            nameTextView = itemView.findViewById(R.id.name);
            emailTextView = itemView.findViewById(R.id.email);
        }

        public void bind(signupClass user) {
            matrixNumberTextView.setText("Matrix Number: " + user.getMatrixNumber());
            nameTextView.setText("Name: " + user.getName());
            emailTextView.setText("Email: " + user.getEmail());
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
        signupClass user = filteredList.get(position);
        holder.bind(user);
    }

    // Return number of items in the data set
    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    // Method to update data in adapter
    public void updateData(List<signupClass> newEntries) {
        originalList.clear();
        originalList.addAll(newEntries);
        userEntries.clear();
        userEntries.addAll(newEntries);
        filterList(""); // Reset filter with empty query
    }

    // Method to filter list based on query
    public void filterList(String newText) {
        newText = newText.toLowerCase(Locale.getDefault());
        filteredList.clear();
        if (newText.isEmpty()) {
            filteredList.addAll(originalList); // Load all data if search query is empty
        } else {
            for (signupClass user : originalList) {
                // Add filtering logic based on your requirements (e.g., filter by name)
                if (user.getName().toLowerCase(Locale.getDefault()).contains(newText)) {
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
