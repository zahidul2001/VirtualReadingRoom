package com.example.virtualreadingroom;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;

public class BookDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String pdfPath = intent.getStringExtra("pdfPath");

        if (pdfPath != null) {
            try {
                Uri pdfUri;
                if (pdfPath.startsWith("content://")) {
                    pdfUri = Uri.parse(pdfPath);
                } else {
                    File file = new File(pdfPath);
                    if (!file.exists()) {
                        Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    pdfUri = FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".provider",
                            file
                    );
                }

                Intent openPdfIntent = new Intent(Intent.ACTION_VIEW);
                openPdfIntent.setDataAndType(pdfUri, "application/pdf");
                openPdfIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(openPdfIntent);

                // Close detail activity if only opening the PDF
                finish();

            } catch (Exception e) {
                Toast.makeText(this, "Unable to open PDF", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                finish();
            }
        } else {
            Toast.makeText(this, "PDF path missing", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
