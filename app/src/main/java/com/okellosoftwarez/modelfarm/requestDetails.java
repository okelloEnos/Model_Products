package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

                if (Build.VERSION.SDK_INT >= 23) {
                    if (checked_Permission()) {
                        Log.e("permission", "Permission Already Granted");
                    } else {
                        requested_Permission();
                    }
                }

                if (checked_Permission()) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + reqPhone));
                    startActivity(callIntent);
                }
            }
        });
        tv_emailReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reqMailIntent = new Intent(Intent.ACTION_SEND);
                reqMailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{reqMail});
                reqMailIntent.putExtra(Intent.EXTRA_SUBJECT, "YOUR " + reqName.toUpperCase() + " PRODUCT REQUEST ");

                reqMailIntent.setType("message/rfc822");

                startActivity(Intent.createChooser(reqMailIntent, "Choose Mail Client :: "));
            }
        });
    }

    private boolean checked_Permission() {
        int callingPerm = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);

        return callingPerm == PackageManager.PERMISSION_GRANTED;
    }

    private void requested_Permission() {
        ActivityCompat.requestPermissions(requestDetails.this, new String[]{Manifest.permission.CALL_PHONE}, REQ_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Accepted...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                }

                break;
        }

    }

    private void requestDetailIntents() {
        if (getIntent().hasExtra("reqName") && getIntent().hasExtra("reqPhone")
                && getIntent().hasExtra("reqImage") && getIntent().hasExtra("reqMail")
                && getIntent().hasExtra("reqLocation") && getIntent().hasExtra("reqPrice")
                && getIntent().hasExtra("reqCapacity")) {

            reqName = getIntent().getStringExtra("reqName");
            reqLocation = getIntent().getStringExtra("reqLocation");
            reqPrice = getIntent().getStringExtra("reqPrice");
            reqCapacity = getIntent().getStringExtra("reqCapacity");
            reqPhone = getIntent().getStringExtra("reqPhone");
            reqImage = getIntent().getStringExtra("reqImage");
            reqMail = getIntent().getStringExtra("reqMail");

            Toast.makeText(this, "The Buyer Email : " + reqMail, Toast.LENGTH_LONG).show();

            assignDetails(reqName, reqPhone, reqImage, reqMail, reqLocation, reqPrice, reqCapacity);

        }
    }

    private void assignDetails(String d_name, String d_phone, String d_image, String d_email, String d_location, String d_price, String d_capacity) {
        tv_nameReq.setText(d_name);
        tv_phoneReq.setText(d_phone);
        tv_emailReq.setText(d_email);
        tv_locationReq.setText(d_location);
        tv_priceReq.setText(d_price);
        tv_capacityReq.setText(d_capacity);

        Picasso.get().load(d_image)
                .placeholder(R.drawable.back_image)
                .fit()
                .centerCrop()
                .into(detail_ReqImage);
    }

}
