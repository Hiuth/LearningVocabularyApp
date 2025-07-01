package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningvocabularyapp.R;

public class ResultQuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_quiz);

        // Nhận dữ liệu từ QuizActivity
        Intent intent = getIntent();
        boolean isCorrect = intent.getBooleanExtra("IS_CORRECT", false);
        String correctAnswer = intent.getStringExtra("CORRECT_ANSWER");
        int currentIndex = intent.getIntExtra("CURRENT_INDEX", 0);
        int total = intent.getIntExtra("TOTAL", 0);
        int score = intent.getIntExtra("SCORE", 0);
        String projectName = intent.getStringExtra("PROJECT_NAME");

        // Đổi tiêu đề quiz thành tên project
        TextView tvQuizTitle = findViewById(R.id.tv_quiz_title);
        if (projectName != null) {
            tvQuizTitle.setText(projectName);
        }

        // Hiển thị số câu hỏi
        TextView tvQuestionProgress = findViewById(R.id.tv_question_progress);
        tvQuestionProgress.setText("Câu hỏi " + (currentIndex + 1) + "/" + total);

        // Hiển thị điểm số
        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText("Score: " + score + "/" + total);

        // Hiển thị icon và thông báo đúng/sai
        ImageView imgResult = findViewById(R.id.img_result_icon);
        TextView tvResult = findViewById(R.id.tv_result);
        TextView tvCorrectAnswer = findViewById(R.id.tv_correct_answer);

        if (isCorrect) {
            imgResult.setImageResource(R.drawable.ic_correct); // icon đúng
            tvResult.setText("Correct!");
            tvResult.setTextColor(0xFF22B14C); // xanh lá
            tvCorrectAnswer.setVisibility(TextView.GONE);
        } else {
            imgResult.setImageResource(R.drawable.ic_incorrect); // icon sai
            tvResult.setText("Incorrect!");
            tvResult.setTextColor(0xFFE53935); // đỏ
            tvCorrectAnswer.setText("The correct answer is: " + correctAnswer);
            tvCorrectAnswer.setVisibility(TextView.VISIBLE);
        }

        // Nút Next Question
        Button btnNext = findViewById(R.id.btn_next_question);
        btnNext.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        // Nút back (nếu muốn)
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        // ProgressBar
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(total);
        progressBar.setProgress(currentIndex + 1);

        ImageView imgMeme = findViewById(R.id.img_meme);
        byte[] memeBytes = getIntent().getByteArrayExtra("MEME_IMAGE");
        if (memeBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(memeBytes, 0, memeBytes.length);
            imgMeme.setImageBitmap(bitmap);
        }
    }
}
