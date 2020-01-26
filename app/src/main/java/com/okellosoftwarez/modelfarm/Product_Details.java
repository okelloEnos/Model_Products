package com.okellosoftwarez.modelfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.daraja.Daraja;
import com.squareup.picasso.Picasso;

public class Product_Details extends AppCompatActivity {

    ImageView detail_image;
    TextView tv_name, tv_location, tv_price, tv_capacity, tv_phone, tv_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);

        detail_image = findViewById(R.id.detailImage);
        tv_name = findViewById(R.id.tv_detailName);
        tv_location = findViewById(R.id.tv_detailLocation);
        tv_price = findViewById(R.id.tv_detailPrice);
        tv_capacity = findViewById(R.id.tv_detailCapacity);
        tv_phone = findViewById(R.id.tv_detailPhone);
        tv_email = findViewById(R.id.tv_detailEmail);
        Button paymentBtn = findViewById(R.id.purchase_button);

        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Product_Details.this, "Payment Method ...", Toast.LENGTH_SHORT).show();
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

            }
        });
        receiveDetailIntents();

//        if (Products_view.buttonString.equals("seller")){
//            paymentBtn.setVisibility(View.INVISIBLE);
//        }
    }

    private void receiveDetailIntents() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("phone")
                && getIntent().hasExtra("image") && getIntent().hasExtra("email")
                && getIntent().hasExtra("location") && getIntent().hasExtra("price")
                && getIntent().hasExtra("capacity")){
            String d_name, d_location, d_price, d_capacity, d_phone, d_image, d_email;
            d_name = getIntent().getStringExtra("name");
            d_location = getIntent().getStringExtra("location");
            d_price = getIntent().getStringExtra("price");
            d_capacity = getIntent().getStringExtra("capacity");
            d_phone = getIntent().getStringExtra("phone");
            d_image = getIntent().getStringExtra("image");
            d_email = getIntent().getStringExtra("email");

            Toast.makeText(this, "The Email : " + d_email, Toast.LENGTH_LONG).show();

            assignDetails(d_name, d_phone, d_image, d_email, d_location, d_price, d_capacity);
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
