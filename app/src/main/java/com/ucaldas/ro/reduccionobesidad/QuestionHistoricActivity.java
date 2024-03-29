package com.ucaldas.ro.reduccionobesidad;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestionHistoricActivity extends AppCompatActivity {

    private ListView questionsList;
    private LinkedList<Question> mQuestions;
    private QuestionsAdapter mQuestionsAdapter;
    private FirebaseDatabase database;
    private DatabaseReference datRef;

    //Referencias a objetos gráficos
    private TextView countOfGreen;
    private TextView countOfRed;
    private TextView countOfGray;
    private TextView totalQuestions;

    private AtomicInteger greenCount = new AtomicInteger(0);
    private AtomicInteger redCount = new AtomicInteger(0);
    private AtomicInteger grayCount = new AtomicInteger(0);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_historic);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        init();
        configureDatabaseAndGetData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void init() {
        questionsList = (ListView) findViewById(R.id.questionsList);
        mQuestions = new LinkedList();
        mQuestionsAdapter = new QuestionsAdapter(getBaseContext(), mQuestions);
        questionsList.setAdapter(mQuestionsAdapter);

        countOfGreen = (TextView) findViewById(R.id.countOfGreen);
        countOfRed = (TextView) findViewById(R.id.countOfRed);
        countOfGray = (TextView) findViewById(R.id.countOfGray);
        totalQuestions = (TextView) findViewById(R.id.totalQuestions);
    }

    private void configureDatabaseAndGetData() {
        database = FirebaseDatabase.getInstance();
        if (database != null && mHome.user != null) {
            datRef = database.getReference().child("questions");
            datRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {

                        HashMap<String, HashMap<String, Object>> map = (HashMap) dataSnapshot.getValue();
                        SortedSet<String> keys = new TreeSet<String>(map.keySet());

                        totalQuestions.setText("/" + keys.size() + "");

                        for (String k : keys) {
                            final Question question = new Question();
                            question.setTitle(map.get(k).get("title") + "");
                            question.setId(map.get(k).get("id") + "");
                            question.setCorrect(map.get(k).get("correct") + "");
                            question.setResponse1(map.get(k).get("response1") + "");
                            question.setResponse2(map.get(k).get("response2") + "");
                            question.setStartDate((long) map.get(k).get("startDate"));
                            question.setEndDate((long) map.get(k).get("endDate"));

                            mQuestions.add(question);
                            mQuestionsAdapter.notifyDataSetChanged();

                            FirebaseDatabase.getInstance().getReference().child("questions-user")
                                    .child(mHome.user.getUid()).orderByChild("question").equalTo(question.getId())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue() != null) {
                                                HashMap<String, HashMap> map = (HashMap) dataSnapshot.getValue();
                                                Iterator it = map.keySet().iterator();
                                                HashMap response = map.get(it.next() + "");

                                                question.setUserResponse((long) response.get("isCorrect"));
                                                mQuestionsAdapter.notifyDataSetChanged();

                                                if ((long) response.get("isCorrect") == 1) {
                                                    greenCount.incrementAndGet();
                                                    countOfGreen.setText(greenCount.get() + "");

                                                } else if ((long) response.get("isCorrect") == 2) {
                                                    redCount.incrementAndGet();
                                                    countOfRed.setText(redCount.get() + "");
                                                }
                                            } else {
                                                grayCount.incrementAndGet();
                                                countOfGray.setText(grayCount.get() + "");
                                            }

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
