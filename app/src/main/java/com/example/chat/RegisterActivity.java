package com.example.chat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.room.Dao;
import androidx.room.Room;

import java.io.IOException;

public class RegisterActivity extends Activity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText verifyEditText;
    private EditText displayNameEditText;
    private ImageView profilePicImageView;
    private Button selectImageBtn;
    private Button registerButton;
    private TextView linkToLoginTextView;

    private UserDao userDao;
    private AppDB db;
    private User user;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "users")
                .allowMainThreadQueries().build();
        userDao = db.UserDao();
        // Initialize views
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        verifyEditText = findViewById(R.id.verify);
        displayNameEditText = findViewById(R.id.displayName);
        profilePicImageView = findViewById(R.id.profilePic);
        selectImageBtn = findViewById(R.id.selectImageBtn);
        registerButton = findViewById(R.id.registerButton);
        linkToLoginTextView = findViewById(R.id.linkToLogin);

        // Set click listener for the "Select Image" button
        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        // Set click listener for the "Register" button
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Set click listener for the "Click here to login" link
        linkToLoginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToLogin();
            }
        });
    }

    // Open image picker to select profile picture
    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Handle the result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                profilePicImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Register the user
    private void registerUser() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String verifyPassword = verifyEditText.getText().toString();
        String displayName = displayNameEditText.getText().toString();

        // Validate input fields
        if (username.isEmpty() || password.isEmpty() || verifyPassword.isEmpty() || displayName.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(verifyPassword)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

//        if (selectedImageUri == null) {
//            Toast.makeText(getApplicationContext(), "Please select a profile picture", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // Perform the registration logic here
        // ...

        // Display success message
        Toast.makeText(getApplicationContext(), "Registration successful", Toast.LENGTH_SHORT).show();

        //save to local db, change later*****************************:
        user = new User(username, password, displayName);
        userDao.insert(user);
        //**********************************************************


        // Navigate to login screen
        navigateToLogin();
    }

    // Navigate to the login screen
    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

