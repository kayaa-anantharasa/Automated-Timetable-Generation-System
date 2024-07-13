package com.example.automatedtimetablegenerationsystem;

import android.os.Bundle;
import android.util.Log;
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

public class adminUserFragment extends Fragment {

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private List<signupClass> userEntries;
    private DatabaseReference databaseRef;
    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_user, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.dataviewuser);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize userEntries
        userEntries = new ArrayList<>();
        databaseRef = FirebaseDatabase.getInstance().getReference("users");

        // Initialize adapter
        adapter = new UserAdapter(getContext(), userEntries);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchDataFromFirebase();

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchuserView);
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

    private void fetchDataFromFirebase() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userEntries.clear(); // Clear existing entries
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    signupClass user = snapshot.getValue(signupClass.class);
                    if (user != null) {
                        userEntries.add(user);
                    }
                }
                adapter.updateData(userEntries); // Update adapter data
                Log.d("FirebaseData", "Data loaded successfully.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "Error loading data: " + databaseError.getMessage());
            }
        });
    }
}
