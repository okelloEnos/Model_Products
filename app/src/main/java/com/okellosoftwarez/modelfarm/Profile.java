package com.okellosoftwarez.modelfarm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {

    Button editProfileBtn;
    TextView dynamicProfileName, dynamicProfileMail, dynamicProfilePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//        SharedPreferences.Editor editor = pref.edit();

//        Retrieving Data

        // getting String
//        pref.getString("eMail", null);
//        pref.getString("passWord", null);
//        pref.getString("userName", null);
//        pref.getString("phone", null);

        editProfileBtn = findViewById(R.id.profileEditBtn);
        dynamicProfileName = findViewById(R.id.dynamicProfileName);
        dynamicProfileMail = findViewById(R.id.dynamicProfileMail);
        dynamicProfilePhone = findViewById(R.id.dynamicProfilePhone);

        dynamicProfileName.setText(pref.getString("userName", null));
        dynamicProfileMail.setText(pref.getString("eMail", null));
        dynamicProfilePhone.setText(pref.getString("phone", null));

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Profile.this, "Feature Coming Soon...", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
