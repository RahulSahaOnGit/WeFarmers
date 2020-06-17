package com.example.wefarmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {
    Button log_out;
    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth=FirebaseAuth.getInstance();

        log_out=(Button)findViewById(R.id.logOutButton);

        //respond on clicking log out button
        log_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log_out_user();
            }
        });
    }

    //function to end Firebase session and log out user
    private void log_out_user() {
        mAuth.signOut();
        sendUsertoLoginActivity();
    }

    private void sendUsertoLoginActivity() {
        Intent i=new Intent(HomeActivity.this,LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }
}
