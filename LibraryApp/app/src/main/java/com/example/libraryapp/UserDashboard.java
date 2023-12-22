package com.example.libraryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserDashboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboard);

        Button issueButton = findViewById(R.id.issueButton);
        Button  logoutButton = findViewById(R.id.logoutButton);
        TextView userTextView = findViewById(R.id.userTextView);

        // issue Button event
        issueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent issueIntent = new Intent(UserDashboard.this, IssueBookActivity.class);
                startActivity(issueIntent);
            }
        });



        // logout event
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent logoutIntent = new Intent(UserDashboard.this, MainActivity.class);
                startActivity(logoutIntent);
                Toast.makeText(UserDashboard.this, "Successfully logged out", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Get the current Firebase user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        userTextView.setText("Welcome to, \n" + getUserEmail(currentUser));


    }

    // logged in user info.
    private String getUserEmail(FirebaseUser currentUser) {
        if (currentUser != null) {
            // signedin user email:
            return currentUser.getEmail();
        }
        else {
            return "jay@admin.com";
        }
    }


    @Override
    public void onBackPressed() {
        // Disable the back button (do nothing)
    }
}