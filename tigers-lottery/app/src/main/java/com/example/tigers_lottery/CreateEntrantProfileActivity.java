package com.example.tigers_lottery;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * This activity allows users to create a new entrant profile with details such as name, email, date of birth,
 * profile photo, and other personal information. It also supports photo selection and uploading functionalities.
 */
public class CreateEntrantProfileActivity extends AppCompatActivity {
    // Database helper instance
    private DatabaseHelper dbHelper;

    // UI components for user interaction
    private Button createAccountButton;
    private FloatingActionButton cancelButton, editProfilePhotoButton;
    private Button saveInfoButton;

    // Used to handle the result of image selection activity
    private ActivityResultLauncher<Intent> activityResultLauncher;

    // URI of the selected profile photo
    private Uri imageUri = null;

    // EditText fields for entrant's information
    private EditText firstnameEditText, lastnameEditText, emailEditText, passwordEdittext, mobileEditText, retypeEditText;

    // TextView for displaying and selecting the date of birth
    private TextView dobEditText;

    // Selected date components for DOB
    private int selectedYear, selectedMonth, selectedDay;

    /**
     * Called when the activity is first created. Initializes the UI components and sets up listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           then this Bundle contains the most recent data, otherwise it is null.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_entrant_profile_activity);

        Intent intent = getIntent();
        dbHelper = new DatabaseHelper(getApplicationContext());
        HashMap<String, Object> userData = new HashMap<>();

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ImageView imageView = findViewById(R.id.imageViewProfilePicture);
                        imageView.setImageURI(imageUri); // Display selected image
                    }
                });

        saveInfoButton = findViewById(R.id.buttonSaveInfo);
        cancelButton = findViewById(R.id.sign_up_cancel_button);
        firstnameEditText = findViewById(R.id.editUserTextFirstName);
        lastnameEditText = findViewById(R.id.editUserTextLastName);
        emailEditText = findViewById(R.id.editTextEmail);
        dobEditText = findViewById(R.id.editUserTextDOB);
        mobileEditText = findViewById(R.id.editUserTextMobile);
        editProfilePhotoButton = findViewById(R.id.sign_up_add_profile_button);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userData.put("first_name", firstnameEditText.getText().toString());
                userData.put("last_name", lastnameEditText.getText().toString());
                userData.put("email_address", emailEditText.getText().toString());
                userData.put("DOB", dobEditText.getText().toString());
                userData.put("phone_number", mobileEditText.getText().toString());

                dbHelper.addUser(userData, imageUri);

                Intent mainActivity =new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
            }
        });

        dobEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker();
            }
        });

        editProfilePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
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

    /**
     * Opens a DatePicker dialog allowing the user to select their date of birth.
     */
    private void openDatePicker() {
        // Use the current date as the default date in the picker
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Save selected day, month, and year
                    this.selectedYear = selectedYear;
                    this.selectedMonth = selectedMonth + 1; // Month is 0-indexed in Calendar
                    this.selectedDay = selectedDay;

                    // Update the TextView with the selected date in "DD/MM/YYYY" format
                    String dateText = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedMonth+1, selectedDay,  selectedYear);
                    dobEditText.setText(dateText);
                }, year, month, day);

        datePickerDialog.show();
    }


}