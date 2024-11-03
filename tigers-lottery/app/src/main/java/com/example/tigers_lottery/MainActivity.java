package com.example.tigers_lottery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.example.tigers_lottery.Admin.AdminDashboardFragment;
import com.example.tigers_lottery.HostedEvents.OrganizerDashboardFragment;
import com.example.tigers_lottery.JoinedEvents.EntrantDashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button adminContinue, entrantContinue, organizerContinue;
    private FirebaseFirestore db;
    private CollectionReference usersRef;
    private DatabaseHelper dbHelper;

    //test comment added
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        // Initialize DatabaseHelper, which will ensure user existence in Firestore
        dbHelper = new DatabaseHelper(this);

        // Optional: Log or use the current user ID if needed for debugging
        String userId = dbHelper.getCurrentUserId();
        Log.d("MainActivity", "Current User ID (Device ID): " + userId);

        // Load the default fragment
        loadFragment(new EntrantDashboardFragment());

        // Set up BottomNavigationView
        BottomNavigationView bottomNav = findViewById(R.id.nav_view);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                if (item.getItemId() == R.id.navigation_admin) {
                    selectedFragment = new AdminDashboardFragment();
                } else if (item.getItemId() == R.id.navigation_entrant) {
                    selectedFragment = new EntrantDashboardFragment();
                } else if (item.getItemId() == R.id.navigation_organizer) {
                    selectedFragment = new OrganizerDashboardFragment();
                }
                return loadFragment(selectedFragment);
            }
        });
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView2, fragment)
                    .commit();
            return true;
        }
        return false;
    }
    //@SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        /*adminContinue = findViewById(R.id.adminSelectButton);
        entrantContinue = findViewById(R.id.entrantSelectButton);
        organizerContinue = findViewById(R.id.organizerSelectButton);

        db.collection("admins").document(deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                adminContinue.setVisibility(View.VISIBLE);
                            } else {
                                adminContinue.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Log.e("Firestore", "Error getting document: ", task.getException());
                        }
                    }
                });

        entrantContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(deviceId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Intent entrantActivityIntent = new Intent(getApplicationContext(), EntrantActivity.class);
                                        startActivity(entrantActivityIntent);
                                    } else {
                                        Intent createEntrantProfileActivity = new Intent(getApplicationContext(), CreateEntrantProfileActivity.class);
                                        createEntrantProfileActivity.putExtra("Device ID", deviceId);
                                        startActivity(createEntrantProfileActivity);
                                    }
                                } else {
                                    Log.e("Firestore", "Error getting document: ", task.getException());
                                }
                            }
                        });
            }
        });

        adminContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("users").document(deviceId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Intent adminActivityIntent = new Intent(getApplicationContext(), AdminActivity.class);
                                        startActivity(adminActivityIntent);
                                    } else {
                                        Intent createEntrantProfileActivity = new Intent(getApplicationContext(), CreateEntrantProfileActivity.class);
                                        createEntrantProfileActivity.putExtra("Device ID", deviceId);
                                        startActivity(createEntrantProfileActivity);
                                    }
                                } else {
                                    Log.e("Firestore", "Error getting document: ", task.getException());
                                }
                            }
                        });
            }
        });*/

}