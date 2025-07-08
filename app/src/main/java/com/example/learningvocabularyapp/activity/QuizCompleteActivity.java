package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.activity.MainActivity;

public class QuizCompleteActivity extends AppCompatActivity {
    
    private static final String TAG = "QuizCompleteActivity";
    
    private int score;
    private int total;
    private String projectName;
    private String quizMode;
    private int projectId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_quiz_complete);
            Log.d(TAG, "Layout set successfully");

            // Get data from Intent
            getIntentData();
            
            // Setup UI
            setupUI();
            
            // Setup click listeners
            setupClickListeners();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            finish();
        }
    }

    private void getIntentData() {
        try {
            Intent intent = getIntent();
            if (intent != null) {
                score = intent.getIntExtra("SCORE", 0);
                total = intent.getIntExtra("TOTAL", 0);
                projectName = intent.getStringExtra("PROJECT_NAME");
                quizMode = intent.getStringExtra("QUIZ_MODE");
                projectId = intent.getIntExtra("PROJECT_ID", -1);
                
                Log.d(TAG, "Intent data - Score: " + score + ", Total: " + total + ", Project: " + projectName);
            } else {
                Log.w(TAG, "Intent is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting intent data: " + e.getMessage(), e);
        }
    }

    private void setupUI() {
        try {
            // Calculate percentage
            int percentage = (total > 0) ? (score * 100 / total) : 0;
            Log.d(TAG, "Calculated percentage: " + percentage + "%");
            
            // Find views safely
            TextView tvPercent = findViewById(R.id.tv_percent);
            TextView tvScore = findViewById(R.id.tv_score);
            TextView tvScoreDetail = findViewById(R.id.tv_score_detail);
            LinearLayout scoreCircle = findViewById(R.id.score_circle);
            
            if (tvPercent == null || tvScore == null || tvScoreDetail == null) {
                Log.e(TAG, "One or more TextViews are null");
                return;
            }

            // Set basic data
            tvPercent.setText(percentage + "%");
            tvScoreDetail.setText(score + " trên " + total + " câu đúng");
            
            // Set achievement message and circle color based on score
            setScoreResult(percentage, tvScore, scoreCircle);
            
            Log.d(TAG, "UI setup completed");
            
        } catch (Exception e) {
            Log.e(TAG, "Error in setupUI: " + e.getMessage(), e);
        }
    }
    
    private void setScoreResult(int percentage, TextView tvScore, LinearLayout scoreCircle) {
        try {
            if (percentage >= 90) {
                tvScore.setText("Xuất sắc! 🌟");
                setTextColorSafely(tvScore, R.color.text_color);
                setBackgroundSafely(scoreCircle, R.drawable.circle_gradient_score);
            } else if (percentage >= 80) {
                tvScore.setText("Giỏi lắm! 👏");
                setTextColorSafely(tvScore, R.color.text_color);
                setBackgroundSafely(scoreCircle, R.drawable.circle_gradient_score);
            } else if (percentage >= 70) {
                tvScore.setText("Khá tốt! 👍");
                setTextColorSafely(tvScore, R.color.text_color);
                setBackgroundSafely(scoreCircle, R.drawable.circle_gradient_score);
            } else if (percentage >= 60) {
                tvScore.setText("Ổn! 😊");
                setTextColorSafely(tvScore, R.color.text_color);
                setBackgroundSafely(scoreCircle, R.drawable.circle_gradient_score);
            } else {
                tvScore.setText("Cần cố gắng hơn! 💪");
                setTextColorSafely(tvScore, R.color.text_color);
                setBackgroundSafely(scoreCircle, R.drawable.circle_gradient_score);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting score result: " + e.getMessage(), e);
        }
    }
    
    private void setTextColorSafely(TextView textView, int colorResId) {
        try {
            if (textView != null) {
                textView.setTextColor(getResources().getColor(colorResId, getTheme()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting text color: " + e.getMessage(), e);
        }
    }
    
    private void setBackgroundSafely(LinearLayout layout, int drawableResId) {
        try {
            if (layout != null) {
                layout.setBackgroundResource(drawableResId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting background: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            // Back Home button
            AppCompatButton btnBackHome = findViewById(R.id.btn_back_home);
            if (btnBackHome != null) {
                btnBackHome.setOnClickListener(v -> {
                    try {
                        // Go back to MainActivity, clear stack
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        Log.e(TAG, "Error in back home click: " + e.getMessage(), e);
                        finish(); // Fallback to just finish this activity
                    }
                });
                Log.d(TAG, "Click listeners setup completed");
            } else {
                Log.e(TAG, "Back home button is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupClickListeners: " + e.getMessage(), e);
        }
    }
}