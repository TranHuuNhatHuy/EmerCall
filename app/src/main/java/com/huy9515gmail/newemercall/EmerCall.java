package com.huy9515gmail.newemercall;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmerCall extends AppCompatActivity {

    SharedPreferences Prefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";

    @BindView(R.id.btnEmerCall)
    ImageButton btnEmerCall;
    @BindView(R.id.bottom_navigation_emercall)
    BottomNavigationView bottomNavigation;

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //checking first-time access
        Prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Boolean welcomeScreenShown = Prefs.getBoolean(welcomeScreenShownPref, false);
        //active welcome state
        if (!welcomeScreenShown) {
            //switch to welcome screens
            Intent intent = new Intent(EmerCall.this, WelcomeActivity1.class);
            startActivity(intent);
            finish();
        }

        setContentView(R.layout.emercall);
        ButterKnife.bind(this);

        //setting up toolbar
        myToolbar.setTitle("EmerCall");
        myToolbar.setTitleTextColor(Color.WHITE);

        //activate button handling
        btnEmerCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show an analog to ask user if they unintentionally touched the button
                AlertDialog.Builder builder = new AlertDialog.Builder(EmerCall.this);
                builder.setTitle("Make sure you are not mistaking!");
                builder.setMessage("You have just activated the emergency mode, and the application will send an emergency signal, equivalent to an ordinary emergency call. Are you sure to continue?");
                builder.setCancelable(false);
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setNegativeButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(EmerCall.this, EmercallActivated.class);
                        startActivity(intent);
                        finish();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //bottom navigation bar handle
        bottomNavigation.getMenu().getItem(1).setChecked(true);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_emercall: break;
                    case R.id.action_emerdict:
                        Intent intent1 = new Intent(EmerCall.this, Emerdict_activity.class);
                        startActivity(intent1); finish(); break;
                    case R.id.action_emeracc:
                        Intent intent2 = new Intent(EmerCall.this, Emeracc_activity.class);
                        startActivity(intent2); finish(); break;
                    default: break;
                }
                return true;
            }
        });
    }
}
