package com.example.animalwelfare;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class PostActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PICK_CAMERA = 1,PICK_GALLERY = 2 ;
    private ImageView closebutton;
    private TextView title;
    private ImageButton imageButton;
    private TextView current_location;
    private TextView user_location;
    private TextView select_issue;
    private Spinner dropdown;
    private EditText textLandmark;
    private EditText textdescription;
    private CheckBox checkBox;
    private Button postButton;

    private Uri ImageUri;

    private StorageReference postimage_storage;
    private DatabaseReference userRef , postRef;
    private FirebaseAuth mAuth;

    private FusedLocationProviderClient mFusedLocationClient;

    private String mCurrentUser, description, landmark, location, selected_issue, user_phonenumber, user_Username;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl;

    private String currentPhotoPath;

    private Double latitude,longitude;

    Geocoder geocoder;
    List<Address> addresses;

    String[] issue ={"Accident", "Feeding", "Left leg injury", "Right leg injury", "Head injury", "Not in the list"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().hide();

        closebutton = (ImageView)findViewById(R.id.post_close);
        title = (TextView)findViewById(R.id.post_title);
        imageButton = findViewById(R.id.post_imageButton);
        user_location = findViewById(R.id.post_LocationtextView);
        select_issue = findViewById(R.id.post_selectissue);
        dropdown = (Spinner)findViewById(R.id.post_spinner);
        textLandmark = findViewById(R.id.post_landmark);
        textdescription = findViewById(R.id.post_description);
        checkBox = findViewById(R.id.post_checkBox);
        postButton = findViewById(R.id.post_caseButton);

        postimage_storage = FirebaseStorage.getInstance().getReference("");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postRef = FirebaseDatabase.getInstance().getReference().child("Posts");

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser().getUid();


        closebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToMainActivity();
            }
        });


        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, PICK_GALLERY);

            }
        });


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });

        dropdown.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,issue);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(aa);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        selected_issue = issue[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void ValidatePostInfo()
    {
        location = user_location.getText().toString();
        landmark = textLandmark.getText().toString();
        description = textdescription.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(landmark))
        {
            Toast.makeText(this, "Please say something about nearest landmark...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(description))
        {
            Toast.makeText(this, "Please say something about your case...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoringImageToFirebaseStorage();
        }
    }

    private void StoringImageToFirebaseStorage()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        Calendar calFordTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calFordTime.getTime());

        postRandomName = saveCurrentDate + saveCurrentTime;

        StorageReference filePath = postimage_storage.child("Post Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    SavingPostInformationToDatabase();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PostActivity.this, "Error occured in getting image: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPostInformationToDatabase()
    {
        userRef.child(mCurrentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    user_Username = dataSnapshot.child("Username").getValue().toString();
                    user_phonenumber = dataSnapshot.child("Phone Number").getValue().toString();

                    HashMap postsMap = new HashMap();
                    postsMap.put("Uid", mCurrentUser);
                    postsMap.put("Date", saveCurrentDate);
                    postsMap.put("Time", saveCurrentTime);
                    postsMap.put("Location", location);
                    postsMap.put("Landmark", landmark);
                    postsMap.put("Description", description);
                    postsMap.put("Postimage", downloadUrl);
                    postsMap.put("Issue", selected_issue);
                    postsMap.put("Username", user_Username);
                    postsMap.put("Phone Number", user_phonenumber);
                    postRef.child(mCurrentUser + postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        SendUserToMainActivity();

                                        Toast.makeText(PostActivity.this, "New case is created successfully.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PostActivity.this, "Error Occured while creating your case...", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void SendUserToMainActivity()
    {
        Intent mainIntent = new Intent(PostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_GALLERY && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            imageButton.setImageURI(ImageUri);
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();

        geocoder = new Geocoder(this, Locale.getDefault());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(PostActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PostActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                new AlertDialog.Builder(this)
                        .setTitle("Required Location Permission")
                        .setMessage("You have to give permission to access the feature")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                ActivityCompat.requestPermissions(PostActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(PostActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();

                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1);

                                    String address = addresses.get(0).getAddressLine(0);

                                    String fullAddress = address+", ";
                                    user_location.setText(fullAddress);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

    }



}
