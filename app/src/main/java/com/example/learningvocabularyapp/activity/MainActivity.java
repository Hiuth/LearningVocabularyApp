package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.adapter.ProjectAdapter;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.model.Project;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button createProjectButton, editWordButton;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private ProjectAdapter projectAdapter;
    private vocabularyAppDatabase db;

    @Override
    protected void onResume() {
        super.onResume();
        loadProjectList(); // reload khi quay về MainActivity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize the button using the ID from your XML
        createProjectButton = findViewById(R.id.createProjectButton);
        recyclerView = findViewById(R.id.recyclerViewProjects);
        textViewEmpty = findViewById(R.id.textViewEmpty);

        db = new vocabularyAppDatabase(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set click listener to navigate to CreateProjectActivity
        createProjectButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateProjectActivity.class);
            startActivity(intent);
        });

    }

    private void loadProjectList() {
        List<Project> projectList = db.getAllProject();

        if (projectList != null && !projectList.isEmpty()) {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);

            projectAdapter = new ProjectAdapter(this, projectList);
            recyclerView.setAdapter(projectAdapter);
        } else {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        }
    }
}
