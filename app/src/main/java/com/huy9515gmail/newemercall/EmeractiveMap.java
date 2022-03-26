package com.huy9515gmail.newemercall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmeractiveMap extends AppCompatActivity implements OnMapReadyCallback {

    public static AppCompatActivity mapActivity;

    public GoogleMap mMap;
    public Location mLastKnownLocation;
    public FusedLocationProviderClient mFusedLocationProviderClient;
    public CameraPosition mCameraPosition;

    public final LatLng mDefaultLocation = new LatLng(16.075505, 108.222208);
    public static final int DEFAULT_ZOOM = 12;
    public static final String TAG = EmeractiveMap.class.getSimpleName();
    public static final String KEY_CAMERA_POSITION = "camera_position";
    public static final String KEY_LOCATION = "location";

    public double userLatitude, userLongitude, ambulanceLatitude, ambulanceLongitude, hospitalLatitude, hospitalLongitude;
    public String deviceID;

    public Timer t;

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @BindView(R.id.fragment_emercallactive_map_status)
    TextView tvMapStatus;
    @BindView(R.id.map_btnSwitch)
    Button map_btnSwitch;
    @BindView(R.id.map_btnFinish)
    Button map_btnFinish;

    public Marker userMarker, ambulanceMarker, hospitalMarker;

    DatabaseReference thisUser, thisUserLocation, userLatitudeRef, userLongitudeRef, patient, patientAge, patientGender, patientSymptoms, thisUserStatus, targetAmbulance;
    final DatabaseReference users = FirebaseDatabase.getInstance().getReference("users");
    final DatabaseReference userStatus = FirebaseDatabase.getInstance().getReference("userStatus");
    final DatabaseReference ambulances = FirebaseDatabase.getInstance().getReference("ambulances");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        setContentView(R.layout.emercallactive_map);
        ButterKnife.bind(this);

        // getting this activity
        mapActivity = this;

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get device ID
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

        // Set up toolbar
        myToolbar.setTitle("Vị trí xe cấp cứu");
        myToolbar.setTitleTextColor(Color.WHITE);

        //Bắt sự kiện cho nhóm nút điều chỉnh phía dưới
        map_btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        map_btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(EmeractiveMap.this);
                builder.setTitle("Xác nhận rời trạng thái cấp cứu?");
                builder.setMessage("Bạn sẽ được chuyển đến màn hình chính của EmerCall. Điều này không làm ảnh hưởng đến lộ trinh xe cấp cứu");
                builder.setCancelable(false);
                builder.setPositiveButton("Hủy bỏ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Xác nhận", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //getting back to main screen
                        Intent intent = new Intent(EmeractiveMap.this, EmerCall.class);
                        startActivity(intent);
                        EmeractiveDict.dictActivity.finish();
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

        // Set up map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /* Get the location of the hospital and update its location data
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getHospitalLocation();
            }
        }, 1000);*/

        //Declare the timer
        t = new Timer();
        // Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                getDeviceLocation();

                getAmbulanceLocation();

            }
        }, 0, 3000);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        //check for the corresponding ambulance
        ambulanceAnnouncementCheck();

        // Get the current location of the device and update its location data
        getDeviceFirstLocation();

        // First attempt to invoke hospital database
        getHospitalLocation();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    public void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Get the user's current location
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            userLatitude = mLastKnownLocation.getLatitude();
                            userLongitude = mLastKnownLocation.getLongitude();
                            // Update Firebase database
                            userLatitudeRef.setValue(userLatitude);
                            userLongitudeRef.setValue(userLongitude);
                            // Set the map's camera position to the current location of the device and change the marker location
                            LatLng currentUserLocation = new LatLng(userLatitude, userLongitude);
                            if (userMarker != null) userMarker.setPosition(currentUserLocation);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getDeviceFirstLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Get the user's current location
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            userLatitude = mLastKnownLocation.getLatitude();
                            userLongitude = mLastKnownLocation.getLongitude();
                            // Update Firebase database
                            userLatitudeRef.setValue(userLatitude);
                            userLongitudeRef.setValue(userLongitude);
                            // Set the map's camera position to the current location of the device and add a marker
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            LatLng currentUserLocation = new LatLng(userLatitude, userLongitude);
                            userMarker = mMap.addMarker(new MarkerOptions()
                                    .position(currentUserLocation)
                                    .title("Bạn hiện ở đây")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.usermarker)));
                        }
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getAmbulanceLocation() {
        // Access ambulance location Firebase database and update its location, then reposition the marker

        // Access Firebase database and update location
        targetAmbulance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!(dataSnapshot.getValue(String.class).equals("")) && !(dataSnapshot.getValue(String.class).equals("none"))) {

                    final String ambulanceID = dataSnapshot.getValue(String.class);
                    //receive target ambulance id and Firebase database
                    DatabaseReference targetAmbulanceRef = ambulances.child(ambulanceID);
                    //receive ambulance location
                    DatabaseReference ambulanceLatitudeRef = targetAmbulanceRef.child("latitude");
                    ambulanceLatitudeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            ambulanceLatitude = dataSnapshot1.getValue(Double.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(EmeractiveMap.this, "Không thể nhận vĩ độ xe cấp cứu", Toast.LENGTH_SHORT).show();
                        }
                    });
                    DatabaseReference ambulanceLongitudeRef = targetAmbulanceRef.child("longitude");
                    ambulanceLongitudeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot2) {
                            ambulanceLongitude = dataSnapshot2.getValue(Double.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(EmeractiveMap.this, "Không thể nhận kinh độ xe cấp cứu", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Add or reposition the marker
                    if ((ambulanceLatitude != 0) && (ambulanceLongitude != 0)) {
                        LatLng currentAmbulanceLocation = new LatLng(ambulanceLatitude, ambulanceLongitude);
                        if (ambulanceMarker != null) {
                            ambulanceMarker.setPosition(currentAmbulanceLocation);
                        } else {
                            ambulanceMarker = mMap.addMarker(new MarkerOptions()
                                    .position(currentAmbulanceLocation)
                                    .title("Vị trí xe cấp cứu")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulancemarker)));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EmeractiveMap.this, "Không thể nhận dữ liệu từ máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getHospitalLocation() {
        // Access the hospital Firebase database, retrieve its location and add a marker

        // Retrieve location
        final DatabaseReference hospitalLocation = FirebaseDatabase.getInstance().getReference("hospitalLocation");
        hospitalLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final DatabaseReference hospitalLatitudeRef = hospitalLocation.child("latitude");
                final DatabaseReference hospitalLongitudeRef = hospitalLocation.child("longitude");

                hospitalLatitudeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot1) {
                        hospitalLatitude = dataSnapshot1.getValue(Double.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                hospitalLongitudeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot2) {
                        hospitalLongitude = dataSnapshot2.getValue(Double.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                // Add a marker
                if ((hospitalLatitude != 0) && (hospitalLongitude != 0)) {
                    LatLng currentHospitalLocation = new LatLng(hospitalLatitude, hospitalLongitude);
                    hospitalMarker = mMap.addMarker(new MarkerOptions()
                            .position(currentHospitalLocation)
                            .title("Vị trí bệnh viện")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.hospitalmarker)));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    public void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        }
    }

    @Override
    public void onPause() {
        t.cancel(); t.purge();
        super.onPause();
    }

    public void ambulanceAnnouncementCheck() {
        //first time announcement for ambulance status
        targetAmbulance.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ambulanceID = dataSnapshot.getValue(String.class);
                //check if string is null
                if (ambulanceID != null) {
                    //in case there is an available ambulance
                    if (!(ambulanceID.equals("")) && !(ambulanceID.equals("none"))) {
                        //inform user that an ambulance is coming
                        AlertDialog.Builder builder = new AlertDialog.Builder(EmeractiveMap.this);
                        builder.setTitle("Xe cấp cứu đang trên đường đến!")
                                .setMessage("Giờ bạn có thể theo dõi vị trí xe cấp cứu, trong khi tiếp tục sơ cứu cho nạn nhân theo hướng dẫn từ Danh mục sơ cứu")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        //update status textview
                        tvMapStatus.setText("Xe cấp cứu đang trên đường đến!");
                    }

                    //in case the hospital confirm that there is no ambulance available
                    if (ambulanceID.equals("none")) {
                        //reset user Firebase database, cuz the user is out from the EmerCallActive system
                        patientAge.setValue(0);
                        patientGender.setValue("");
                        patientSymptoms.setValue("");
                        targetAmbulance.setValue("");
                        //inform user that no ambulances coming
                        AlertDialog.Builder builder = new AlertDialog.Builder(EmeractiveMap.this);
                        builder.setTitle("Hiện không có xe cấp cứu nào!")
                                .setMessage("Hiện tất cả xe cấp cứu đều đang bận. Chúng tôi rất xin lỗi vì sự bất tiện này. Xin hãy tìm cách đưa bệnh nhân đến cơ sở y tế, bệnh viện gần nhất, càng sớm càng tốt!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                        //update status textview
                        tvMapStatus.setText("Không có xe cấp cứu. Yêu cầu tìm cách chuyên chở nạn nhân đến bệnh viện, cơ sở y tế!");
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

}

