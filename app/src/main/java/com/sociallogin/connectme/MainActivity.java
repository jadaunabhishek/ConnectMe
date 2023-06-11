package com.sociallogin.connectme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gsc;
    GoogleSignInClient googleSignInUser;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog progressDialog;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button login;
    TextView signup;
    ImageView googlelogin, phonelogin, twitterlogin;
    EditText emailET, passwordET, confirmpasswordET;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // login activities assign ids
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        confirmpasswordET = findViewById(R.id.password);
        googlelogin = findViewById(R.id.googlelogin);
        phonelogin = findViewById(R.id.phonelogin);
        twitterlogin = findViewById(R.id.twitterlogin);
        login = findViewById(R.id.loginemail);
        signup = findViewById(R.id.signupemail);

        // initialize animations
        Animation bottom2 = AnimationUtils.loadAnimation(this, R.anim.bottom_animation2);
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //setting bottom animation
        LinearLayout linearLayout1 = findViewById(R.id.mainlinearlayout);
        linearLayout1.setAnimation(bottom);

        //setting bottom2 animation
        ConstraintLayout constraintLayout = findViewById(R.id.linearlayout1);
        constraintLayout.setAnimation(bottom2);


        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();


        // google login
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        gsc = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        googleSignInUser = GoogleSignIn.getClient(MainActivity.this, gsc);


        // facebook login
        twitterlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TwitterLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });


        // phone authentication login
        phonelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PhoneNumberActivity.class));
            }
        });

        // new user sign up

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, RegisterUserActivity.class));
            }
        });

        //old user login

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailstr = emailET.getText().toString();
                String passwordstr = passwordET.getText().toString();

                if(!emailstr.matches(emailPattern)){
                    emailET.setError("Enter correct Email");
                }
                else if (passwordstr.isEmpty() || passwordstr.length()<8){
                    passwordET.setError("Enter proper password");
                }
                else{
                    progressDialog.setMessage("Please wait...");
                    progressDialog.setTitle("Login");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.signInWithEmailAndPassword(emailstr,passwordstr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                sendUserToNextActivity();
//                                finish();
                            }
                            else{
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, "Username or password incorrect", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });


        googlelogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });
    }



    // google sign in
    private void signIn() {
        Intent intent = googleSignInUser.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Store in firebase
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("NAME", account.getDisplayName());
                            map.put("USERNAME", account.getEmail());
                            map.put("PROFILE", String.valueOf(account.getPhotoUrl()));
                            map.put("UID", firebaseUser.getUid());

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users Google Data");
                            reference.child(firebaseUser.getUid()).setValue(map);
                            startActivity(new Intent(MainActivity.this, GoogleUserProfileActivity.class));
                        } else {
//                            Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (Exception e) {
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendUserToNextActivity(){
        Intent intent = new Intent(MainActivity.this, RegisterUserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}