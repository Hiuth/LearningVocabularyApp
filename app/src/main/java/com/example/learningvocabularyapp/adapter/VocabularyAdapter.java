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
    private OnVocabularyCountChangeListener countChangeListener;

    public interface OnVocabularyCountChangeListener {
        void onVocabularyCountChanged(int newCount);
    }

    public VocabularyAdapter(List<Vocabulary> vocabularyList) {
        this.vocabularyList = vocabularyList;
    }

    public void setOnVocabularyCountChangeListener(OnVocabularyCountChangeListener listener) {
        this.countChangeListener = listener;
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
        
        // Hiển thị số thứ tự (position + 1)
        holder.orderNumber.setText(String.valueOf(position + 1));
        
        holder.wordText.setText(vocabulary.getWord());
        holder.wordTranslation.setText(vocabulary.getMeaning());

        // Sự kiện xóa từ vựng - Custom dialog
        holder.deleteWordButton.setOnClickListener(v -> {
            showDeleteVocabularyDialog(holder.itemView.getContext(), vocabulary, position);
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

    private void showDeleteVocabularyDialog(Context context, Vocabulary vocabulary, int position) {
        // Tạo custom dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_delete_vocabulary, null);
        builder.setView(dialogView);
        
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        
        // Set word information
        TextView wordToDelete = dialogView.findViewById(R.id.word_to_delete);
        TextView meaningToDelete = dialogView.findViewById(R.id.meaning_to_delete);
        
        wordToDelete.setText(vocabulary.getWord());
        meaningToDelete.setText(vocabulary.getMeaning());
        
        // Cancel button
        dialogView.findViewById(R.id.btn_cancel_delete).setOnClickListener(v -> dialog.dismiss());
        
        // Confirm delete button
        dialogView.findViewById(R.id.btn_confirm_delete).setOnClickListener(v -> {
            dialog.dismiss();
            
            // Xóa từ vựng khỏi database
            com.example.learningvocabularyapp.database.vocabularyAppDatabase db =
                    new com.example.learningvocabularyapp.database.vocabularyAppDatabase(context);
            db.deleteVocabulary(vocabulary.getId());

            // Xóa khỏi danh sách và cập nhật RecyclerView
            vocabularyList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, vocabularyList.size());

            // Thông báo thay đổi số lượng
            if (countChangeListener != null) {
                countChangeListener.onVocabularyCountChanged(vocabularyList.size());
            }

            Toast.makeText(context, "✅ Đã xóa từ vựng \"" + vocabulary.getWord() + "\"", Toast.LENGTH_SHORT).show();
        });
        
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return vocabularyList != null ? vocabularyList.size() : 0;
    }

    public static class VocabularyViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, wordText, wordTranslation;
        ImageView editWordButton, deleteWordButton;

        public VocabularyViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            wordText = itemView.findViewById(R.id.word_text);
            wordTranslation = itemView.findViewById(R.id.word_translation);
            editWordButton = itemView.findViewById(R.id.edit_word_button);
            deleteWordButton = itemView.findViewById(R.id.delete_word_button);
        }
    }
}