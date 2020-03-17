package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
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

import java.util.ArrayList;
import java.util.List;

public class productRequests extends AppCompatActivity {
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
//        requestAdapter.setOnCartClickListener(this);

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
            Toast.makeText(this, "Did not Register AS Expected Try Creating a New Account...", Toast.LENGTH_SHORT).show();
            ;
        }
    }
}