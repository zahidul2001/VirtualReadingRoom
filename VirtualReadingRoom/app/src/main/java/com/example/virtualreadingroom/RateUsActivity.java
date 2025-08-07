package com.example.virtualreadingroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RateUsActivity extends AppCompatActivity {

    TextView tvBookToRate;
    RatingBar ratingBar;
    EditText etComment;
    Button btnSubmit;

    FirebaseFirestore db;

    String bookName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);

        tvBookToRate = findViewById(R.id.tvBookToRate);
        ratingBar = findViewById(R.id.ratingBar);
        etComment = findViewById(R.id.etComment);
        btnSubmit = findViewById(R.id.btnSubmit);

        db = FirebaseFirestore.getInstance();

        bookName = getIntent().getStringExtra("bookName");
        tvBookToRate.setText("Rate this book: " + bookName);

        btnSubmit.setOnClickListener(v -> submitRating());
    }

    private void submitRating() {
        float rating = ratingBar.getRating();
        String comment = etComment.getText().toString().trim();

        if (rating == 0) {
            Toast.makeText(this, "Please give a rating", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to save in Firestore
        Map<String, Object> ratingData = new HashMap<>();
        ratingData.put("bookName", bookName);
        ratingData.put("rating", rating);
        ratingData.put("comment", comment);
        ratingData.put("timestamp", System.currentTimeMillis());

        // Save to Firestore collection "book_ratings"
        db.collection("book_ratings")
                .add(ratingData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(RateUsActivity.this, "Thanks for your feedback!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(RateUsActivity.this, "Failed to submit. Try again.", Toast.LENGTH_SHORT).show());
    }
}
