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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<signupClass> userEntries;
    private List<signupClass> filteredList;
    private Context context;

    // Constructor
    public UserAdapter(Context context, List<signupClass> userEntries) {
        this.context = context;
        this.userEntries = userEntries;
        this.filteredList = new ArrayList<>(userEntries);
    }

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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_useritem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        signupClass user = filteredList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public void updateData(List<signupClass> newEntries) {
        userEntries.clear();
        userEntries.addAll(newEntries);
        filterList(""); // Reset filter when data changes
    }

    public void filterList(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userEntries);
        } else {
            query = query.toLowerCase().trim();
            for (signupClass user : userEntries) {
                if (user.getName().toLowerCase().contains(query) ||
                        user.getMatrixNumber().toLowerCase().contains(query) ||
                        user.getEmail().toLowerCase().contains(query)) {
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }
}
