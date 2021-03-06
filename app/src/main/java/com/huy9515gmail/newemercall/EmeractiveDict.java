package com.huy9515gmail.newemercall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmeractiveDict extends AppCompatActivity {

    public static AppCompatActivity dictActivity;

    public FusedLocationProviderClient mFusedLocationProviderClient;

    public final String EMERACTIVECASELIST = "EMERACTIVECASELIST";
    public final String DATABASE = "DATABASE";
    public final String CASENAME = "CASENAME";
    public final String INFOFILENAME = "INFOFILENAME";

    DatabaseReference thisUser, thisUserLocation, userLatitudeRef, userLongitudeRef, patient, patientAge, patientGender, patientSymptoms, thisUserStatus, targetAmbulance;
    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    final DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference("userStatus");

    public final ArrayList<String> emeractiveCaseList = new ArrayList<>();
    public final ArrayList<String> database = new ArrayList<>();

    String deviceID;

    boolean gotAmbulancesReceive = false;

    double userLatitude, userLongitude;

    @BindView(R.id.emercallactive_dict_listview)
    ListView fragment_dict_listview;

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @BindView(R.id.dict_btnSwitch)
    Button dict_btnSwitch;
    @BindView(R.id.dict_btnFinish)
    Button dict_btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emercallactive_dict);
        ButterKnife.bind(this);

        // getting this activity
        dictActivity = this;

        //acquire user ID
        deviceID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        //initiating personal Firebase database
        thisUser = users.child(deviceID);
        thisUserLocation = thisUser.child("location");
        userLatitudeRef = thisUserLocation.child("latitude");
        userLongitudeRef = thisUserLocation.child("longitude");
        patient = thisUser.child("patient");
        patientAge = patient.child("age");
        patientGender = patient.child("gender");
        patientSymptoms = patient.child("symptoms");
        thisUserStatus = userStatus.child(deviceID);
        targetAmbulance = thisUser.child("targetAmbulance");

        myToolbar.setTitle("C??c ph????ng ph??p c???p c???u");
        myToolbar.setTitleTextColor(Color.WHITE);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //receive current location and put in on the Firebase database in case people stuck in this activity for too long
        getDeviceLocation();

        //inform user that sometimes they have to switch acivity to check for ambulance updates
        AlertDialog.Builder builder = new AlertDialog.Builder(EmeractiveDict.this);
        builder.setTitle("H??? th???ng ???? ghi nh???n y??u c???u c???a b???n!");
        builder.setMessage("B???n s??? nh???n ???????c ph???n h???i t??? xe c???p c???u trong th???i gian s???m nh???t. Trong khi s?? c???u cho n???n nh??n, h??y nh??? th?????ng xuy??n chuy???n qua V??? tr?? xe c???p c???u ????? n???m b???t t??nh h??nh xe c???p c???u")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        emeractiveCaseList.addAll(bundle.getStringArrayList(EMERACTIVECASELIST));
        database.addAll(bundle.getStringArrayList(DATABASE));

        CustomAdapter customAdaper = new CustomAdapter(this, R.layout.row_listview_emerdict, emeractiveCaseList);
        fragment_dict_listview.setAdapter(customAdaper);

        fragment_dict_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                String caseName = emeractiveCaseList.get(i).toString();
                String infoFileName="";
                for (int j = 0; j < database.size(); j++) {
                    if (database.get(j).equalsIgnoreCase(caseName)) {
                        infoFileName = "EmerDictFile" + (j+1) +".txt";
                        break;
                    }
                }

                Intent intent = new Intent(EmeractiveDict.this, EmeractiveDictCaseInfo.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString(CASENAME, caseName);
                bundle1.putString(INFOFILENAME, infoFileName);
                bundle1.putStringArrayList(EMERACTIVECASELIST, emeractiveCaseList);
                bundle1.putStringArrayList(DATABASE, database);

                intent.putExtras(bundle1);
                startActivity(intent);

            }
        });

        dict_btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EmeractiveDict.this, EmeractiveMap.class);
                startActivity(intent);
            }
        });

        dict_btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EmeractiveDict.this);
                builder.setTitle("X??c nh???n r???i tr???ng th??i c???p c???u?");
                builder.setMessage("B???n s??? ???????c chuy???n ?????n m??n h??nh ch??nh c???a EmerCall. ??i???u n??y kh??ng l??m ???nh h?????ng ?????n l??? tr??nh c???a xe c???p c???u, nh??ng b???n s??? kh??ng th??? theo d??i v??? tr?? xe");
                builder.setCancelable(false);
                builder.setPositiveButton("H???y b???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("X??c nh???n", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //getting back to main screen
                        Intent intent = new Intent(EmeractiveDict.this, EmerCall.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

    }

    public void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Get the user's current location
                        Location mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            userLatitude = mLastKnownLocation.getLatitude();
                            userLongitude = mLastKnownLocation.getLongitude();
                            // Update Firebase database
                            userLatitudeRef.setValue(userLatitude);
                            userLongitudeRef.setValue(userLongitude);
                        }
                    } else {
                        Log.d("getDeviceLocation", "Current location is null. Using defaults.");
                        Log.e("getDeviceLocation", "Exception: %s", task.getException());
                    }
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

}
