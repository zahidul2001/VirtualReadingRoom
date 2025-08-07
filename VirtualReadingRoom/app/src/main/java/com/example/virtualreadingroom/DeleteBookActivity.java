package com.example.virtualreadingroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DeleteBookActivity extends AppCompatActivity {

    private EditText etBookName, etAuthor, etGenre;
    private Button btnDeleteBook;
    private BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_book);

        etBookName = findViewById(R.id.etBookName);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        btnDeleteBook = findViewById(R.id.btnDeleteBook);

        dbHelper = new BookDatabaseHelper(this);

        btnDeleteBook.setOnClickListener(v -> {
            String name = etBookName.getText().toString().trim();
            String author = etAuthor.getText().toString().trim();
            String genre = etGenre.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(author) || TextUtils.isEmpty(genre)) {
                Toast.makeText(DeleteBookActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean deleted = dbHelper.deleteBook(name, author, genre);
            if (deleted) {
                Toast.makeText(DeleteBookActivity.this, "Book deleted successfully", Toast.LENGTH_SHORT).show();
                // Optionally clear the inputs:
                etBookName.setText("");
                etAuthor.setText("");
                etGenre.setText("");
            } else {
                Toast.makeText(DeleteBookActivity.this, "Book not found", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
