package com.example.animalwelfare;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

public class PetAdoptActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int Gallery_Pick = 1;
    private ImageView backbutton;
    private TextView title;
    private ImageButton petimagebutton;
    private TextView current_pet_location;
    private EditText petname;
    private TextView select_pet_type;
    private Spinner pet_type;
    private TextView select_breed;
    private Spinner breed;
    private TextView select_dob;
    private EditText dob;
    private TextView select_mark;
    private EditText mark_on_body;
    private Button submitButton;

    private Uri ImageUri;

    private FusedLocationProviderClient mFusedLocationClient;
    Geocoder geocoder;
    List<Address> addresses;
    private Double latitude,longitude;

    String[] PetType ={"Dog", "Cat"};
    String[] PetBreed_Dog ={"Australian Shephard", "Bulldog", "Chithuahua", "Dalmatian", "German Shephard", "Great Dane"};
    String[] PetBreed_Cat ={"American Bobtail", "Bengal Cats", "Cornish Rex", "Devon Rex", "Egyptian Mau", "Himalayan"};

    Calendar myCalender;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef , adoptRef;
    private StorageReference petimage_storage;

    private String mUser;
    private String type, name, location, markonbody, selected_breed;
    private String saveCurrentDate, saveCurrentTime, postRandomName, downloadUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_adopt);
        getSupportActionBar().hide();

        backbutton = (ImageView)findViewById(R.id.adopt_back);
        title = (TextView)findViewById(R.id.adopt_title);
        petimagebutton = (ImageButton) findViewById(R.id.adopt_imageButton);
        petname =(EditText) findViewById(R.id.adopt_petname);
        current_pet_location =(TextView) findViewById(R.id.adopt_LocationtextView);
        select_pet_type =(TextView) findViewById(R.id.adopt_pettype);
        pet_type = (Spinner)findViewById(R.id.adopt_type_spinner);
        select_breed = (TextView) findViewById(R.id.adopt_breed);
        breed = (Spinner)findViewById(R.id.adopt_breed_spinner);
        select_dob = (TextView) findViewById(R.id.adopt_dob);
        dob =(EditText) findViewById(R.id.adopt_add_dob);
        select_mark = (TextView) findViewById(R.id.adopt_textview);
        mark_on_body =(EditText) findViewById(R.id.adopt_mark_body);
        submitButton = (Button) findViewById(R.id.adopt_submit);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        adoptRef = FirebaseDatabase.getInstance().getReference().child("Posts").child("Adoption Posts");
        petimage_storage = FirebaseStorage.getInstance().getReference();


        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                SendUserToMainActivity();
            }
        });


        petimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                OpenGallery();
            }
        });


        pet_type.setOnItemSelectedListener(this);
        breed.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,PetType);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pet_type.setAdapter(aa);


        myCalender = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                 myCalender.set(Calendar.YEAR,year);
                 myCalender.set(Calendar.MONTH,month);
                 myCalender.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                 setdate();
            }
        };


        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PetAdoptActivity.this, date, myCalender.get(Calendar.YEAR), myCalender.get(Calendar.MONTH), myCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();
            }
        });


    }


    private void setdate()
    {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dob.setText(sdf.format(myCalender.getTime()));
    }


    private void OpenGallery()
    {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, Gallery_Pick);
    }


    private void ValidatePostInfo()
    {
        name = petname.getText().toString();
        location = current_pet_location.getText().toString();
        markonbody = mark_on_body.getText().toString();


        if(ImageUri == null)
        {
            Toast.makeText(this, "Please select post image...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write the name of the pet...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(markonbody))
        {
            Toast.makeText(this, "Please provide the info of any mark on the body of the pet...", Toast.LENGTH_SHORT).show();
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

        StorageReference filePath = petimage_storage.child("Pet Images").child(ImageUri.getLastPathSegment() + postRandomName + ".jpg");

        filePath.putFile(ImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
            {
                if(task.isSuccessful())
                {
                    downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    SavingPetAdoptionInformationToDatabase();
                }
                else
                {
                    String message = task.getException().getMessage();
                    Toast.makeText(PetAdoptActivity.this, "Error occured in getting image: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SavingPetAdoptionInformationToDatabase()
    {
        userRef.child(mUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String user_Username = dataSnapshot.child("Username").getValue().toString();
                    String user_Phonenumber = dataSnapshot.child("Phone Number").getValue().toString();

                    HashMap adoptionsMap = new HashMap();
                    adoptionsMap.put("Uid", mUser);
                    adoptionsMap.put("Date", saveCurrentDate);
                    adoptionsMap.put("Time", saveCurrentTime);
                    adoptionsMap.put("Username", user_Username);
                    adoptionsMap.put("Phone Number", user_Phonenumber);
                    adoptionsMap.put("Petimage", downloadUrl);
                    adoptionsMap.put("Pet Location", location);
                    adoptionsMap.put("Pet Name", name);
                    adoptionsMap.put("Pet Type", type);
                    adoptionsMap.put("Pet Breed", selected_breed);
                    adoptionsMap.put("Date of Birth", dob);
                    adoptionsMap.put("Mark on body", markonbody);
                    adoptRef.child(mUser + postRandomName).updateChildren(adoptionsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful())
                                    {
                                        SendUserToMainActivity();

                                        Toast.makeText(PetAdoptActivity.this, "New pet adoption has been created successfully...", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PetAdoptActivity.this, "Error Occured while creating your adoption post...", Toast.LENGTH_SHORT).show();
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
        Intent mainIntent = new Intent(PetAdoptActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri = data.getData();
            petimagebutton.setImageURI(ImageUri);
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {

        if(parent.getId() == R.id.adopt_type_spinner)
        {
            type = String.valueOf(pet_type.getSelectedItem());
            if (type.contentEquals("Dog")) {
                ArrayAdapter aa2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, PetBreed_Dog);
                aa2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                breed.setAdapter(aa2);
            }

            if (type.contentEquals("Cat")) {

                ArrayAdapter aa3 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, PetBreed_Cat);
                aa3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                breed.setAdapter(aa3);

            }
        }
        else if(parent.getId() == R.id.adopt_breed_spinner)
        {
            selected_breed = String.valueOf(breed.getSelectedItem());
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }




    @Override
    protected void onStart()
    {
        super.onStart();

        geocoder = new Geocoder(this, Locale.getDefault());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(PetAdoptActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(PetAdoptActivity.this,
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
                                ActivityCompat.requestPermissions(PetAdoptActivity.this,
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
                ActivityCompat.requestPermissions(PetAdoptActivity.this,
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
                                    current_pet_location.setText(fullAddress);

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }
    }

}
