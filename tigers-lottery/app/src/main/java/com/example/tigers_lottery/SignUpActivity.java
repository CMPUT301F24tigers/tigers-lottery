package com.example.tigers_lottery;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference entrantsRef;
    private CollectionReference organizersRef;
    private CollectionReference adminsRef;
    private Button createAccountButton;
    private FloatingActionButton cancelButton;
    private FloatingActionButton addProfileButton;
    private CheckBox entrantCheckBox, organizerCheckBox, adminCheckBox;
    private EditText firstNameEditText, lastNameEditText, usernameEditText, emailEditText, dobEditText, passwordEdittext, mobileEditText, retypeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_activity);

        db = FirebaseFirestore.getInstance();
        entrantsRef = db.collection("entrants");
        organizersRef = db.collection("organizers");
        adminsRef = db.collection("admins");
        HashMap<String, String> userData = new HashMap<>();

        createAccountButton = findViewById(R.id.buttonCreateAccount);
        cancelButton = findViewById(R.id.sign_up_cancel_button);
        addProfileButton = findViewById(R.id.sign_up_add_profile_button);
        entrantCheckBox = findViewById(R.id.entrant_checkbox);
        organizerCheckBox = findViewById(R.id.organizer_checkbox);
        adminCheckBox = findViewById(R.id.admin_checkbox);
        firstNameEditText = findViewById(R.id.editTextFirstName);
        lastNameEditText = findViewById(R.id.editTextLastName);
        usernameEditText = findViewById(R.id.editTextUsername);
        emailEditText = findViewById(R.id.editTextEmail);
        dobEditText = findViewById(R.id.editTextDOB);
        passwordEdittext = findViewById(R.id.editTextPassword);
        mobileEditText = findViewById(R.id.editTextMobile);
        retypeEditText = findViewById(R.id.editTextRePassword);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logInActivity = new Intent(getApplicationContext(), LogInActivity.class);
                startActivity(logInActivity);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData.put("First Name", firstNameEditText.getText().toString());
                userData.put("Last Name", lastNameEditText.getText().toString());
                userData.put("Email", emailEditText.getText().toString());
                userData.put("DOB", dobEditText.getText().toString());
                userData.put("Mobile", mobileEditText.getText().toString());
                userData.put("Password", passwordEdittext.getText().toString());

                entrantsRef.document(usernameEditText.getText().toString()).set(userData)
                        .addOnFailureListener(e -> {
                            Log.w("Error", e);
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Intent logInActivity = new Intent(getApplicationContext(), LogInActivity.class);
                                startActivity(logInActivity);
                            }
                        });
            }
        });
    }



    private Boolean getCheckBoxValid(CheckBox adminCheckBox, CheckBox entrantCheckBox, CheckBox organizerCheckBox) {
        int checkedCount = 0;

        if (adminCheckBox.isChecked()) checkedCount++;
        if (entrantCheckBox.isChecked()) checkedCount++;
        if (organizerCheckBox.isChecked()) checkedCount++;

        return checkedCount == 1;
    }
}