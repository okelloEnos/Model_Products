package com.okellosoftwarez.modelfarm;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "SignIn";
    private static FirebaseAuth signInmAuth;
    private EditText signInMail, signInPassword, signInPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



//        Initialize Firebase Auth
        signInmAuth = FirebaseAuth.getInstance();

        signInMail = findViewById(R.id.etSignInMail);
        signInPassword = findViewById(R.id.etPassword_signIn);
        signInPhone = findViewById(R.id.etPhone_signIn);

        Button next = findViewById(R.id.nextBtn_signIn);

        TextView registerBtn = findViewById(R.id.registerBtn_signIn);

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
                String phone = signInPhone.getText().toString().trim();

                if (email.isEmpty()) {
                    signInMail.setError("Missing Log In Mail");
                } else if (password.isEmpty()) {
                    signInPassword.setError("Missing Log In PassWord");
                }
                else if (password.length() < 6) {
                    signInPassword.setError("PassWord Too Short");
                }
                else {
                    loadPreference(phone);
                    logInUser(email, password);
                }
            }
        });

    }

    private void loadPreference(String signInPhone) {
        DatabaseReference signPreference = FirebaseDatabase.getInstance().getReference("userProfile").child(signInPhone);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        final SharedPreferences.Editor signEditor = pref.edit();

        signPreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel signInUser = dataSnapshot.getValue(userModel.class);

//                    Storing Preference Data
                signEditor.putString("eMail", signInUser.getEmail());
//                editor.putString("passWord", signInUser.);
                signEditor.putString("userName", signInUser.getUserName());
                signEditor.putString("phone", signInUser.getPhone());
                signEditor.putString("location", signInUser.getLocation());

                // commit changes
                signEditor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(SignIn.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
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

    public void forgotPassWord(View view) {
        AlertDialog.Builder resetBuilder = new AlertDialog.Builder(this);
        resetBuilder.setTitle("Reset");
        resetBuilder.setMessage("Enter Email To Receive Password Resent Link");

        final EditText resetInput = new EditText(this);
        resetBuilder.setView(resetInput);
        resetBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               String resetMail =  resetInput.getText().toString().trim();

                FirebaseAuth auth = FirebaseAuth.getInstance();
//                String emailAddress = "user@example.com";

                auth.sendPasswordResetEmail(resetMail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Email sent.");
                                }
                            }
                        });

            }
        });

        resetBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(SignIn.this, "Request Cancelled...", Toast.LENGTH_SHORT).show();
            }
        });

        resetBuilder.show();

//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        FirebaseUser user = auth.getCurrentUser();
//
//        user.sendEmailVerification()
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Log.d(TAG, "Email sent.");
//                        }
//                    }
//                });
    }
}
