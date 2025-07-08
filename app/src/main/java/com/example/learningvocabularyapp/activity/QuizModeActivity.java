package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.model.Vocabulary;

import java.util.List;

public class QuizModeActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView txtProjectName;
    private AppCompatButton btnVNToForeign, btnForeignToVN, btnMixedMode;
    
    private int projectId;
    private String projectName;
    private String learningLanguage;
    private vocabularyAppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_mode);

        // Nhận dữ liệu từ Intent
        getIntentData();
        
        // Khởi tạo database
        db = new vocabularyAppDatabase(this);
        
        // Khởi tạo views
        initViews();
        
        // Setup UI
        setupUI();
        
        // Setup click listeners
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        projectId = intent.getIntExtra("PROJECT_ID", -1);
        projectName = intent.getStringExtra("PROJECT_NAME");
        learningLanguage = intent.getStringExtra("LEARNING_LANGUAGE");
        
        if (projectId == -1 || projectName == null) {
            Toast.makeText(this, "❌ Lỗi: Không nhận được dữ liệu dự án", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        txtProjectName = findViewById(R.id.txtProjectName);
        btnVNToForeign = findViewById(R.id.btnVNToForeign);
        btnForeignToVN = findViewById(R.id.btnForeignToVN);
        btnMixedMode = findViewById(R.id.btnMixedMode);
    }

    private void setupUI() {
        // Set project name in header
        txtProjectName.setText("📚 " + projectName);
        
        // Set button texts với ngôn ngữ cụ thể
        btnVNToForeign.setText("Vietnamese → " + learningLanguage);
        btnForeignToVN.setText(learningLanguage + " → Vietnamese");
        btnMixedMode.setText("🔄 Trộn cả hai hướng");
    }

    private void setupClickListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());
        
        // VN to Foreign mode
        btnVNToForeign.setOnClickListener(v -> startQuiz("VN_TO_FOREIGN"));
        
        // Foreign to VN mode
        btnForeignToVN.setOnClickListener(v -> startQuiz("FOREIGN_TO_VN"));
        
        // Mixed mode
        btnMixedMode.setOnClickListener(v -> startQuiz("MIXED"));
    }

    private void startQuiz(String quizMode) {
        // Kiểm tra lại xem có từ vựng không
        List<Vocabulary> vocabularies = db.getVocabularyByProjectId(projectId);
        if (vocabularies == null || vocabularies.isEmpty()) {
            Toast.makeText(this, "❌ Dự án chưa có từ vựng để kiểm tra", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Chuyển đến QuizActivity với mode đã chọn
        Intent quizIntent = new Intent(this, QuizActivity.class);
        quizIntent.putExtra("PROJECT_ID", projectId);
        quizIntent.putExtra("PROJECT_NAME", projectName);
        quizIntent.putExtra("LEARNING_LANGUAGE", learningLanguage);
        quizIntent.putExtra("QUIZ_MODE", quizMode);
        startActivity(quizIntent);
    }
}