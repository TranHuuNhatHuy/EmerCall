package com.huy9515gmail.newemercall;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Emeracc_activity extends Activity {

    public static final String NAME = "Name";
    public static final String DOB = "DOB";
    public static final String GENDER = "Gender";
    public static final String ID = "ID";
    public static final String ADDRESS = "Address";
    public static final String CMND = "CMND";
    public static final int REQUEST_CODE = 7820;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    final DatabaseReference users = database.getReference("users");

    @BindView(R.id.bottom_navigation_emeracc)
    BottomNavigationView bottomNavigation;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvDOB)
    TextView tvDOB;
    @BindView(R.id.tvGender)
    TextView tvGender;
    @BindView(R.id.tvID)
    TextView tvID;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvCMND)
    TextView tvCMND;
    @BindView(R.id.btnCustomize)
    Button btnCustomize;

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emeraccount);
        ButterKnife.bind(this);

        SharedPreferences sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        if (sharedPreferences != null) {

            tvName.setText(sharedPreferences.getString("name", null));
            tvDOB.setText(sharedPreferences.getString("dob", null));
            tvGender.setText(sharedPreferences.getString("gender", null));
            tvID.setText(sharedPreferences.getString("id", null));
            tvAddress.setText(sharedPreferences.getString("address", null));
            tvCMND.setText(sharedPreferences.getString("cmnd", null));

        }

        myToolbar.setTitle("User Information");
        myToolbar.setTitleTextColor(Color.WHITE);

        bottomNavigation.getMenu().getItem(2).setChecked(true);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_emeracc: break;
                    case R.id.action_emercall:
                        Intent intent1 = new Intent(Emeracc_activity.this, EmerCall.class);
                        startActivity(intent1); finish(); break;
                    case R.id.action_emerdict:
                        Intent intent2 = new Intent(Emeracc_activity.this, Emerdict_activity.class);
                        startActivity(intent2); finish(); break;
                    default: break;
                }
                return true;
            }
        });

        btnCustomize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Emeracc_activity.this);
                builder.setTitle("You cannot change the information right now!");
                builder.setMessage("If you want to change your EmerCall personal information, please contact the nearest hospital or medical facility that has connected to EmerCall system for further instructions.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}
