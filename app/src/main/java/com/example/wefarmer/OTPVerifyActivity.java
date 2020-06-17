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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class OTPVerifyActivity extends AppCompatActivity {
    EditText otp;
    Button sub;
    FirebaseAuth mAuth;
    FirebaseUser currentuser;
    DatabaseReference UsersRef;
    String verifyid;
    String number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p_verify);
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

    //function to wait and receive OTP (don't hamper this function)
    private void sendverificationcode(String number)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OTPVerifyActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

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

                        Toast.makeText(OTPVerifyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }              // Activity (for callback binding)
        );        // OnVerificationStateChangedCallbacks
    }

    //function to check if OTP verification is successful and redirect to Home or SignUp page accordingly
    private void signinwithCredentials(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    /*Intent intent=new Intent(OTPVerifyActivity.this,SignUpActivity.class);
                    intent.putExtra("phone",number);
                    startActivity(intent);
                    finish();*/
                        CheckUserExists(); //method checks if user exists then send him to home or sign up page accordingly
                }
                else
                {
                    Toast.makeText(OTPVerifyActivity.this, "OTP Verification Failed! Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CheckUserExists() {
        UsersRef= FirebaseDatabase.getInstance().getReference().child("users");
        currentuser=mAuth.getCurrentUser();

        CheckUserisFarmer();
    }

    //check if user exists as a farmer then send him home else check if he exists as a wholeseller
    private void CheckUserisFarmer() {
        UsersRef.child("farmer").orderByKey().equalTo(currentuser.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            sendUsertoHome();
                        else
                            CheckUserisWholeSeller(); //function to check user is wholeseller
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //check if user exists as a wholeseller then send him home else send him to SignUp
    private void CheckUserisWholeSeller() {
        UsersRef.child("wholeseller").orderByKey().equalTo(currentuser.getUid()).
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                            sendUsertoHome();
                        else{
                            Toast.makeText(OTPVerifyActivity.this, "Sign Up First!", Toast.LENGTH_SHORT).show();
                            sendUsertoSignUp(); //function to send User to SignUp
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    //if User phone number exists in database send him Home
    private void sendUsertoHome() {
        Toast.makeText(OTPVerifyActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
        Intent mainIntent = new Intent(OTPVerifyActivity.this, HomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    //if User phone number does not exist in database send him to SignUp
    private void sendUsertoSignUp() {
        Intent intent=new Intent(OTPVerifyActivity.this,SignUpActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone",number);
        startActivity(intent);
        finish();
    }
}
