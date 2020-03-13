package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity {

    RecyclerView ordersRecyclerView;
    LinearLayoutManager ordersLayoutManager;
    cartAdapter cartAdapter ;
    List<orderModel> ordersList;
    Button payBtn;
    DatabaseReference orderDatabase;
    ProgressBar loadingOrders;

//    private StorageReference orderStorageReference;
//    private DatabaseReference orderDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Toast.makeText(this, "On create", Toast.LENGTH_LONG).show();

//        orderStorageReference = FirebaseStorage.getInstance().getReference("Orders");
//        obtaining the order database Reference from the order
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        ordersList = new ArrayList<>();

        payBtn = findViewById(R.id.paymentBtn);
        loadingOrders = findViewById(R.id.loadingOrders);

//        receiveSelectedOrder(ordersList);
        ordersRecyclerView = findViewById(R.id.cartList);
        ordersRecyclerView.setHasFixedSize(true);

        ordersLayoutManager = new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        cartAdapter = new cartAdapter(this, ordersList);
        ordersRecyclerView.setAdapter(cartAdapter);

        orderDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear();

                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()){
                    orderModel orderedProduct = orderSnapshot.getValue(orderModel.class);
                    ordersList.add(orderedProduct);
                }
                cartAdapter.notifyDataSetChanged();
                loadingOrders.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Order.this, "Permission Denied... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                loadingOrders.setVisibility(View.INVISIBLE);

            }
        });

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Order.this, "Feature Coming Soon...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void receiveSelectedOrder(List<orderModel> ordersList) {
        if (getIntent().hasExtra("prdName") && getIntent().hasExtra("prdCapacity") && getIntent().hasExtra("prdPrice")){
            String prdName, prdCapacity, prdPrice;
            prdName = getIntent().getStringExtra("prdName");
            prdCapacity  = getIntent().getStringExtra("prdCapacity");
            prdPrice  = getIntent().getStringExtra("prdPrice");

            orderModel orderedPrd = new orderModel(prdName, prdCapacity, prdPrice);
//            ordersList = new ArrayList<>();
            ordersList.add(orderedPrd);
            Toast.makeText(this, "Received :" + prdName +"\n"+prdCapacity+"\n"+prdPrice, Toast.LENGTH_LONG).show();
        }
    }
}
