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

public class placedOrders extends AppCompatActivity {

    RecyclerView placedRecyclerView;
    LinearLayoutManager placedLayoutManager;
    cartAdapter placedAdapter;
    List<orderModel> placed_productsList;
    ProgressBar placed_progressBar;
    TextView defaultPlacedView;
    DatabaseReference placedReference;
    Button clearBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placed_orders);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phoneNo = pref.getString("phone", null);
        if (phoneNo != null) {
            placedReference = FirebaseDatabase.getInstance().getReference("placedOrders").child(phoneNo);
            placed_productsList = new ArrayList<>();

            placed_progressBar = findViewById(R.id.loadingPlacedOrders);
            defaultPlacedView = findViewById(R.id.defaultPlacedView);

            placedRecyclerView = findViewById(R.id.placedOrderList);
            placedRecyclerView.setHasFixedSize(true);

            placedLayoutManager = new LinearLayoutManager(this);
            placedRecyclerView.setLayoutManager(placedLayoutManager);

            placedAdapter = new cartAdapter(this, placed_productsList);
            placedRecyclerView.setAdapter(placedAdapter);

            placedReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    placed_productsList.clear();

                    for (DataSnapshot placedShot : dataSnapshot.getChildren()) {
                        orderModel placedOrder = placedShot.getValue(orderModel.class);
                        placed_productsList.add(placedOrder);
                    }

                    if (placed_productsList.isEmpty()) {
                        defaultPlacedView.setVisibility(View.VISIBLE);
                    }
                    placedAdapter.notifyDataSetChanged();
                    placed_progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(placedOrders.this, "Permission Denied... " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    placed_progressBar.setVisibility(View.INVISIBLE);

                }
            });


            clearBtn = findViewById(R.id.clearOrderBtn);
            clearBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placedReference.removeValue();
                }
            });
        }
        else {
            Toast.makeText(this, "Did not Register AS Expected Try Creating a New Account...", Toast.LENGTH_SHORT).show();
        }
    }
}
