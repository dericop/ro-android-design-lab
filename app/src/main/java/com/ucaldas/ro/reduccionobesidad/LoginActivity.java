package com.ucaldas.ro.reduccionobesidad;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private boolean buttonAgreeIsOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonAgreeIsOK = false;

        final SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setEnabled(false);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        TextView btnConditions = (TextView) findViewById(R.id.btn_conditions);
        btnConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(buttonAgreeIsOK){
                buttonAgreeIsOK = false;
                signInButton.setEnabled(false);
            }else{
                buttonAgreeIsOK = true;
                signInButton.setEnabled(true);
                startActivity(new Intent(getBaseContext(), TermsAndConditions.class));
            }
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

        final LoginActivity that = this;

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // AUser is signed in
                    Log.d("AUser", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getBaseContext(), getString(R.string.login_successfull), Toast.LENGTH_LONG);

                    mHome.user = user; //Asignación de usuario a la clase principal

                    //Almacenar el usuario que se ha logueado
                    AUser newUser = new AUser(user.getUid(), user.getDisplayName(), user.getEmail(), user.getPhotoUrl().toString());
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

                    PrefManager prefManager;
                    prefManager = new PrefManager(that);

                    if (prefManager.isFirstTimeLaunch()) {
                        if(WelcomeActivity.CURRENT_APP_VERSION.equals("A"))
                            mDatabase.child("users").child(user.getUid()).setValue(newUser);
                        else
                            mDatabase.child("users-reflexive").child(user.getUid()).setValue(newUser);
                    }

                    startActivity(new Intent(getBaseContext(), mHome.class));

                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.login_fail), Toast.LENGTH_LONG);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mHome.user != null)
            startActivity(new Intent(getBaseContext(), mHome.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {

                handleSignInResult(result);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("AUser", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("AUser", "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w("AUser", "signInWithCredential", task.getException());

                            Toast.makeText(LoginActivity.this, "Revisa tu conexión a internet e intentalo más tarde.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(getCurrentFocus(), "Revise su conexión a internet e intentelo más tarde", 2000).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Sign In", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
        }
    }
}
