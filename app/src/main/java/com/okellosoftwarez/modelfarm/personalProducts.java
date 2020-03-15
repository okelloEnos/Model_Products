package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class personalProducts extends AppCompatActivity {

    RecyclerView personalRecyclerView;
    LinearLayoutManager personalLayoutManager;
    personalAdapter personalAdapter;
    List<Products> personal_productsList;
    ProgressBar personal_progressBar;

    DatabaseReference personalReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_prducts);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String recPersonalPhone = "No";

        if (getIntent().hasExtra("pPhone")) {
            recPersonalPhone = getIntent().getStringExtra("pPhone");
            personalReference = FirebaseDatabase.getInstance().getReference("personalProducts").child(recPersonalPhone);


//        if (recPersonalPhone.equals(null)){

//            recPersonalPhone = pref.getString("phone", null);
//        }
//        Obtaining the reference of personal database
//        personalReference = FirebaseDatabase.getInstance().getReference("personalProducts").child(recPersonalPhone);
            personal_productsList = new ArrayList<>();

            personal_progressBar = findViewById(R.id.loadingPersonalProducts);

            personalRecyclerView = findViewById(R.id.personalList);
            personalRecyclerView.setHasFixedSize(true);

            personalLayoutManager = new LinearLayoutManager(this);
            personalRecyclerView.setLayoutManager(personalLayoutManager);

            personalAdapter = new personalAdapter(this, personal_productsList);
            personalRecyclerView.setAdapter(personalAdapter);

            personalReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    personal_productsList.clear();

                    for (DataSnapshot personalSnapShot : dataSnapshot.getChildren()) {
                        Products personalProduct = personalSnapShot.getValue(Products.class);
                        personal_productsList.add(personalProduct);
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

            Toast.makeText(this, "Nothing to Show", Toast.LENGTH_LONG).show();
        }
    }
}
