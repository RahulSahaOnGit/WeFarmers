package com.example.wefarmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VeriActivity extends AppCompatActivity {
    EditText otp;
    Button sub;
    FirebaseAuth mAuth;
    String verifyid;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veri);
        otp=(EditText)findViewById(R.id.verytxt);
        sub=(Button)findViewById(R.id.subt);
        mAuth=FirebaseAuth.getInstance();

        Intent intent=getIntent();
        number=intent.getStringExtra("PHoneNo");

        sendverificationcode(number);

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code=otp.getText().toString();
                if(code.isEmpty() || code.length()<6)
                {
                    otp.setError("Code Error");
                }
                VerifyCode(code);
            }
        });
    }

    private void VerifyCode(String code) {

        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verifyid,code);
        signinwithCredentials(credential);
    }

    private void signinwithCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Intent intent=new Intent(VeriActivity.this,SignUpActivity.class);
                    intent.putExtra("phone",number);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void sendverificationcode (String number)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                VeriActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verifyid=s;
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                        String code= phoneAuthCredential.getSmsCode();
                        otp.setText(code);
                        VerifyCode(code);

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                        Toast.makeText(VeriActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }              // Activity (for callback binding)
        );        // OnVerificationStateChangedCallbacks
    }
}
