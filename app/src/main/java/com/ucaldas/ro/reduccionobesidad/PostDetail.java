package com.ucaldas.ro.reduccionobesidad;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class PostDetail extends AppCompatActivity {

    private LinkedList<Comment> mComments;
    private CommentsAdapter comAdapter;
    private ListView commentListView;
    private FirebaseDatabase database;
    private DatabaseReference datRef;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        init();
    }

    private void configureDatabase(){
        database = FirebaseDatabase.getInstance();
        if(database!=null && mHome.user!=null){
            if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                datRef = database.getReference().child("user-comments");
            }else{
                datRef = database.getReference().child("user-comments-reflexive");
            }
        }

    }


    private void init(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        commentListView = (ListView) findViewById(R.id.commentsList);
        commentListView.setEmptyView(findViewById(android.R.id.empty));

        mComments = new LinkedList();


        comAdapter = new CommentsAdapter(
                getBaseContext(),
                mComments);

        commentListView.setAdapter(comAdapter);


        //Recuperación de datos de la vista anterior
        String name = getIntent().getStringExtra("name");
        String imageForComment = getIntent().getStringExtra("image");
        String user = getIntent().getStringExtra("userName");
        postId = getIntent().getStringExtra("id");
        double r_pi = getIntent().getDoubleExtra("r_pi", 0);
        double r_aa = getIntent().getDoubleExtra("r_aa", 0);
        double r_gs = getIntent().getDoubleExtra("r_gs", 0);
        double r_ch = getIntent().getDoubleExtra("r_ch", 0);
        long result = getIntent().getLongExtra("result", 0);

        //Actualización componentes gráficos
        ImageView imgPreview = (ImageView) findViewById(R.id.imgPreview);
        TextView title = (TextView) findViewById(R.id.title);
        TextView userName = (TextView) findViewById(R.id.userName);

        Glide.with(this).load(imageForComment).into(imgPreview);
        title.setText(name);
        userName.setText(user);

        LinearLayout corusQualificationContainer = (LinearLayout)  findViewById(R.id.corus_qualification);
        LinearLayout coconoQualificationContainer = (LinearLayout)  findViewById(R.id.cocono_qualification);



        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
            corusQualificationContainer.setVisibility(View.VISIBLE);
            coconoQualificationContainer.setVisibility(View.GONE);

            LinearLayout resultItem = (LinearLayout) findViewById(R.id.result_item);
            Log.v("Result", result+"result ");

            switch ((int)result) {
                case 0:
                    resultItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_clock));
                    break;
                case 1:
                    resultItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bad));
                    break;
                case 2:
                    resultItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_medium));
                    break;
                case 3:
                    resultItem.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_good));
                    break;
            }

        }else{
            corusQualificationContainer.setVisibility(View.GONE);
            coconoQualificationContainer.setVisibility(View.VISIBLE);

            updateQualificationInfo(r_pi, r_aa, r_gs, r_ch);

        }


        //Consultar los comentarios de la base de datos
        getPostComments();

    }

    private void closeKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void saveComment(View view){

        if(isOnline()){
            final EditText editMessage = ((EditText) findViewById(R.id.edit_message));
            String comment = editMessage.getText().toString();
            if(!comment.equals("")){
                Calendar cal = Calendar.getInstance();
                long timeInMillis = cal.getTimeInMillis();

                Comment com = new Comment(comment, timeInMillis, postId);
                com.setUser(mHome.user.getUid());
                com.setUserPhoto(mHome.user.getPhotoUrl().toString());

                DatabaseReference database = FirebaseDatabase.getInstance().getReference();

                String key;

                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                    key = database.child("user-comments").push().getKey();
                }else{
                    key = database.child("user-comments-reflexive").push().getKey();
                }


                Map<String, Object> commentValues = com.toMap();
                Map<String, Object> childUpdates = new HashMap<>();

                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                    childUpdates.put("/user-comments/" + key, commentValues);
                }else{
                    childUpdates.put("/user-comments-reflexive/" + key, commentValues);
                }

                OnCompleteListener saveListener = new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if (task.isSuccessful()) {
                            updateComments();
                            editMessage.setText("");
                            closeKeyboard();

                        } else {
                            Log.v("DB", task.getResult() + "");
                            if(getCurrentFocus()!=null)
                                Snackbar.make(getCurrentFocus(), "Revise su conexión a internet o intentelo más tarde", 2000).show();
                        }
                    }
                };
                database.updateChildren(childUpdates).addOnCompleteListener(saveListener);

            }

        }else{
            View piContainer = findViewById(R.id.piContainer);
            Snackbar.make(piContainer, "No tienes conexión a internet", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void getPostComments(){
        configureDatabase();
        updateComments();
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private void getCommentsDetail(){
        final DatabaseReference userDataDB = FirebaseDatabase.getInstance().getReference();
        configureDatabase();

        if(datRef != null){
            datRef.orderByChild("id").equalTo(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue() != null){
                        HashMap<String, HashMap<String, Object>> map = (HashMap)dataSnapshot.getValue();
                        SortedSet<String> keys = new TreeSet<String>(map.keySet());

                        DatabaseReference userDataRef;

                        mComments.clear();
                        for (String k: keys){
                            final Comment com = new Comment();
                            com.setDetail(map.get(k).get("detail")+"");
                            com.setDate((long)map.get(k).get("date"));
                            com.setId(map.get(k).get("id")+"");

                            if(map.get(k).get("user") != null){
                                String userKey = map.get(k).get("user")+"";

                                if(WelcomeActivity.CURRENT_APP_VERSION.equals("A")){
                                    userDataRef = userDataDB.child("users");
                                }else{
                                    userDataRef = userDataDB.child("users-reflexive");
                                }

                                mComments.addFirst(com);
                                comAdapter.notifyDataSetChanged();

                                userDataRef.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot.hasChildren()){
                                            HashMap<String, HashMap<String, Object>> map = (HashMap)dataSnapshot.getValue();
                                            SortedSet<String> keys = new TreeSet<String>(map.keySet());

                                            com.setUser(map.get("mUserName")+"");
                                            com.setUserPhoto(map.get("mPhotoUrl")+"");


                                            comAdapter.notifyDataSetChanged();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    private void updateComments(){
        if(isOnline()){
            if(datRef != null){
                getCommentsDetail();
            }else{
                try {
                    configureDatabase();
                    getCommentsDetail();
                }catch (DatabaseException de){
                    Log.v("Error", de.getMessage());
                }

            }
        }else{
            View piContainer = findViewById(R.id.piContainer);
            Snackbar.make(piContainer, "No tienes conexión a internet", Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    private void updateQualificationInfo(double pi, double aa, double gs, double ch){
        View piContainer = findViewById(R.id.piContainer);
        View aaContainer = findViewById(R.id.aaContainer);
        View gsContainer = findViewById(R.id.gsContainer);
        View chContainer = findViewById(R.id.chContainer);


        int maxCalificationForPI = 10;
        int relativeLayoutHeight = 50;

        if(pi != 0){
            int percentage = (int)(pi*100)/maxCalificationForPI;
            int graphicalHeight = (percentage*relativeLayoutHeight) / 100;

            ViewGroup.LayoutParams piParams = piContainer.getLayoutParams();
            piParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            piContainer.setLayoutParams(piParams);
        }

        int maxCalificationForOthers = 3;
        if(aa != 0){
            int percentage = (int)(aa*100)/maxCalificationForOthers;
            int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

            ViewGroup.LayoutParams aaParams = aaContainer.getLayoutParams();
            aaParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            aaContainer.setLayoutParams(aaParams);
        }

        if(gs != 0){
            int percentage = (int)(gs*100)/maxCalificationForOthers;
            int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;

            ViewGroup.LayoutParams gsParams = gsContainer.getLayoutParams();
            gsParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            gsContainer.setLayoutParams(gsParams);
        }

        if(ch != 0){

            int percentage = (int)(ch*100)/maxCalificationForOthers;
            int graphicalHeight = (percentage*relativeLayoutHeight)/ 100;
            ViewGroup.LayoutParams chParams = chContainer.getLayoutParams();
            chParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, graphicalHeight, getResources().getDisplayMetrics());
            chContainer.setLayoutParams(chParams);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        /*
        * Habilitar la navegación en esta vista
        * */
        finish();
        return false;
    }

}
