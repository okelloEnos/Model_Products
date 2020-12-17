package com.okellosoftwarez.modelfarm;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class welcome_screen extends AppCompatActivity implements View.OnClickListener {

    Button wlcCreateBtn, wlcLogBtn;
    ImageView welcomeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        wlcCreateBtn = findViewById(R.id.welcomeCreateAccountBtn);
        wlcLogBtn = findViewById(R.id.welcomeLogInBtn);

        welcomeImage = findViewById(R.id.imageViewWelcome);

        wlcLogBtn.setOnClickListener(this);
        wlcCreateBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.welcomeCreateAccountBtn:
//                open create account screen
                Intent accountScreenIntent = new Intent(welcome_screen.this, SignUp.class);
                startActivity(accountScreenIntent);

                break;

            case R.id.welcomeLogInBtn:
//                open log in screen
                Intent logInScreenIntent = new Intent(welcome_screen.this, SignIn.class);
                startActivity(logInScreenIntent);

                break;
        }
    }
}
