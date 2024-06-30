package com.example.automatedtimetablegenerationsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class adminUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<signupClass> userEntries;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.dataviewuser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize userEntries
        userEntries = new ArrayList<>();

        // Initialize adapter
        adapter = new UserAdapter(getContext(), userEntries);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase Realtime Database
        fetchDataFromFirebase();

        return view;
    }

    // Method to fetch data from Firebase Realtime Database
    private void fetchDataFromFirebase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users"); // Replace with your database reference
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userEntries.clear(); // Clear existing entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    signupClass user = snapshot.getValue(signupClass.class);
                    userEntries.add(user);

                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
