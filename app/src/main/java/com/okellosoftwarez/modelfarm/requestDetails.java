package com.okellosoftwarez.modelfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class requestDetails extends AppCompatActivity {

    private String reqPrice, reqName, reqCapacity, reqImage, reqPhone, reqLocation, reqMail;
    ImageView detail_ReqImage;
    TextView tv_nameReq, tv_locationReq, tv_priceReq, tv_capacityReq, tv_phoneReq, tv_emailReq;

    private static final int REQ_PERMISSION = 31;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        Toolbar toolbarDetailReq = findViewById(R.id.toolbarDetailsReq);
        setSupportActionBar(toolbarDetailReq);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        detail_ReqImage = findViewById(R.id.detailImageRequest);
        tv_nameReq = findViewById(R.id.tv_detailNameRequest);
        tv_locationReq = findViewById(R.id.tv_detailLocationRequest);
        tv_capacityReq = findViewById(R.id.tv_detailCapacityRequest);
        tv_priceReq = findViewById(R.id.tv_detailPriceRequest);
        tv_phoneReq = findViewById(R.id.tv_detailPhoneRequest);
        tv_emailReq = findViewById(R.id.tv_detailEmailRequest);


        requestDetailIntents();

        tv_phoneReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reqIntent = new Intent(Intent.ACTION_DIAL);
                reqIntent.setData(Uri.parse("tel:" + reqPhone));

                if (ActivityCompat.checkSelfPermission(requestDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                    String[] phonePerm = {Manifest.permission.CALL_PHONE};

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        requestPermissions(phonePerm, REQ_PERMISSION);
                        Toast.makeText(requestDetails.this, "Permission Granted", Toast.LENGTH_SHORT).show();

                        startActivity(reqIntent);
                    }
                    else {
                        Toast.makeText(requestDetails.this, "No Permission...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                else {
                    startActivity(reqIntent);
                }
            }
        });
        tv_emailReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reqMailIntent = new Intent(Intent.ACTION_SEND);
                reqMailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { reqMail});
                reqMailIntent.putExtra(Intent.EXTRA_SUBJECT, "YOUR " + reqName.toUpperCase() + " PRODUCT REQUEST ");

                reqMailIntent.setType("message/rfc822");

                startActivity(Intent.createChooser(reqMailIntent, "Choose Mail Client :: "));
            }
        });
    }

    private void requestDetailIntents() {
        if (getIntent().hasExtra("reqName") && getIntent().hasExtra("reqPhone")
                && getIntent().hasExtra("reqImage") && getIntent().hasExtra("reqMail")
                && getIntent().hasExtra("reqLocation") && getIntent().hasExtra("reqPrice")
                && getIntent().hasExtra("reqCapacity")){
//            String  d_email;
            reqName = getIntent().getStringExtra("reqName");
            reqLocation = getIntent().getStringExtra("reqLocation");
            reqPrice = getIntent().getStringExtra("reqPrice");
            reqCapacity = getIntent().getStringExtra("reqCapacity");
            reqPhone = getIntent().getStringExtra("reqPhone");
            reqImage = getIntent().getStringExtra("reqImage");
            reqMail = getIntent().getStringExtra("reqMail");

            Toast.makeText(this, "The Buyer Email : " + reqMail, Toast.LENGTH_LONG).show();

            assignDetails(reqName, reqPhone, reqImage, reqMail, reqLocation, reqPrice, reqCapacity);

//            uploadOrders();
        }
    }

    private void assignDetails(String d_name, String d_phone, String d_image, String d_email, String d_location, String d_price, String d_capacity) {
        tv_nameReq.setText(d_name);
        tv_phoneReq.setText(d_phone);
        tv_emailReq.setText(d_email);
        tv_locationReq.setText(d_location);
        tv_priceReq.setText(d_price);
        tv_capacityReq.setText(d_capacity);

        Picasso.with(this).load(d_image)
                .placeholder(R.mipmap.ic_launcher)
                .fit()
                .centerCrop()
                .into(detail_ReqImage);
    }

}
