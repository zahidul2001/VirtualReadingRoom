package com.example.virtualreadingroom;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BookDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "virtual_reading_room.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_BOOKS = "books";

    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_GENRE = "genre";
    public static final String COLUMN_COVER_URI = "coverUri";
    public static final String COLUMN_PDF_URI = "pdfUri";
    public static final String COLUMN_TIMESTAMP = "timestamp";

    public BookDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_BOOKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_AUTHOR + " TEXT NOT NULL, " +
                COLUMN_GENRE + " TEXT NOT NULL, " +
                COLUMN_COVER_URI + " TEXT, " +
                COLUMN_PDF_URI + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT)";
        db.execSQL(createTable);
    }

    /**
     * Deletes all books from the database.
     */
    public void deleteAllBooks() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKS, null, null);
        db.close();
    }

    /**
     * Deletes a specific book by matching name, author, and genre.
     * Returns true if any rows were deleted.
     */
    public boolean deleteBook(String name, String author, String genre) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Using COLLATE NOCASE for case-insensitive match (optional)
        int rowsDeleted = db.delete(TABLE_BOOKS,
                COLUMN_NAME + "=? COLLATE NOCASE AND " +
                        COLUMN_AUTHOR + "=? COLLATE NOCASE AND " +
                        COLUMN_GENRE + "=? COLLATE NOCASE",
                new String[]{name, author, genre});

        db.close();
        return rowsDeleted > 0;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop the old table and create a new one if database version changes
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(db);
    }
}
