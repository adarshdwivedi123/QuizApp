package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private Button startBtn,bookmarkbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn=findViewById(R.id.start_btn);
        bookmarkbtn=findViewById(R.id.bookmarks_btn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent=new Intent(MainActivity.this, CategoriesActivity.class);
                startActivity(categoryIntent);
            }
        });


        bookmarkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookmarksIntent=new Intent(MainActivity.this, BookmarksActivity.class);
                startActivity(bookmarksIntent);
            }
        });
    }
}