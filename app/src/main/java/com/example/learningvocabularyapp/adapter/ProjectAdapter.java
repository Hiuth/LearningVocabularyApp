package com.example.learningvocabularyapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.activity.EditVocabularyActivity;
import com.example.learningvocabularyapp.activity.CreateProjectActivity;
import com.example.learningvocabularyapp.activity.QuizModeActivity;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.model.Project;
import com.example.learningvocabularyapp.model.Vocabulary;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private Context context;
    private List<Project> projectList;
    private vocabularyAppDatabase db;
    
    // Array of note colors
    private int[] noteColors = {
        R.color.note_yellow,
        R.color.note_pink,
        R.color.note_blue,
        R.color.note_green,
        R.color.note_purple,
        R.color.note_orange,
        R.color.note_mint,
        R.color.note_coral
    };

    public ProjectAdapter(Context context, List<Project> projectList) {
        this.context = context;
        this.projectList = projectList;
        this.db = new vocabularyAppDatabase(context);
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        // Set random note color
        int colorIndex = position % noteColors.length;
        holder.noteContainer.setBackgroundColor(context.getResources().getColor(noteColors[colorIndex]));

        // Set project data
        holder.textViewProjectName.setText(project.getProjectName());
        holder.textViewLanguage.setText(project.getLearningLanguage());

        // Set project image
        if (project.getProjectImage() != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(project.getProjectImage(), 0, project.getProjectImage().length);
            holder.imageViewProject.setImageBitmap(bitmap);
        }

        // Load and display vocabulary preview
        loadVocabularyPreview(holder, project.getId());

        // Set item click listener (go to EditVocabularyActivity)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditVocabularyActivity.class);
            intent.putExtra("PROJECT_ID", project.getId());
            intent.putExtra("PROJECT_NAME", project.getProjectName());
            intent.putExtra("LEARNING_LANGUAGE", project.getLearningLanguage());
            context.startActivity(intent);
        });

        // Set long click listener (show options dialog)
        holder.itemView.setOnLongClickListener(v -> {
            showOptionsDialog(project, position);
            return true;
        });
    }

    private void loadVocabularyPreview(ProjectViewHolder holder, int projectId) {
        holder.vocabularyPreview.removeAllViews();

        // Get vocabulary words from the project
        List<Vocabulary> vocabularies = db.getVocabularyByProjectId(projectId);

        if (vocabularies == null || vocabularies.isEmpty()) {
            // No vocabulary: show empty message
            holder.textViewEmptyVocabulary.setVisibility(View.VISIBLE);
        } else {
            // Has vocabulary: show vocabulary list
            holder.textViewEmptyVocabulary.setVisibility(View.GONE);

            // Display vocabulary words (limit to prevent overflow)
            int maxWords = Math.min(vocabularies.size(), 8);

            for (int i = 0; i < maxWords; i++) {
                Vocabulary vocabulary = vocabularies.get(i);
                TextView wordView = new TextView(context);
                wordView.setText("• " + vocabulary.getWord());
                wordView.setTextSize(11);
                wordView.setTextColor(context.getResources().getColor(android.R.color.black));
                wordView.setPadding(0, 2, 0, 2);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                wordView.setLayoutParams(params);
                holder.vocabularyPreview.addView(wordView);
            }

            // If there are more words than we can display, show count
            if (vocabularies.size() > maxWords) {
                TextView moreView = new TextView(context);
                moreView.setText("... và " + (vocabularies.size() - maxWords) + " từ khác");
                moreView.setTextSize(10);
                moreView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                moreView.setTypeface(null, Typeface.ITALIC);
                moreView.setPadding(0, 4, 0, 0);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                );
                moreView.setLayoutParams(params);
                holder.vocabularyPreview.addView(moreView);
            }
        }
    }

    private void showOptionsDialog(Project project, int position) {
        // Tạo custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_project_options, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set project name in title
        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        dialogTitle.setText("📚 " + project.getProjectName());
        
        // Edit option
        dialogView.findViewById(R.id.option_edit).setOnClickListener(v -> {
            dialog.dismiss();
            Intent editIntent = new Intent(context, CreateProjectActivity.class);
            editIntent.putExtra("IS_EDIT", true);
            editIntent.putExtra("PROJECT_ID", project.getId());
            context.startActivity(editIntent);
        });
        
        // Quiz option
        dialogView.findViewById(R.id.option_quiz).setOnClickListener(v -> {
            dialog.dismiss();
            // Kiểm tra xem dự án có từ vựng không
            List<Vocabulary> vocabularies = db.getVocabularyByProjectId(project.getId());
            if (vocabularies == null || vocabularies.isEmpty()) {
                Toast.makeText(context, "❌ Dự án chưa có từ vựng để kiểm tra", Toast.LENGTH_SHORT).show();
            } else {
                // Chuyển đến QuizModeActivity thay vì QuizActivity
                Intent quizModeIntent = new Intent(context, QuizModeActivity.class);
                quizModeIntent.putExtra("PROJECT_ID", project.getId());
                quizModeIntent.putExtra("PROJECT_NAME", project.getProjectName());
                quizModeIntent.putExtra("LEARNING_LANGUAGE", project.getLearningLanguage());
                context.startActivity(quizModeIntent);
            }
        });
        
        // Delete option
        dialogView.findViewById(R.id.option_delete).setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmation(project, position);
        });
        
        // Cancel button
        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }

    private void showDeleteConfirmation(Project project, int position) {
        // Tạo custom delete confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_confirmation, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set project name
        TextView projectNameView = dialogView.findViewById(R.id.project_name_to_delete);
        projectNameView.setText("\"" + project.getProjectName() + "\"");
        
        // Cancel button
        dialogView.findViewById(R.id.btn_cancel_delete).setOnClickListener(v -> dialog.dismiss());
        
        // Confirm delete button
        dialogView.findViewById(R.id.btn_confirm_delete).setOnClickListener(v -> {
            dialog.dismiss();
            
            // Xóa tất cả từ vựng trước
            List<Vocabulary> vocabularies = db.getVocabularyByProjectId(project.getId());
            if (vocabularies != null) {
                for (Vocabulary vocab : vocabularies) {
                    db.deleteVocabulary(vocab.getId());
                }
            }
            
            // Xóa dự án
            db.deleteProject(project.getId());
            projectList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, projectList.size());
            
            Toast.makeText(context, "✅ Đã xóa dự án \"" + project.getProjectName() + "\"", Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {
        LinearLayout noteContainer;
        ImageView imageViewProject;
        TextView textViewProjectName;
        TextView textViewLanguage;
        LinearLayout vocabularyPreview;
        TextView textViewEmptyVocabulary;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            noteContainer = itemView.findViewById(R.id.noteContainer);
            imageViewProject = itemView.findViewById(R.id.imageViewProject);
            textViewProjectName = itemView.findViewById(R.id.textViewProjectName);
            textViewLanguage = itemView.findViewById(R.id.textViewLanguage);
            vocabularyPreview = itemView.findViewById(R.id.vocabularyPreview);
            textViewEmptyVocabulary = itemView.findViewById(R.id.textViewEmptyVocabulary);
        }
    }
}
