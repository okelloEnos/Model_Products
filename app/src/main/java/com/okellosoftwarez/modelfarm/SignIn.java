package com.okellosoftwarez.modelfarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.okellosoftwarez.modelfarm.models.userModel;

import java.net.InetAddress;

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

        TextView registerBtn = findViewById(R.id.registerTxt_signIn);

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
                if (isNetworkConnected()) {
//                    if (isInternetAvailable()){

                    String email = signInMail.getText().toString().trim();
                    String password = signInPassword.getText().toString().trim();
                    String phone = signInPhone.getText().toString().trim();

                    if (email.isEmpty()) {
                        signInMail.setError("Missing Log In Mail");
                    } else if (phone.isEmpty()) {
                        signInPhone.setError("Phone Number required");
                    } else if (phone.length() < 10) {
                        signInPhone.setError("Short Phone Number");
                    } else if (password.isEmpty()) {
                        signInPassword.setError("Missing Log In PassWord");
                    } else if (password.length() < 6) {
                        signInPassword.setError("PassWord Too Short");
                    } else {
                        loadPreference(phone, email, password);
//                            logInUser(email, password);
                    }

//                    }
//                    else {

//                        Toast.makeText(SignIn.this, R.string.No_internet, Toast.LENGTH_LONG).show();
//                    }

                } else {
                    Toast.makeText(SignIn.this, R.string.No_network, Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void loadPreference(String signInPhone, final String signInMail, final String signInPassword) {
        DatabaseReference signPreference = FirebaseDatabase.getInstance().getReference("userProfile").child(signInPhone);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        final SharedPreferences.Editor signEditor = pref.edit();

        signPreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModel signInUser = dataSnapshot.getValue(userModel.class);

                if (signInUser != null) {
//                    Storing Preference Data
                    signEditor.putString("eMail", signInUser.getEmail());
                    signEditor.putString("userName", signInUser.getUserName());
                    signEditor.putString("phone", signInUser.getPhone());
                    signEditor.putString("location", signInUser.getLocation());

                    // commit changes
                    signEditor.commit();

                    String confMail = signInUser.getEmail();
                    if (!confMail.isEmpty()) {
                        if (confMail.equals(signInMail)) {
                            logInUser(signInMail, signInPassword);
                        } else {
                            Toast.makeText(SignIn.this, "Incorrect Phone Number", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(SignIn.this, "No Retrieved User Mail", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(SignIn.this, "Invalid Fields Try Creating an Account", Toast.LENGTH_SHORT).show();
                }
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
                            if (user != null) {
                                updateUI();
                            }
                        } else {

                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "signInWithEmail:failure:" + task.getException().getMessage() + ":");
                            String attempts = "We have blocked all requests from this device due to unusual activity. Try again later. [ Too many unsuccessful login attempts. Please try again later. ]";
                            if (task.getException().getMessage().equals("The email address is badly formatted.")) {
                                Toast.makeText(SignIn.this, "Bad Format E mail...", Toast.LENGTH_LONG).show();
                                signInMail.setError("Bad Format for E-Mail Address");
                            }
                            if (task.getException().getMessage().equals("The password is invalid or the user does not have a password.")) {
                                Toast.makeText(SignIn.this, "PassWord or E mail is Incorrect...", Toast.LENGTH_LONG).show();
                            }
                            if (task.getException().getMessage().equals("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                Toast.makeText(SignIn.this, "Your E mail is Not Registered Try Create Account...", Toast.LENGTH_LONG).show();
                            }
                            if (task.getException().getMessage().equals(attempts)) {
                                Toast.makeText(SignIn.this, "Too Many Attempts !!! Get The Right Password and Try Again Later...", Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });
    }

    public void forgotPassWord(View view) {
        if (isNetworkConnected()) {
//            if (isInternetAvailable()){

            AlertDialog.Builder resetBuilder = new AlertDialog.Builder(this);
            resetBuilder.setTitle("Reset");
            resetBuilder.setMessage("Enter Email To Receive Password Resent Link");

            final EditText resetInput = new EditText(this);
            resetBuilder.setView(resetInput);
            resetBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String resetMail = resetInput.getText().toString().trim();

                    if (resetMail.isEmpty()) {
                        Toast.makeText(SignIn.this, "The Email Address is Blank...", Toast.LENGTH_SHORT).show();
                    }

                    if (Patterns.EMAIL_ADDRESS.matcher(resetMail).matches()) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();

                        auth.sendPasswordResetEmail(resetMail)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "Email sent.");

                                            Toast.makeText(SignIn.this, "Recovery Email has been Sent to : " + resetMail, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(SignIn.this, "Invalid E-Mail Address...", Toast.LENGTH_LONG).show();
                    }

                }
            });

            resetBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(SignIn.this, "Request Cancelled...", Toast.LENGTH_SHORT).show();
                }
            });

            resetBuilder.show();
//            }
//            else {
//
//                Toast.makeText(SignIn.this, R.string.No_internet, Toast.LENGTH_LONG).show();
//            }

        } else {
            Toast.makeText(SignIn.this, R.string.No_network, Toast.LENGTH_LONG).show();
        }
    }

    //    This method checks whether mobile is connected to internet and returns true if connected:
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    //    This method actually checks if device is connected to internet(There is a possibility it's connected to a network but not to internet).
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
//            super.onBackPressed();
        Intent backLintent = new Intent(this, welcome_screen.class);
        backLintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(backLintent);
        finish();
    }
}
