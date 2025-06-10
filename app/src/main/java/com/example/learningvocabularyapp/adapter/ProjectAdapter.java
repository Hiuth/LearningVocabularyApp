package com.example.learningvocabularyapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.activity.EditVocabularyActivity;
import com.example.learningvocabularyapp.model.Project;

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
//        holder.buttonStartQuiz.setOnClickListener(v -> {
//            // TODO: Implement quiz functionality
//        });
//
//        // Handle edit icon click (if needed)
//        holder.editIcon.setOnClickListener(v -> {
//            // TODO: Implement edit project functionality
//        });
//
//        // Handle delete icon click (if needed)
//        holder.deleteIcon.setOnClickListener(v -> {
//            // TODO: Implement delete project functionality
//        });
        //TODO: Gắn sự kiện cho các nút Start Quiz, Manage Words, Edit, Delete nếu cần
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        ImageView projectImage,editIcon, deleteIcon;
        TextView textViewProjectName, textViewLanguage;
        Button buttonStart, buttonManageWords;

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
