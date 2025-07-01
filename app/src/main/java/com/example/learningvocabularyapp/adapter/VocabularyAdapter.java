package com.example.learningvocabularyapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.model.Vocabulary;

import java.util.List;

public class VocabularyAdapter extends RecyclerView.Adapter<VocabularyAdapter.VocabularyViewHolder> {

    private List<Vocabulary> vocabularyList;

    public VocabularyAdapter(List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
    }

    public void setVocabularyList(List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VocabularyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vocabulary, parent, false);
        return new VocabularyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VocabularyViewHolder holder, int position) {
        Vocabulary vocabulary = vocabularyList.get(position);
        holder.wordText.setText(vocabulary.getWord());
        holder.wordTranslation.setText(vocabulary.getMeaning());

        // Sự kiện xóa từ vựng
        holder.deleteWordButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            new AlertDialog.Builder(context)
                    .setTitle("Delete Vocabulary")
                    .setMessage("Are you sure you want to delete this word?")
                    .setIcon(R.drawable.ic_delete)
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Xóa từ vựng khỏi database
                        com.example.learningvocabularyapp.database.vocabularyAppDatabase db =
                                new com.example.learningvocabularyapp.database.vocabularyAppDatabase(context);
                        db.deleteVocabulary(vocabulary.getId());

                        // Xóa khỏi danh sách và cập nhật RecyclerView
                        vocabularyList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, vocabularyList.size());

                        Toast.makeText(context, "Word deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Sự kiện sửa từ vựng
        holder.editWordButton.setOnClickListener(v -> {
            Context context = holder.itemView.getContext();
            if (context instanceof com.example.learningvocabularyapp.activity.EditVocabularyActivity) {
                ((com.example.learningvocabularyapp.activity.EditVocabularyActivity) context)
                    .showEditWordForm(vocabulary);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vocabularyList != null ? vocabularyList.size() : 0;
    }

    public static class VocabularyViewHolder extends RecyclerView.ViewHolder {
        TextView wordText, wordTranslation;
        ImageView editWordButton, deleteWordButton;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            wordText = itemView.findViewById(R.id.word_text);
            wordTranslation = itemView.findViewById(R.id.word_translation);
            editWordButton = itemView.findViewById(R.id.edit_word_button);
            deleteWordButton = itemView.findViewById(R.id.delete_word_button);
        }
    }
}