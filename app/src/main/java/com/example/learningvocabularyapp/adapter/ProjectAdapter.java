package com.example.learningvocabularyapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.activity.CreateProjectActivity;
import com.example.learningvocabularyapp.activity.EditVocabularyActivity;
import com.example.learningvocabularyapp.activity.QuizActivity;
import com.example.learningvocabularyapp.activity.QuizModeActivity;
import com.example.learningvocabularyapp.model.Project;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projectList;

    public ProjectAdapter(Context context, List<Project> projectList){
        this.context=context;
        this.projectList=projectList;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent,false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {// CHẠY MỖI KHI CẦN HIỂN THỊ DATA MỚI
        Project project = projectList.get(position);
        holder.textViewProjectName.setText(project.getProjectName());
        holder.textViewLanguage.setText(project.getLearningLanguage());
        byte[] imageBytes = project.getProjectImage();
        if(imageBytes != null && imageBytes.length>0){
            try{
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.length);
                holder.projectImage.setImageBitmap(bitmap);
            }catch (Exception e){
                e.printStackTrace();
                holder.projectImage.setImageResource(R.drawable.ic_launcher_foreground); // fallback
            }

        }else{
            holder.projectImage.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.buttonManageWords.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditVocabularyActivity.class);
            // Pass project ID to EditVocabularyActivity
            intent.putExtra("PROJECT_ID", project.getId());
            intent.putExtra("LEARNING_LANGUAGE",project.getLearningLanguage());
            intent.putExtra("PROJECT_NAME", project.getProjectName());
            context.startActivity(intent);
        });

        // Handle Start Quiz button click (if needed)
        holder.buttonStart.setOnClickListener(v -> {
            // Đúng: chuyển sang QuizModeActivity để chọn chế độ quiz
            Intent intent = new Intent(context, QuizModeActivity.class);
            intent.putExtra("PROJECT_ID", project.getId());
            intent.putExtra("LEARNING_LANGUAGE", project.getLearningLanguage());
            intent.putExtra("PROJECT_NAME", project.getProjectName());
            context.startActivity(intent);
        });
//
        // Handle edit icon click (if needed)
        holder.editIcon.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateProjectActivity.class);
            intent.putExtra("PROJECT_ID", project.getId());
            intent.putExtra("IS_EDIT", true);
            context.startActivity(intent);
        });

     // Handle delete icon click (if needed)
        holder.deleteIcon.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete Project")
                    .setMessage("Are you sure you want to delete this project?\nAll vocabularies in this project will also be deleted.")
                    .setIcon(R.drawable.ic_delete) // Đặt icon xóa nếu có
                    .setPositiveButton("Delete", (dialog, which) -> {
                        vocabularyAppDatabase db = new vocabularyAppDatabase(context);
                        db.deleteProject(project.getId());

                        projectList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, projectList.size());

                        Toast.makeText(context, "Project deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            // Làm nút Delete nổi bật màu đỏ
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
        });
        //TODO: Gắn sự kiện cho các nút Start Quiz, Manage Words, Edit, Delete nếu cần
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        ImageView projectImage,editIcon, deleteIcon;
        TextView textViewProjectName, textViewLanguage;
        Button buttonStart, buttonManageWords,buttonStartQuiz;

        public ProjectViewHolder(@NonNull View itemView){
            super(itemView);
            projectImage = itemView.findViewById(R.id.imageViewProject);
            editIcon = itemView.findViewById(R.id.editIcon);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);

            textViewProjectName = itemView.findViewById(R.id.textViewProjectName);
            textViewLanguage = itemView.findViewById(R.id.textViewLanguage);

            buttonStart = itemView.findViewById(R.id.buttonStartQuiz);
            buttonManageWords = itemView.findViewById(R.id.buttonManageWords);
        }
    }
}
