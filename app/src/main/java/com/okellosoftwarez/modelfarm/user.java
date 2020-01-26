package com.okellosoftwarez.modelfarm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class user extends AppCompatActivity {

    private static final String TAG = "Buyer_Seller";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: on Create started ...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Button buyerBtn, sellerBtn;

        buyerBtn = findViewById(R.id.buyerBtn);
        sellerBtn = findViewById(R.id.sellerBtn);

        buyerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Buyer1 Button Clicked");
                Intent buyerIntent = new Intent(user.this, Products_view.class);
                buyerIntent.putExtra("button", "buyer");
                startActivity(buyerIntent);
            }
        });

        sellerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "seller Button Clicked");
                Intent sellerIntent = new Intent(user.this, Products_view.class);
                sellerIntent.putExtra("button", "seller");
                startActivity(sellerIntent);

            }
        });
    }
}
