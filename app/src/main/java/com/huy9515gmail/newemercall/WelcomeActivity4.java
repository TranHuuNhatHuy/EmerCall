package com.huy9515gmail.newemercall;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity4 extends AppCompatActivity{

    @BindView(R.id.btn_next)
    Button btnNext;

    @BindView(R.id.edtName)
    EditText edtName;
    @BindView(R.id.edtDOB)
    EditText edtDOB;
    @BindView(R.id.edtGender)
    EditText edtGender;
    @BindView(R.id.edtID)
    EditText edtID;
    @BindView(R.id.edtAddress)
    EditText edtAddress;
    @BindView(R.id.edtCMND)
    EditText edtCMND;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen_4);
        ButterKnife.bind(this);

        final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
        final DatabaseReference userstatus = FirebaseDatabase.getInstance().getReference("userStatus");

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String name = edtName.getText().toString().trim();
                String dob = edtDOB.getText().toString().trim();
                String gender = edtGender.getText().toString().trim();
                String id = edtID.getText().toString().trim();
                String address = edtAddress.getText().toString().trim();
                String cmnd = edtCMND.getText().toString().trim();
                String deviceID = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

                if ( (!(name.matches(""))) && (!(dob.matches(""))) && (!(gender.matches(""))) && (!(id.matches(""))) && (!(address.matches(""))) && (!(cmnd.matches(""))) ) {

                    SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putString("name", name);
                    editor.putString("dob", dob);
                    editor.putString("gender", gender);
                    editor.putString("id", id);
                    editor.putString("address", address);
                    editor.putString("cmnd", cmnd);
                    editor.putString("deviceID", deviceID);

                    editor.apply();
                    editor.commit();

                    //initiating Firebase database
                    DatabaseReference thisUser = users.child(deviceID);
                    //user information
                    DatabaseReference thisUserInfo = thisUser.child("userInfo");
                    DatabaseReference firebaseName = thisUserInfo.child("name"); firebaseName.setValue(name);
                    DatabaseReference firebaseDOB = thisUserInfo.child("dob"); firebaseDOB.setValue(dob);
                    DatabaseReference firebaseGender = thisUserInfo.child("gender"); firebaseGender.setValue(gender);
                    DatabaseReference firebaseBHYT = thisUserInfo.child("bhyt"); firebaseBHYT.setValue(id);
                    DatabaseReference firebaseAddress = thisUserInfo.child("address"); firebaseAddress.setValue(address);
                    DatabaseReference firebaseCMND = thisUserInfo.child("cmnd"); firebaseCMND.setValue(cmnd);
                    //user status inactive
                    DatabaseReference status = userstatus.child(deviceID); status.setValue(0);
                    DatabaseReference thisStatus = thisUser.child("status"); thisStatus.setValue(0); //inner status
                    //patient status
                    DatabaseReference patient = thisUser.child("patient");
                    DatabaseReference patientGender = patient.child("gender"); patientGender.setValue("");
                    DatabaseReference patientAge = patient.child("age"); patientAge.setValue("");
                    DatabaseReference patientSymptoms = patient.child("symptoms"); patientSymptoms.setValue("");
                    //user target ambulance
                    DatabaseReference targetAmbulance = thisUser.child("targetAmbulance"); targetAmbulance.setValue("");
                    //user location
                    DatabaseReference thisUserLocation = thisUser.child("location");
                    DatabaseReference thisUserLatitude = thisUserLocation.child("latitude"); thisUserLatitude.setValue(0);
                    DatabaseReference thisUserLongitude = thisUserLocation.child("longitude"); thisUserLongitude.setValue(0);
                    //user call count
                    DatabaseReference thisUserCallCount = thisUser.child("callCount");
                    DatabaseReference thisUserCall = thisUserCallCount.child("call"); thisUserCall.setValue(0);
                    DatabaseReference thisUserFake = thisUserCallCount.child("fake"); thisUserFake.setValue(0);

                    Toast.makeText(WelcomeActivity4.this, "Information saved!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(WelcomeActivity4.this, WelcomeActivity5.class);
                    startActivity(intent);
                    finish();

                }
                else
                    Toast.makeText(WelcomeActivity4.this, "Yêu cầu người dùng điền đầy đủ thông tin cá nhân!", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
