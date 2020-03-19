package com.okellosoftwarez.modelfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private static FirebaseAuth mAuth;
    private userModel currentUser ;

//    public static  SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);;

    EditText  etPassword, etUserName, etPhone, etMail, etConfirmPassword, etLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword_signIn);
        etUserName = findViewById(R.id.etUserName);
        etPhone = findViewById(R.id.etPhone);
        etConfirmPassword = findViewById(R.id.etConfirmPassword_signUp);
        etLocation = findViewById(R.id.etLocation);

        Button registerBtn = findViewById(R.id.registerBtn_signUp);

        Button logBtn = findViewById(R.id.logInBtn_signUp);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent( SignUp.this, SignIn.class);
                startActivity(logIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String userName = etUserName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();
                String location = etLocation.getText().toString().trim();

                if (userName.isEmpty()) {
                    etUserName.setError("Missing User Name");

                } else if (password.isEmpty()) {
                    etPassword.setError("Missing PassWord");

                } else if (confirmPassword.isEmpty()) {
                    etConfirmPassword.setError("Missing Confirm Password");

                } else if (phone.isEmpty()) {
                    etPhone.setError("Missing Phone Number");

                } else if (email.isEmpty()) {
                    etMail.setError("Missing Mail");

                } else if (location.isEmpty()){
                    etLocation.setError("Location Required");

                }else if (password.length() < 6) {
                    etPassword.setError("PassWord Too Short");

                } else if (!password.equals(confirmPassword)){
                    etConfirmPassword.setError("Confirm PassWord Do not Match ");

                } else if (phone.length() < 10){
                    etPhone.setError("Phone Number Too Short");

                }
                else {

                   SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
                    SharedPreferences.Editor editor = pref.edit();

//                    Storing Preference Data
                    editor.putString("eMail", email);
                    editor.putString("passWord", password);
                    editor.putString("userName", userName);
                    editor.putString("phone", phone);
                    editor.putString("location", location);

                    // commit changes
                    editor.commit();

                    DatabaseReference profileRef = FirebaseDatabase.getInstance().getReference("userProfile").child(phone);
//                    String profileKey = profileRef.push().getKey();
//                    String profMail, profUserName, profPhone, profLocation;
//                    profUserName = pref.getString("eMail", null);
//                    profMail = pref.getString("userName", null);
//                    profPhone = pref.getString("phone", null);
//                    profLocation = pref.getString("location", null);

//                    currentUser = new userModel(profUserName, profMail, profPhone, profLocation);
                    currentUser = new userModel(userName, email, phone, location);

                    profileRef.setValue(currentUser);

                    registerUser(email, password);
                }
            }
        });


    }
    private void updateUI(FirebaseUser currentUser) {
        Intent autoIntent = new Intent(this, user.class);
//        autoIntent.putExtra(message, currentUser.getEmail());
        startActivity(autoIntent);
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Successfull Registration...", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                updateUI(user);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(SignUp.this, "Authentication failed. Check Your Details : " + task.getException().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            if (task.getException().getMessage().equals("The email address is already in use by another account.")){
                                Toast.makeText(SignUp.this, "Your E mail is already registered Try Log In...", Toast.LENGTH_LONG).show();
                            }

                            if (task.getException().getMessage().equals("The email address is badly formatted.")){
                                Toast.makeText(SignUp.this, "Bad Format E mail...", Toast.LENGTH_SHORT).show();
                            }
                        }

                        // ...
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            updateUI(currentUser);
        }
    }
//    public void signOut() {
//        mAuth.signOut();
//        Intent outIntent = new Intent(getApplicationContext(), SignUp.class);
//        startActivity(outIntent);
//    }
}
