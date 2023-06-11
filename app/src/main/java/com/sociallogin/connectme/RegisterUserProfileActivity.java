package com.sociallogin.connectme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterUserProfileActivity extends AppCompatActivity {

    TextView TextEmail, TextName, TextAge, TextlogOut;
    String useremail, username, userage;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;
    FirebaseAuth authprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_userprofile_activity);

        TextEmail = findViewById(R.id.email);
        TextName = findViewById(R.id.name);
        TextAge = findViewById(R.id.age);
        TextlogOut = findViewById(R.id.logout02);

        // initialize animations
        Animation bottom2 = AnimationUtils.loadAnimation(this, R.anim.bottom_animation2);
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //setting bottom animation
        LinearLayout linearLayout1 = findViewById(R.id.mainlinearlayout);
        linearLayout1.setAnimation(bottom);

        //setting bottom2 animation
        ConstraintLayout constraintLayout = findViewById(R.id.cardView7);
        constraintLayout.setAnimation(bottom2);

        progressDialog = new ProgressDialog(this);
        authprofile = FirebaseAuth.getInstance();
        firebaseUser = authprofile.getCurrentUser();

        TextlogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(RegisterUserProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        if (firebaseUser == null){
            Toast.makeText(this, "Something went wrong! User's details are not available!", Toast.LENGTH_SHORT).show();
        }
        else{
            progressDialog.setMessage("Please wait..");
            progressDialog.setTitle("Updating Profile");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            showUserProfile(firebaseUser);
        }

    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        DatabaseReference referenceprofile = FirebaseDatabase.getInstance().getReference("Users Email Registration Data");
        referenceprofile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                readwriteuserdata4 readuserdata = snapshot.getValue(readwriteuserdata4.class);
                if (readuserdata != null){

                    progressDialog.dismiss();

                    useremail = firebaseUser.getEmail();
                    username = firebaseUser.getDisplayName();
                    userage = readuserdata.AGE;



                    TextName.setText(username);
                    TextEmail.setText(useremail);
                    TextAge.setText(userage);

                }
                else{

                    progressDialog.dismiss();

                    Toast.makeText(RegisterUserProfileActivity.this, "Something went wrong! User's details are not synced to firebase!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                Toast.makeText(RegisterUserProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //retrieve user data
}