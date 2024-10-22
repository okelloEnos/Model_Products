package com.okellosoftwarez.modelfarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.okellosoftwarez.modelfarm.adapters.productAdapter;
import com.okellosoftwarez.modelfarm.models.Products;
import com.okellosoftwarez.modelfarm.models.userModel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Products_view extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Products_view";

    public static String buttonString;
    DatabaseReference mDatabase;
    FirebaseStorage storage;
    RecyclerView recyclerView;
    productAdapter adapter;
    LinearLayoutManager layoutManager;
    List<Products> productsList;
    List<Products> filteredProductsList;
    ArrayList<String> nameList;
    ProgressBar circleP_bar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FloatingActionButton fab;
    TextView navMail, navPhone, defaultProductView;
    EditText searchText;
    String pPhone;
    FirebaseAuth signOutmAuth;
    Products personalProduct;
    TextView cartText;

    long cartCount = 0;
    private List<String> commentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        circleP_bar = findViewById(R.id.progressBarCircle);
        defaultProductView = findViewById(R.id.defaultProductView);

        // Initialize Firebase Auth
        signOutmAuth = FirebaseAuth.getInstance();

        personalProduct = new Products();

        searchText = findViewById(R.id.editTextSearch);

        defaultProductView.setVisibility(View.INVISIBLE);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()) {

//                    productSearch(s.toString());
                    settAdapter(s.toString());
                } else {
                    adapter = new productAdapter(Products_view.this, productsList);
                    recyclerView.setAdapter(adapter);
                }
            }
        });
//        Obtaining reference to the firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("Products");

        storage = FirebaseStorage.getInstance();

        commentsList = new ArrayList<>();
        productsList = new ArrayList<>();
        filteredProductsList = new ArrayList<>();
        nameList = new ArrayList<>();
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

//       Initializing and Setting up of layout Manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

//        Initializing and Setting up of adapter
        adapter = new productAdapter(Products_view.this, productsList);
        recyclerView.setAdapter(adapter);

        if (isNetworkConnected()) {
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    productsList.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        String name = postSnapshot.child("name").getValue(String.class);
                        String email = postSnapshot.child("email").getValue(String.class);
                        String capacity = postSnapshot.child("capacity").getValue(String.class);
                        String phone = postSnapshot.child("phone").getValue(String.class);
                        String location = postSnapshot.child("location").getValue(String.class);
                        String image = postSnapshot.child("image").getValue(String.class);
//                        String comments = postSnapshot.child("comments").getValue(String.class);
                        String price = postSnapshot.child("price").getValue(String.class);
                        String ratings = postSnapshot.child("ratings").getValue(String.class);
                        String voters = postSnapshot.child("voters").getValue(String.class);
//                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
//                        Log.d(TAG, "Value is: " + name);

                        final Products receivedProduct = new Products(name, phone, location, image, price, capacity, email, ratings, voters);
                        receivedProduct.setID(postSnapshot.getKey());

                        if (Integer.valueOf(receivedProduct.getCapacity()) > 0) {

                            productsList.add(receivedProduct);
                        } else {
                            StorageReference storeRef = storage.getReferenceFromUrl(receivedProduct.getImage());

                            storeRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mDatabase.child(receivedProduct.getID()).removeValue();
                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Products_view.this, "Error in Deletion", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }

                    if (productsList.isEmpty()) {
                        defaultProductView.setVisibility(View.VISIBLE);
                        circleP_bar.setVisibility(View.INVISIBLE);
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
        } else {
            circleP_bar.setVisibility(View.INVISIBLE);
            defaultProductView.setVisibility(View.VISIBLE);
            defaultProductView.setText(R.string.No_network);
            Toast.makeText(this, "Okello Check out", Toast.LENGTH_LONG).show();
        }
//        Floating Btn to add new Product
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProducts();
            }
        });

        //        Setting up the navigation Drawer and view
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        invalidateOptionsMenu();

        receiveButtonIntents();

//        get navigation Menu
        Menu menu = navigationView.getMenu();

