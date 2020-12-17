package com.okellosoftwarez.modelfarm;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import com.okellosoftwarez.modelfarm.models.userModel;

import java.net.InetAddress;

public class SignUp extends AppCompatActivity {
    private static final String TAG = "SignUp";
    private static FirebaseAuth mAuth;
    private userModel currentUser;

    EditText etPassword, etUserName, etPhone, etMail, etConfirmPassword, etLocation;
    TextView tvTermsPolicy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        etMail = findViewById(R.id.etMail);
        etPassword = findViewById(R.id.etPassword);
        etUserName = findViewById(R.id.etUserName);
        etPhone = findViewById(R.id.etPhone);
        etConfirmPassword = findViewById(R.id.etConfirmPassword_signUp);
        etLocation = findViewById(R.id.etLocation);

        tvTermsPolicy = findViewById(R.id.tvTermsPolicy);

        String text = getString(R.string.tvTermsPolicy);
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan terms = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUp.this, "Our Terms of Service", Toast.LENGTH_SHORT).show();
            }
        };
        ClickableSpan policy = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUp.this, "Our Privacy Policy", Toast.LENGTH_SHORT).show();
            }
        };
        spannableString.setSpan(terms, 56, 73, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(policy, 78, 92, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        tvTermsPolicy.setText(spannableString);
        tvTermsPolicy.setMovementMethod(LinkMovementMethod.getInstance());


        Button registerBtn = findViewById(R.id.registerBtn_signUp);

        TextView logBtn = findViewById(R.id.logInTxt_signUp);
        logBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent logIntent = new Intent(SignUp.this, SignIn.class);
                startActivity(logIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkConnected()) {
//                    if (isInternetAvailable()){

                    String email = etMail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String confirmPassword = etConfirmPassword.getText().toString().trim();
                    String userName = etUserName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String location = etLocation.getText().toString().trim();

                    if (userName.isEmpty()) {
                        etUserName.setError("User Name Required");

                    } else if (email.isEmpty()) {
                        etMail.setError("E-Mail Address Required");

                    } else if (phone.isEmpty()) {
                        etPhone.setError("Phone Number Required");

                    } else if (phone.length() < 10) {
                        etPhone.setError("Phone Number Too Short");

                    } else if (location.isEmpty()) {
                        etLocation.setError("Location Required");

                    } else if (password.isEmpty()) {
                        etPassword.setError("Password Required");

                    } else if (password.length() < 6) {
                        etPassword.setError("PassWord Too Short");

                    } else if (confirmPassword.isEmpty()) {
                        etConfirmPassword.setError("Confirm Password Required");

                    } else if (!password.equals(confirmPassword)) {
                        etConfirmPassword.setError("Confirm Password Do not Match ");

                    }
//                    }
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
                        currentUser = new userModel(userName, email, phone, location);

                        profileRef.setValue(currentUser);

                        registerUser(email, password);

                    }
//                    }
//                    else {
//
//                        Toast.makeText(SignUp.this, R.string.No_internet, Toast.LENGTH_LONG).show();
//                    }

                } else {
                    Toast.makeText(SignUp.this, R.string.No_network, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        Intent autoIntent = new Intent(this, user.class);
        startActivity(autoIntent);
    }

    private void registerUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(SignUp.this, "Successful Registration...", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                updateUI(user);
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
//                            Toast.makeText(SignUp.this, "Authentication failed. Check Your Details : " + task.getException().getMessage(),
//                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                Toast.makeText(SignUp.this, "Your E mail is already registered Try Log In...", Toast.LENGTH_LONG).show();
                            }

                            if (task.getException().getMessage().equals("The email address is badly formatted.")) {
//                                Toast.makeText(SignUp.this, "Bad Format E mail...", Toast.LENGTH_LONG).show();
                                etMail.setError("Bad Format for E-Mail Address");
                            }
                        }

                        // ...
                    }
                });
    }

    //    @Override
//    protected void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if (currentUser != null){
//            updateUI(currentUser);
//        }
//    }
//    public void signOut() {
//        mAuth.signOut();
//        Intent outIntent = new Intent(getApplicationContext(), SignUp.class);
//        startActivity(outIntent);
//    }
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
}
