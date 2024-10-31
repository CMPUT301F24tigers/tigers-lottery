package com.example.tigers_lottery;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {
    Button adminContinue, entrantContinue, organizerContinue;
    private FirebaseFirestore db;
    private CollectionReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");

        @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
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

}