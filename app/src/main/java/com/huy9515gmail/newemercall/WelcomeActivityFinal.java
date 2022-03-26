package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;

public class WelcomeActivityFinal extends AppCompatActivity{

    SharedPreferences Prefs;
    final String welcomeScreenShownPref = "welcomeScreenShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen_final);
        ButterKnife.bind(this);

        Prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor = Prefs.edit();
        editor.putBoolean(welcomeScreenShownPref, true);
        editor.commit();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivityFinal.this, EmerCall.class);
                startActivity(intent);
                finish();
            }}, 3000
        );
    }
}
