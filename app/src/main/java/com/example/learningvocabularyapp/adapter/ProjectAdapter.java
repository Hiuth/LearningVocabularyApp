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
import com.example.learningvocabularyapp.activity.QuizActivity;
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(project.getProjectName());

        String[] options = {"Edit Project", "Delete Project", "Start Quiz"};

        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0: // Edit Project
                    Intent editIntent = new Intent(context, CreateProjectActivity.class);
                    editIntent.putExtra("IS_EDIT", true);
                    editIntent.putExtra("PROJECT_ID", project.getId());
                    context.startActivity(editIntent);
                    break;

                case 1: // Delete Project
                    showDeleteConfirmation(project, position);
                    break;

                case 2: // Start Quiz
                    // Check if project has vocabulary first
                    List<Vocabulary> vocabularies = db.getVocabularyByProjectId(project.getId());
                    if (vocabularies == null || vocabularies.isEmpty()) {
                        Toast.makeText(context, "Project has no vocabulary words for quiz", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent quizIntent = new Intent(context, QuizActivity.class);
                        quizIntent.putExtra("PROJECT_ID", project.getId());
                        quizIntent.putExtra("PROJECT_NAME", project.getProjectName());
                        context.startActivity(quizIntent);
                    }
                    break;
            }
        });

        builder.show();
    }

    private void showDeleteConfirmation(Project project, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Project");
        builder.setMessage("Are you sure you want to delete \"" + project.getProjectName() + "\"?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            db.deleteProject(project.getId());
            projectList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Project deleted", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
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
