package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
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
    List<NotificationModel> notificationList;
    Button payBtn;
    DatabaseReference orderDatabase;
    ProgressBar loadingOrders;
    TextView defaultOrderView;
    private int priceSum, remainder;
//    private orderModel orderedProduct;

    private static final int NOT_PERMISSION = 60 ;

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

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String loadPhone = pref.getString("phone", null);
//        obtaining the order database Reference from the order
        orderDatabase = FirebaseDatabase.getInstance().getReference("Orders").child(loadPhone);
        ordersList = new ArrayList<>();
        notificationList = new ArrayList<>();

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
                notificationList.clear();

                priceSum = 0;
                for (DataSnapshot orderSnapshot : dataSnapshot.getChildren()) {
                    orderModel orderedProduct = orderSnapshot.getValue(orderModel.class);
                    orderedProduct.setPrdOrderKey(orderSnapshot.getKey());
                    ordersList.add(orderedProduct);
                    priceSum = priceSum + Integer.parseInt(orderedProduct.prdOrderedTotal);
//                    writePlacedOrders(orderedProduct);

                    NotificationModel  notificationModel = new NotificationModel(orderedProduct.getPrdOrderPhone(), orderedProduct.getPrdOrderedTotal(), orderedProduct.getPrdOrderedName());
                    notificationList.add(notificationModel);
                }
                if (ordersList.isEmpty()) {
                    defaultOrderView.setVisibility(View.VISIBLE);
//                    payBtn.setEnabled(false);
                    payBtn.setVisibility(View.INVISIBLE);
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

//                checkingMessagePermission();
                sendOrderNotification();
                defaultOrderView.setVisibility(View.VISIBLE);
            }
        });
    }

//    private boolean checkingMessagePermission() {
//        if (Build.VERSION.SDK_INT >= 23){
//            if (check_permission()){
//                Log.e("Permission", "Permission Granted");
//            }
//            else {
//                request_permission();
//
//            }
//
//        }
//    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case NOT_PERMISSION :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, "Permission Accepted...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Permission Denied...", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void request_permission() {

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS}, NOT_PERMISSION);

    }

    private boolean check_permission() {
        int results = ContextCompat.checkSelfPermission(Order.this, Manifest.permission.SEND_SMS);

        if (results == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        else {
            return false;
        }
    }

    private void sendOrderNotification() {

        if (Build.VERSION.SDK_INT >= 23){
            if (check_permission()){
                Log.e("Permission", "Permission Granted");
            }
            else {
                request_permission();

            }

        }
        else {
            Toast.makeText(this, "Version is Lower than 23", Toast.LENGTH_LONG).show();
        }

        for (NotificationModel nModel : notificationList) {
            String notPhone = nModel.getnPhone();
            String notTotal = nModel.getnTotal();
            String notName = nModel.getnName();

            if (!TextUtils.isEmpty(notPhone) && !TextUtils.isEmpty(notTotal) && !TextUtils.isEmpty(notName)){

                if (check_permission()){
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(notPhone, null, "Product Request : " + notName + " Received and Totalling : " + notTotal, null, null);

                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }

        }
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
