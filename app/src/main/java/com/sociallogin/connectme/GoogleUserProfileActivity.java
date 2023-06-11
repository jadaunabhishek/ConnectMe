package com.sociallogin.connectme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;

public class GoogleUserProfileActivity extends AppCompatActivity {

    ImageView Profile;
    TextView UserName, Name;
    TextView LogOut;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_userprofile_activity);

        Profile = findViewById(R.id.profile02);
        UserName = findViewById(R.id.username02);
        Name = findViewById(R.id.name02);
        LogOut = findViewById(R.id.logout02);
        auth = FirebaseAuth.getInstance();

        // initialize animations
        Animation bottom2 = AnimationUtils.loadAnimation(this, R.anim.bottom_animation2);
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //setting bottom animation
        LinearLayout linearLayout1 = findViewById(R.id.mainlinearlayout);
        linearLayout1.setAnimation(bottom);

        //setting bottom2 animation
        ConstraintLayout constraintLayout = findViewById(R.id.linearlayout1);
        constraintLayout.setAnimation(bottom2);

        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                startActivity(new Intent(GoogleUserProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            String name = account.getDisplayName();
            String username = account.getEmail();
            Uri photo = account.getPhotoUrl();

            UserName.setText(username);
            Name.setText(name);

            Glide.with(this).load(photo).circleCrop().into(Profile);
        }
    }
}