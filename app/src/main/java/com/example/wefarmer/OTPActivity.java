package com.example.wefarmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class OTPActivity extends AppCompatActivity {
    EditText mphone;
    Button regnum;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p);

        mphone=(EditText)findViewById(R.id.phonem);
        regnum=(Button)findViewById(R.id.subtim);
        mAuth=FirebaseAuth.getInstance();
        regnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneNumber="+"+"91"+mphone.getText().toString();
                if(PhoneNumber.isEmpty() || PhoneNumber.length()<10)
                {
                    mphone.setError("Enter proper phone number");
                    return;
                }
                else
                {
                    Intent intent=new Intent(OTPActivity.this,VeriActivity.class);
                    intent.putExtra("PHoneNo", PhoneNumber);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
