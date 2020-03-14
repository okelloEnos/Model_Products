package com.okellosoftwarez.modelfarm;

import android.content.Intent;
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

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";
    private static FirebaseAuth signInmAuth;
    private EditText signInMail, signInPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Initialize Firebase Auth
        signInmAuth = FirebaseAuth.getInstance();

        signInMail = findViewById(R.id.etSignInMail);
        signInPassword = findViewById(R.id.etPassword_signIn);

        Button next = findViewById(R.id.nextBtn_signIn);

        Button registerBtn = findViewById(R.id.registerBtn_signIn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent regIntent = new Intent(SignIn.this, SignUp.class);
                startActivity(regIntent);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = signInMail.getText().toString().trim();
                String password = signInPassword.getText().toString().trim();

                if (email.isEmpty()) {
                    signInMail.setError("Missing Log In Mail");
                } else if (password.isEmpty()) {
                    signInPassword.setError("Missing Log In PassWord");
                }
                else if (password.length() < 6) {
                    signInPassword.setError("PassWord Too Short");
                }
                else {
                    logInUser(email, password);
                }
            }
        });

    }
    private void updateUI() {
        Intent autoIntent = new Intent(this, user.class);
        startActivity(autoIntent);
    }

    private void logInUser(String email, String password) {

        signInmAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            Toast.makeText(SignIn.this, "Welcome Back to Agriculture Commerce...", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = signInmAuth.getCurrentUser();
                            if (user != null){
                                updateUI();
                            }

                        }
                        else {

                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure:"+task.getException().getMessage()+":");
                            String attempts = "We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts. Please try again later. ]";
//                            Toast.makeText(SignIn.this, "Authentication failed : " + task.getException().getMessage(),
//                                    Toast.LENGTH_LONG).show();
                            if (task.getException().getMessage().equals("The email address is badly formatted.")){
                                Toast.makeText(SignIn.this, "Bad Format E mail...", Toast.LENGTH_SHORT).show();
                            }
                            if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")){
                                Toast.makeText(SignIn.this, "PassWord or E mail is Incorrect...", Toast.LENGTH_SHORT).show();
                            }
                            if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")){
                                Toast.makeText(SignIn.this, "Your E mail is Not Registered Try Create Account...", Toast.LENGTH_SHORT).show();
                            }
                            if (task.getException().getMessage().equals(attempts)){
                                Toast.makeText(SignIn.this, "Too Many Attempts !!! Get The Right Password and Try Again Later...", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }
}
