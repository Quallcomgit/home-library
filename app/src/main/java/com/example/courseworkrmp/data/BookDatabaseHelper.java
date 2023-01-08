// Субкласс SQLiteOpenHelper, определяющий базу данных приложения
package com.example.courseworkrmp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Book.db";
    private static final int DATABASE_VERSION = 1;

    // Конструктор
    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Создание таблицы contacts при создании базы данных
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Команда SQL для создания таблицы contacts
        final String CREATE_BOOKS_TABLE =
                "CREATE TABLE " + DatabaseDescription.Book.TABLE_NAME + "(" +
                        DatabaseDescription.Book._ID + " integer primary key, " +
                        DatabaseDescription.Book.COLUMN_AUTHOR + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_TITLE + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_GENRE + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_WHERE_BOOK + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_CHAPTER + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_RACK + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_SHELF + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_CENTURY + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_WHO_HAS_BOOK + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_IS_READ + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_PHOTO + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_ELECT_VERSION + " TEXT, " +
                        DatabaseDescription.Book.COLUMN_FAVOURITE + " TEXT);";
        db.execSQL(CREATE_BOOKS_TABLE); // Создание таблицы contacts
    }

    // Обычно определяет способ обновления при изменении схемы базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }
}
