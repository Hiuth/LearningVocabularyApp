package com.example.learningvocabularyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learningvocabularyapp.model.Project;
import com.example.learningvocabularyapp.model.Vocabulary;

import java.util.ArrayList;
import java.util.List;

public class vocabularyAppDatabase extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "vocabulary_app.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_PROJECTS = "projects";

    private static final String TABLE_VOCABULARY = "vocabulary";

    private static final String KEY_ID = "id";

    //project column
    private static final String PROJECT_NAME = "project_name";
    private static final String LEARNING_LANGUAGE = "learning_language";
    private static final String PROJECT_IMAGE = "project_image";
    private static final String CORRECT_IMAGE = "correct_image";
    private static final String WRONG_IMAGE = "wrong_image";

    //vocabulary column
    private static final String WORD = "word";
    private static final String MEANING ="meaning";
    private static final String PROJECT_KEY= "project_key";

    public vocabularyAppDatabase(@Nullable Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
        this.context=context;
    }

    private static final String CREATE_TABLE_PROJECTS = "CREATE TABLE " + TABLE_PROJECTS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + PROJECT_NAME + " TEXT, "
            + LEARNING_LANGUAGE + " TEXT, "
            + PROJECT_IMAGE + " BLOB, "
            + CORRECT_IMAGE + " BLOB, "
            + WRONG_IMAGE + " BLOB)";


    private static final String CREATE_TABLE_VOCABULARY = "CREATE TABLE " + TABLE_VOCABULARY +"("
            +KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            +WORD +" TEXT,"
            +MEANING+" TEXT,"
            +PROJECT_KEY + " INTEGER,"
            +"FOREIGN KEY("+ PROJECT_KEY+") REFERENCES " +TABLE_PROJECTS+"("+KEY_ID +"))";

    @Override
    public void onCreate(SQLiteDatabase db) { //hàm tạo bảng
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_VOCABULARY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //hàm nâng cấp database
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCABULARY);
        onCreate(db);
    }

    //project
    public void createProject(Project project){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROJECT_NAME,project.getProjectName());
        values.put(LEARNING_LANGUAGE,project.getLearningLanguage());
        values.put(PROJECT_IMAGE,project.getProjectImage());
        values.put(CORRECT_IMAGE,project.getCorrectImage());
        values.put(WRONG_IMAGE,project.getWrongImage());
        long check = db.insert(TABLE_PROJECTS,null,values);
        if(check == -1){
            Toast.makeText(context, "Create project failed !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Create project successfully !", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    public List<Project> getAllProject(){
        List<Project> projectList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+TABLE_PROJECTS,null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
                String name  = cursor.getString(cursor.getColumnIndexOrThrow(PROJECT_NAME));
                String language = cursor.getString(cursor.getColumnIndexOrThrow(LEARNING_LANGUAGE));
                byte [] imageProject = cursor.getBlob(cursor.getColumnIndexOrThrow(PROJECT_IMAGE));
                byte [] imageCorrect = cursor.getBlob(cursor.getColumnIndexOrThrow(CORRECT_IMAGE));
                byte [] imageWrong = cursor.getBlob(cursor.getColumnIndexOrThrow(WRONG_IMAGE));

                Project project = new Project(id, name, language, imageCorrect, imageWrong, imageProject);
                projectList.add(project);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return projectList;
    }

    public Project getProjectById(int projectId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROJECTS + " WHERE " + KEY_ID + " = ?", new String[]{String.valueOf(projectId)});
        Project project = null;
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(PROJECT_NAME));
            String language = cursor.getString(cursor.getColumnIndexOrThrow(LEARNING_LANGUAGE));
            byte[] imageProject = cursor.getBlob(cursor.getColumnIndexOrThrow(PROJECT_IMAGE));
            byte[] imageCorrect = cursor.getBlob(cursor.getColumnIndexOrThrow(CORRECT_IMAGE));
            byte[] imageWrong = cursor.getBlob(cursor.getColumnIndexOrThrow(WRONG_IMAGE));
            project = new Project(projectId, name, language, imageCorrect, imageWrong, imageProject);
        }
        cursor.close();
        db.close();
    return project;
}

    public void updateProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PROJECT_NAME, project.getProjectName());
        values.put(LEARNING_LANGUAGE, project.getLearningLanguage());
        values.put(PROJECT_IMAGE, project.getProjectImage());
        values.put(CORRECT_IMAGE, project.getCorrectImage());
        values.put(WRONG_IMAGE, project.getWrongImage());
        db.update(TABLE_PROJECTS, values, KEY_ID + " = ?", new String[]{String.valueOf(project.getId())});
        db.close();
        Toast.makeText(context, "Project updated successfully!", Toast.LENGTH_SHORT).show();
    }
    
    public void deleteProject(int projectId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Xóa tất cả từ vựng thuộc project này (nếu có liên kết)
        db.delete(TABLE_VOCABULARY, PROJECT_KEY + " = ?", new String[]{String.valueOf(projectId)});
        // Xóa project
        db.delete(TABLE_PROJECTS, KEY_ID + " = ?", new String[]{String.valueOf(projectId)});
        db.close();
        Toast.makeText(context, "Project deleted successfully!", Toast.LENGTH_SHORT).show();
    }   


    //vocabulary
    public void CreateVocabulary(int projectId, Vocabulary vocabulary){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WORD,vocabulary.getWord());
        values.put(MEANING,vocabulary.getMeaning());
        values.put(PROJECT_KEY,projectId);
        long check = db.insert(TABLE_VOCABULARY,null,values);
        if(check == -1){
            Toast.makeText(context, "Create vocabulary failed !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Create vocabulary successfully !", Toast.LENGTH_SHORT).show();

        }
        db.close();
    }

    public void updateVocabulary(Vocabulary vocabulary) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WORD, vocabulary.getWord());
        values.put(MEANING, vocabulary.getMeaning());
        db.update(TABLE_VOCABULARY, values, KEY_ID + " = ?", new String[]{String.valueOf(vocabulary.getId())});
        db.close();
        Toast.makeText(context, "Vocabulary updated successfully!", Toast.LENGTH_SHORT).show();
    }

    public List<Vocabulary> getVocabularyByProjectId(int projectId){
        List<Vocabulary> vocabularyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_VOCABULARY + " WHERE " + PROJECT_KEY + " = ?",
         new String[]{String.valueOf(projectId)});
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID));
                String word = cursor.getString(cursor.getColumnIndexOrThrow(WORD));
                String meaning = cursor.getString(cursor.getColumnIndexOrThrow(MEANING));
                Vocabulary vocabulary = new Vocabulary(id, word, meaning, projectId);
                vocabularyList.add(vocabulary);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return vocabularyList;
    }
    
    public void deleteVocabulary(int vocabularyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_VOCABULARY, KEY_ID + " = ?", new String[]{String.valueOf(vocabularyId)});
        db.close();
        Toast.makeText(context, "Vocabulary deleted successfully!", Toast.LENGTH_SHORT).show();
    }
}
