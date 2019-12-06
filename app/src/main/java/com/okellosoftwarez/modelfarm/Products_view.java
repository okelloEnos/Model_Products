package com.okellosoftwarez.modelfarm;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class Products_view extends AppCompatActivity {
    private static final String TAG = "Products_view";
    DatabaseReference mDatabase;
    RecyclerView recyclerView;
    productAdapter adapter;
    LinearLayoutManager layoutManager;
    List<Products> productsList;
    ProgressBar circleP_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        circleP_bar = findViewById(R.id.progressBarCircle);

//        Obtaining reference to the firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("Products");
        productsList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

//       Initializing and Setting up of layout Manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Initializing and Setting up of adapter
        adapter = new productAdapter(Products_view.this, productsList);
        recyclerView.setAdapter(adapter);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                productsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Products receivedProduct = postSnapshot.getValue(Products.class);
                    productsList.add(receivedProduct);
                }
                adapter.notifyDataSetChanged();
                circleP_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(Products_view.this, "Permission Denied...", Toast.LENGTH_SHORT).show();
                Toast.makeText(Products_view.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                circleP_bar.setVisibility(View.INVISIBLE);
            }
        });

//        Floating Btn to add new Product
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProducts();
            }
        });
    }

//    Opening Main Activity
    private void addProducts() {
        Intent addIntent = new Intent(this, MainActivity.class);

        startActivity(addIntent);

    }
}