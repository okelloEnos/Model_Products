package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

public class personalProducts extends AppCompatActivity implements personalAdapter.onItemClickListener{

    RecyclerView personalRecyclerView;
    LinearLayoutManager personalLayoutManager;
    personalAdapter personalAdapter;
    List<Products> personal_productsList;
    ProgressBar personal_progressBar;
    TextView defaultView;

    DatabaseReference personalReference, delProductRef;
    FirebaseStorage personalStorage;

    private ValueEventListener personalValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_prducts);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//        String recPersonalPhone = "No";

//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phoneNo = pref.getString("phone", null);
//        Toast.makeText(this, "Phone :" + phoneNo, Toast.LENGTH_SHORT).show();
        if (!phoneNo.equals(null)){
//        if (getIntent().hasExtra("pPhone")) {
//            recPersonalPhone = getIntent().getStringExtra("pPhone");
            personalReference = FirebaseDatabase.getInstance().getReference("personalProducts").child(phoneNo);

            personalStorage = FirebaseStorage.getInstance();

            delProductRef = FirebaseDatabase.getInstance().getReference("Products");

//        if (recPersonalPhone.equals(null)){

//            recPersonalPhone = pref.getString("phone", null);
//        }
//        Obtaining the reference of personal database
//        personalReference = FirebaseDatabase.getInstance().getReference("personalProducts").child(recPersonalPhone);
            personal_productsList = new ArrayList<>();

            personal_progressBar = findViewById(R.id.loadingPersonalProducts);
            defaultView = findViewById(R.id.defaultPersonalView);


            personalRecyclerView = findViewById(R.id.personalList);
            personalRecyclerView.setHasFixedSize(true);

            personalLayoutManager = new LinearLayoutManager(this);
            personalRecyclerView.setLayoutManager(personalLayoutManager);

            personalAdapter = new personalAdapter(this, personal_productsList);
            personalRecyclerView.setAdapter(personalAdapter);
            personalAdapter.setOnItemClickListener(this);

            personalValueEventListener = personalReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    personal_productsList.clear();

                    for (DataSnapshot personalSnapShot : dataSnapshot.getChildren()) {
                        Products personalProduct = personalSnapShot.getValue(Products.class);
                        personalProduct.setID(personalSnapShot.getKey());
                        personal_productsList.add(personalProduct);
                    }
                    if (personal_productsList.isEmpty()){
                        defaultView.setVisibility(View.VISIBLE);
//                        Toast.makeText(personalProducts.this, "Nothing to Show...", Toast.LENGTH_SHORT).show();
                    }
                    personalAdapter.notifyDataSetChanged();
                    personal_progressBar.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(personalProducts.this, "Permission Denied... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    personal_progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {

            Toast.makeText(this, "Did not Register AS Expected Try Creating a New Account...", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemClick(int position) {

        Toast.makeText(this, "Long Press On An Item To Open A Menu... ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDeleteItem(int position) {
//        Toast.makeText(this, "Delete Click... " + position, Toast.LENGTH_SHORT).show();
    Products selectedProduct = personal_productsList.get(position);
    final String selectedKey = selectedProduct.getID();

        StorageReference selectedRef = personalStorage.getReferenceFromUrl(selectedProduct.getImage());
        selectedRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                personalReference.child(selectedKey).removeValue();
                delProductRef.child(selectedKey).removeValue();
                Toast.makeText(personalProducts.this, "Product Deleted...", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(personalProducts.this, "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void editProductDetails(int position) {
        Products selectedProduct = personal_productsList.get(position);
        String selectedKey = selectedProduct.getID();

        Intent updateIntent = new Intent(this, MainActivity.class);
        updateIntent.putExtra("editKey", selectedKey);
        updateIntent.putExtra("editImage", selectedProduct.getImage());
        updateIntent.putExtra("editName", selectedProduct.getName());
        updateIntent.putExtra("editLocation", selectedProduct.getLocation());
        updateIntent.putExtra("editPrice", selectedProduct.getPrice());
        updateIntent.putExtra("editCapacity", selectedProduct.getCapacity());
        updateIntent.putExtra("editMail", selectedProduct.getEmail());
        updateIntent.putExtra("editPhone", selectedProduct.getPhone());
        startActivity(updateIntent);
//        Toast.makeText(this, "Whatever Click... " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        personalReference.removeEventListener(personalValueEventListener);
    }
}
