package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class placedOrders extends AppCompatActivity {

    RecyclerView placedRecyclerView;
    LinearLayoutManager placedLayoutManager;
    cartAdapter placedAdapter;
    List<orderModel> placed_productsList;
    ProgressBar placed_progressBar;
    TextView defaultPlacedView;
    DatabaseReference placedReference;
    Button clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_orders);

        Toolbar toolbarPlaced = findViewById(R.id.toolbarPlaced);
        setSupportActionBar(toolbarPlaced);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (isNetworkConnected()){
//            if (isInternetAvailable()){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phoneNo = pref.getString("phone", null);
        if (phoneNo != null) {
            placedReference = FirebaseDatabase.getInstance().getReference("placedOrders").child(phoneNo);
            placed_productsList = new ArrayList<>();

            placed_progressBar = findViewById(R.id.loadingPlacedOrders);
            defaultPlacedView = findViewById(R.id.defaultPlacedView);

            clearBtn = findViewById(R.id.clearOrderBtn);
            placedRecyclerView = findViewById(R.id.placedOrderList);
            placedRecyclerView.setHasFixedSize(true);

            placedLayoutManager = new LinearLayoutManager(this);
            placedRecyclerView.setLayoutManager(placedLayoutManager);

            placedAdapter = new cartAdapter(this, placed_productsList);
            placedRecyclerView.setAdapter(placedAdapter);

            if (isNetworkConnected()) {
                placedReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        placed_productsList.clear();

                        for (DataSnapshot placedShot : dataSnapshot.getChildren()) {
                            orderModel placedOrder = placedShot.getValue(orderModel.class);
                            placed_productsList.add(placedOrder);
                        }

                        if (placed_productsList.isEmpty()) {
                            defaultPlacedView.setVisibility(View.VISIBLE);
                            clearBtn.setVisibility(View.INVISIBLE);
                        }
                        placedAdapter.notifyDataSetChanged();
                        placed_progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(placedOrders.this, "Permission Denied... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        placed_progressBar.setVisibility(View.INVISIBLE);

                    }
                });

                clearBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        placedReference.removeValue();
                    }
                });
            } else {
                placed_progressBar.setVisibility(View.INVISIBLE);
                defaultPlacedView.setVisibility(View.VISIBLE);
                defaultPlacedView.setText(R.string.No_network);
            }
        } else {
            Toast.makeText(this, "Did not Register AS Expected Try Creating a New Account...", Toast.LENGTH_SHORT).show();
        }

//            }
//            else {
//
//                placed_progressBar.setVisibility(View.INVISIBLE);
//                defaultPlacedView.setText(R.string.No_internet);
//                Toast.makeText(placedOrders.this, R.string.No_internet, Toast.LENGTH_LONG).show();
//            }
//
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
}
