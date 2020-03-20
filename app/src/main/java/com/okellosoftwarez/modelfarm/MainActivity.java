package com.okellosoftwarez.modelfarm;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int IMAGE_REQUEST = 1, CAMERA_REQUEST = 2;
    private static final int PERMISSION_CODE = 1000 ;

    EditText etName, etLocation, etPrice, etCapacity, etPhone, etMail;
    Button chooseBtn, takeBtn, uploadBtn, changeBtn, changePhoto;
    ImageView addImage;
    Uri image_uri;
    Products product;
    String sName, sPhone, sEmail, sLocation, sPrice, sCapacity, editKey, receivedImage;
    String newName, newPhone, newEmail, newLocation, newPrice, newCapacity;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar mProgress;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace_black_24dp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getSupportActionBar().setIcon(getDrawable(R.drawable.ic_keyboard_backspace_black_24dp));
//        }
        setTitle("New Product");


//        toolbar.setTitle("New Product Okay");

        storageReference = FirebaseStorage.getInstance().getReference("Products");
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        product = new Products();
        etName = findViewById(R.id.et_addName);
        etLocation = findViewById(R.id.et_addLocation);
        etPrice = findViewById(R.id.et_addPrice);
        etCapacity = findViewById(R.id.et_addCapacity);
        etPhone = findViewById(R.id.et_addPhone);
        etMail = findViewById(R.id.et_addEmail);


        chooseBtn = findViewById(R.id.choosePhoto);
        takeBtn = findViewById(R.id.takePhoto);
        uploadBtn = findViewById(R.id.upload);
        changePhoto = findViewById(R.id.updatePhoto);
        changeBtn = findViewById(R.id.uploadChanges);
        addImage = findViewById(R.id.addImage);

        mProgress = findViewById(R.id.progressBar);

        chooseBtn.setOnClickListener(this);
        takeBtn.setOnClickListener(this);


        editProductDetails(toolbar);
//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                editProductDetails();
//                receiveEntries();
//            }
//        });
    }

    private void editProductDetails(Toolbar toolbar) {
        if (getIntent().hasExtra("editKey")){
            editKey = getIntent().getStringExtra("editKey");
            setTitle("Edit Product");
            changePhoto.setVisibility(View.VISIBLE);
            chooseBtn.setVisibility(View.INVISIBLE);
            takeBtn.setVisibility(View.INVISIBLE);
            changeBtn.setVisibility(View.VISIBLE);

            uploadBtn.setVisibility(View.INVISIBLE);
            if (getIntent().hasExtra("editImage")){
                receivedImage = getIntent().getStringExtra("editImage");
                Picasso.with(this).load(receivedImage).placeholder(R.mipmap.ic_launcher)
                                    .fit().centerCrop().into(addImage);
                image_uri = Uri.parse(receivedImage);

                if (getIntent().hasExtra("editName")){
                    sName = getIntent().getStringExtra("editName");
                    etName.setText(sName);

                    if (getIntent().hasExtra("editLocation")){
                        sLocation = getIntent().getStringExtra("editLocation");
                        etLocation.setText(sLocation);

                        if (getIntent().hasExtra("editPrice")){
                            sPrice = getIntent().getStringExtra("editPrice");
                            etPrice.setText(sPrice);

                            if (getIntent().hasExtra("editCapacity")){
                                sCapacity = getIntent().getStringExtra("editCapacity");
                                etCapacity.setText(sCapacity);

                                if (getIntent().hasExtra("editMail")) {
                                    sEmail = getIntent().getStringExtra("editMail");
                                    etMail.setText(sEmail);

                                    if (getIntent().hasExtra("editPhone")) {
                                        sPhone = getIntent().getStringExtra("editPhone");
                                        etPhone.setText(sPhone);

                                        changePhoto.setOnClickListener(this);
                                        changeBtn.setOnClickListener(this);

//                                        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//                                            @Override
//                                            public boolean onMenuItemClick(MenuItem item) {
//                                                Toast.makeText(MainActivity.this, "Back Clicked", Toast.LENGTH_SHORT).show();
//                                                return true;
//                                            }
//                                        });
                                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
//                                                Toast.makeText(MainActivity.this, "Back Clicked", Toast.LENGTH_SHORT).show();
                                                Intent changeIntent = new Intent(getApplicationContext(), personalProducts.class);
                                                startActivity(changeIntent);
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }


        else {
            uploadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    receiveEntries();
                }
            });
        }

    }

    private void updatingDetails() {

        newName = etName.getText().toString().trim();
        newLocation = etLocation.getText().toString().trim();
        newPrice = etPrice.getText().toString().trim();
        newCapacity = etCapacity.getText().toString().trim();
        newEmail = etMail.getText().toString().trim();
        newPhone = etPhone.getText().toString().trim();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
        String phoneNo = pref.getString("phone", null);
        final DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("personalProducts").child(phoneNo);

        String image = image_uri.toString();
        if (image.startsWith("https")) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mProgress.setProgress(0);
                }
                }, 500);
//            Toast.makeText(this, ".............................................................", Toast.LENGTH_SHORT).show();
            Products updatedProduct = new Products(newName, newPhone, newLocation, receivedImage, newPrice, newCapacity, newEmail);
            updateRef.child(editKey).setValue(updatedProduct);
            databaseReference.child(editKey).setValue(updatedProduct);
            mProgress.setProgress(100);
        }
        else {

            if (image_uri != null) {

                StorageReference updatePhotoReference = storageReference.child(System.currentTimeMillis() + "."
                        + getFileExtension(image_uri));
                UploadTask uploadUpdateTask = updatePhotoReference.putFile(image_uri);

                Toast.makeText(this, "UP " + uploadUpdateTask, Toast.LENGTH_SHORT).show();

                // Register observers to listen for when the download is done or if it fails
                uploadUpdateTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        // Handle unsuccessful uploads
                        Toast.makeText(MainActivity.this, "Fail Changing Photo..."+ exception.getMessage(), Toast.LENGTH_LONG).show();

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(MainActivity.this, "Success Changing Photo...", Toast.LENGTH_LONG).show();

                        if (taskSnapshot.getMetadata() != null) {
                            if (taskSnapshot.getMetadata().getReference() != null) {
                                Task<Uri> resultUpdate = taskSnapshot.getStorage().getDownloadUrl();
                                resultUpdate.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Toast.makeText(MainActivity.this, "Complete...", Toast.LENGTH_LONG).show();
                                        String newImage = uri.toString();

                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgress.setProgress(0);
                                            }
                                        }, 500);
                                        Toast.makeText(MainActivity.this, "Upload Successful..." + newImage, Toast.LENGTH_SHORT).show();

                                        Products updatedProduct = new Products(newName, newPhone, newLocation, newImage, newPrice, newCapacity, newEmail);

                                        updateRef.child(editKey).setValue(updatedProduct);
                                        databaseReference.child(editKey).setValue(updatedProduct);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Toast.makeText(MainActivity.this, "Database Fail...", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        }
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        mProgress.setProgress((int) progress);
                    }
                });
            }
        }
