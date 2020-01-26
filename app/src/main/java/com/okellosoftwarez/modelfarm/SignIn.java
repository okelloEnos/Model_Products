package com.okellosoftwarez.modelfarm;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SignIn extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button next = findViewById(R.id.nextBtn_signIn);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent nextIntent = new Intent(SignIn.this, user.class);

                startActivity(nextIntent);
            }
        });
    }
}
