package com.example.wefarmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

//currently not getting used, loginactivity and otpverifyactivity are doin the task
public class GenerateOTPActivity extends AppCompatActivity {
    EditText mphone;
    Button regnum;
    FirebaseAuth mAuth;
    DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_o_t_p);

        mphone=(EditText)findViewById(R.id.phonem);
        regnum=(Button)findViewById(R.id.subtim);
        mAuth=FirebaseAuth.getInstance();
        regnum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String PhoneNumber="+"+"91"+mphone.getText().toString();
                if(PhoneNumber.isEmpty() || PhoneNumber.length()<10)
                {
                    mphone.setError("Enter proper phone number");
                    return;
                }
                else
                {
                    sendUsertoVerifyOTPActivity(PhoneNumber);

                }
            }
        });
    }

    private void sendUsertoVerifyOTPActivity(String PhoneNumber) {
        Intent i = new Intent(GenerateOTPActivity.this, OTPVerifyActivity.class);
        i.putExtra("PHoneNo", PhoneNumber);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

}