//        FirebaseStorage updateImageStorage = FirebaseStorage.getInstance();
//        SharedPreferences pref = getApplicationContext().getSharedPreferences("Preferences", 0);
//        String phoneNo = pref.getString("phone", null);
//        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference("personalProducts").child(phoneNo);
//        Products updatedProduct = new Products();
//        Products updatedProduct = new Products(newName, newPhone, newLocation, receivedImage, newPrice, newCapacity, newEmail);

        Toast.makeText(this, "Updating :"+image+"\n"+ "\n"+ newName+"\n"+newPrice+"\n"+ newCapacity+"\n"+newEmail+"\n"+image_uri , Toast.LENGTH_LONG).show();

//        StorageReference updateImageRef = updateImageStorage.getReferenceFromUrl(receivedImage);
//        updateImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                updateRef.child(editKey).setValue(updatedProduct);
//                databaseReference.child(editKey).setValue(updatedProduct);
//                Toast.makeText(MainActivity.this, "Update Success...", Toast.LENGTH_SHORT).show();
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MainActivity.this, "Update Failed..." + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        updateRef.child(editKey).setValue(updatedProduct);
//        databaseReference.child(editKey).setValue(updatedProduct);
    }


    private void backToMain(String key) {
        Intent backIntent = new Intent(this, Products_view.class);
        backIntent.putExtra("phone", product.getPhone());
        backIntent.putExtra("name", product.getName());
        backIntent.putExtra("location", product.getLocation());
        backIntent.putExtra("price", product.getPrice());
        backIntent.putExtra("capacity", product.getCapacity());
        backIntent.putExtra("mail", product.getEmail());
        backIntent.putExtra("image", product.getImage());
        backIntent.putExtra("key", key);
        Toast.makeText(this, "Inserted Values are :" +
                "\nName : " + product.getName() +
                "\nLocation : " + product.getLocation() +
                "\nPrice : " + product.getPrice() +
                "\nCapacity : " + product.getCapacity() +
                "\nPhone : " + product.getPhone() +
                "\n Email : " + product.getLocation() +
//                "\n Image Name : " + product.getImageUrl() +
                "\nProduct ID : " + product.getID() +
                "\nImage String : " + product.getImage(), Toast.LENGTH_LONG).show();
        startActivity(backIntent);
//        Toast.makeText(this, "Inserted Values are :" +
//                "\nName : " + product.getName() +
//                "\nLocation : " + product.getLocation() +
//                "\nPrice : " + product.getPrice() +
//                "\nCapacity : " + product.getCapacity() +
//                "\nPhone : " + product.getPhone() +
//                "\n Email : " + product.getLocation() +
////                "\n Image Name : " + product.getImageUrl() +
//                "\nProduct ID : " + product.getID() +
//                "\nImage String : " + product.getImage(), Toast.LENGTH_LONG).show();
    }

    private void receiveEntries() {
        sName = etName.getText().toString().trim();
        sLocation = etLocation.getText().toString().trim();
        sPrice = etPrice.getText().toString().trim();
        sCapacity = etCapacity.getText().toString().trim();
        sPhone = etPhone.getText().toString().trim();
        sEmail = etMail.getText().toString().trim();
        checkFields();

    }

    private void checkFields() {
        if (sName.isEmpty() || sLocation.isEmpty() || sPrice.isEmpty() || sCapacity.isEmpty() || sPhone.isEmpty() || sEmail.isEmpty() || image_uri == null) {
            Toast.makeText(this, "Missing Fields...", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Check Field else part.......", Toast.LENGTH_SHORT).show();
            uploadDetails();
        }
    }

    private void uploadDetails() {
        Toast.makeText(this, "Start Uploading....", Toast.LENGTH_LONG).show();
        if (image_uri != null) {
            okelloModel();
        } else {
            Toast.makeText(this, "Empty Uri...", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "End Uploading....", Toast.LENGTH_LONG).show();
    }

    private void okelloModel() {
        Toast.makeText(this, "Start okello....", Toast.LENGTH_LONG).show();

        StorageReference photoReference = storageReference.child(System.currentTimeMillis() + "."
                + getFileExtension(image_uri));

        UploadTask uploadTask = photoReference.putFile(image_uri);

        Toast.makeText(this, "UP " + uploadTask, Toast.LENGTH_SHORT).show();

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                // Handle unsuccessful uploads
                Toast.makeText(MainActivity.this, "Fail...okello", Toast.LENGTH_SHORT).show();

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                Toast.makeText(MainActivity.this, "Success...okello", Toast.LENGTH_LONG).show();

                if (taskSnapshot.getMetadata() != null) {
                    if (taskSnapshot.getMetadata().getReference() != null) {
                        Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                        result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Toast.makeText(MainActivity.this, "Proceed...", Toast.LENGTH_LONG).show();
                                String sImage = uri.toString();

                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        mProgress.setProgress(0);
                                    }
                                }, 500);
                                Toast.makeText(MainActivity.this, "Upload Successful..." + sImage, Toast.LENGTH_SHORT).show();
//                                product = new Products(sName, sPhone, sEmail, sImage);
                                product = new Products(sName, sPhone, sLocation, sImage, sPrice, sCapacity, sEmail);
                                String key = databaseReference.push().getKey();
                                product.setID(key);
                                databaseReference.child(key).setValue(product);
//                                databaseReference.child(sPhone).child(key).setValue(product);
//                                databaseReference.child(sPhone).setValue(product);
                                Toast.makeText(MainActivity.this, "Success Key retention...", Toast.LENGTH_LONG).show();
//                                backToMain(sPhone);
                                backToMain(key);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Toast.makeText(MainActivity.this, "Database Fail...", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                mProgress.setProgress((int) progress);
            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = mime.getExtensionFromMimeType(contentResolver.getType(uri));
        return extension;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoto:
//              Take a photo using the camera

                takePhoto();

                break;

            case R.id.choosePhoto:
//                Choose a photo from gallery
                        choosingPhoto();
//                Intent chooseIntent = new Intent();
//                chooseIntent.setType("image/*");
//                chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(chooseIntent, IMAGE_REQUEST);
                break;

            case R.id.updatePhoto:
                chooserPhoto();
//                    Toast.makeText(this, "Changing Photo Feature...", Toast.LENGTH_SHORT).show();
                    break;

            case R.id.uploadChanges :
                Toast.makeText(MainActivity.this, "Updating Feature Coming Soon...", Toast.LENGTH_SHORT).show();
                updatingDetails();
                break;
        }
    }

    private void chooserPhoto() {
        Intent chooseIntent = new Intent();
        chooseIntent.setType("image/*");
        chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseIntent, IMAGE_REQUEST);
    }

    private void choosingPhoto() {
        Intent chooseIntent = new Intent();
        chooseIntent.setType("image/*");
        chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseIntent, IMAGE_REQUEST);
    }

    private void takePhoto() {

//        if system OS is greater than Marshmallow require Permissions at run time

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
//                Permissions not enabled so request for them
                String [] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

                requestPermissions(permissions, PERMISSION_CODE);
            }
            else {
//                Permission already granted
                openCamera();
            }
        }
        else {
//            For OS lesser than Marshmallow
            openCamera();
        }
    }

    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "NEW PICTURE");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From The Camera");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

//        Camera Intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        This Method is called when user selects either allow or deny in the pop up notification
        switch (requestCode){
            case PERMISSION_CODE : {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Permission from pop up was granted
                    openCamera();
                }
                else {
//                    Permission from pop up was denied
                    Toast.makeText(this, "Permission Denied ...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {

                addImage.setImageURI(image_uri);
                Toast.makeText(this, "Photo loaded...", Toast.LENGTH_SHORT).show();

            } else if (requestCode == IMAGE_REQUEST) {

                image_uri = data.getData();
                Picasso.with(this).load(image_uri).into(addImage);

            }
        }
    }
}
