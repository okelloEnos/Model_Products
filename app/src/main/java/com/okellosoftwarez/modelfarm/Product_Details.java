package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class Product_Details extends AppCompatActivity {

    private String d_price, d_name, d_capacity, d_image, d_phone, d_location, d_email, d_key;
    ImageView detail_image;
    TextView tv_name, tv_location, tv_price, tv_capacity, tv_phone, tv_email;

    private StorageReference orderStorageReference;
    private DatabaseReference orderDatabaseReference;
    private orderModel fullOrder;

    private static final int PHONE_PERMISSION = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);

        Toolbar toolbarDetail = findViewById(R.id.toolbarDetails);
        setSupportActionBar(toolbarDetail);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderStorageReference = FirebaseStorage.getInstance().getReference("Orders");
        orderDatabaseReference = FirebaseDatabase.getInstance().getReference("Orders");

        fullOrder = new orderModel();

        detail_image = findViewById(R.id.detailImage);
        tv_name = findViewById(R.id.tv_detailName);
        tv_location = findViewById(R.id.tv_detailLocation);
        tv_price = findViewById(R.id.tv_detailPrice);
        tv_capacity = findViewById(R.id.tv_detailCapacity);
        tv_phone = findViewById(R.id.tv_detailPhone);
        tv_email = findViewById(R.id.tv_detailEmail);
        Button orderBtn = findViewById(R.id.order_button);

        tv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 23){
                    if (checkedPermission()){
                        Log.e("permission", "Permission Already Granted");
                    }
                    else {
                        requestedPermission();
                    }
                }

                if (checkedPermission()){
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + d_phone));
                    startActivity(callIntent);
                }

//                Intent callIntent = new Intent(Intent.ACTION_DIAL);
//                callIntent.setData(Uri.parse("tel:" + d_phone));

//                if (ActivityCompat.checkSelfPermission(Product_Details.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    Toast.makeText(Product_Details.this, "No Permission", Toast.LENGTH_SHORT).show();
//                    Getting permission
//                    String[] callPerm = {Manifest.permission.CALL_PHONE};

//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(callPerm, PHONE_PERMISSION);
//                        startActivity(callIntent);
//                        Toast.makeText(Product_Details.this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(Product_Details.this, "No Permission granted", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                } else {
//                    startActivity(callIntent);
//                }
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
//                @Provides
//                @Singleton
//                Daraja providesDaraja() {
//                    return Daraja.Builder(Config.CONSUMER_KEY, Config.CONSUMER_SECRET)
//                            .setBusinessShortCode(Config.BUSINESS_SHORTCODE)
//                            .setPassKey(AppUtils.getPassKey())
//                            .setTransactionType(Config.ACCOUNT_TYPE)
//                            .setCallbackUrl(Config.CALLBACK_URL)
//                            .setEnvironment(Environment.SANDBOX)
//                            .build();
//                }
                priceConfirmationDialog();

            }
        });
        receiveDetailIntents();
    }

    private void requestedPermission() {
        ActivityCompat.requestPermissions(Product_Details.this, new String[] { Manifest.permission.CALL_PHONE}, PHONE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PHONE_PERMISSION :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Accepted...", Toast.LENGTH_SHORT).show();
                }
                else {
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

        alert.setTitle("Capacity");
        alert.setMessage("Enter the Capacity in KG :");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                int totalPrice = Integer.valueOf(value) * Integer.valueOf(d_price);

                String price = Integer.toString(totalPrice);

//                Toast.makeText(Product_Details.this, "KES : " + totalPrice + "Compared By : " + price, Toast.LENGTH_SHORT).show();

                uploadingData(totalPrice, value);

                Intent cartIntent = new Intent(Product_Details.this, Order.class);
                cartIntent.putExtra("prdName", d_name);
                cartIntent.putExtra("prdMail", d_email);
                cartIntent.putExtra("prdLocation", d_location);
                cartIntent.putExtra("prdPhone", d_phone);
                cartIntent.putExtra("prdCapacity", value);
                cartIntent.putExtra("prdPrice", price);
                cartIntent.putExtra("remPrdCapacity", (Integer.valueOf(d_capacity) - Integer.valueOf(value)));
                startActivity(cartIntent);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private void uploadingData(int totalPrice, String value) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String loadedPhone = pref.getString("phone", null);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Orders").child(loadedPhone);
//        String orderKey = ref.push().getKey();

//        DatabaseReference orderDatabaseRef = FirebaseDatabase.getInstance().getReference("Products");
//        String orderKey = orderDatabaseRef.push().getKey();

//        fullOrder = new orderModel(d_name, value , Integer.toString(totalPrice), d_image);
//        fullOrder = new orderModel(d_name, value , Integer.toString(totalPrice), d_image, d_phone);
//        fullOrder = new orderModel(d_name, value , Integer.toString(totalPrice), d_image, d_phone, d_location);
        int remCapacity = Integer.valueOf(d_capacity) - Integer.valueOf(value);

        fullOrder = new orderModel(d_name, value, Integer.toString(totalPrice), d_image, d_phone, d_location, d_email, Integer.toString(remCapacity));
        ref.child(d_key).setValue(fullOrder);

        Toast.makeText(this, "Success Upload of Data...", Toast.LENGTH_SHORT).show();
    }

    private void receiveDetailIntents() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("phone")
                && getIntent().hasExtra("image") && getIntent().hasExtra("email")
                && getIntent().hasExtra("location") && getIntent().hasExtra("price")
                && getIntent().hasExtra("capacity") && getIntent().hasExtra("key")) {
//            String  d_email;
            d_name = getIntent().getStringExtra("name");
            d_location = getIntent().getStringExtra("location");
            d_price = getIntent().getStringExtra("price");
            d_capacity = getIntent().getStringExtra("capacity");
            d_phone = getIntent().getStringExtra("phone");
            d_image = getIntent().getStringExtra("image");
            d_email = getIntent().getStringExtra("email");
            d_key = getIntent().getStringExtra("key");

            Toast.makeText(this, "The Email : " + d_email, Toast.LENGTH_LONG).show();

            assignDetails(d_name, d_phone, d_image, d_email, d_location, d_price, d_capacity);

//            uploadOrders();
        }
    }

    private void assignDetails(String d_name, String d_phone, String d_image, String d_email, String d_location, String d_price, String d_capacity) {
        tv_name.setText(d_name);
        tv_phone.setText(d_phone);
        tv_email.setText(d_email);
        tv_location.setText(d_location);
        tv_price.setText(d_price);
        tv_capacity.setText(d_capacity);

        Picasso.with(this).load(d_image)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(detail_image);
    }
}
