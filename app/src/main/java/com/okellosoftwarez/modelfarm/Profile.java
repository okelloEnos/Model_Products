package com.okellosoftwarez.modelfarm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends AppCompatActivity implements View.OnClickListener {

    Button editProfileBtn, profileUpdateBtn;
    EditText dynamicProfileName, dynamicProfileMail, dynamicProfilePhone, dynamicProfileLocation;
    DatabaseReference receiveProf;
    userModel profileModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbarProfile = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbarProfile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phone = pref.getString("phone", null);
        receiveProf = FirebaseDatabase.getInstance().getReference("userProfile").child(phone);

        editProfileBtn = findViewById(R.id.profileEditBtn);
        profileUpdateBtn = findViewById(R.id.profileUpdateBtn);
        dynamicProfileName = findViewById(R.id.dynamicProfileName);
        dynamicProfileMail = findViewById(R.id.dynamicProfileMail);
        dynamicProfilePhone = findViewById(R.id.dynamicProfilePhone);
        dynamicProfileLocation = findViewById(R.id.dynamicProfileLocation);

        receiveProf.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                profileModel = dataSnapshot.getValue(userModel.class);
                dynamicProfileName.setText(profileModel.getUserName());
                dynamicProfileMail.setText(profileModel.getEmail());
                dynamicProfileLocation.setText(profileModel.getLocation());
                dynamicProfilePhone.setText(profileModel.getPhone());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Profile.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        profileUpdateBtn.setOnClickListener(this);
        editProfileBtn.setOnClickListener(this);

    }

    private void submitProfileUpdate() {
        String changedName, changedMail, changedLocation, phone;
        changedName = dynamicProfileName.getText().toString().trim();
        changedMail = dynamicProfileMail.getText().toString().trim();
        changedLocation = dynamicProfileLocation.getText().toString();
        phone = dynamicProfilePhone.getText().toString();

        if (changedName.isEmpty()) {
            dynamicProfileName.setError("Required User Name");

        } else if (changedMail.isEmpty()) {
            dynamicProfileMail.setError("E Mail Required");

        } else if (changedLocation.isEmpty()) {
            dynamicProfileLocation.setError("Location Required");

        } else {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            user.updateEmail(changedMail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                Log.d(TAG, "User email address updated.");
                                Toast.makeText(Profile.this, "User email address updated.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


            userModel changedProfile = new userModel(changedName, changedMail, phone, changedLocation);
            receiveProf.setValue(changedProfile);

            updateSharedPref(changedName, changedMail, phone, changedLocation);

        }

    }

    private void updateSharedPref(String changedName, String changedMail, String phone, String changedLocation) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        SharedPreferences.Editor editorChanged = pref.edit();

//                    Storing Preference Data
        editorChanged.putString("eMail", changedMail);
        editorChanged.putString("userName", changedName);
        editorChanged.putString("phone", changedLocation);
        editorChanged.putString("location", changedLocation);

        // commit changes
        editorChanged.commit();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.profileUpdateBtn:

                submitProfileUpdate();

                dynamicProfileName.setEnabled(false);
                dynamicProfileLocation.setEnabled(false);
                dynamicProfileMail.setEnabled(false);

                profileUpdateBtn.setVisibility(View.INVISIBLE);
                editProfileBtn.setVisibility(View.VISIBLE);
                break;

            case R.id.profileEditBtn:

                dynamicProfileName.setEnabled(true);
                dynamicProfileLocation.setEnabled(true);
                dynamicProfileMail.setEnabled(true);

                editProfileBtn.setVisibility(View.INVISIBLE);
                profileUpdateBtn.setVisibility(View.VISIBLE);
                break;
        }
    }
}
