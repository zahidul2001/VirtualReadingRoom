package com.example.virtualreadingroom;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class Book {
    private String name;
    private String author;
    private String genre;
    private String coverPath;
    private String pdfPath;
    private String timestamp;
    private float averageRating;

    public float getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(float averageRating) {
        this.averageRating = averageRating;
    }

    public Book(String name, String author, String genre, String coverPath, String pdfPath, String timestamp) {
        this.name = name;
        this.author = author;
        this.genre = genre;
        this.coverPath = coverPath;
        this.pdfPath = pdfPath;
        this.timestamp = timestamp;
    }

    // Getters
    public String getName() { return name; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getCoverPath() { return coverPath; }
    public String getPdfPath() { return pdfPath; }
    public String getTimestamp() { return timestamp; }
}
