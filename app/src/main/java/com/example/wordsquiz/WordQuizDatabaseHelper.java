package com.example.wordsquiz;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WordQuizDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "wordquiz.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_QUESTIONS = "questions";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_QUESTION = "question";
    public static final String COLUMN_ANSWER = "answer";

    private static final String CREATE_TABLE_QUESTIONS = "CREATE TABLE " + TABLE_QUESTIONS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_QUESTION + " TEXT, " +
            COLUMN_ANSWER + " TEXT)";

    public WordQuizDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUESTIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
        onCreate(db);
    }

    public long addQuestion(String question, String answer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUESTION, question);
        values.put(COLUMN_ANSWER, answer);
        long result = db.insert(TABLE_QUESTIONS, null, values);
        db.close();
        return result;
    }
}
