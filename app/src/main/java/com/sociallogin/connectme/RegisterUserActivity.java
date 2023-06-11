package com.sociallogin.connectme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUserActivity extends AppCompatActivity {

    ProgressDialog progressDialog;
    FirebaseAuth mAuth;

    EditText email2, password2, confirmpassword2, name2, age2;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button register2;
    TextView login2;
    // User Register Attributes

    // User Data add to Firebase Attributes
    String strname, stremail, strage, strpassword, strconfirmpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user_activity);

        // User Register Attributes
        name2 = findViewById(R.id.nameuser);
        email2 = findViewById(R.id.emailuser);
        age2 = findViewById(R.id.ageuser);
        password2 = findViewById(R.id.passworduser);
        confirmpassword2 = findViewById(R.id.confirmpassworduser);
        register2 = findViewById(R.id.registeremail);
        login2 = findViewById(R.id.loginemail);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        // initialize animations
        Animation bottom2 = AnimationUtils.loadAnimation(this, R.anim.bottom_animation2);
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //setting bottom animation
        LinearLayout linearLayout1 = findViewById(R.id.mainlinearlayout);
        linearLayout1.setAnimation(bottom);

        //setting bottom2 animation
        ConstraintLayout constraintLayout = findViewById(R.id.cardView7);
        constraintLayout.setAnimation(bottom2);

        // User Register Attributes

        // Taking user to login window from register window with login button
        login2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterUserActivity.this, MainActivity.class));
            }
        });


        // User Register Attributes
        register2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                strname = name2.getText().toString();
                stremail = email2.getText().toString();
                strage = age2.getText().toString();
                strpassword = password2.getText().toString();
                strconfirmpassword = confirmpassword2.getText().toString();


                if (TextUtils.isEmpty(strname)) {
                    name2.setError("Please enter name");
                    name2.requestFocus();
                    return;
                }

                else if(!stremail.matches(emailPattern)){
                    email2.setError("Enter correct Email");
                }

                else if(strage.isEmpty()){
                    age2.setError("Age is less than 18, not eligible");
                }

                else if (strpassword.isEmpty() || strpassword.length()<8){
                    password2.setError("Enter proper password");
                }

                else if (!strpassword.equals(strconfirmpassword)){
                    confirmpassword2.setError("Password not match both field");
                }

                else{

                    //if email and password are correct and passes all test cases then data will be entered in firebase and then intent to next activity

                    progressDialog.setMessage("Please wait while Registration...");
                    progressDialog.setTitle("Registration");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    mAuth.createUserWithEmailAndPassword(stremail,strpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                // User image add to Firebase Attributes


                                FirebaseUser mUser = mAuth.getCurrentUser();
                                //display name of user
                                UserProfileChangeRequest userProfileChangeRequest  = new UserProfileChangeRequest.Builder().setDisplayName(strname).build();
                                mUser.updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });

                                readwriteuserdata4 writeuserdata = new readwriteuserdata4(strname, stremail, strage, strpassword);
                                DatabaseReference profilereference = FirebaseDatabase.getInstance().getReference("Users Email Registration Data");
                                profilereference.child(mUser.getUid()).setValue(writeuserdata).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            //take user to next activity
                                            sendUserToNextActivity();
                                            Toast.makeText(RegisterUserActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }

                                        else{
                                            Toast.makeText(RegisterUserActivity.this, "Registration failed! Please try again.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                                // User Data, add to Firebase Attributes

                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(RegisterUserActivity.this, "User not registered, please try again!"+task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
        // User Register Attributes

    }

    // User Register Attributes, intent to this new class
    private void sendUserToNextActivity(){
        Intent intent = new Intent(RegisterUserActivity.this, RegisterUserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    // User Register Attributes
}