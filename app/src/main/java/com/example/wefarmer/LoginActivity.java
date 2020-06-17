package com.example.wefarmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //TextView newuser;
    EditText phone;
    Button login;

    FirebaseUser user;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phone=(EditText)findViewById(R.id.loginphone);
        login=(Button)findViewById(R.id.loginButton);

        //check if a user is already logged in in device earlier
       /* firebaseAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    Toast.makeText(LoginActivity.this, user.toString(), Toast.LENGTH_SHORT).show();
                    SendUserToHomeActivity();
                }
            }
        };*/


        //on clicking login button user will receive an OTP which he has to enter in OTPVerifyActivity
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phone.getText().toString().length()==10)
                    sendUsertoOTPVerifyActivity();
                else
                    Toast.makeText(LoginActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
            }
        });

        /*newuser=(TextView)findViewById(R.id.signup);

       //if user clicks on hyperlink to Sign Up he'll be sent to GenerateOTPActivity first to enter his number for OTP
        newuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, GenerateOTPActivity.class);
                startActivity(intent);
            }
        });*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null)
        {
            SendUserToHomeActivity();
        }
    }

    private void sendUsertoOTPVerifyActivity() {
        Intent intent=new Intent(LoginActivity.this, OTPVerifyActivity.class);
        intent.putExtra("PHoneNo", "+91"+phone.getText().toString());
        startActivity(intent);
    }

    private void SendUserToHomeActivity() {
        finish();
        Intent i=new Intent(LoginActivity.this,HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

}
