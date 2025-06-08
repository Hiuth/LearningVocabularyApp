package com.example.learningvocabularyapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.model.Project;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CreateProjectActivity extends AppCompatActivity {

    private EditText etProjectName, etTargetLanguage;
    private Button btnCreate;

    private ImageView ivCoverPreview, ivCorrectPreview, ivWrongPreview;

    private byte[] coverImageBytes = null;
    private byte[] memeCorrectBytes = null;
    private byte[] memeWrongBytes = null;

    private enum ImageType { COVER, MEME_CORRECT, MEME_WRONG }
    private ImageType currentImageType = null;

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        setImageData(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    setImageData(bitmap);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        etProjectName = findViewById(R.id.et_project_name);
        etTargetLanguage = findViewById(R.id.et_target_language);
        btnCreate = findViewById(R.id.btn_create_project);

        ivCoverPreview = findViewById(R.id.iv_cover_preview);
        ivCorrectPreview = findViewById(R.id.iv_correct_preview);
        ivWrongPreview = findViewById(R.id.iv_wrong_preview);

        checkPermissions();

        // Cover Image
        findViewById(R.id.btn_cover_gallery).setOnClickListener(v -> {
            currentImageType = ImageType.COVER;
            galleryLauncher.launch("image/*");
        });

        findViewById(R.id.btn_cover_camera).setOnClickListener(v -> {
            currentImageType = ImageType.COVER;
            launchCamera();
        });

        // Meme correct
        findViewById(R.id.btn_correct_gallery).setOnClickListener(v -> {
            currentImageType = ImageType.MEME_CORRECT;
            galleryLauncher.launch("image/*");
        });

        findViewById(R.id.btn_correct_camera).setOnClickListener(v -> {
            currentImageType = ImageType.MEME_CORRECT;
            launchCamera();
        });

        // Meme wrong
        findViewById(R.id.btn_wrong_gallery).setOnClickListener(v -> {
            currentImageType = ImageType.MEME_WRONG;
            galleryLauncher.launch("image/*");
        });

        findViewById(R.id.btn_wrong_camera).setOnClickListener(v -> {
            currentImageType = ImageType.MEME_WRONG;
            launchCamera();
        });

        btnCreate.setOnClickListener(v -> saveProjectToDatabase());

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private void setImageData(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageData = stream.toByteArray();

        switch (currentImageType) {
            case COVER:
                coverImageBytes = imageData;
                ivCoverPreview.setVisibility(ImageView.VISIBLE);
                ivCoverPreview.setImageBitmap(bitmap);
                break;
            case MEME_CORRECT:
                memeCorrectBytes = imageData;
                ivCorrectPreview.setVisibility(ImageView.VISIBLE);
                ivCorrectPreview.setImageBitmap(bitmap);
                break;
            case MEME_WRONG:
                memeWrongBytes = imageData;
                ivWrongPreview.setVisibility(ImageView.VISIBLE);
                ivWrongPreview.setImageBitmap(bitmap);
                break;
        }
    }

    private void saveProjectToDatabase() {
        String name = etProjectName.getText().toString().trim();
        String language = etTargetLanguage.getText().toString().trim();

        if (name.isEmpty() || language.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Project project = new Project(name, language, coverImageBytes, memeCorrectBytes, memeWrongBytes);
        vocabularyAppDatabase db = new vocabularyAppDatabase(this);
        db.createProject(project);

    }

    private void checkPermissions() {
        List<String> permissions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.CAMERA);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);

        if (!permissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission denied. Some features may not work.", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }
}
