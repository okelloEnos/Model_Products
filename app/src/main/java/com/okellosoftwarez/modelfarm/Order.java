package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Order extends AppCompatActivity implements cartAdapter.onCartClickListener {

    RecyclerView ordersRecyclerView;
    LinearLayoutManager ordersLayoutManager;
    cartAdapter cartAdapter;
    List<orderModel> ordersList;
    Button payBtn;
    DatabaseReference orderDatabase;
    ProgressBar loadingOrders;
    TextView defaultOrderView;
    private int priceSum, remainder;
//    private orderModel orderedProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Toolbar toolbarOrder = findViewById(R.id.toolbarOrder);
        setSupportActionBar(toolbarOrder);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        remainder = getIntent().getIntExtra("remPrdCapacity", 0);

        Toast.makeText(this, "Rem Capacity :" + remainder, Toast.LENGTH_LONG).show();
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
        cartAdapter.setOnCartClickListener(this);

        orderDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ordersList.clear();

                priceSum = 0;
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    orderModel orderedProduct = orderSnapshot.getValue(orderModel.class);
                    orderedProduct.setPrdOrderKey(orderSnapshot.getKey());
                    ordersList.add(orderedProduct);
                    priceSum = priceSum + Integer.parseInt(orderedProduct.prdOrderedTotal);
//                    writePlacedOrders(orderedProduct);
                }
                if (ordersList.isEmpty()) {
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
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phoneNo = pref.getString("phone", null);
        String buyerLocation = pref.getString("location", null);
        String buyerMail = pref.getString("eMail", null);

        DatabaseReference placedRef = FirebaseDatabase.getInstance().getReference("placedOrders").child(phoneNo);
        DatabaseReference receivedRef = FirebaseDatabase.getInstance().getReference("receivedOrders");

        for (orderModel placedOrder : ordersList) {
            String phone = placedOrder.getPrdOrderPhone();
            String placedKey = placedRef.push().getKey();
//            String placedKey = placedOrder.getPrdOrderKey();
            placedRef.child(placedKey).setValue(placedOrder);


            receivedRef.child(phone).child(placedKey).setValue(placedOrder);
//            receivedRef.child(phone).child("prdOrderLocation").setValue(buyerLocation);
            receivedRef.child(phone).child(placedKey).child("prdOrderLocation").setValue(buyerLocation);
            receivedRef.child(phone).child(placedKey).child("prdOrderedMail").setValue(buyerMail);

            DatabaseReference prodRef = FirebaseDatabase.getInstance().getReference("Products").child(placedOrder.getPrdOrderKey());
            prodRef.child("capacity").setValue(placedOrder.getPrdRemCapacity());

        }
//        placedRef.setValue(ordersList);

//        Intent placedIntent = new Intent(this, placedOrders.class);
//        startActivity(placedIntent);

    }

    private void paymentMethod(int priceSum) {

    }

    @Override
    public void cartItemClick(int position) {
        Toast.makeText(this, "Long Press On An Item To Open A Menu... ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void deleteCartItem(int position) {

        orderModel selectedOrder = ordersList.get(position);
        String selectedOrderKey = selectedOrder.getPrdOrderKey();

        orderDatabase.child(selectedOrderKey).removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Order.this, "Failed To Delete your Current Order...", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Order.this, "Success Product Order Deletion ... ", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void editCartItem(int position) {

//        Toast.makeText(this, "Editing Interface...", Toast.LENGTH_SHORT).show();

        final orderModel selectedOrder = ordersList.get(position);
        final String selectedOrderKey = selectedOrder.getPrdOrderKey();

//        Toast.makeText(this, "Editing Interface..." + selectedOrderKey, Toast.LENGTH_LONG).show();

        AlertDialog.Builder alertCapacity = new AlertDialog.Builder(this);
        alertCapacity.setTitle("Capacity");
        alertCapacity.setMessage("Change the Ordered Capacity in KG : ");

        final EditText capacity = new EditText(this);
        alertCapacity.setView(capacity);

        alertCapacity.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Order.this, "Updates Made...", Toast.LENGTH_SHORT).show();
                String newOrderCapacity = capacity.getText().toString().trim();
                int newOrder = Integer.parseInt(newOrderCapacity);

                String initialCapacity, initialTotal, initialRem;
                initialCapacity = selectedOrder.getPrdOrderedCapacity();
                initialTotal = selectedOrder.getPrdOrderedTotal();
                initialRem = selectedOrder.getPrdRemCapacity();

                int price = Integer.parseInt(initialTotal) / Integer.parseInt(initialCapacity);
                int newTotal = newOrder * price;
                int newRem = (Integer.valueOf(initialRem) + Integer.parseInt(initialCapacity)) - newOrder;

                orderDatabase.child(selectedOrderKey).child("prdOrderedCapacity").setValue(newOrderCapacity);
                orderDatabase.child(selectedOrderKey).child("prdOrderedTotal").setValue(Integer.toString(newTotal));
                orderDatabase.child(selectedOrderKey).child("prdRemCapacity").setValue(Integer.toString(newRem));

//                Toast.makeText(Order.this, "New Price : " + price, Toast.LENGTH_LONG).show();

//                Intent updateIntent = new Intent(getApplicationContext(), Order.class);
//                startActivity(updateIntent);


            }
        });
        alertCapacity.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Order.this, "Updates Cancelled...", Toast.LENGTH_SHORT).show();
            }
        });

        alertCapacity.show();
    }
}
