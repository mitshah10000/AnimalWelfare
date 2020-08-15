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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private EditText UserName, UserPhoneNumber,  UserEmail, UserPassword, UserCnfPassword;
    Button RegisterButton;
    TextView LoginpageLink;
    private ProgressDialog loadingBar;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        UserName = (EditText) findViewById(R.id.register_username);
        UserPhoneNumber = (EditText) findViewById(R.id.register_phonenumber);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        UserCnfPassword = (EditText) findViewById(R.id.register_cnf_password);
        RegisterButton = (Button) findViewById(R.id.register_button);
        LoginpageLink = (TextView) findViewById(R.id.textview_login_link);
        loadingBar = new ProgressDialog(this);


        LoginpageLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                loginpagelink();
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });


    }

    private void loginpagelink()
    {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    private void CreateNewAccount()
    {
        final String username = UserName.getText().toString().trim();
        final String phonenumber = UserPhoneNumber.getText().toString().trim();
        final String email = UserEmail.getText().toString().trim();
        final String password = UserPassword.getText().toString().trim();
        String confirmPassword = UserCnfPassword.getText().toString().trim();

        if(TextUtils.isEmpty(username))
        {
            Toast.makeText(this, "Please write your Name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(phonenumber))
        {
            Toast.makeText(this, "Please write your Phone Number...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please write your Email Id...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please confirm your Password...", Toast.LENGTH_SHORT).show();
        }
        else if(!password.equals(confirmPassword))
        {
            Toast.makeText(this, "Your password do not match with your confirm password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {

                            String user_id =mAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = mDatabase.child(user_id);
                            current_user_db.child("Username").setValue(username);
                            current_user_db.child("Phone Number").setValue(phonenumber);


                            if(task.isSuccessful())
                            {
                                Toast.makeText(RegisterActivity.this, "You are Registered Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                                loginpagelink();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }

                        }

                    });
        }
    }


}
