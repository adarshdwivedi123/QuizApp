package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookmarksActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SharedPreferences preferences;
    private int matchedQuestionPosition;
    private SharedPreferences.Editor editor;
    private Gson gson;
    public static final String FiLE_NAME="QUIZZER";
    public static final String KEY_NAME="QUESTION";
    private List<QuestionModel> bookmarksList;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);
        Toolbar toolbar=findViewById(R.id.toolsbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Bookmark");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView=findViewById(R.id.rv_bookmarks);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        //bookmarkBtn =(FloatingActionButton) this.<View>findViewById(R.id.floatingActionButton);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

        preferences=getSharedPreferences(FiLE_NAME, Context.MODE_PRIVATE);
        editor=preferences.edit();
        gson=new Gson();
        getBookmarks();
        recyclerView.setLayoutManager(layoutManager);
/*just for trial we use
        List<QuestionModel>  list=new ArrayList<>();

        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
        list.add(new QuestionModel("what is your name?","","","","","ajju",0));
*/
        BookmarksAdapter adapter=new BookmarksAdapter(bookmarksList);
        recyclerView.setAdapter(adapter);

        }
    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() ==android.R.id.home);
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }




    private void getBookmarks(){
        String json=preferences.getString(KEY_NAME,"");
        Type type=new TypeToken<List<QuestionModel>>(){}.getType();
        bookmarksList =gson.fromJson(json,type);
        if(bookmarksList==null)
        {
            bookmarksList=new ArrayList<>();
        }
    }
    // which queston is match
    //here we store our list in shared prefernces
    private void storeBookmarks()
    {
        String json=gson.toJson(bookmarksList);
        editor.putString(KEY_NAME,json);
        editor.commit();

    }



}