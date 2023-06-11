package com.sociallogin.connectme;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.hbb20.CountryCodePicker;

public class PhoneNumberActivity extends AppCompatActivity {

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_activity);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneInput = findViewById(R.id.phoneinput);
        sendOtp = findViewById(R.id.nextphone);

        // initialize animations
        Animation bottom2 = AnimationUtils.loadAnimation(this, R.anim.bottom_animation2);
        Animation bottom = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //setting bottom animation
        LinearLayout linearLayout1 = findViewById(R.id.mainlinearlayout);
        linearLayout1.setAnimation(bottom);

        //setting bottom2 animation
        sendOtp.setAnimation(bottom2);

        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtp.setOnClickListener((v)->{
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Enter valid phone number");
            }
            Intent intent = new Intent(PhoneNumberActivity.this, PhoneOTPActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            startActivity(intent);

        });


    }
}