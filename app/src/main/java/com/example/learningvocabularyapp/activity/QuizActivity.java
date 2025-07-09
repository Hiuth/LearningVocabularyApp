package com.example.learningvocabularyapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.model.Project;
import com.example.learningvocabularyapp.model.Vocabulary;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private List<Question> questions;
    private int currentIndex = 0;
    private int score = 0;
    private String projectLanguage = "English";
    private String quizMode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Nhận dữ liệu từ Intent
        getIntentData();
        
        // Khởi tạo views và setup
        initViews();
        setupQuiz();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String projectName = intent.getStringExtra("PROJECT_NAME");
        projectLanguage = intent.getStringExtra("LEARNING_LANGUAGE");
        String quizModeCode = intent.getStringExtra("QUIZ_MODE");
        
        // Set quiz mode display text
        switch (quizModeCode) {
            case "VN_TO_FOREIGN":
                quizMode = "Vietnamese → " + projectLanguage;
                break;
            case "FOREIGN_TO_VN":
                quizMode = projectLanguage + " → Vietnamese";
                break;
            case "MIXED":
                quizMode = "🔄 Trộn cả hai hướng";
                break;
            default:
                quizMode = "Mixed Mode";
        }
        
        // Set title and mode
        TextView tvQuizTitle = findViewById(R.id.tv_quiz_title);
        TextView tvQuizMode = findViewById(R.id.tv_quiz_mode);
        
        if (projectName != null) {
            tvQuizTitle.setText("📚 " + projectName);
        }
        tvQuizMode.setText(quizMode);
    }

    private void initViews() {
        // Setup back button với dialog xác nhận
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> showExitQuizDialog());
        
        // Setup submit button
        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> checkAnswer());
    }

    private void setupQuiz() {
        vocabularyAppDatabase db = new vocabularyAppDatabase(this);
        int projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        
        // Lấy danh sách từ vựng
        List<Vocabulary> vocabList = db.getVocabularyByProjectId(projectId);
        
        // Sinh danh sách câu hỏi dựa trên mode
        String quizModeCode = getIntent().getStringExtra("QUIZ_MODE");
        questions = generateQuestions(vocabList, quizModeCode);
        
        // Hiển thị câu hỏi đầu tiên
        showQuestion();
    }

    private void showExitQuizDialog() {
        // Tạo custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_exit_quiz, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // 🔧 SỬA PROGRESS INFO - Hiển thị thông tin chính xác
        TextView progressInfo = dialogView.findViewById(R.id.quiz_progress_info);
        int completedQuestions = currentIndex; // Số câu đã hoàn thành
        progressInfo.setText("Tiến độ hiện tại: " + completedQuestions + "/" + questions.size() + " câu đã hoàn thành (Điểm: " + score + "/" + completedQuestions + ")");
        
        // Continue button
        dialogView.findViewById(R.id.btn_continue_quiz).setOnClickListener(v -> dialog.dismiss());
        
        // Exit button
        dialogView.findViewById(R.id.btn_exit_quiz).setOnClickListener(v -> {
            dialog.dismiss();
            finish(); // Quay lại QuizModeActivity
        });
        
        dialog.show();
    }

    private List<Question> generateQuestions(List<Vocabulary> vocabList, String quizMode) {
        List<Question> questions = new ArrayList<>();
        Random random = new Random();

        for (Vocabulary vocab : vocabList) {
            switch (quizMode) {
                case "VN_TO_FOREIGN": // VN -> Project Language
                    questions.add(new Question(vocab.getMeaning(), vocab.getWord(), true));
                    break;
                case "FOREIGN_TO_VN": // Project Language -> VN
                    questions.add(new Question(vocab.getWord(), vocab.getMeaning(), false));
                    break;
                case "MIXED": // Mix
                    boolean vnToForeign = random.nextBoolean();
                    if (vnToForeign) {
                        questions.add(new Question(vocab.getMeaning(), vocab.getWord(), true));
                    } else {
                        questions.add(new Question(vocab.getWord(), vocab.getMeaning(), false));
                    }
                    break;
            }
        }
        Collections.shuffle(questions);
        return questions;
    }

    private void showQuestion() {
        if (currentIndex >= questions.size()) {
            // Kết thúc quiz, chuyển sang QuizCompleteActivity
            Intent intent = new Intent(this, QuizCompleteActivity.class);
            intent.putExtra("SCORE", score);
            intent.putExtra("TOTAL", questions.size());
            intent.putExtra("PROJECT_NAME", getIntent().getStringExtra("PROJECT_NAME"));
            intent.putExtra("QUIZ_MODE", quizMode);
            startActivity(intent);
            finish();
            return;
        }
        
        Question q = questions.get(currentIndex);

        // 🔧 SỬA PROGRESS - Hiển thị câu hỏi hiện tại
        TextView tvQuestionProgress = findViewById(R.id.tv_question_progress);
        tvQuestionProgress.setText("📝 Câu hỏi " + (currentIndex + 1) + "/" + questions.size());

        // 🔧 SỬA PROGRESS BAR - Hiển thị tiến độ các câu đã hoàn thành
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(questions.size());
        progressBar.setProgress(currentIndex); // currentIndex = số câu đã hoàn thành

        // 🔧 SỬA PROGRESS PERCENTAGE - Tính phần trăm câu đã hoàn thành
        TextView tvProgressPercentage = findViewById(R.id.tv_progress_percentage);
        int percentage = (int) (((double) currentIndex / questions.size()) * 100);
        tvProgressPercentage.setText(percentage + "% hoàn thành");

        // Update question content
        TextView tvDirection = findViewById(R.id.tv_translate_direction);
        TextView tvQuestionWord = findViewById(R.id.tv_question_word);
        EditText etAnswer = findViewById(R.id.et_answer);
        TextView tvScore = findViewById(R.id.tv_score);

        if (q.isVnToForeign) {
            tvDirection.setText("🌍 Translate to " + projectLanguage);
        } else {
            tvDirection.setText("🌍 Translate to Vietnamese");
        }
        
        tvQuestionWord.setText(q.questionText);
        etAnswer.setText("");
        etAnswer.requestFocus();
        
        // 🔧 SỬA SCORE DISPLAY - Hiển thị điểm của các câu đã hoàn thành
        tvScore.setText("🏆 " + score + "/" + questions.size());
    }

    private void checkAnswer() {
        EditText etAnswer = findViewById(R.id.et_answer);
        String userAnswer = etAnswer.getText().toString().trim();
        
        if (userAnswer.isEmpty()) {
            Toast.makeText(this, "❌ Vui lòng nhập câu trả lời", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Question q = questions.get(currentIndex);
        boolean isCorrect = userAnswer.equalsIgnoreCase(q.answerText);

        // 🔧 UPDATE SCORE - Cập nhật điểm ngay khi trả lời
        if (isCorrect) {
            score++;
        }

        // Lấy project images
        int projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        vocabularyAppDatabase db = new vocabularyAppDatabase(this);
        Project project = db.getProjectById(projectId);
        byte[] correctImageBytes = null;
        byte[] wrongImageBytes = null;
        if (project != null) {
            correctImageBytes = project.getCorrectImage();
            wrongImageBytes = project.getWrongImage();
        }
        byte[] memeBytes = isCorrect ? correctImageBytes : wrongImageBytes;

        Intent intent = new Intent(this, ResultQuizActivity.class);
        intent.putExtra("IS_CORRECT", isCorrect);
        intent.putExtra("CORRECT_ANSWER", q.answerText);
        intent.putExtra("USER_ANSWER", userAnswer);
        intent.putExtra("CURRENT_INDEX", currentIndex);
        intent.putExtra("TOTAL", questions.size());
        intent.putExtra("SCORE", score); // Score đã được update
        intent.putExtra("PROJECT_NAME", getIntent().getStringExtra("PROJECT_NAME"));
        intent.putExtra("MEME_IMAGE", memeBytes);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                // 🔧 UPDATE INDEX - Chuyển sang câu tiếp theo
                currentIndex++;
                showQuestion();
            } else if (resultCode == RESULT_CANCELED && data != null) {
                boolean exitQuiz = data.getBooleanExtra("EXIT_QUIZ", false);
                if (exitQuiz) {
                    // User chose to exit from ResultActivity
                    finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Override back button để hiển thị dialog
        showExitQuizDialog();
    }

    // Inner class cho câu hỏi
    public static class Question {
        public String questionText;
        public String answerText;
        public boolean isVnToForeign;

        public Question(String questionText, String answerText, boolean isVnToForeign) {
            this.questionText = questionText;
            this.answerText = answerText;
            this.isVnToForeign = isVnToForeign;
        }
    }
}