//        Changing Switch User Appropriately
//        find the Item
        MenuItem switchUserItem = menu.findItem(R.id.nav_switchUser);
        String user;
        if (buttonString.equals("buyer")) {
            user = "Seller";
        } else {
            user = "Buyer";
        }
        switchUserItem.setTitle("Switch to " + user);

        //        Listen to the selected item
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        navMail = headerView.findViewById(R.id.navMail);
        navPhone = headerView.findViewById(R.id.navPhone);

        final SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);

        if (pref.getString("phone", null) != null) {
            String loadedNavPhone = pref.getString("phone", null);
            DatabaseReference navRef = FirebaseDatabase.getInstance().getReference("userProfile").child(loadedNavPhone);

            navRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModel navModel = dataSnapshot.getValue(userModel.class);
                    if (navModel.getEmail().isEmpty()) {
                        navMail.setText(pref.getString("eMail", "User@domain.com"));
                    } else {

                        navMail.setText(navModel.getEmail());
                    }
                    if (navModel.getPhone().isEmpty()) {
                        navPhone.setText(pref.getString("phone", "07xxxxxxxx"));
                    } else {
                        navPhone.setText(navModel.getPhone());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(Products_view.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            DatabaseReference orderDatabaseCount = FirebaseDatabase.getInstance().getReference("Orders").child(loadedNavPhone);
            orderDatabaseCount.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    cartCount = dataSnapshot.getChildrenCount();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        personalProductsIntents();

    }

    private void settAdapter(final String queryString) {


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                filteredProductsList.clear();
                recyclerView.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String pName = snapshot.child("name").getValue(String.class);
                    String pEmail = snapshot.child("email").getValue(String.class);
                    String pLocation = snapshot.child("location").getValue(String.class);

                    Products filteredProduct = snapshot.getValue(Products.class);

                    if (pName.equals(null)) {
                        Toast.makeText(Products_view.this, "Name is null", Toast.LENGTH_SHORT).show();
                    } else {
                        if (pName.toLowerCase().contains(queryString.toLowerCase())) {
                            nameList.add(pName);
                            filteredProductsList.add(filteredProduct);
                        }

                        if (pEmail.equals(null)) {
                            Toast.makeText(Products_view.this, "Mail is Null", Toast.LENGTH_SHORT).show();
                        } else {
                            if (pEmail.toLowerCase().contains(queryString.toLowerCase())) {
                                filteredProductsList.add(filteredProduct);
                            }

                            if (pLocation.equals(null)) {
                                Toast.makeText(Products_view.this, "Location is null", Toast.LENGTH_SHORT).show();
                            } else {
                                if (pLocation.toLowerCase().contains(queryString.toLowerCase())) {
                                    filteredProductsList.add(filteredProduct);
                                }
                            }
                        }
                    }
                }

                if (!filteredProductsList.isEmpty()) {
                    defaultProductView.setVisibility(View.INVISIBLE);
                    adapter = new productAdapter(Products_view.this, filteredProductsList);
                    recyclerView.setAdapter(adapter);
                } else {
                    defaultProductView.setVisibility(View.VISIBLE);
                    defaultProductView.setText("No Products based on Your Search Criteria... Try Again");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void personalProductsIntents() {
        if (getIntent().hasExtra("phone") && getIntent().hasExtra("name") && getIntent().hasExtra("location") &&
                getIntent().hasExtra("price") && getIntent().hasExtra("capacity") && getIntent().hasExtra("mail") &&
                getIntent().hasExtra("image") && getIntent().hasExtra("key")) {
            String pName, pLocation, pPrice, pCapacity, pMail, pImage, dKey;
            pPhone = getIntent().getStringExtra("phone");
            pName = getIntent().getStringExtra("name");
            pLocation = getIntent().getStringExtra("location");
            pPrice = getIntent().getStringExtra("price");
            pCapacity = getIntent().getStringExtra("capacity");
            pMail = getIntent().getStringExtra("mail");
            pImage = getIntent().getStringExtra("image");
            dKey = getIntent().getStringExtra("key");

            FirebaseDatabase personalDatabase = FirebaseDatabase.getInstance();
            DatabaseReference personalDatabaseReference = personalDatabase.getReference("personalProducts");
            personalProduct = new Products(pName, pPhone, pLocation, pImage, pPrice, pCapacity, pMail);
            personalDatabaseReference.child(pPhone).child(dKey).setValue(personalProduct);

            Toast.makeText(this, "Personal Orders are SuccessFull...", Toast.LENGTH_SHORT).show();

        }
    }

    //    Opening Main Activity
    private void addProducts() {
        Intent addIntent = new Intent(this, MainActivity.class);

        startActivity(addIntent);

    }

    private void receiveButtonIntents() {
        if (getIntent().hasExtra("button")) {
            buttonString = getIntent().getStringExtra("button");
            Toast.makeText(this, "Which Button : " + buttonString, Toast.LENGTH_LONG).show();

        }
        checkUser(buttonString);
    }

    private void checkUser(String userType) {

        Menu menu = navigationView.getMenu();

        if (userType.equals("buyer")) {

            fab.hide();
//            Removing some features in the navigation view if user is a buyer
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(5).setVisible(false);

        } else {
            menu.getItem(4).setVisible(false);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.products_view, menu);

        final MenuItem cartSellerItem = menu.findItem(R.id.action_add_cart);

        if (buttonString.equals("seller")) {
            cartSellerItem.setVisible(false);
        }


        View cartView = cartSellerItem.getActionView();
        cartText = cartView.findViewById(R.id.cartBadge);

        setUpBadge();

        cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(cartSellerItem);
            }
        });
        return true;
    }

    private void productSearch(final String query) {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Products> filteredList = new ArrayList<>();
                adapter = new productAdapter(Products_view.this, filteredList);
                productsList.clear();

                for (DataSnapshot prdSnapshot : dataSnapshot.getChildren()) {
                    Products receivedProductSnap = prdSnapshot.getValue(Products.class);
//                    receivedProductSnap.setID(prdSnapshot.getKey());
                    if (receivedProductSnap.getName().toLowerCase().contains(query.toLowerCase())) {
                        filteredList.add(receivedProductSnap);
                    }
//                    productsList.add(receivedProduct);
                }
                if (filteredList.isEmpty()) {
                    defaultProductView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                circleP_bar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUpBadge() {

        if (cartText != null) {
            if (cartCount == 0) {
                if (cartText.getVisibility() != View.GONE) {
                    cartText.setVisibility(View.GONE);
                }
            } else {
                cartText.setText(String.valueOf(cartCount));
                if (cartText.getVisibility() != View.VISIBLE) {
                    cartText.setVisibility(View.VISIBLE);
                }
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        //        Handle several selection of products and calculating total
        if (id == R.id.action_add_cart) {
            cartOrderedList();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void cartOrderedList() {
        Intent cartIntent = new Intent(this, Order.class);
        startActivity(cartIntent);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the Profile  action of the user details
            profileView();
        } else if (id == R.id.nav_newProduct) {
            //            Handle how to add a new product to the products view
            addProducts();
        } else if (id == R.id.nav_urProduct) {
            //              Handle how you can display posts belonging to the user
            loadPersonalProducts();
        } else if (id == R.id.nav_help) {
            //              Handle what to be displayed in the help like the frequently asked questions
            Toast.makeText(this, "A Guide Document on What to do ... Coming Soon ", Toast.LENGTH_SHORT).show();
            //        } else if (id == R.id.nav_share) {
        } else if (id == R.id.nav_logOut) {
            //              Handle user logout procedure
            signOut();

        } else if (id == R.id.nav_switchUser) {
            switchUser();
        } else if (id == R.id.nav_prdRequest) {
            //              Handle how you can display posts belonging to the user
            Toast.makeText(this, " Feature Coming Soon ...", Toast.LENGTH_SHORT).show();
            moveToRequestedOrders();
        } else if (id == R.id.nav_prdOrders) {
            //              Handle how you can display posts belonging to the user
            Toast.makeText(this, " Feature Coming Soon ...", Toast.LENGTH_SHORT).show();
            moveToPlacedOrders();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void moveToRequestedOrders() {
        Intent reqIntent = new Intent(this, productRequests.class);
        startActivity(reqIntent);
    }

    private void moveToPlacedOrders() {
        Intent placedIntent = new Intent(this, placedOrders.class);
        startActivity(placedIntent);
    }

    private void loadPersonalProducts() {
        Intent loadIntent = new Intent(this, personalProducts.class);
        startActivity(loadIntent);
    }

    private void signOut() {

        AlertDialog.Builder signOutDialog = new AlertDialog.Builder(this);
        signOutDialog.setTitle("Log Out...");
        signOutDialog.setMessage("Are you Sure you want to exit ?");
        signOutDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOutmAuth.signOut();
                Intent outIntent = new Intent(Products_view.this, SignIn.class);
//        outIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
//                Intent.FLAG_ACTIVITY_CLEAR_TASK |
//                Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(outIntent);
//        finish();
            }
        });
        signOutDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Products_view.this, "Request Cancelled...", Toast.LENGTH_SHORT).show();
            }
        });
        signOutDialog.show();

    }

    private void switchUser() {
        if (buttonString.equals("buyer")) {
            buttonString = "seller";
        } else {
            buttonString = "buyer";
        }
        Intent switchIntent = new Intent(this, Products_view.class);
        startActivity(switchIntent);
    }

    private void profileView() {
        Intent profileIntent = new Intent(this, Profile.class);
        startActivity(profileIntent);
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