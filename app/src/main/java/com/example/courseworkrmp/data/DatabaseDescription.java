// Класс описывает имя таблицы и имена столбцов базы данных, а также
// содержит другую информацию, необходимую для ContentProvider
package com.example.courseworkrmp.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class DatabaseDescription {
    // Имя ContentProvider: обычно совпадает с именем пакета
    public static final String AUTHORITY =
            "com.example.courseworkrmp.data";

    // Базовый URI для взаимодействия с ContentProvider
    private static final Uri BASE_BOOK_URI =
            Uri.parse("content://" + AUTHORITY);

    // Вложенный класс, определяющий содержимое таблицы books
    public static final class Book implements BaseColumns {
        public static final String TABLE_NAME = "books"; // Имя таблицы
        // Объект Uri для таблицы books
        public static final Uri BOOK_URI =
                BASE_BOOK_URI.buildUpon().appendPath(TABLE_NAME).build();

        // Имена столбцов таблицы
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_GENRE = "genre";
        public static final String COLUMN_WHERE_BOOK = "whereBook";
        public static final String COLUMN_CHAPTER = "chapter";
        public static final String COLUMN_RACK = "rack";
        public static final String COLUMN_SHELF = "shelf";
        public static final String COLUMN_CENTURY = "century";
        public static final String COLUMN_WHO_HAS_BOOK = "whoHasBook";
        public static final String COLUMN_IS_READ = "isRead";
        public static final String COLUMN_PHOTO = "Photo";
        public static final String COLUMN_ELECT_VERSION = "electVersion";
        public static final String COLUMN_FAVOURITE = "isFavourite";

        // Создание Uri для конкретной книги
        public static Uri buildBookUri(long id) {
            return ContentUris.withAppendedId(BOOK_URI, id);
        }
    }

}
