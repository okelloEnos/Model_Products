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

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private static FirebaseAuth mAuth;

//    public static  SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);;

    EditText  etPassword, etUserName, etPhone, etMail, etConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword_signIn);
        etUserName = findViewById(R.id.etUserName);
        etPhone = findViewById(R.id.etPhone);
        etConfirmPassword = findViewById(R.id.etConfirmPassword_signUp);

        Button next = findViewById(R.id.nextBtn_signUp);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = etMail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                String userName = etUserName.getText().toString().trim();
                String phone = etPhone.getText().toString().trim();

                if (email.isEmpty()) {
                    etMail.setError("Missing Mail");
                } else if (userName.isEmpty()) {
                    etUserName.setError("Missing User Name");
                } else if (password.isEmpty()) {
                    etPassword.setError("Missing PassWord");
                } else if (confirmPassword.isEmpty()) {
                    etConfirmPassword.setError("Missing Confirm Password");
                } else if (phone.isEmpty()) {
                    etPhone.setError("Missing Phone Number");
                } else {


                   SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
                    SharedPreferences.Editor editor = pref.edit();

//                    Storing Preference Data
                    editor.putString("eMail", email);
                    editor.putString("passWord", password);
                    editor.putString("userName", userName);
                    editor.putString("phone", phone);

                    // commit changes
                    editor.commit();

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
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null){
                                updateUI(user);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUp.this, "Authentication failed. Check Your Details : " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
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
    public static void signOut() {
        mAuth.signOut();
    }
}
