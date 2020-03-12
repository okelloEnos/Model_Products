package com.okellosoftwarez.modelfarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity {

    RecyclerView ordersRecyclerView;
    LinearLayoutManager ordersLayoutManager;
    cartAdapter cartAdapter ;
    List<orderModel> ordersList;
    Button payBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toast.makeText(this, "On create", Toast.LENGTH_LONG).show();

        payBtn = findViewById(R.id.paymentBtn);
        ordersList = new ArrayList<>();
        receiveSelectedOrder(ordersList);
        ordersRecyclerView = findViewById(R.id.cartList);
        ordersRecyclerView.setHasFixedSize(true);

        ordersLayoutManager = new LinearLayoutManager(this);
        ordersRecyclerView.setLayoutManager(ordersLayoutManager);

        cartAdapter = new cartAdapter(this, ordersList);
        ordersRecyclerView.setAdapter(cartAdapter);

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

    @Override
    protected void onStart() {
        Toast.makeText(this, "on Start", Toast.LENGTH_LONG).show();
        super.onStart();
        Toast.makeText(this, "After On Start", Toast.LENGTH_LONG).show();
//        receiveSelectedOrder();
        cartAdapter.notifyDataSetChanged();
    }
}
