package com.example.learningvocabularyapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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

    private EditText projectName,language;;
    private ImageView projectImage, correctImage, wrongImage;
    private Button btnSave;
    private byte[] projectImageBytes = null;
    private byte[] correctImageBytes = null;
    private byte[] wrongImageBytes = null;
    private vocabularyAppDatabase appDatabase;
    private enum ImageType{
        PROJECT_IMAGE,
        CORRECT_IMAGE,
        WRONG_IMAGE
    }
    private ImageType currentImageType = null;

    private void checkPermission(){
        List<String> permission_need = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){ //PERMISSION_GRANTED = quyền đã được cấp
            permission_need.add(Manifest.permission.CAMERA);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){ //PERMISSION_GRANTED = quyền đã được cấp
            permission_need.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(!permission_need.isEmpty()){
            ActivityCompat.requestPermissions(this,permission_need.toArray(new String[0]),100);
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
    private void setImageData(Bitmap bitmap){ //bitmap chuyên dùng để xử lý ảnh
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //Bitmap được chuyển thành dữ liệu PNG và ghi vào stream
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] imageData = stream.toByteArray();
        switch (currentImageType){
            case PROJECT_IMAGE:
                projectImageBytes = imageData; //Dữ liệu này sẽ được lưu vào database
                projectImage.setVisibility(ImageView.VISIBLE); //hiển thị thẻ image (ban đầu đang bị ẩn)
                projectImage.setImageBitmap(bitmap); //Set ảnh cho ImageView để hiển thị preview
                break;
            case CORRECT_IMAGE:
                correctImageBytes=imageData;
                correctImage.setVisibility(ImageView.VISIBLE);
                correctImage.setImageBitmap(bitmap);
                break;
            case WRONG_IMAGE:
                wrongImageBytes=imageData;
                wrongImage.setVisibility(ImageView.VISIBLE);
                wrongImage.setImageBitmap(bitmap);
                break;
        }
    }

    // Launcher riêng cho từng loại ảnh
    private final ActivityResultLauncher<String> projectGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) handleImageFromUri(uri, ImageType.PROJECT_IMAGE);
            });

    private final ActivityResultLauncher<String> correctGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) handleImageFromUri(uri, ImageType.CORRECT_IMAGE);
            });

    private final ActivityResultLauncher<String> wrongGalleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) handleImageFromUri(uri, ImageType.WRONG_IMAGE);
            });

    // Camera launcher dùng chung, truyền loại ảnh qua biến tạm
    private ImageType cameraImageType = null;
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if(result.getResultCode() == RESULT_OK && result.getData() != null){
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                    if (cameraImageType != null) {
                        setImageDataWithType(bitmap, cameraImageType);
                    }
                }
            });

    private void handleImageFromUri(android.net.Uri uri, ImageType imageType) {
        String mimeType = getContentResolver().getType(uri);
        if (mimeType != null && mimeType.equals("image/webp")) {
            Toast.makeText(this, "Ảnh WebP không được hỗ trợ. Vui lòng chọn PNG hoặc JPG.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            setImageDataWithType(bitmap, imageType);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchCamera(ImageType imageType) {
        cameraImageType = imageType;
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

    private boolean isEditMode = false;
    private int editingProjectId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        projectName = findViewById(R.id.et_project_name);
        language = findViewById(R.id.et_target_language);
        btnSave = findViewById(R.id.btn_create_project);

        projectImage = findViewById(R.id.iv_cover_preview);
        correctImage = findViewById(R.id.iv_correct_preview);
        wrongImage = findViewById(R.id.iv_wrong_preview);

        checkPermission();

        // Cover Image
        findViewById(R.id.btn_cover_gallery).setOnClickListener(v -> {
            projectGalleryLauncher.launch("image/*");
        });
        findViewById(R.id.btn_cover_camera).setOnClickListener(v -> {
            launchCamera(ImageType.PROJECT_IMAGE);
        });

        // Meme correct
        findViewById(R.id.btn_correct_gallery).setOnClickListener(v -> {
            correctGalleryLauncher.launch("image/*");
        });
        findViewById(R.id.btn_correct_camera).setOnClickListener(v -> {
            launchCamera(ImageType.CORRECT_IMAGE);
        });

        // Meme wrong
        findViewById(R.id.btn_wrong_gallery).setOnClickListener(v -> {
            wrongGalleryLauncher.launch("image/*");
        });
        findViewById(R.id.btn_wrong_camera).setOnClickListener(v -> {
            launchCamera(ImageType.WRONG_IMAGE);
        });

        TextView title = findViewById(R.id.tv_create_project_title); // id của TextView tiêu đề

        Intent intent = getIntent();
        isEditMode = intent.getBooleanExtra("IS_EDIT", false);
        if (isEditMode) {
            editingProjectId = intent.getIntExtra("PROJECT_ID", -1);
            if (title != null) title.setText("Update Project");

            // Lấy dữ liệu từ database
            appDatabase = new vocabularyAppDatabase(this);
            Project project = appDatabase.getProjectById(editingProjectId);
            if (project != null) {
                projectName.setText(project.getProjectName());
                language.setText(project.getLearningLanguage());

                byte[] projectImgBytes = project.getProjectImage();
                byte[] correctImgBytes = project.getCorrectImage();
                byte[] wrongImgBytes = project.getWrongImage();

                if (projectImgBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(projectImgBytes, 0, projectImgBytes.length);
                    projectImage.setImageBitmap(bitmap);
                    projectImage.setVisibility(ImageView.VISIBLE);
                    projectImageBytes = projectImgBytes;
                }
                if (correctImgBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(correctImgBytes, 0, correctImgBytes.length);
                    correctImage.setImageBitmap(bitmap);
                    correctImage.setVisibility(ImageView.VISIBLE);
                    correctImageBytes = correctImgBytes;
                }
                if (wrongImgBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(wrongImgBytes, 0, wrongImgBytes.length);
                    wrongImage.setImageBitmap(bitmap);
                    wrongImage.setVisibility(ImageView.VISIBLE);
                    wrongImageBytes = wrongImgBytes;
                }
            }
            btnSave.setText("Update Project");
        }

        btnSave.setOnClickListener(v -> {
            if (isEditMode) {
                updateProjectInDatabase();
            } else {
                saveProjectToDatabase();
            }
        });

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    private void saveProjectToDatabase(){
        String name = projectName.getText().toString().trim();
        String learningLanguage = language.getText().toString().trim();

        if(name.isEmpty() || learningLanguage.isEmpty()){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Project project = new Project(name, learningLanguage,correctImageBytes,wrongImageBytes,projectImageBytes);
        appDatabase = new vocabularyAppDatabase(this);
        appDatabase.createProject(project);
        finish();
    }

    private void updateProjectInDatabase() {
        String name = projectName.getText().toString().trim();
        String learningLanguage = language.getText().toString().trim();

        if(name.isEmpty() || learningLanguage.isEmpty()){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Project project = new Project(editingProjectId, name, learningLanguage, correctImageBytes, wrongImageBytes, projectImageBytes);
        appDatabase = new vocabularyAppDatabase(this);
        appDatabase.updateProject(project);
        finish();
    }

    // Sửa lại setImageData để nhận thêm tham số loại ảnh:
    private void setImageDataWithType(Bitmap bitmap, ImageType imageType){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageData = stream.toByteArray();

        // Giới hạn dung lượng ảnh tối đa 10MB
        if (imageData.length > 10 * 1024 * 1024) {
            Toast.makeText(this, "Ảnh quá lớn! Vui lòng chọn ảnh nhỏ hơn 10MB.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (imageType){
            case PROJECT_IMAGE:
                projectImageBytes = imageData;
                projectImage.setVisibility(ImageView.VISIBLE);
                projectImage.setImageBitmap(bitmap);
                break;
            case CORRECT_IMAGE:
                correctImageBytes = imageData;
                correctImage.setVisibility(ImageView.VISIBLE);
                correctImage.setImageBitmap(bitmap);
                break;
            case WRONG_IMAGE:
                wrongImageBytes = imageData;
                wrongImage.setVisibility(ImageView.VISIBLE);
                wrongImage.setImageBitmap(bitmap);
                break;
        }
    }
}
