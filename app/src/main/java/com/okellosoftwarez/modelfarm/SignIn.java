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
                            FirebaseUser user = signInmAuth.getCurrentUser();
                            if (user != null){
                                updateUI();
                            }

                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignIn.this, "Authentication failed. Wrong Log In Credentials Combination",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
