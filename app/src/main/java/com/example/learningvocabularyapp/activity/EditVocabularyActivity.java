package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;

public class EditVocabularyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView title, subtitle, wordsCount;
    private RecyclerView recyclerViewWords;
    private vocabularyAppDatabase db;
    private int projectId;
    private String projectName, projectLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_words);

        // Initialize views
        initViews();

        // Get data from intent
        getIntentData();

        // Setup UI
        setupUI();

        // Setup click listeners
        setupClickListeners();

        // Load vocabulary list
        loadVocabularyList();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
//        wordsCount = findViewById(R.id.words_count);
        recyclerViewWords = findViewById(R.id.recyclerViewWords);

        db = new vocabularyAppDatabase(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        projectId = intent.getIntExtra("PROJECT_ID", -1);
        projectLanguage = intent.getStringExtra("LEARNING_LANGUAGE");
        projectName = intent.getStringExtra("PROJECT_NAME");
    }

    private void setupUI() {
        // Set project name as title
        if (projectName != null) {
            title.setText(projectName);
        }

        if(projectLanguage!=null){
            subtitle.setText(projectLanguage);
        }
        // Setup RecyclerView
        recyclerViewWords.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        // Back button
        backButton.setOnClickListener(v -> finish());

        // Add new word button
        findViewById(R.id.add_word_button).setOnClickListener(v -> {
            // TODO: Navigate to Add Word Activity
            // Intent intent = new Intent(EditVocabularyActivity.this, AddWordActivity.class);
            // intent.putExtra("PROJECT_ID", projectId);
            // startActivity(intent);
        });
    }

    private void loadVocabularyList() {
        // TODO: Load vocabulary list from database based on projectId
        // List<Vocabulary> vocabularyList = db.getVocabularyByProjectId(projectId);

        // For now, show empty state or sample data
        // Update words count
//        wordsCount.setText("0 words");

        // Show empty state
        findViewById(R.id.empty_state).setVisibility(android.view.View.VISIBLE);
        recyclerViewWords.setVisibility(android.view.View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload vocabulary list when returning from add/edit word activity
        loadVocabularyList();
    }
}
