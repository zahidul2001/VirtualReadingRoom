package com.example.virtualreadingroom;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class InsertBookActivity extends AppCompatActivity {

    EditText etBookName, etAuthorName, etGenre;
    TextView btnSelectCover, btnSelectPDF, btnUpload;

    Uri coverUri = null;
    Uri pdfUri = null;
    ImageView imgThumbnail;

    ProgressDialog progressDialog;

    static final int REQUEST_IMAGE = 101;
    static final int REQUEST_PDF = 102;

    BookDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_insert_book);

        imgThumbnail = findViewById(R.id.imgThumbnail);

        etBookName = findViewById(R.id.etBookName);
        etAuthorName = findViewById(R.id.etAuthorName);
        etGenre = findViewById(R.id.etGenre);
        btnSelectCover = findViewById(R.id.b_selectImage);
        btnSelectPDF = findViewById(R.id.btnSelectPDF);
        btnUpload = findViewById(R.id.btnUpload);

        dbHelper = new BookDatabaseHelper(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Saving...");

        btnSelectCover.setOnClickListener(v -> selectImage());
        btnSelectPDF.setOnClickListener(v -> selectPDF());
        btnUpload.setOnClickListener(v -> saveToDatabase());
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Cover Image"), REQUEST_IMAGE);
    }

    private void selectPDF() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(Intent.createChooser(intent, "Select PDF File"), REQUEST_PDF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == REQUEST_IMAGE) {
                coverUri = data.getData();
                imgThumbnail.setImageURI(coverUri);
                imgThumbnail.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Cover image selected", Toast.LENGTH_SHORT).show();
            } else if (requestCode == REQUEST_PDF) {
                pdfUri = data.getData();
                Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void saveToDatabase() {
        String name = etBookName.getText().toString().trim();
        String author = etAuthorName.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(author) || TextUtils.isEmpty(genre)
                || coverUri == null || pdfUri == null) {
            Toast.makeText(this, "Please fill all fields and select files", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        long timestamp = System.currentTimeMillis();
        String coverFileName = "cover_" + timestamp + ".jpg";
        String pdfFileName = "book_" + timestamp + ".pdf";

        String coverPath = saveFileToInternalStorage(coverUri, coverFileName);
        String pdfPath = saveFileToInternalStorage(pdfUri, pdfFileName);

        if (coverPath == null || pdfPath == null) {
            progressDialog.dismiss();
            Toast.makeText(this, "Failed to save files", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(BookDatabaseHelper.COLUMN_NAME, name);
        values.put(BookDatabaseHelper.COLUMN_AUTHOR, author);
        values.put(BookDatabaseHelper.COLUMN_GENRE, genre);
        values.put(BookDatabaseHelper.COLUMN_COVER_URI, coverPath);
        values.put(BookDatabaseHelper.COLUMN_PDF_URI, pdfPath);
        values.put(BookDatabaseHelper.COLUMN_TIMESTAMP, String.valueOf(timestamp));

        long rowId = db.insert(BookDatabaseHelper.TABLE_BOOKS, null, values);
        db.close();

        progressDialog.dismiss();

        if (rowId != -1) {
            Toast.makeText(this, "Book saved locally!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(this, "Failed to save book", Toast.LENGTH_SHORT).show();
        }
    }


    private void clearFields() {
        etBookName.setText("");
        etAuthorName.setText("");
        etGenre.setText("");

        imgThumbnail.setImageDrawable(null);
        imgThumbnail.setVisibility(View.GONE);

        coverUri = null;
        pdfUri = null;


        btnSelectCover.setText("Select Cover");
        btnSelectPDF.setText("Select PDF");
    }

    private String saveFileToInternalStorage(Uri uri, String filename) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            File outputFile = new File(getFilesDir(), filename);
            OutputStream outputStream = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return outputFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
