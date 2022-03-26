package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BannedActivity extends AppCompatActivity {

    @BindView(R.id.btnQuestion)
    Button btnQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banned);
        ButterKnife.bind(this);

        btnQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BannedActivity.this, BannedExplainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
