package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningvocabularyapp.R;

public class QuizCompleteActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_complete);

        int score = getIntent().getIntExtra("SCORE", 0);
        int total = getIntent().getIntExtra("TOTAL", 0);

        TextView tvPercent = findViewById(R.id.tv_percent);
        TextView tvScore = findViewById(R.id.tv_score);
        TextView tvScoreDetail = findViewById(R.id.tv_score_detail);

        int percent = (total > 0) ? (score * 100 / total) : 0;
        tvPercent.setText(percent + "%");
        tvScore.setText("Your Score");
        tvScoreDetail.setText(score + " out of " + total + " correct");

        Button btnBackHome = findViewById(R.id.btn_back_home);
        btnBackHome.setOnClickListener(v -> {
            // Quay về MainActivity, xóa stack
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}