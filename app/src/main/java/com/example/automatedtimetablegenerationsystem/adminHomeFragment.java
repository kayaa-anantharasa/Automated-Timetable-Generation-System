package com.example.automatedtimetablegenerationsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

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

public class adminHomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private TimetableAdapter adapter;
    private List<Timetable> timetableEntries;
    private DatabaseReference databaseRef;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.dataview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize timetableEntries
        timetableEntries = new ArrayList<>();

        // Initialize Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("timetable");

        // Initialize adapter
        adapter = new TimetableAdapter(getContext(), timetableEntries, databaseRef);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase Realtime Database
        fetchDataFromFirebase();

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterList(newText); // Call filter method in adapter
                return true;
            }
        });

        return view;
    }

    // Method to fetch data from Firebase Realtime Database
    private void fetchDataFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                timetableEntries.clear(); // Clear existing entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Timetable timetable = snapshot.getValue(Timetable.class);
                    if (timetable != null) {
                        timetable.setKey(snapshot.getKey()); // Set the key from Firebase snapshot
                        timetableEntries.add(timetable);
                    }
                }
                adapter.updateData(timetableEntries); // Update adapter data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
            }
        });
    }
}
