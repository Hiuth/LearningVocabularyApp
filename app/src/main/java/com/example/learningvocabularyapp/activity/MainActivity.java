package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningvocabularyapp.R;

public class MainActivity extends AppCompatActivity {

    private Button createProjectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the button using the ID from your XML
        createProjectButton = findViewById(R.id.createProjectButton);

        // Set click listener to navigate to CreateProjectActivity
        createProjectButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateProjectActivity.class);
            startActivity(intent);
        });
    }
}
