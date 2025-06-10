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

    private final ActivityResultLauncher<String> galleryLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    String mimeType = getContentResolver().getType(uri);
                    if (mimeType != null && mimeType.equals("image/webp")) {
                        Toast.makeText(this, "Ảnh WebP không được hỗ trợ. Vui lòng chọn PNG hoặc JPG.", Toast.LENGTH_SHORT).show();
                        return; // Không xử lý ảnh WebP
                    }

                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        setImageData(bitmap); // ✅ xử lý ảnh hợp lệ
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi xử lý ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),result ->{
                if(result.getResultCode() == RESULT_OK && result.getData() != null){ //kiểm tra kết quả ảnh lấy từ camera về
                    Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data"); // lấy dữ liệu từ Intent (intent chứa data của ảnh vừa chụp) và ép kiểu về bitmap
                    setImageData(bitmap);
                }
            });

    private void launchCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }

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
            currentImageType = ImageType.PROJECT_IMAGE;
            galleryLauncher.launch("image/*");
        });

        findViewById(R.id.btn_cover_camera).setOnClickListener(v -> {
            currentImageType = ImageType.PROJECT_IMAGE;
            launchCamera();
        });

        // Meme correct
        findViewById(R.id.btn_correct_gallery).setOnClickListener(v -> {
            currentImageType = ImageType.CORRECT_IMAGE;
            galleryLauncher.launch("image/*");
        });

        findViewById(R.id.btn_correct_camera).setOnClickListener(v -> {
            currentImageType = ImageType.CORRECT_IMAGE;
            launchCamera();
        });

        // Meme wrong
        findViewById(R.id.btn_wrong_gallery).setOnClickListener(v -> {
            currentImageType = ImageType.WRONG_IMAGE;
            galleryLauncher.launch("image/*");
        });

        findViewById(R.id.btn_wrong_camera).setOnClickListener(v -> {
            currentImageType = ImageType.WRONG_IMAGE;
            launchCamera();
        });

        btnSave.setOnClickListener(v -> saveProjectToDatabase());

        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    private void saveProjectToDatabase(){
        String name = projectName.getText().toString().trim();
        String learningLanguage = language.getText().toString().trim();

        if(name.isEmpty() || learningLanguage.isEmpty()){
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }
        Project project = new Project(name, learningLanguage,projectImageBytes,correctImageBytes,wrongImageBytes);
        appDatabase = new vocabularyAppDatabase(this);
        appDatabase.createProject(project);
    }
}
