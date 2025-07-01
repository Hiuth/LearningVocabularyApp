package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.adapter.VocabularyAdapter;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.model.Vocabulary;

import java.util.List;

public class EditVocabularyActivity extends AppCompatActivity {

    private ImageButton backButton;
    private TextView title, subtitle, wordsCount;
    private RecyclerView recyclerViewWords;
    private vocabularyAppDatabase db;
    private int projectId;
    private String projectName, projectLanguage;

    private Vocabulary editingVocabulary = null;

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

        setupAddWordForm();
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
        // Lấy danh sách từ vựng từ database theo projectId
        List<Vocabulary> vocabularyList = db.getVocabularyByProjectId(projectId);

        // Nếu adapter chưa khởi tạo thì khởi tạo và set cho RecyclerView
        if (recyclerViewWords.getAdapter() == null) {
            VocabularyAdapter adapter = new VocabularyAdapter(vocabularyList);
            recyclerViewWords.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewWords.setAdapter(adapter);
        } else {
            // Nếu đã có adapter thì cập nhật lại dữ liệu
            VocabularyAdapter adapter = (VocabularyAdapter) recyclerViewWords.getAdapter();
            adapter.setVocabularyList(vocabularyList);
            adapter.notifyDataSetChanged();
        }

        // Hiển thị hoặc ẩn empty state
        View emptyState = findViewById(R.id.empty_state);
        if (vocabularyList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerViewWords.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerViewWords.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload vocabulary list when returning from add/edit word activity
        loadVocabularyList();
    }
    private void setupAddWordForm() {
        View addWordForm = findViewById(R.id.add_word_form);
        AppCompatButton addWordButton = findViewById(R.id.add_word_button);

        addWordButton.setOnClickListener(v -> showAddWordForm());

        Button btnCancel = addWordForm.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> hideAddWordForm());

        Button btnSave = addWordForm.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> saveNewWord());
    }

    private void showAddWordForm() {
        View addWordForm = findViewById(R.id.add_word_form);
        addWordForm.setVisibility(View.VISIBLE);

        // Đặt tên project cho label
        TextView tvWordLabel = addWordForm.findViewById(R.id.tv_word_label);
        tvWordLabel.setText(projectName);

        AppCompatButton addWordButton = findViewById(R.id.add_word_button);
        addWordButton.setAlpha(0.5f);
        addWordButton.setEnabled(false);
    }

    private void hideAddWordForm() {
        findViewById(R.id.add_word_form).setVisibility(View.GONE);
        AppCompatButton addWordButton = findViewById(R.id.add_word_button);
        addWordButton.setAlpha(1.0f); // Trả lại bình thường
        addWordButton.setEnabled(true); // Cho bấm lại
    }

    private void saveNewWord() {
        View addWordForm = findViewById(R.id.add_word_form);
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);

        String english = etEnglish.getText().toString().trim();
        String vietnamese = etVietnamese.getText().toString().trim();

        if (english.isEmpty() || vietnamese.isEmpty()) {
            if (english.isEmpty()) etEnglish.setError("Required");
            if (vietnamese.isEmpty()) etVietnamese.setError("Required");
            return;
        }
        // TODO: Lưu vào database
         db.CreateVocabulary(projectId, new Vocabulary(english, vietnamese));
         loadVocabularyList();
        etEnglish.setText("");
        etVietnamese.setText("");
        hideAddWordForm();
    }

    public void showEditWordForm(Vocabulary vocabulary) {
        View addWordForm = findViewById(R.id.add_word_form);
        addWordForm.setVisibility(View.VISIBLE);

        // Đổi tiêu đề
        TextView tvTitle = addWordForm.findViewById(R.id.tv_add_word_title);
        tvTitle.setText("Edit Word");

        // Điền dữ liệu cũ
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);
        etEnglish.setText(vocabulary.getWord());
        etVietnamese.setText(vocabulary.getMeaning());

        // Làm mờ nút Add New Word
        AppCompatButton addWordButton = findViewById(R.id.add_word_button);
        addWordButton.setAlpha(0.5f);
        addWordButton.setEnabled(false);

        // Lưu lại từ vựng đang sửa
        editingVocabulary = vocabulary;

        // Đổi sự kiện nút Save
        Button btnSave = addWordForm.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(v -> saveEditedWord());
    }

    private void saveEditedWord() {
        View addWordForm = findViewById(R.id.add_word_form);
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);

        String english = etEnglish.getText().toString().trim();
        String vietnamese = etVietnamese.getText().toString().trim();

        if (english.isEmpty() || vietnamese.isEmpty()) {
            if (english.isEmpty()) etEnglish.setError("Required");
            if (vietnamese.isEmpty()) etVietnamese.setError("Required");
            return;
        }

        // Cập nhật database
        editingVocabulary.setWord(english);
        editingVocabulary.setMeaning(vietnamese);
        db.updateVocabulary(editingVocabulary);

        // Reload danh sách
        loadVocabularyList();

        // Reset form về trạng thái thêm mới
        etEnglish.setText("");
        etVietnamese.setText("");
        TextView tvTitle = addWordForm.findViewById(R.id.tv_add_word_title);
        tvTitle.setText("Add New Word");
        editingVocabulary = null;
        hideAddWordForm();
    }
}
