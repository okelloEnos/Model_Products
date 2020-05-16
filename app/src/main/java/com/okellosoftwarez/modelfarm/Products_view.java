package com.okellosoftwarez.modelfarm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class Products_view extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Products_view";

    public static String buttonString;
    DatabaseReference mDatabase;
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
//        navMail = findViewById(R.id.navMail);
//        navPhone = findViewById(R.id.navPhone);

//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//
//        if (pref.getString("eMail", null) != null){
//            navMail.setText(pref.getString("eMail", null));
//        }
//        if (pref.getString("phone", null) != null){
//            navPhone.setText(pref.getString("phone", null));
//        }

//        String phone = "NoPath";
//        if (getIntent().hasExtra("phone")){
//            phone = getIntent().getStringExtra("phone");
//            sendIntents_details(buttonString);
//            Toast.makeText(this, "Phone No : " + phone, Toast.LENGTH_LONG).show();

//

        personalProduct = new Products();

        searchText = findViewById(R.id.editTextSearch);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!s.toString().isEmpty()){

                    setAdapter(s.toString());
                }
                else {
                    recyclerView.removeAllViews();
                }
            }
        });
//        Obtaining reference to the firebase database
        mDatabase = FirebaseDatabase.getInstance().getReference("Products");
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

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                productsList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Products receivedProduct = postSnapshot.getValue(Products.class);
                    receivedProduct.setID(postSnapshot.getKey());
                    productsList.add(receivedProduct);
                }
                if (productsList.isEmpty()){
                    defaultProductView.setVisibility(View.VISIBLE);
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

//        invalidateOptionsMenu();
//        get navigation Menu
        Menu menu = navigationView.getMenu();

//        Changing Switch User Appropriately
//        find the Item
        MenuItem switchUserItem = menu.findItem(R.id.nav_switchUser);
        String user;
        if (buttonString.equals("buyer")){
            user = "Seller";
//            fab.setVisibility(View.INVISIBLE);
        }
        else { user = "Buyer" ;}
        switchUserItem.setTitle("Switch to " + user);

        //        Listen to the selected item
        navigationView.setNavigationItemSelectedListener(this);

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navMail = headerView.findViewById(R.id.navMail);
        navPhone = headerView.findViewById(R.id.navPhone);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//        String loadedNavPhone = pref.getString("phone", null);
//        if (pref.getString("eMail", null) != null){
//            navMail.setText(pref.getString("eMail", null));
//        }
//        String loadedNavPhone;

        if (pref.getString("phone", null) != null){
//            navPhone.setText(pref.getString("phone", null));
            String loadedNavPhone = pref.getString("phone", null);
            DatabaseReference navRef = FirebaseDatabase.getInstance().getReference("userProfile").child(loadedNavPhone);

            navRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userModel navModel = dataSnapshot.getValue(userModel.class);
                    navMail.setText(navModel.getEmail());
                    navPhone.setText(navModel.getPhone());
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
//        receiveButtonIntents();
        personalProductsIntents();

//        DatabaseReference orderDatabaseCount = FirebaseDatabase.getInstance().getReference("Orders").child(loadedNavPhone);
//        orderDatabaseCount.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                 cartCount = dataSnapshot.getChildrenCount();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

    }

    private void setAdapter(final String queryString) {


        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                productsList.clear();
                filteredProductsList.clear();
                recyclerView.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String uid = snapshot.getKey();
                    String Fname = snapshot.child("name").getValue(String.class);
                    Products filteredProduct = snapshot.getValue(Products.class);

                    if (Fname.toLowerCase().contains(queryString.toLowerCase())){
                        nameList.add(Fname);
                        filteredProductsList.add(filteredProduct);
                    }
                }
                adapter = new productAdapter(Products_view.this, filteredProductsList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void personalProductsIntents() {
        if (getIntent().hasExtra("phone") && getIntent().hasExtra("name") && getIntent().hasExtra("location") &&
                getIntent().hasExtra("price") && getIntent().hasExtra("capacity") && getIntent().hasExtra("mail") &&
                getIntent().hasExtra("image") && getIntent().hasExtra("key")){
            String pName, pLocation, pPrice, pCapacity, pMail, pImage, dKey ;
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
//            String personalKey = personalDatabaseReference.push().getKey();

//            String personalKey = productsList
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
        if (getIntent().hasExtra("button")){
            buttonString = getIntent().getStringExtra("button");
//            sendIntents_details(buttonString);
            Toast.makeText(this, "Which Button : " + buttonString, Toast.LENGTH_LONG).show();

        }
        checkUser(buttonString);
    }
    private void checkUser(String userType) {

        Menu menu = navigationView.getMenu();

        if (userType.equals("buyer")) {
//            fab.setEnabled(false);

            fab.hide();
//            Removing some features in the navigation view if user is a buyer
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
            menu.getItem(5).setVisible(false);

        }
        else {
//            MenuItem cartItem ;
//            int sellerCart = cartItem.getItemId();
//            int sCart = MenuItem.
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

//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) searchItem.getActionView();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.getFilter().filter(query);
//                if (!query.isEmpty()){
//                    productSearch(query);
//                }
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.getFilter().filter(newText);
//               if (!newText.isEmpty()){
//                   productSearch(newText);
//               }
//                return false;
//            }
//        });

        if (buttonString.equals("seller")){
//            MenuItem cartSellerItem = menu.findItem(R.id.action_add_cart);
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
                    if (receivedProductSnap.getName().toLowerCase().contains(query.toLowerCase())){
                        filteredList.add(receivedProductSnap);
                    }
//                    productsList.add(receivedProduct);
                }
                if (filteredList.isEmpty()){
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

        if (cartText != null){
            if (cartCount == 0) {
                if (cartText.getVisibility() != View.GONE) {
                    cartText.setVisibility(View.GONE);
                }
            }
                else {
                    cartText.setText(String.valueOf(cartCount));
//                    cartText.setText(String.valueOf(Math.min(cartCount, 3)));
                    if (cartText.getVisibility() != View.VISIBLE){
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
//        cartIntent.putExtra("Iphone", pPhone);
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

        } else if (id == R.id.nav_switchUser){
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
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//        phoneNo = pref.getString("phone", null);
//        if (phoneNo.equals(null)) {
//            Toast.makeText(this, "Nothing to Show...", Toast.LENGTH_SHORT).show();
//        } else {
            Intent loadIntent = new Intent(this, personalProducts.class);
//            loadIntent.putExtra("pPhone", phoneNo);
            startActivity(loadIntent);
//        }
    }

    private void signOut() {
        signOutmAuth.signOut();
        Intent outIntent = new Intent(this, SignUp.class);
        startActivity(outIntent);
    }
    private void switchUser() {
        if (buttonString.equals("buyer")){
            buttonString = "seller" ;
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

}