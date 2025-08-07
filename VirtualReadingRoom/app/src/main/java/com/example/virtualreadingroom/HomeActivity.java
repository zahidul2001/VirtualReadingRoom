package com.example.virtualreadingroom;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnLogout = findViewById(R.id.btn_logout_admin);

        Button getStartedButton = findViewById(R.id.btn_get_started);
        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookListActivity.class); // or whatever your activity is
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            // Sign out from Firebase
            FirebaseAuth.getInstance().signOut();

            // Show a logout message
            Toast.makeText(HomeActivity.this, "Logged Out Successfully!", Toast.LENGTH_SHORT).show();

            // Navigate to MainActivity and clear the back stack
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // Finish HomeActivity
            finish();
        });
    }
}
