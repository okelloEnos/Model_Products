package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

    private String d_price, d_name, d_capacity, d_image;
    ImageView detail_image;
    TextView tv_name, tv_location, tv_price, tv_capacity, tv_phone, tv_email;

    private StorageReference orderStorageReference;
    private DatabaseReference orderDatabaseReference;
    private orderModel fullOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);

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
                cartIntent.putExtra("prdName", d_name );
                cartIntent.putExtra("prdCapacity", value);
                cartIntent.putExtra("prdPrice", price);
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
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Orders");
        String orderKey = ref.push().getKey();

        fullOrder = new orderModel(d_name, value , Integer.toString(totalPrice), d_image);
        ref.child(orderKey).setValue(fullOrder);

        Toast.makeText(this, "Success Upload of Data...", Toast.LENGTH_SHORT).show();
    }

    private void receiveDetailIntents() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("phone")
                && getIntent().hasExtra("image") && getIntent().hasExtra("email")
                && getIntent().hasExtra("location") && getIntent().hasExtra("price")
                && getIntent().hasExtra("capacity")){
            String d_location, d_phone, d_email;
            d_name = getIntent().getStringExtra("name");
            d_location = getIntent().getStringExtra("location");
            d_price = getIntent().getStringExtra("price");
            d_capacity = getIntent().getStringExtra("capacity");
            d_phone = getIntent().getStringExtra("phone");
            d_image = getIntent().getStringExtra("image");
            d_email = getIntent().getStringExtra("email");

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
