package com.example.learningvocabularyapp.activity;

import android.annotation.SuppressLint;
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
        // Khởi tạo views
        initViews();
        // Lấy dữ liệu từ intent
        getIntentData();
        // Thiết lập UI
        setupUI();
        // Thiết lập click listeners
        setupClickListeners();
        // Tải danh sách từ vựng
        loadVocabularyList();

        setupAddWordForm();
    }

    private void initViews() {
        backButton = findViewById(R.id.back_button);
        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        wordsCount = findViewById(R.id.words_count); // Thêm dòng này
        recyclerViewWords = findViewById(R.id.recyclerViewWords);

        db = new vocabularyAppDatabase(this);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        projectId = intent.getIntExtra("PROJECT_ID", -1);
        projectLanguage = intent.getStringExtra("LEARNING_LANGUAGE");
        projectName = intent.getStringExtra("PROJECT_NAME");
    }

    @SuppressLint("SetTextI18n")
    private void setupUI() {
        // Đặt tên dự án làm tiêu đề
        if (projectName != null) {
            title.setText(projectName);
        }

        if(projectLanguage != null){
            subtitle.setText("Ngôn ngữ: " + projectLanguage);
        }
        // Thiết lập RecyclerView
        recyclerViewWords.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupClickListeners() {
        // Nút quay lại
        backButton.setOnClickListener(v -> finish());
        
        // Nút bắt đầu quiz
        findViewById(R.id.start_quiz_button).setOnClickListener(v -> {
            Intent intent = new Intent(EditVocabularyActivity.this, QuizModeActivity.class);
            intent.putExtra("PROJECT_ID", projectId);
            intent.putExtra("PROJECT_NAME", projectName);
            intent.putExtra("LEARNING_LANGUAGE", projectLanguage);
            startActivity(intent);
        });
        
        // Nút thêm từ mới
        findViewById(R.id.add_word_button).setOnClickListener(v -> showAddWordForm());
    }

    private void loadVocabularyList() {
        // Lấy danh sách từ vựng từ database theo projectId
        List<Vocabulary> vocabularyList = db.getVocabularyByProjectId(projectId);

        // Cập nhật số lượng từ vựng - chỉ hiển thị số
        updateVocabularyCount(vocabularyList.size());

        // Nếu adapter chưa khởi tạo thì khởi tạo và set cho RecyclerView
        if (recyclerViewWords.getAdapter() == null) {
            VocabularyAdapter adapter = new VocabularyAdapter(vocabularyList);
            
            // Set callback để cập nhật count khi xóa
            adapter.setOnVocabularyCountChangeListener(this::updateVocabularyCount);
            
            recyclerViewWords.setLayoutManager(new LinearLayoutManager(this));
            recyclerViewWords.setAdapter(adapter);
        } else {
            // Nếu đã có adapter thì cập nhật lại dữ liệu
            VocabularyAdapter adapter = (VocabularyAdapter) recyclerViewWords.getAdapter();
            adapter.setVocabularyList(vocabularyList);
            adapter.notifyDataSetChanged();
        }

        // Hiển thị hoặc ẩn trạng thái trống
        View emptyState = findViewById(R.id.empty_state);
        if (vocabularyList.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerViewWords.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerViewWords.setVisibility(View.VISIBLE);
        }
    }

    // Method riêng để cập nhật số lượng từ vựng
    private void updateVocabularyCount(int count) {
        if (wordsCount != null) {
            wordsCount.setText(String.valueOf(count));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tải lại danh sách từ vựng khi quay lại từ activity thêm/sửa từ
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

    private void resetAddWordForm() {
        View addWordForm = findViewById(R.id.add_word_form);
        
        // Reset các EditText về trống
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);
        etEnglish.setText("");
        etVietnamese.setText("");
        
        // Clear error messages
        etEnglish.setError(null);
        etVietnamese.setError(null);
        
        // Reset labels về trạng thái thêm mới
        TextView tvWordLabel = addWordForm.findViewById(R.id.tv_word_label);
        tvWordLabel.setText("🌍 Từ vựng " + projectLanguage);
        
        // Reset header title
        TextView tvAddWordTitle = addWordForm.findViewById(R.id.tv_add_word_title);
        tvAddWordTitle.setText("✏️ Thêm từ mới");
        
        // Reset button text
        Button btnSave = addWordForm.findViewById(R.id.btn_save);
        btnSave.setText("💾 Lưu");
        
        // Reset save button action về default
        btnSave.setOnClickListener(v -> saveNewWord());
        
        // Reset editing state
        editingVocabulary = null;
        
        // Enable nút Add Word lại
        AppCompatButton addWordButton = findViewById(R.id.add_word_button);
        addWordButton.setAlpha(1.0f);
        addWordButton.setEnabled(true);
    }

    private void showAddWordForm() {
        View addWordForm = findViewById(R.id.add_word_form);
        
        // Reset form trước khi show (đề phòng còn dữ liệu cũ)
        resetAddWordForm();
        
        addWordForm.setVisibility(View.VISIBLE);

        // Đặt tên ngôn ngữ cho label
        TextView tvWordLabel = addWordForm.findViewById(R.id.tv_word_label);
        tvWordLabel.setText("🌍 Từ vựng " + projectLanguage);

        // Cập nhật tiêu đề header
        TextView tvAddWordTitle = addWordForm.findViewById(R.id.tv_add_word_title);
        tvAddWordTitle.setText("✏️ Thêm từ mới");

        AppCompatButton addWordButton = findViewById(R.id.add_word_button);
        addWordButton.setAlpha(0.5f);
        addWordButton.setEnabled(false);
    }

    private void hideAddWordForm() {
        // Reset form trước khi hide
        resetAddWordForm();
        
        // Hide form
        findViewById(R.id.add_word_form).setVisibility(View.GONE);
    }

    private void saveNewWord() {
        View addWordForm = findViewById(R.id.add_word_form);
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);

        String english = etEnglish.getText().toString().trim();
        String vietnamese = etVietnamese.getText().toString().trim();

        if (english.isEmpty() || vietnamese.isEmpty()) {
            if (english.isEmpty()) etEnglish.setError("Vui lòng nhập từ vựng");
            if (vietnamese.isEmpty()) etVietnamese.setError("Vui lòng nhập nghĩa");
            return;
        }
        
        // Lưu vào database
        db.CreateVocabulary(projectId, new Vocabulary(english, vietnamese));
        loadVocabularyList(); // Sẽ tự động cập nhật count
        
        // Reset form và hide
        resetAddWordForm();
        hideAddWordForm();
    }

    public void showEditWordForm(Vocabulary vocabulary) {
        View addWordForm = findViewById(R.id.add_word_form);
        addWordForm.setVisibility(View.VISIBLE);

        // Điền dữ liệu cũ
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);
        etEnglish.setText(vocabulary.getWord());
        etVietnamese.setText(vocabulary.getMeaning());

        // Clear error messages
        etEnglish.setError(null);
        etVietnamese.setError(null);

        // Đổi label cho chế độ sửa
        TextView tvWordLabel = addWordForm.findViewById(R.id.tv_word_label);
        tvWordLabel.setText("✏️ Sửa từ vựng " + projectLanguage);

        // Cập nhật tiêu đề header cho chế độ sửa
        TextView tvAddWordTitle = addWordForm.findViewById(R.id.tv_add_word_title);
        tvAddWordTitle.setText("✏️ Sửa từ vựng");

        // Đổi text button
        Button btnSave = addWordForm.findViewById(R.id.btn_save);
        btnSave.setText("✅ Cập nhật");

        // Làm mờ nút Thêm từ mới
        AppCompatButton addWordButton = findViewById(R.id.add_word_button);
        addWordButton.setAlpha(0.5f);
        addWordButton.setEnabled(false);

        // Lưu lại từ vựng đang sửa
        editingVocabulary = vocabulary;

        // Đổi sự kiện nút Lưu
        btnSave.setOnClickListener(v -> saveEditedWord());
    }

    private void saveEditedWord() {
        View addWordForm = findViewById(R.id.add_word_form);
        EditText etEnglish = addWordForm.findViewById(R.id.et_english_word);
        EditText etVietnamese = addWordForm.findViewById(R.id.et_vietnamese_meaning);

        String english = etEnglish.getText().toString().trim();
        String vietnamese = etVietnamese.getText().toString().trim();

        if (english.isEmpty() || vietnamese.isEmpty()) {
            if (english.isEmpty()) etEnglish.setError("Vui lòng nhập từ vựng");
            if (vietnamese.isEmpty()) etVietnamese.setError("Vui lòng nhập nghĩa");
            return;
        }

        // Cập nhật database
        editingVocabulary.setWord(english);
        editingVocabulary.setMeaning(vietnamese);
        db.updateVocabulary(editingVocabulary);

        // Tải lại danh sách - sẽ tự động cập nhật count
        loadVocabularyList();

        // Reset form và hide
        resetAddWordForm();
        hideAddWordForm();
    }
}
