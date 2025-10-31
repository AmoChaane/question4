package com.example.question4;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Redirect to SplashActivity as the main entry point
        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish(); // Close MainActivity so user can't go back to it
    }

}