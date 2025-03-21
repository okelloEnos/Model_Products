package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
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
import com.okellosoftwarez.modelfarm.adapters.personalAdapter;
import com.okellosoftwarez.modelfarm.models.Products;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class personalProducts extends AppCompatActivity implements com.okellosoftwarez.modelfarm.adapters.personalAdapter.onItemClickListener {

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

        Toolbar toolbarPersonal = findViewById(R.id.toolbarPersonal);
        setSupportActionBar(toolbarPersonal);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        if (isNetworkConnected()){
//            if (isInternetAvailable()){

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phoneNo = pref.getString("phone", null);
        if (!phoneNo.equals(null)) {
            personalReference = FirebaseDatabase.getInstance().getReference("personalProducts").child(phoneNo);

            personalStorage = FirebaseStorage.getInstance();

            delProductRef = FirebaseDatabase.getInstance().getReference("Products");

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

            if (isNetworkConnected()) {
                personalValueEventListener = personalReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        personal_productsList.clear();

                        for (DataSnapshot personalSnapShot : dataSnapshot.getChildren()) {
                            Products personalProduct = personalSnapShot.getValue(Products.class);
                            personalProduct.setID(personalSnapShot.getKey());

                            if (Integer.valueOf(personalProduct.getCapacity()) > 0) {

                                personal_productsList.add(personalProduct);
                            } else {
                                Toast.makeText(personalProducts.this, "Your : " + personalProduct.getName() + " Product is out of stock", Toast.LENGTH_SHORT).show();

                            }
                        }
                        if (personal_productsList.isEmpty()) {
                            defaultView.setVisibility(View.VISIBLE);
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
//            else {
//
//                personal_progressBar.setVisibility(View.INVISIBLE);
//                defaultView.setText(R.string.No_internet);
//                Toast.makeText(personalProducts.this, R.string.No_internet, Toast.LENGTH_LONG).show();
//            }
            } else {

                personal_progressBar.setVisibility(View.INVISIBLE);
                defaultView.setVisibility(View.VISIBLE);
                defaultView.setText(R.string.No_network);
                Toast.makeText(personalProducts.this, R.string.No_network, Toast.LENGTH_LONG).show();
            }
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
    }

    public static AlertDialog displayMobileDataSettingDialog(final Activity activity, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("NO INTERNET");
        builder.setMessage("Please connect to your internet");
        builder.setPositiveButton("WiFi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                dialog.cancel();
            }
        });

        builder.setNegativeButton("Mobile Data", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent mobileIntent = new Intent();
                mobileIntent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                dialog.cancel();
                context.startActivity(mobileIntent);
                activity.finish();
            }
        });
        builder.show();

        return builder.create();
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    //    This method checks whether mobile is connected to internet and returns true if connected:
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


    //    This method actually checks if device is connected to internet(There is a possibility it's connected to a network but not to internet).
    public boolean isInternetAvailable() {
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");

            return !ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }
}
