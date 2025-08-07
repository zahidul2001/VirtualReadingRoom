package com.example.virtualreadingroom;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BookListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<Book> bookList;
    BookAdapter adapter;
    TextView tvBookCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.filterList(query);
                tvBookCount.setText("Books: " + adapter.getFilteredCount());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterList(newText);
                tvBookCount.setText("Books: " + adapter.getFilteredCount());
                return true;
            }
        });


        tvBookCount = findViewById(R.id.tvBookCount);

        recyclerView = findViewById(R.id.recyclerViewBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        loadBooksFromDatabase();

        ImageView ivToggle = findViewById(R.id.ivToggleSubcategory);
        LinearLayout layoutSubcategories = findViewById(R.id.layoutSubcategories);

        ivToggle.setOnClickListener(v -> {
            if (layoutSubcategories.getVisibility() == View.GONE) {
                layoutSubcategories.setVisibility(View.VISIBLE);
            } else {
                layoutSubcategories.setVisibility(View.GONE);
            }
        });

        adapter = new BookAdapter(this, bookList, book -> {
            Intent intent = new Intent(BookListActivity.this, BookDetailActivity.class);
            intent.putExtra("bookName", book.getName());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("genre", book.getGenre());
            intent.putExtra("coverPath", book.getCoverPath());
            intent.putExtra("pdfPath", book.getPdfPath());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        adapter.updateFullList(bookList);
        tvBookCount.setText("Books: " + bookList.size());
        // Fetch ratings from Firestore
        fetchRatingsFromFirestore();
    }


    private void fetchRatingsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        for (Book book : bookList) {
            firestore.collection("book_ratings") // <- Changed this
                    .whereEqualTo("bookName", book.getName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        float total = 0;
                        int count = 0;

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            Number rating = doc.getDouble("rating");
                            if (rating != null) {
                                total += rating.floatValue();
                                count++;
                            }
                        }

                        if (count > 0) {
                            float avg = total / count;
                            book.setAverageRating(avg);
                            adapter.notifyDataSetChanged();
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error fetching ratings", e));
        }
    }


    private void loadBooksFromDatabase() {
        BookDatabaseHelper dbHelper = new BookDatabaseHelper(this);
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(
                    BookDatabaseHelper.TABLE_BOOKS,
                    null, null, null, null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(BookDatabaseHelper.COLUMN_NAME);
                int authorIndex = cursor.getColumnIndex(BookDatabaseHelper.COLUMN_AUTHOR);
                int genreIndex = cursor.getColumnIndex(BookDatabaseHelper.COLUMN_GENRE);
                int coverUriIndex = cursor.getColumnIndex(BookDatabaseHelper.COLUMN_COVER_URI);
                int pdfUriIndex = cursor.getColumnIndex(BookDatabaseHelper.COLUMN_PDF_URI);
                int timestampIndex = cursor.getColumnIndex(BookDatabaseHelper.COLUMN_TIMESTAMP);

                do {
                    String name = cursor.getString(nameIndex);
                    String author = cursor.getString(authorIndex);
                    String genre = cursor.getString(genreIndex);
                    String coverUri = cursor.getString(coverUriIndex);
                    String pdfUri = cursor.getString(pdfUriIndex);
                    String timestamp = cursor.getString(timestampIndex);

                    bookList.add(new Book(name, author, genre, coverUri, pdfUri, timestamp));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("BookListActivity", "Error loading books from DB", e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }
}
