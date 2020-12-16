package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class productRequests extends AppCompatActivity implements cartAdapter.onCartClickListener{
    RecyclerView requestRecyclerView;
    LinearLayoutManager requestLayoutManager;
    cartAdapter requestAdapter;
    List<orderModel> requestList;
    Button clearReqBtn;
    DatabaseReference requestDatabase;
    ProgressBar loadingRequestOrders;
    TextView defaultRequestView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_requests);

        Toolbar toolbarRequest = findViewById(R.id.toolbarRequest);
        setSupportActionBar(toolbarRequest);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (isNetworkConnected()){
//            if (isInternetAvailable()){

                SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
                String phoneNo = pref.getString("phone", null);

                if (phoneNo != null) {
                    requestDatabase = FirebaseDatabase.getInstance().getReference("receivedOrders").child(phoneNo);
                    requestList = new ArrayList<>();

                    clearReqBtn = findViewById(R.id.clearRequestBtn);
                    loadingRequestOrders = findViewById(R.id.loadingRequestOrders);
                    defaultRequestView = findViewById(R.id.defaultRequestView);

                    requestRecyclerView = findViewById(R.id.requestList);
                    requestRecyclerView.setHasFixedSize(true);

                    requestLayoutManager = new LinearLayoutManager(this);
                    requestRecyclerView.setLayoutManager(requestLayoutManager);

                    requestAdapter = new cartAdapter(this, requestList);
                    requestRecyclerView.setAdapter(requestAdapter);
                    requestAdapter.setOnCartClickListener(this);

                    if (isNetworkConnected()) {
                        requestDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                requestList.clear();

//                priceSum = 0;
                                for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
                                    orderModel requestProduct = requestSnapshot.getValue(orderModel.class);
//                    orderedProduct.setPrdOrderKey(orderSnapshot.getKey());
                                    requestList.add(requestProduct);
//                    priceSum = priceSum + Integer.parseInt(orderedProduct.prdOrderedTotal);
//                    writePlacedOrders(orderedProduct);
                                }
                                if (requestList.isEmpty()) {
                                    loadingRequestOrders.setVisibility(View.INVISIBLE);
                                    clearReqBtn.setVisibility(View.INVISIBLE);
                                    defaultRequestView.setVisibility(View.VISIBLE);

                                }
                                requestAdapter.notifyDataSetChanged();
                                loadingRequestOrders.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                Toast.makeText(productRequests.this, "Permission Denied... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                loadingRequestOrders.setVisibility(View.INVISIBLE);

                            }
                        });

                        clearReqBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(productRequests.this, "Feature Coming Soon... : Clearing Requests ", Toast.LENGTH_SHORT).show();
//                paymentMethod(priceSum);
//                ordersList.clear();
//                passingPlacedOrders();
                                requestDatabase.removeValue();

                                defaultRequestView.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        loadingRequestOrders.setVisibility(View.INVISIBLE);
                        defaultRequestView.setVisibility(View.VISIBLE);
                        defaultRequestView.setText(R.string.No_internet);
                    }
                }
                else {
                    Toast.makeText(this, "Did not Register AS Expected Try Creating a New Account...", Toast.LENGTH_SHORT).show();
                    ;
                }

//            }
//            else {

//                loadingRequestOrders.setVisibility(View.INVISIBLE);
//                defaultRequestView.setText(R.string.No_internet);
//                Toast.makeText(productRequests.this, R.string.No_internet, Toast.LENGTH_LONG).show();
//            }

        }
//        else {
//            loadingRequestOrders.setVisibility(View.INVISIBLE);
//            defaultRequestView.setText(R.string.No_network);
//            Toast.makeText(productRequests.this, R.string.No_network, Toast.LENGTH_LONG).show();
//        }

//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//        String phoneNo = pref.getString("phone", null);
//
//        if (phoneNo != null) {
//            requestDatabase = FirebaseDatabase.getInstance().getReference("receivedOrders").child(phoneNo);
//            requestList = new ArrayList<>();
//
//            clearReqBtn = findViewById(R.id.clearRequestBtn);
//            loadingRequestOrders = findViewById(R.id.loadingRequestOrders);
//            defaultRequestView = findViewById(R.id.defaultRequestView);
//
//            requestRecyclerView = findViewById(R.id.requestList);
//            requestRecyclerView.setHasFixedSize(true);
//
//            requestLayoutManager = new LinearLayoutManager(this);
//            requestRecyclerView.setLayoutManager(requestLayoutManager);
//
//            requestAdapter = new cartAdapter(this, requestList);
//            requestRecyclerView.setAdapter(requestAdapter);
//            requestAdapter.setOnCartClickListener(this);
//
//            loadingRequestOrders.setVisibility(View.VISIBLE);
//            requestDatabase.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    requestList.clear();
//
////                priceSum = 0;
//                    for (DataSnapshot requestSnapshot : dataSnapshot.getChildren()) {
//                        orderModel requestProduct = requestSnapshot.getValue(orderModel.class);
////                    orderedProduct.setPrdOrderKey(orderSnapshot.getKey());
//                        requestList.add(requestProduct);
////                    priceSum = priceSum + Integer.parseInt(orderedProduct.prdOrderedTotal);
////                    writePlacedOrders(orderedProduct);
//                    }
//                    if (requestList.isEmpty()) {
//                        loadingRequestOrders.setVisibility(View.INVISIBLE);
//                        clearReqBtn.setVisibility(View.INVISIBLE);
//                        defaultRequestView.setVisibility(View.VISIBLE);
//
//                    }
//                    requestAdapter.notifyDataSetChanged();
//                    loadingRequestOrders.setVisibility(View.INVISIBLE);
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    Toast.makeText(productRequests.this, "Permission Denied... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    loadingRequestOrders.setVisibility(View.INVISIBLE);
//
//                }
//            });
//
//            clearReqBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(productRequests.this, "Feature Coming Soon... : Clearing Requests ", Toast.LENGTH_SHORT).show();
////                paymentMethod(priceSum);
////                ordersList.clear();
////                passingPlacedOrders();
//                    requestDatabase.removeValue();
//
//                    defaultRequestView.setVisibility(View.VISIBLE);
//                }
//            });
//        } else {
//            Toast.makeText(this, "Did not Register AS Expected Try Creating a New Account...", Toast.LENGTH_SHORT).show();
//            ;
//        }
//    }

    @Override
    public void cartItemClick(int position) {

        orderModel requestProduct = requestList.get(position);
        Intent requestIntent = new Intent(this, requestDetails.class);
        requestIntent.putExtra("reqImage", requestProduct.getPrdOrderImage());
        requestIntent.putExtra("reqName", requestProduct.getPrdOrderedName());
        requestIntent.putExtra("reqLocation", requestProduct.getPrdOrderLocation());
        requestIntent.putExtra("reqCapacity", requestProduct.getPrdOrderedCapacity());
        requestIntent.putExtra("reqPrice", requestProduct.getPrdOrderedTotal());
        requestIntent.putExtra("reqPhone", requestProduct.getPrdOrderPhone());
        requestIntent.putExtra("reqMail", requestProduct.getPrdOrderedMail());
        startActivity(requestIntent);
    }

    @Override
    public void deleteCartItem(int position) {

    }

    @Override
    public void editCartItem(int position) {

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