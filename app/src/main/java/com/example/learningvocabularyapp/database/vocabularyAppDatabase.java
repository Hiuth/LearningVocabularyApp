package com.example.learningvocabularyapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.learningvocabularyapp.model.Project;

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
}
