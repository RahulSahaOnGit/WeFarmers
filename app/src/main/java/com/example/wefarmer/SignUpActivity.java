package com.example.wefarmer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    ImageView profile_image, location_image;
    TextView localityText;
    EditText fullnme;
    RadioGroup rgroup;
    Spinner state_spinner, ditrict_spinner;
    Button sigup;
    ProgressDialog loadingBar;

    DatabaseReference UsersRef;
    FirebaseAuth mAuth;
    private StorageReference UserProfileImageRef;

    Uri selectedImage;

    FusedLocationProviderClient fusedLocationProviderClient;

    String currentUserID,latitude="",longitude="", locality="";
    int RESULT_LOAD_IMAGE = 1, profile_update_flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        handlePermission();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("User Profile Images");

        profile_image = (ImageView) findViewById(R.id.profileImageView);
        location_image = (ImageView) findViewById(R.id.locationImageView);
        fullnme = (EditText) findViewById(R.id.nam);
        rgroup = (RadioGroup) findViewById(R.id.rdiogroup);
        localityText=(TextView)findViewById(R.id.localityTextView);
        sigup = (Button) findViewById(R.id.nxt);
        loadingBar = new ProgressDialog(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        state_spinner = (Spinner) findViewById(R.id.statesSpinner);
        ArrayList<String> state_items = getStates("states.json");
        ArrayAdapter<String> state_adapter = new ArrayAdapter<String>(this, R.layout.state_spinner_layout, R.id.state_spinner_txt, state_items);
        state_spinner.setAdapter(state_adapter);

        //to set district spinner only when an item in state spinner is selected
        state_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                show_district_spinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        location_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SignUpActivity.this, "location clicked", Toast.LENGTH_SHORT).show();
                getLocation();
            }
        });

    }


    private void getLocation() {

        //check if detect location permission is granted
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location=task.getResult();
                    //if location is accessed store required information in String variables
                    if(location!=null)
                    {
                        try{
                            Geocoder geocoder=new Geocoder(SignUpActivity.this, Locale.getDefault());
                            List<Address> addresses=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                            latitude=Double.toString(addresses.get(0).getLatitude());
                            longitude=Double.toString(addresses.get(0).getLongitude());
                            locality=addresses.get(0).getLocality();
                            localityText.setText("Locality:"+locality);
                            Toast.makeText(SignUpActivity.this, "got location", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            });
        }
        else{
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);

        }

    }

    //ask for storage read permission for profile image
    private void handlePermission() {
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M)
        {
            return;
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    RESULT_LOAD_IMAGE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sigup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveAccountSetupInformation();
            }
        });
    }

    //selecting photo from gallery and put in SignUp page
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RESULT_LOAD_IMAGE && resultCode==RESULT_OK && data!=null)
        {
            selectedImage=data.getData();
            String filePathColumn[]={MediaStore.Images.Media.DATA};
            Cursor cursor=getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();
            int columnIndex=cursor.getColumnIndex(filePathColumn[0]);
            String picturePath=cursor.getString(columnIndex);
            cursor.close();
            profile_image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            profile_update_flag=1;
        }
    }

    //finally registering account with information
    private void SaveAccountSetupInformation() {

        String name=fullnme.getText().toString().toLowerCase();
        String phone = getIntent().getStringExtra("phone");
        String state_selected=state_spinner.getSelectedItem().toString().toLowerCase();
        String district_selected=ditrict_spinner.getSelectedItem().toString().toLowerCase();

        int occupation=rgroup.getCheckedRadioButtonId();
        RadioButton selectedbtn = (RadioButton)findViewById(occupation);

        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(this, "Please write your name...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(state_selected))
        {
            Toast.makeText(this, "Please select a state", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(district_selected))
        {
            Toast.makeText(this, "Please select a district", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(latitude) || TextUtils.isEmpty(longitude) || TextUtils.isEmpty(locality) )
        {
            Toast.makeText(this, "Please use location detection service to register more info", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(profile_update_flag==1)
            {
                firebase_upload_image();
            }

            String typeofperson=selectedbtn.getText().toString().toLowerCase();

            UsersRef=FirebaseDatabase.getInstance().getReference().child("users").child(typeofperson).child(currentUserID);
            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait, while we are creating your new Account...");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            //create key value pairs in database
            HashMap userMap = new HashMap();
            userMap.put("name", name);
            userMap.put("user_id",currentUserID);
            userMap.put("typeofperson",typeofperson);
            userMap.put("phone",phone);
            userMap.put("state", state_selected);
            userMap.put("district", district_selected);
            userMap.put("locality", locality);
            userMap.put("latitude", latitude);
            userMap.put("longitude", longitude);

            UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(SignUpActivity.this, "Your Account is created Successfully.", Toast.LENGTH_SHORT).show();
                        SendUserToHomeActivity();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        String message =  task.getException().getMessage();
                        Toast.makeText(SignUpActivity.this, "Error Occured: " + message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                }
            });
        }
    }

    //upload profile photo to firebase
    private void firebase_upload_image() {
        loadingBar.setTitle("Set Profile Image");
        loadingBar.setMessage("Please wait your profile image is updating...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        StorageReference filePath = UserProfileImageRef.child(currentUserID + ".jpg");
        filePath.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful())
                {

                    Toast.makeText(SignUpActivity.this, "Profile pic upload successful", Toast.LENGTH_SHORT).show();

                    String downloadUrl = task.getResult().getMetadata().getReference().getDownloadUrl().toString();

                    UsersRef.child("profileimage")
                            .setValue(downloadUrl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){

                                        Toast.makeText(SignUpActivity.this, "Image is saved", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    } else {

                                        String message = task.getException().toString();
                                        Toast.makeText(SignUpActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });

                } else {
                    String message = task.getException().toString();
                    Toast.makeText(SignUpActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();

                }
            }
        });
    }

    //if SignUp is successful user is sent to HomeActivity
    private void SendUserToHomeActivity() {
        Intent mainIntent = new Intent(SignUpActivity.this, HomeActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    //method that returns array list into state spinner
    public ArrayList<String> getStates(String fileName){
        JSONArray jsonArray=null;
        ArrayList<String> slist=new ArrayList<String>();
        try{
            InputStream is=getResources().getAssets().open(fileName);
            int size=is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            jsonArray=new JSONArray(json);
            if(jsonArray!=null){
                for(int i=0;i<jsonArray.length();i++)
                {
                    slist.add(jsonArray.getJSONObject(i).getString("state"));
                }
            }
        }
        catch (IOException e){e.printStackTrace();}
        catch (JSONException je){je.printStackTrace();}
        return slist;
    }

    //method that views corresponding array list into district spinner
    private void show_district_spinner() {
        ditrict_spinner=(Spinner)findViewById(R.id.districtSpinner);
        ArrayList<String> district_items=getDistricts("states.json",state_spinner.getSelectedItem().toString());
        ArrayAdapter<String> district_adapter=new ArrayAdapter<String>(this,R.layout.district_layout_spinner,R.id.district_spinner_txt,district_items);
        ditrict_spinner.setAdapter(district_adapter);
    }

    //method that returns array list into district spinner
    public ArrayList<String> getDistricts(String fileName, String state){
        Toast.makeText(this, state, Toast.LENGTH_SHORT).show();
        String s1="";
        JSONArray jsonArray=null;
        ArrayList<String> dlist=new ArrayList<String>();
        try{
            InputStream is=getResources().getAssets().open(fileName);
            int size=is.available();
            byte[] data=new byte[size];
            is.read(data);
            is.close();
            String json=new String(data,"UTF-8");
            jsonArray=new JSONArray(json);
            if(jsonArray!=null){
                for(int i=0;i<jsonArray.length();i++)
                {
                    s1=jsonArray.getJSONObject(i).getString("state");
                    if(s1.equalsIgnoreCase(state))
                    {
                        Toast.makeText(this, s1+"2", Toast.LENGTH_SHORT).show();
                        JSONArray jsonArray2=jsonArray.getJSONObject(i).getJSONArray("districts");
                        if(jsonArray2!=null)
                        {
                            for(int j=0;j<jsonArray2.length();j++)
                                dlist.add(jsonArray2.getString(j));
                        }
                    }
                }
            }
        }
        catch (IOException e){e.printStackTrace();}
        catch (JSONException je){je.printStackTrace();}
        return dlist;
    }

}
