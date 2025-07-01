package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
    private String projectLanguage = "English"; // hoặc lấy từ intent nếu cần

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        String projectName = getIntent().getStringExtra("PROJECT_NAME");
        TextView tvQuizTitle = findViewById(R.id.tv_quiz_title);
        if (projectName != null) {
            tvQuizTitle.setText(projectName);
        }

        vocabularyAppDatabase db = new vocabularyAppDatabase(this);
        int projectId = getIntent().getIntExtra("PROJECT_ID", -1);
        Project project = db.getProjectById(projectId);
        byte[] correctImageBytes = null;
        byte[] wrongImageBytes = null;
        if (project != null) {
            correctImageBytes = project.getCorrectImage();
            wrongImageBytes = project.getWrongImage();
        }

        int quizMode = getIntent().getIntExtra("QUIZ_MODE", 1);

        // Lấy ngôn ngữ project nếu truyền qua intent
        if (getIntent().hasExtra("LEARNING_LANGUAGE")) {
            projectLanguage = getIntent().getStringExtra("LEARNING_LANGUAGE");
        }

        // Lấy danh sách từ vựng
        List<Vocabulary> vocabList = db.getVocabularyByProjectId(projectId);

        // Sinh danh sách câu hỏi
        questions = generateQuestions(vocabList, quizMode);

        // Hiển thị câu hỏi đầu tiên
        showQuestion();

        Button btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(v -> checkAnswer());
    }

    private List<Question> generateQuestions(List<Vocabulary> vocabList, int quizMode) {
        List<Question> questions = new ArrayList<>();
        Random random = new Random();

        for (Vocabulary vocab : vocabList) {
            switch (quizMode) {
                case 1: // VN -> Project Language
                    questions.add(new Question(vocab.getMeaning(), vocab.getWord(), true));
                    break;
                case 2: // Project Language -> VN
                    questions.add(new Question(vocab.getWord(), vocab.getMeaning(), false));
                    break;
                case 3: // Mix
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
            startActivity(intent);
            finish();
            return;
        }
        Question q = questions.get(currentIndex);

        TextView tvQuestionProgress = findViewById(R.id.tv_question_progress);
        tvQuestionProgress.setText("Câu hỏi " + (currentIndex + 1) + "/" + questions.size());

        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(questions.size());
        progressBar.setProgress(currentIndex + 1);

        TextView tvDirection = findViewById(R.id.tv_translate_direction);
        TextView tvQuestionWord = findViewById(R.id.tv_question_word);
        EditText etAnswer = findViewById(R.id.et_answer);
        TextView tvScore = findViewById(R.id.tv_score);

        if (q.isVnToForeign) {
            tvDirection.setText("Translate to " + projectLanguage);
        } else {
            tvDirection.setText("Translate to Vietnamese");
        }
        tvQuestionWord.setText(q.questionText);
        etAnswer.setText("");
        tvScore.setText("Score: " + score + "/" + currentIndex);
    }

    private void checkAnswer() {
        EditText etAnswer = findViewById(R.id.et_answer);
        String userAnswer = etAnswer.getText().toString().trim();
        Question q = questions.get(currentIndex);

        boolean isCorrect = userAnswer.equalsIgnoreCase(q.answerText);

        if (isCorrect) score++;

        // Lấy lại projectId nếu cần
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
        intent.putExtra("CURRENT_INDEX", currentIndex);
        intent.putExtra("TOTAL", questions.size());
        intent.putExtra("SCORE", score);
        intent.putExtra("PROJECT_NAME", getIntent().getStringExtra("PROJECT_NAME"));
        intent.putExtra("MEME_IMAGE", memeBytes);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            currentIndex++;
            showQuestion();
        }
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