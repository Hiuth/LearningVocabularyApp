package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningvocabularyapp.R;

public class QuizModeActivity extends AppCompatActivity {

    private int projectId;
    private String projectName, projectLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_mode);

        // Nhận dữ liệu project
        Intent intent = getIntent();
        projectId = intent.getIntExtra("PROJECT_ID", -1);
        projectName = intent.getStringExtra("PROJECT_NAME");
        projectLanguage = intent.getStringExtra("LEARNING_LANGUAGE");

        // Set tên project lên giao diện
        TextView txtProjectName = findViewById(R.id.txtProjectName);
        if (projectName != null) {
            txtProjectName.setText(projectName);
        }

        // Đổi text nút cho đúng ngôn ngữ project
        Button btnVNToForeign = findViewById(R.id.btnVNToForeign);
        Button btnForeignToVN = findViewById(R.id.btnForeignToVN);

        if (projectLanguage != null) {
            btnVNToForeign.setText("Vietnamese → " + projectLanguage);
            btnForeignToVN.setText(projectLanguage + " → Vietnamese");
        }

        // Sự kiện chọn chế độ
        btnVNToForeign.setOnClickListener(v -> startQuiz(1));
        btnForeignToVN.setOnClickListener(v -> startQuiz(2));
        findViewById(R.id.btnMixedMode).setOnClickListener(v -> startQuiz(3));

        // Sự kiện back
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void startQuiz(int mode) {
        Intent intent = new Intent(this, QuizActivity.class);
        intent.putExtra("PROJECT_ID", projectId);
        intent.putExtra("LEARNING_LANGUAGE", projectLanguage);
        intent.putExtra("PROJECT_NAME", projectName);
        intent.putExtra("QUIZ_MODE", mode);
        startActivity(intent);
    }
}