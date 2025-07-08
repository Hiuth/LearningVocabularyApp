package com.example.learningvocabularyapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.learningvocabularyapp.R;

public class ResultQuizActivity extends AppCompatActivity {

    private boolean isCorrect;
    private String correctAnswer;
    private String userAnswer;
    private int currentIndex;
    private int total;
    private int score;
    private String projectName;
    private byte[] memeImageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_quiz);

        // Nhận dữ liệu từ Intent
        getIntentData();
        
        // Khởi tạo views
        initViews();
        
        // Setup UI
        setupUI();
        
        // Setup click listeners
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        isCorrect = intent.getBooleanExtra("IS_CORRECT", false);
        correctAnswer = intent.getStringExtra("CORRECT_ANSWER");
        userAnswer = intent.getStringExtra("USER_ANSWER");
        currentIndex = intent.getIntExtra("CURRENT_INDEX", 0);
        total = intent.getIntExtra("TOTAL", 0);
        score = intent.getIntExtra("SCORE", 0);
        projectName = intent.getStringExtra("PROJECT_NAME");
        memeImageBytes = intent.getByteArrayExtra("MEME_IMAGE");
    }

    private void initViews() {
        // Setup back button với dialog xác nhận
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> showExitQuizDialog());
        
        // Setup next button
        AppCompatButton btnNext = findViewById(R.id.btn_next_question);
        btnNext.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }

    private void setupUI() {
        // Set title and progress
        TextView tvQuizTitle = findViewById(R.id.tv_quiz_title);
        TextView tvQuestionProgress = findViewById(R.id.tv_question_progress);
        TextView tvScore = findViewById(R.id.tv_score);
        TextView tvProgressPercentage = findViewById(R.id.tv_progress_percentage);
        ProgressBar progressBar = findViewById(R.id.progress_bar);

        tvQuizTitle.setText("📚 " + projectName);
        tvQuestionProgress.setText("📝 Câu hỏi " + (currentIndex + 1) + "/" + total);
        tvScore.setText("🏆 Điểm: " + score + "/" + (currentIndex + 1));
        
        int percentage = (int) (((double) (currentIndex + 1) / total) * 100);
        tvProgressPercentage.setText(percentage + "%");
        progressBar.setMax(total);
        progressBar.setProgress(currentIndex + 1);

        // Set result content
        setupResultContent();
        
        // Set meme image
        setupMemeImage();
    }

    private void setupResultContent() {
        TextView tvResult = findViewById(R.id.tv_result);
        TextView tvUserAnswer = findViewById(R.id.tv_user_answer);
        TextView tvCorrectAnswer = findViewById(R.id.tv_correct_answer);

        tvUserAnswer.setText("Câu trả lời của bạn: " + userAnswer);

        if (isCorrect) {
            // Correct answer
            tvResult.setText("✅ Chính xác!");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvCorrectAnswer.setVisibility(View.GONE);
        } else {
            // Wrong answer
            tvResult.setText("❌ Sai rồi!");
            tvResult.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            tvCorrectAnswer.setText("Đáp án đúng: " + correctAnswer);
            tvCorrectAnswer.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            tvCorrectAnswer.setVisibility(View.VISIBLE);
        }
    }

    private void setupMemeImage() {
        ImageView imgMeme = findViewById(R.id.img_meme);
        
        if (memeImageBytes != null && memeImageBytes.length > 0) {
            // Có meme image -> hiển thị meme
            Bitmap bitmap = BitmapFactory.decodeByteArray(memeImageBytes, 0, memeImageBytes.length);
            
            // Set image với aspect ratio tự nhiên
            imgMeme.setImageBitmap(bitmap);
            imgMeme.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imgMeme.setAdjustViewBounds(true);
            
        } else {
            // Không có meme image -> hiển thị placeholder theo kết quả
            if (isCorrect) {
                // Đúng -> hiển thị placeholder đúng
                imgMeme.setImageResource(R.drawable.ic_meme_correct_placeholder);
            } else {
                // Sai -> hiển thị placeholder sai
                imgMeme.setImageResource(R.drawable.ic_meme_wrong_placeholder);
            }
            
            imgMeme.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            
            // Set minimum height cho placeholder
            imgMeme.setMinimumHeight(200);
        }
    }

    private void setupClickListeners() {
        AppCompatButton btnNext = findViewById(R.id.btn_next_question);
        
        // Change button text if it's the last question
        if (currentIndex >= total - 1) {
            btnNext.setText("🏁 Xem kết quả");
        } else {
            btnNext.setText("🚀 Câu tiếp theo");
        }
    }

    private void showExitQuizDialog() {
        // Tạo custom dialog giống QuizActivity
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exit_quiz, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set progress info
        TextView progressInfo = dialogView.findViewById(R.id.quiz_progress_info);
        progressInfo.setText("Tiến độ hiện tại: " + (currentIndex + 1) + "/" + total + " câu (Điểm: " + score + ")");
        
        // Continue button
        dialogView.findViewById(R.id.btn_continue_quiz).setOnClickListener(v -> dialog.dismiss());
        
        // Exit button
        dialogView.findViewById(R.id.btn_exit_quiz).setOnClickListener(v -> {
            dialog.dismiss();
            // Finish this activity and QuizActivity to go back to QuizModeActivity
            Intent intent = new Intent();
            intent.putExtra("EXIT_QUIZ", true);
            setResult(RESULT_CANCELED, intent);
            finish();
        });
        
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        // Override back button để hiển thị dialog
        showExitQuizDialog();
    }
}
