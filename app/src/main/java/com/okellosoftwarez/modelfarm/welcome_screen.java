package com.okellosoftwarez.modelfarm;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class welcome_screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        Button continueBtn = findViewById(R.id.continueBtn);

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent continueIntent = new Intent(welcome_screen.this, SignUp.class);

                startActivity(continueIntent);
            }
        });

        TextView tvTerms = findViewById(R.id.tvTerms);
        String text = getString(R.string.tvTerms);
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan terms = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(welcome_screen.this, "Our Terms of Service", Toast.LENGTH_SHORT).show();
            }
        };
        ClickableSpan policy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(welcome_screen.this, "Our Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        };
        spannableString.setSpan(terms,52,69, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(policy,74,88, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvTerms.setText(spannableString);
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
