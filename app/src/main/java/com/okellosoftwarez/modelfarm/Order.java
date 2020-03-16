package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
    TextView defaultOrderView;
    private int priceSum;
//    private orderModel orderedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        obtaining the order database Reference from the order
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders");
        ordersList = new ArrayList<>();

        payBtn = findViewById(R.id.paymentBtn);
        loadingOrders = findViewById(R.id.loadingOrders);
        defaultOrderView = findViewById(R.id.defaultOrderView);

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

                priceSum = 0;
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()){
                    orderModel orderedProduct = orderSnapshot.getValue(orderModel.class);
                    ordersList.add(orderedProduct);
                    priceSum = priceSum + Integer.parseInt(orderedProduct.prdOrderedTotal);
//                    writePlacedOrders(orderedProduct);
                }
                if (ordersList.isEmpty()){
                    defaultOrderView.setVisibility(View.VISIBLE);
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
                Toast.makeText(Order.this, "Feature Coming Soon... : Clear " + priceSum, Toast.LENGTH_SHORT).show();
                paymentMethod(priceSum);
//                ordersList.clear();
                passingPlacedOrders();
                orderDatabase.removeValue();

                defaultOrderView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void writePlacedOrders(orderModel orderedProduct) {
        DatabaseReference placedRef = FirebaseDatabase.getInstance().getReference("placedOrders");
        String placedKey = placedRef.push().getKey();
        placedRef.child(placedKey).setValue(orderedProduct);
//        List<orderModel> list = new ArrayList<>();
//        list.add(orderedProduct);
    }

    private void passingPlacedOrders() {
//        Intent placedIntent = new Intent(this, placedOrders.class);
//        placedIntent.putExtra("orders", );
        DatabaseReference placedRef = FirebaseDatabase.getInstance().getReference("placedOrders");
        String placedKey = placedRef.push().getKey();
        for (orderModel placedOrder : ordersList) {
            placedRef.child(placedKey).setValue(placedOrder);
        }
//        placedRef.setValue(ordersList);

//        Intent placedIntent = new Intent(this, placedOrders.class);
//        startActivity(placedIntent);

    }

    private void paymentMethod(int priceSum) {

    }
}
