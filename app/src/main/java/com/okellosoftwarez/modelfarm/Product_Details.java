package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.okellosoftwarez.modelfarm.models.Products;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

public class Product_Details extends AppCompatActivity implements RatingDialogListener {

    private String d_price, d_name, d_capacity, d_image, d_phone, d_location, d_email, d_key, d_ratings, d_voters, d_comments;
    ImageView detail_image;
    TextView tv_name, tv_location, tv_price, tv_capacity, tv_phone, tv_email, tv_comments;
    RatingBar ratingBar;
    private DatabaseReference databaseReference;
    private orderModel fullOrder;

    private static final int PHONE_PERMISSION = 30;
    private int capacityRem;
    private String price;
    private String value;
    private String comts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);

        Toolbar toolbarDetail = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbarDetail);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        fullOrder = new orderModel();

        detail_image = findViewById(R.id.detailImage);
        tv_name = findViewById(R.id.tv_detailName);
        tv_location = findViewById(R.id.tv_detailLocation);
        tv_price = findViewById(R.id.tv_detailPrice);
        tv_capacity = findViewById(R.id.tv_detailCapacity);
        tv_phone = findViewById(R.id.tv_detailPhone);
        tv_email = findViewById(R.id.tv_detailEmail);
        tv_comments = findViewById(R.id.tv_Comments);
        Button orderBtn = findViewById(R.id.order_button);
        ratingBar = findViewById(R.id.RatingBar);

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkedPermission()) {
                        Log.e("permission", "Permission Already Granted");
                    } else {
                        requestedPermission();
                    }
                }

                if (checkedPermission()) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + d_phone));
                    startActivity(callIntent);
                }
            }
        });

        tv_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mailIntent = new Intent(Intent.ACTION_SEND);
                mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{d_email});
                mailIntent.putExtra(Intent.EXTRA_SUBJECT, "PLACING " + d_name.toUpperCase() + "PRODUCT ORDER ");
                mailIntent.setType("message/rfc822");

                startActivity(Intent.createChooser(mailIntent, "Choose an Email Client : "));
            }
        });
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Product_Details.this, "Making Order ...", Toast.LENGTH_SHORT).show();
//                ratingDialog();
                priceConfirmationDialog();

            }
        });
        receiveDetailIntents();
    }

    private void ratingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Send review")
                .setNeutralButtonText("Later")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not good", "Quite ok", "Very Good", "Excellent !!!"))
                .setDefaultRating(0)
                .setTitle("Rate this Product")
                .setDescription("Please select some stars and give your feedback")
                .setCommentInputEnabled(true)
                .setDefaultComment("This product is pretty cool !")
                .setStarColor(R.color.colorAccent)
                .setNoteDescriptionTextColor(R.color.colorPrimaryDark)
                .setTitleTextColor(R.color.colorPrimaryDark)
                .setDescriptionTextColor(R.color.colorPrimaryDark)
                .setHint("Please write your comment here ...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.MyDialogFadeAnimation)
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .create(Product_Details.this)
                .show();
    }

    private void requestedPermission() {
        ActivityCompat.requestPermissions(Product_Details.this, new String[]{Manifest.permission.CALL_PHONE}, PHONE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PHONE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Accepted...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    private boolean checkedPermission() {
        int callingPerm = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

        return callingPerm == PackageManager.PERMISSION_GRANTED;
    }

    private void priceConfirmationDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(Html.fromHtml("<font color='#0A810F'> Capacity </font>"));
        alert.setMessage(Html.fromHtml("<font color='#0A810F'> Enter the Capacity in KG : </font>"));

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                value = input.getText().toString();

                try {

                    if (Integer.valueOf(value) >= 1) {

                        int totalPrice = Integer.valueOf(value) * Integer.valueOf(d_price);

                        price = Integer.toString(totalPrice);

                        capacityRem = (Integer.valueOf(d_capacity) - Integer.valueOf(value));

                        if (capacityRem >= 0) {

                            uploadingData(totalPrice, value, String.valueOf(capacityRem));

                            tv_capacity.setText(String.valueOf(capacityRem));
//uncomment
//                            ratingDialog();
//                            proceedToOrders(0, "empty", 0);
                            Intent cartIntent = new Intent(Product_Details.this, Order.class);
                            cartIntent.putExtra("prdName", d_name);
                            cartIntent.putExtra("prdMail", d_email);
                            cartIntent.putExtra("prdLocation", d_location);
                            cartIntent.putExtra("prdPhone", d_phone);
                            cartIntent.putExtra("prdCapacity", value);
                            cartIntent.putExtra("prdPrice", price);
                            cartIntent.putExtra("remPrdCapacity", capacityRem);

                            Products ratedProduct = new Products(d_name, d_phone, d_location, d_image, d_price, d_capacity, d_email, "nulled", "nulled", "nulled");
//        updateRef.child(editKey).setValue(updatedProduct);
                            databaseReference.child(d_key).setValue(ratedProduct);
                            startActivity(cartIntent);

                        } else {
                            Toast.makeText(Product_Details.this, "Not Possible For Excess Order : Capacity Available is : " + d_capacity, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(Product_Details.this, "Capacity cannot be less than 1", Toast.LENGTH_SHORT).show();
                    }
//                startActivity(cartIntent);
                } catch (NumberFormatException e) {
                    Toast.makeText(Product_Details.this, value + " Is not a Valid Number input", Toast.LENGTH_SHORT).show();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled. uncomment
//                ratingDialog();
            }
        });

        alert.show();
    }

    private void uploadingData(int totalPrice, String value, String capRem) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String loadedPhone = pref.getString("phone", null);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Orders").child(loadedPhone);

        DatabaseReference perPref = database.getReference("personalProducts").child(d_phone).child(d_key);
        perPref.child("capacity").setValue(capRem);
        int remCapacity = Integer.valueOf(d_capacity) - Integer.valueOf(value);

        fullOrder = new orderModel(d_name, value, Integer.toString(totalPrice), d_image, d_phone, d_location, d_email, Integer.toString(remCapacity));
        ref.child(d_key).setValue(fullOrder);

        Toast.makeText(this, "Success Upload of Data...", Toast.LENGTH_SHORT).show();
    }

    private void receiveDetailIntents() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("phone")
                && getIntent().hasExtra("image") && getIntent().hasExtra("email")
                && getIntent().hasExtra("location") && getIntent().hasExtra("price")
                && getIntent().hasExtra("capacity") && getIntent().hasExtra("key")
                && getIntent().hasExtra("ratings") && getIntent().hasExtra("voters")
                && getIntent().hasExtra("comments")) {

            d_name = getIntent().getStringExtra("name");
            d_location = getIntent().getStringExtra("location");
            d_price = getIntent().getStringExtra("price");
            d_capacity = getIntent().getStringExtra("capacity");
            d_phone = getIntent().getStringExtra("phone");
            d_image = getIntent().getStringExtra("image");
            d_email = getIntent().getStringExtra("email");
            d_key = getIntent().getStringExtra("key");
            d_ratings = getIntent().getStringExtra("ratings");
            d_voters = getIntent().getStringExtra("voters");
            d_comments = getIntent().getStringExtra("comments");

            Toast.makeText(this, "The Email : " + d_email, Toast.LENGTH_LONG).show();

            assignDetails(d_name, d_phone, d_image, d_email, d_location, d_price, d_capacity, d_ratings, d_voters, d_key);

        }
    }

    private void assignDetails(String d_name, String d_phone, String d_image, String d_email, String d_location,
                               String d_price, String d_capacity, String d_ratings, String d_voters, String d_key) {
        tv_name.setText(d_name);
        tv_phone.setText(d_phone);
        tv_email.setText(d_email);
        tv_location.setText(d_location);
        tv_price.setText(d_price);
        tv_capacity.setText(d_capacity);

        Picasso.get().load(d_image)
                .placeholder(R.drawable.back_image)
                .fit()
                .centerCrop()
                .into(detail_image);

        if (d_ratings != null) {
            float rate, ratingsFloat, ratingsVoters;
            if (d_voters == null){
                d_voters = Integer.toString(0);
            }
            ratingsFloat = Float.parseFloat(d_ratings);
            ratingsVoters = Float.parseFloat(d_voters);
            rate = ratingsFloat / ratingsVoters;
            ratingBar.setRating(rate);
        }
        else {
            ratingBar.setRating(0);
        }


        databaseReference.child(d_key).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot commentSnap :
                        dataSnapshot.getChildren()) {
                    comts = commentSnap.getValue(String.class);
                }
                tv_comments.setText(comts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (d_comments != null){
            tv_comments.setText("problem");
        }
        else {
            tv_comments.setText("nothing");
        }

    }

    @Override
    public void onNegativeButtonClicked() {
        int count, newRate;
        if (d_voters != null && d_ratings != null) {
            count = Integer.parseInt(d_voters);
            newRate = Integer.parseInt(d_ratings);
        }
        else {
            count = 0;
            newRate = 0;

        }
        proceedToOrders(newRate, "", count);
    }

    private void proceedToOrders(int i, String s, int count) {
        Intent cartIntent = new Intent(Product_Details.this, Order.class);
        cartIntent.putExtra("prdName", d_name);
        cartIntent.putExtra("prdMail", d_email);
        cartIntent.putExtra("prdLocation", d_location);
        cartIntent.putExtra("prdPhone", d_phone);
        cartIntent.putExtra("prdCapacity", value);
        cartIntent.putExtra("prdPrice", price);
        cartIntent.putExtra("remPrdCapacity", capacityRem);


        Products ratedProduct = new Products(d_name, d_phone, d_location, d_image, d_price, d_capacity, d_email, Integer.toString(i), Integer.toString(count), s);
//        updateRef.child(editKey).setValue(updatedProduct);
        databaseReference.child(d_key).setValue(ratedProduct);

        String commentsKey = databaseReference.child(d_key).child("comments").push().getKey();

        String cKey = databaseReference.push().getKey();
//        comment commentInfo = new comment(s);
//        databaseReference.child(d_key).setValue(ratedProduct);
        databaseReference.child(d_key).child("comments").child(commentsKey).setValue(s);

        startActivity(cartIntent);
    }

    @Override
    public void onNeutralButtonClicked() {
//        priceConfirmationDialog();
        int count, newRate;
        if (d_voters != null && d_ratings != null) {
            count = Integer.parseInt(d_voters);
            newRate = Integer.parseInt(d_ratings);
//            count = count + 1;
//            newRate = newRate + i;
        }
        else {
            count = 0;
            newRate = 0;

        }
        proceedToOrders(newRate, "", count);
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        Toast.makeText(this, "Rating : "+ i + "  : Comment : " + s, Toast.LENGTH_SHORT).show();
//        priceConfirmationDialog();
        int count, newRate;
        if (d_voters != null && d_ratings != null) {
            count = Integer.parseInt(d_voters);
            newRate = Integer.parseInt(d_ratings);
            count = count + 1;
            newRate = newRate + i;
        }
        else {
            count = 1;
            newRate = i;

        }
        proceedToOrders(newRate, s, count);
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Toast.makeText(this, "Back Pressed...", Toast.LENGTH_SHORT).show();
//    }
}
