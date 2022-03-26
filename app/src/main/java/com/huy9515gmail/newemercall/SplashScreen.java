package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashScreen extends AppCompatActivity {

    DatabaseReference bannedUsers;
    String deviceID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //get device ID
        deviceID = Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                bannedUsers =  FirebaseDatabase.getInstance().getReference("bannedUsers");

                //check if the user has been banned
                bannedUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        boolean isBanned = false;

                        for (DataSnapshot child: dataSnapshot.getChildren()) {
                            if (child.getKey().toString().equals(deviceID)) {
                                //announce user that his device has been banned from EmerCall system
                                isBanned = true;
                            }
                        }

                        if (isBanned) {
                            //if the user is a dick head
                            Intent intent1 = new Intent(SplashScreen.this, BannedActivity.class);
                            startActivity(intent1);
                            finish();
                        } else {
                            //if the user still be a gud person
                            Intent intent2 = new Intent(SplashScreen.this, EmerCall.class);
                            startActivity(intent2);
                            finish();

                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

            }
        }, 3000);

    }
}
