package com.example.wefarmer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {
    EditText fullnme, mailid, paswrd;
    RadioGroup rgroup;
    Button sigup;
    DatabaseReference ref;
    FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullnme=(EditText)findViewById(R.id.nam);
        mailid=(EditText)findViewById(R.id.mail);
        paswrd=(EditText)findViewById(R.id.entrpass);
        rgroup=(RadioGroup)findViewById(R.id.rdiogroup);

        sigup=(Button)findViewById(R.id.nxt);

        sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase=FirebaseDatabase.getInstance();
                ref=mDatabase.getReference("Users");

                String name=fullnme.getText().toString();
                String email=mailid.getText().toString();
                String password=paswrd.getText().toString();
                String phone = getIntent().getStringExtra("phone");
                int occupation=rgroup.getCheckedRadioButtonId();

                RadioButton selectedbtn = (RadioButton)findViewById(occupation);
                String typeofperson = selectedbtn.getText().toString();

                if(name.isEmpty()||email.isEmpty()||password.isEmpty()||typeofperson.isEmpty())
                {

                    Toast.makeText(SignUpActivity.this,"Please complete fill up",Toast.LENGTH_SHORT).show();
                }
                else
                {

                    String userid=ref.push().getKey();
                    UserHelperClass helperClass=new UserHelperClass(name,email,password,typeofperson,phone);

                    ref.child(userid).setValue(helperClass, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if(databaseError==null)
                            {
                                Intent intent=new Intent(SignUpActivity.this,HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this,"There are some issues",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                }



            }
        });

       /* sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object> map= new HashMap<>();
                map.put("Full Name",fullnme.getText().toString());
                map.put("Email ID",mailid.getText().toString());
                map.put("Password",paswrd.getText().toString());


                FirebaseDatabase.getInstance().getReference().child("Post")
                        .setValue(map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful())
                                {
                                    Intent intent=new Intent(SignUpActivity.this,HomeActivity.class);
                                    startActivity(intent);
                                }
                                Log.i("tvtvtv", "Successful");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("tvtvtv","Unsuccessful"+e.toString());
                    }
                });


            }
        });*/




    }
}
