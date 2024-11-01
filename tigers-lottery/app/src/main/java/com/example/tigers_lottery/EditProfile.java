package com.example.tigers_lottery;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProfile extends AppCompatActivity {

    private EditText editTextEmail, editTextDOB, editTextMobile, editTextLocation;
    private Button buttonSave;


    private FirebaseFirestore db;
    private DocumentReference userRef;
    private String deviceId;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile_activity);


        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        db = FirebaseFirestore.getInstance();


        userRef = db.collection("users").document(deviceId);


        editTextEmail = findViewById(R.id.editTextEmail);
        editTextDOB = findViewById(R.id.editTextDOB);
        editTextMobile = findViewById(R.id.editTextMobile);
        editTextLocation = findViewById(R.id.editTextLocation);
        buttonSave = findViewById(R.id.button2);


        loadProfileData();


        buttonSave.setOnClickListener(v -> saveProfileData());
    }


    private void loadProfileData() {
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        editTextEmail.setText(document.getString("Email"));
                        editTextDOB.setText(document.getString("DOB"));
                        editTextMobile.setText(document.getString("Mobile"));
                        editTextLocation.setText(document.getString("Location"));
                    }
                } else {
                    Toast.makeText(EditProfile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void saveProfileData() {
        String email = editTextEmail.getText().toString();
        String dob = editTextDOB.getText().toString();
        String mobile = editTextMobile.getText().toString();
        String location = editTextLocation.getText().toString();


        UserProfile updatedProfile = new UserProfile(email, dob, mobile, location, deviceId);

        userRef.set(updatedProfile)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
    }
}
