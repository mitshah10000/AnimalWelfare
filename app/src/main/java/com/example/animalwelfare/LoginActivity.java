package com.example.animalwelfare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;;

public class LoginActivity extends AppCompatActivity {

    private EditText UserEmailID, UserPassword;
    private Button LoginButton;
    private TextView RegisterpageLink;

    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        UserEmailID = (EditText)findViewById(R.id.login_email);
        UserPassword = (EditText)findViewById(R.id.login_password);
        LoginButton = (Button)findViewById(R.id.login_button);
        RegisterpageLink = (TextView)findViewById(R.id.textview_register_link);
        loadingBar = new ProgressDialog(this);


        RegisterpageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });



        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String email = UserEmailID.getText().toString();
                String password = UserPassword.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(LoginActivity.this, "Please write your Email...", Toast.LENGTH_SHORT).show();
                }
                else if(TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "Please write your Password...", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Login");
                    loadingBar.setMessage("Please wait, while we are logging you in...");
                    loadingBar.setCanceledOnTouchOutside(true);
                    loadingBar.show();


                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        final String user_id = mAuth.getCurrentUser().getUid();

                                        mDatabase.addValueEventListener(new ValueEventListener()
                                        {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot)
                                            {

                                                if (dataSnapshot.hasChild(user_id))
                                                {
                                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                    startActivity(mainIntent);
                                                    loadingBar.dismiss();
                                                    Toast.makeText(LoginActivity.this, "You are Logged In successfully.", Toast.LENGTH_SHORT).show();
                                                }
                                                else
                                                {
                                                    loadingBar.dismiss();
                                                    Toast.makeText(LoginActivity.this, "User not registered!!!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError)
                                            {
                                            }
                                        });
                                    }
                                    else
                                    {
                                        Toast.makeText(LoginActivity.this, "Couldn’t login, User not found.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }

                                }
                            });
                }
            }
        });
    }
}
