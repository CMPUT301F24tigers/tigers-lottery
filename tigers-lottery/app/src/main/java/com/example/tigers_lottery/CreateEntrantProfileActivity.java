package com.example.tigers_lottery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CreateEntrantProfileActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference entrantsRef;
    private Button createAccountButton;
    private FloatingActionButton cancelButton;
    private Button saveInfoButton;
    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText, dobEditText, passwordEdittext, mobileEditText, retypeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entrant_profile_activity);

        Intent intent = getIntent();
        db = FirebaseFirestore.getInstance();
        entrantsRef = db.collection("users");
        HashMap<String, String> userData = new HashMap<>();

        saveInfoButton = findViewById(R.id.buttonSaveInfo);
        cancelButton = findViewById(R.id.sign_up_cancel_button);
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        dobEditText = findViewById(R.id.editTextDOB);
        mobileEditText = findViewById(R.id.editTextMobile);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData.put("First Name", firstNameEditText.getText().toString());
                userData.put("Last Name", lastNameEditText.getText().toString());
                userData.put("Email", emailEditText.getText().toString());
                userData.put("DOB", dobEditText.getText().toString());
                userData.put("Mobile", mobileEditText.getText().toString());

                entrantsRef.document(intent.getStringExtra("Device ID")).set(userData)
                        .addOnFailureListener(e -> {
                            Log.w("Error", e);
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {

                            }
                        });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainActivityIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivityIntent);
            }
        });
    }

}