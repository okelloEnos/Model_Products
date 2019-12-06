package com.okellosoftwarez.modelfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class Product_Details extends AppCompatActivity {

    ImageView detail_image;
    TextView tv_name, tv_phone, tv_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product__details);

        detail_image = findViewById(R.id.detailImage);
        tv_name = findViewById(R.id.tv_detailName);
        tv_phone = findViewById(R.id.tv_detailPhone);
        tv_email = findViewById(R.id.tv_detailEmail);

        receiveDetailIntents();
    }

    private void receiveDetailIntents() {
        if (getIntent().hasExtra("name") && getIntent().hasExtra("phone")
                && getIntent().hasExtra("image") && getIntent().hasExtra("email")){
            String d_name, d_phone, d_image, d_email;
            d_name = getIntent().getStringExtra("name");
            d_phone = getIntent().getStringExtra("phone");
            d_image = getIntent().getStringExtra("image");
            d_email = getIntent().getStringExtra("email");

            Toast.makeText(this, "The Email : " + d_email, Toast.LENGTH_LONG).show();

            assignDetails(d_name, d_phone, d_image, d_email);
        }
    }

    private void assignDetails(String d_name, String d_phone, String d_image, String d_email) {
        tv_name.setText(d_name);
        tv_phone.setText(d_phone);
        tv_email.setText(d_email);

        Picasso.with(this).load(d_image).into(detail_image);
    }
}
