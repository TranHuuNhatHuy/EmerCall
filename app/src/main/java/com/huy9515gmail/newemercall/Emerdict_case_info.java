package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Emerdict_case_info extends AppCompatActivity {
    public static final String CASENAME = "CaseName";
    public static final String INFOFILENAME = "InfoFileName";
    String caseName, fileName;

    @BindView(R.id.tv_Case)
    TextView tv_Case;
    @BindView(R.id.tv_CaseInfo)
    TextView tv_CaseInfo;
    @BindView(R.id.btnBack)
    Button btnBack;

    @BindView(R.id.toolbar_main)
    Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emerdict_case_info);
        ButterKnife.bind(this);

        myToolbar.setTitle("EmerDict Case");
        myToolbar.setTitleTextColor(Color.WHITE);

        Intent intent = getIntent();
        Bundle bundle = getIntent().getExtras();
        caseName = bundle.getString(CASENAME);
        fileName = bundle.getString(INFOFILENAME);

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
