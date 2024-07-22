package com.example.automatedtimetablegenerationsystem;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class usereditFragment extends Fragment {

    private DatabaseReference updatesRef;
    private TextView notificationTitleTextView;
    private TextView notificationMessageTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_useredit, container, false);
        notificationTitleTextView = view.findViewById(R.id.notification_title);
        notificationMessageTextView = view.findViewById(R.id.notification_message);

        // Initialize Firebase Database reference for timetable_updates
        updatesRef = FirebaseDatabase.getInstance().getReference("timetable_updates");

        // Listen for updates
        retrieveLatestUpdate();

        return view;
    }

    private void retrieveLatestUpdate() {
        updatesRef.orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String updateMessage = snapshot.child("updateMessage").getValue(String.class);
                                updateNotificationViews(updateMessage);
                                return; // Exit after finding the latest update
                            }
                        } else {
                            updateNotificationViews("No updates available");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to read updates: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateNotificationViews(String message) {
        notificationTitleTextView.setText("Timetable Update");
        notificationMessageTextView.setText(message);
    }
}
