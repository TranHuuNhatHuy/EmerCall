package com.huy9515gmail.newemercall;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmeractiveDictCaseInfo extends Activity {

    public final String CASENAME = "CASENAME";
    public final String INFOFILENAME = "INFOFILENAME";
    public final String EMERACTIVECASELIST = "EMERACTIVECASELIST";
    public final String DATABASE = "DATABASE";
    public final String BUNDLE = "BUNDLE";

    @BindView(R.id.tv_Case)
    TextView tv_Case;
    @BindView(R.id.tv_CaseInfo)
    TextView tv_CaseInfo;
    @BindView(R.id.btnBack)
    Button btnBack;
    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    String caseName, fileName;
    public ArrayList<String> emeractiveCaseList = new ArrayList<>();
    public ArrayList<String> database = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emerdict_case_info);
        ButterKnife.bind(this);

        myToolbar.setTitle("Phương pháp cấp cứu");
        myToolbar.setTitleTextColor(Color.WHITE);

        Bundle bundle = getIntent().getExtras();
        caseName = bundle.getString(CASENAME);
        fileName = bundle.getString(INFOFILENAME);
        emeractiveCaseList = bundle.getStringArrayList(EMERACTIVECASELIST);
        database = bundle.getStringArrayList(DATABASE);

        tv_Case.setText(caseName);

        BufferedReader input = null;
        try {
            InputStream iS = getResources().getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(iS));

            StringBuilder stringBuilder = new StringBuilder();
            String s = null;
            while ((s=reader.readLine()) != null) {
                stringBuilder.append(s).append("\n");
            }

            tv_CaseInfo.setText(stringBuilder.toString());

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

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
