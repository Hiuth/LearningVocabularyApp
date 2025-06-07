package com.example.learningvocabularyapp.activity;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.learningvocabularyapp.R;

public class CreateProjectActivity extends AppCompatActivity {

    private EditText projectName;
    private EditText language;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
    }
}
