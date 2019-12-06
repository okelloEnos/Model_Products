package com.okellosoftwarez.modelfarm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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


public class MainActivity extends AppCompatActivity {

    private static final int IMAGE_REQUEST = 1;
    EditText etName, etPhone, etMail;
    Button chooseBtn, takeBtn, uploadBtn;
    ImageView addImage;
    Uri image_uri;
    Products product;
    String sname, sphone, semail;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private ProgressBar mProgress;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storageReference = FirebaseStorage.getInstance().getReference("Products");
        databaseReference = FirebaseDatabase.getInstance().getReference("Products");

        product = new Products();
        etName = findViewById(R.id.et_addName);
        etMail = findViewById(R.id.et_addEmail);
        etPhone = findViewById(R.id.et_addPhone);

        chooseBtn = findViewById(R.id.choosePhoto);
        takeBtn = findViewById(R.id.takePhoto);
        uploadBtn = findViewById(R.id.upload);
        addImage = findViewById(R.id.addImage);

        mProgress = findViewById(R.id.progressBar);

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePhoto();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                receiveEntries();
            }
        });
    }

    private void backToMain() {
        Intent backIntent = new Intent(this, Products_view.class);
////        backIntent.putExtra("name", product.getName());
////        backIntent.putExtra("phone", product.getPhone());
////        backIntent.putExtra("image", product.getImageUrl().toString());
////        backIntent.putExtra("email", product.getLocation());
//        backIntent.putExtra("Product", product);
        startActivity(backIntent);
        Toast.makeText(this, "Inserted Values are :" +
                "\nName : " + product.getName() +
                "\nPhone : " + product.getPhone() +
                "\n Email : " + product.getLocation() +
                "\n Image Name : " + product.getImageUrl() +
                "\nProduct ID : " + product.getID() +
                "\nImage String : " + product.getImage(), Toast.LENGTH_LONG).show();
    }

    private void receiveEntries() {
        sname = etName.getText().toString().trim();
        sphone = etPhone.getText().toString().trim();
        semail = etMail.getText().toString().trim();
        checkFields();

    }

    private void checkFields() {
        if (sname.isEmpty() || sphone.isEmpty() || semail.isEmpty() || image_uri == null) {
            Toast.makeText(this, "Missing Fields...", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Check Field else part.......", Toast.LENGTH_SHORT).show();
            uploadDetails();
//            backToMain();
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
                                Toast.makeText(MainActivity.this, "Upload Successful...", Toast.LENGTH_SHORT).show();
                                product = new Products(sname, sphone, semail, sImage);
                                String key = databaseReference.push().getKey();
                                databaseReference.child(key).setValue(product);
                                Toast.makeText(MainActivity.this, "Success Key retention...", Toast.LENGTH_LONG).show();
                                backToMain();
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
                double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
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

    private void choosePhoto() {
        Intent chooseIntent = new Intent();
        chooseIntent.setType("image/*");
        chooseIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(chooseIntent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image_uri = data.getData();
            Picasso.with(this).load(image_uri).into(addImage);
        }
    }

}
