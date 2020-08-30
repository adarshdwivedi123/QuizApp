package com.example.quizapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();
        public static final String FiLE_NAME="QUIZZER";
    public static final String KEY_NAME="QUESTION";
    private TextView question,noindicator;
    private FloatingActionButton bookmarkBtn;
    private LinearLayout optionContainer;
    private Button shareBtn,nextBtn;
    private int count=0;
    private List<QuestionModel> list;
    private  int position =0;
    private String category;
    private int setNo;
    private Dialog loadingdialog;
    int score=0;
    private SharedPreferences preferences;
    private int matchedQuestionPosition;
    private SharedPreferences.Editor editor;
    private Gson gson;
    private List<QuestionModel> bookmarksList;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        optionContainer =findViewById(R.id.option_container);
        question =findViewById(R.id.question);
        noindicator =findViewById(R.id.no_indicator);
        bookmarkBtn =(FloatingActionButton) this.<View>findViewById(R.id.floatingActionButton);
        shareBtn =findViewById(R.id.share_btn);
        nextBtn =findViewById(R.id.next_btn);
        preferences=getSharedPreferences(FiLE_NAME, Context.MODE_PRIVATE);
        editor=preferences.edit();
        gson=new Gson();
        getBookmarks();

        bookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(modelMatch())
                {
                    //if question already there then we should removed
                    bookmarksList.remove(matchedQuestionPosition);
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));
                }
                else
                {
                    bookmarksList.add(list.get(position));
                    bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));
                }
            }
        });

        category=getIntent().getStringExtra("category");
        setNo=getIntent().getIntExtra("setNO",1);


        loadingdialog=new Dialog(this);
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        loadingdialog.setContentView(R.layout.loading);
        loadingdialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.rounded_corners));
        loadingdialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        loadingdialog.setCancelable(false);










        //query
        list=new ArrayList<>();
        loadingdialog.show();
        myRef.child("SETS").child(category).child("question").orderByChild("setNO").equalTo(setNo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot  snapshot2:snapshot.getChildren())
                {
                    list.add(snapshot2.getValue(QuestionModel.class));
                }
                if(list.size()>0)
                {


                    //kisi bhi option pe user click kre to  option create
                    for(int i=0;i<4;i++)
                    {
                        optionContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {

                            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                            @Override
                            public void onClick(View view) {
                                checkAnswer((Button)view);
                            }
                        });
                    }

                    //first question animation
                    playAnim(question,0,list.get(position).getQuestion());//by default first question is  set

                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onClick(View view) {
                            nextBtn.setEnabled(false);
                            nextBtn.setAlpha(0.7f);
                            enableoption(true);
                            position++;
                            if(position==list.size()){
                                Intent scoreIntent=new Intent(QuestionActivity.this,ScoreActivity.class);
                                scoreIntent.putExtra("score",score);
                                scoreIntent.putExtra("total",list.size());
                                startActivity(scoreIntent);
                                    finish();
                                ///score Activity
                                return;
                            }
                            count=0;

                            playAnim(question,0,list.get(position).getQuestion());
                        }
                    });

                    shareBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String body=list.get(position).getQuestion()+ list.get(position) .getOptionA()+"\n"+
                                     list.get(position) .getOptionB()+ "\n"+
                                     list.get(position) .getOptionC()+"\n"+
                                     list.get(position) .getOptionD();
                            Intent shareIntent=new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("plain/text");
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT,"Quizzer challenge");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,body);
                            startActivity(Intent.createChooser(shareIntent,"share Via "));

                        }
                    });
                }else{
                    Toast.makeText(QuestionActivity.this, "no Queestions", Toast.LENGTH_SHORT).show();
                }
                loadingdialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuestionActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();
                loadingdialog.dismiss();
                finish();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        storeBookmarks();
    }

    //here we set the animation

    private void playAnim(final View view, final int  value, final String data)
    {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100)
                .setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if(value ==0 && count<4)
                {
                    String option ="";

                    if(count==0){
                        option=list.get(position).getOptionA();
                    }
                    else if(count==1) {
                        option=list.get(position).getOptionB();
                    }else if(count==2) {
                        option=list.get(position).getOptionC();
                    }else if(count==3) {
                        option=list.get(position).getOptionD();

                    }

                    playAnim(optionContainer.getChildAt(count),0,option);
                    count++;
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onAnimationEnd(Animator animator) {
                //data change
                if(value==0){
                    try{
                        ((TextView)view).setText(data);
                        noindicator.setText(position+1+"/"+list.size());

                        if(modelMatch())
                        {
                            //if question already there then we should removed

                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark));

                        }
                        else
                        {

                            bookmarkBtn.setImageDrawable(getDrawable(R.drawable.bookmark_border));

                        }


                    }catch(ClassCastException ex){
                        ((Button)view).setText(data);

                    }
                    view.setTag(data);
                    playAnim(view,1,data);
                }

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(Button  selectedoption)
    {
        enableoption(false);
        nextBtn.setEnabled(true);
        nextBtn.setAlpha(1);
        if(selectedoption.getText().toString().equals(list.get(position).getCorrectANS())){
            //correct if ans is correct the color should we change //correct answer color is grren
            score++;
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));


        }
        else
        {
             // for incoorect the color is red
            selectedoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button    correctoption =(Button) optionContainer.findViewWithTag(list.get(position).getCorrectANS());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));


        }
    }

    //here just diable the other option if user click on any option
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void enableoption(boolean enable){

            for(int i=0;i<4;i++)
            {
                optionContainer.getChildAt(i).setEnabled(enable);
                //extra may be
                if(enable){
                    optionContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#989898")));
                }
            }

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
    private boolean modelMatch()
    {
        boolean matched=false;
        int i=0;
            for(QuestionModel model:bookmarksList){

                if(model.getQuestion().equals(list.get(position).getQuestion())
                && model.getCorrectANS().equals(list.get(position).getCorrectANS())  &&
                        model.getSetNO()==list.get(position).getSetNO())
                {
                                matched=true;
                                matchedQuestionPosition=i;

                }
                i++;

            }

        return matched;
    }

    //here we store our list in shared prefernces
    private void storeBookmarks()
    {
        String json=gson.toJson(bookmarksList);
        editor.putString(KEY_NAME,json);
        editor.commit();

    }



}