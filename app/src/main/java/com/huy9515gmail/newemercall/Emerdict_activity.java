package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Emerdict_activity extends AppCompatActivity {
    public static final String CASENAME = "CaseName";
    public static final String INFOFILENAME = "InfoFileName";
    public static final String DATABASE = "DATABASE";

    @BindView(R.id.bottom_navigation_emerdict)
    BottomNavigationView bottomNavigation;
    @BindView(R.id.lvEmerdict)
    ListView lvEmerdict;

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emerdictionary);
        ButterKnife.bind(this);

        myToolbar.setTitle("EmerCall Dictionary");
        myToolbar.setTitleTextColor(Color.WHITE);

        ArrayList<String> database = new ArrayList<>();

        bottomNavigation.getMenu().getItem(0).setChecked(true);
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_emerdict: break;
                    case R.id.action_emercall:
                        Intent intent1 = new Intent(Emerdict_activity.this, EmerCall.class);
                        startActivity(intent1); finish(); break;
                    case R.id.action_emeracc:
                        Intent intent2 = new Intent(Emerdict_activity.this, Emeracc_activity.class);
                        startActivity(intent2); finish(); break;
                    default: break;
                }
                return true;
            }
        });

        BufferedReader input = null;
        try {
            String filenameTXT = "EmergencyCase.txt";

            InputStream iS = getResources().getAssets().open(filenameTXT);
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));
            String sCurrentLine;

            while ((sCurrentLine = reader.readLine()) != null) {
                database.add(sCurrentLine);
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null){
                    input.close();
                }
            } catch (IOException ex) {

                ex.printStackTrace();

            }
        }

        CustomAdapter customAdaper = new CustomAdapter(this, R.layout.row_listview_emerdict, database);
        lvEmerdict.setAdapter(customAdaper);

        lvEmerdict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String caseName = new String(lvEmerdict.getItemAtPosition(position).toString());
                String infoFileName = new String("EmerDictFile" + (position + 1) + ".txt");

                Intent intent = new Intent(Emerdict_activity.this, Emerdict_case_info.class);
                Bundle bundle = new Bundle();
                bundle.putString(CASENAME, caseName);
                bundle.putString(INFOFILENAME, infoFileName);

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
}
