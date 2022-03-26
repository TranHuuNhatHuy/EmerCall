package com.huy9515gmail.newemercall;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WelcomeActivity5 extends AppCompatActivity {

    @BindView(R.id.btnAskPermissions)
    Button btnAskPermissions;
    @BindView(R.id.btn_next)
    Button btnNext;

    public static final int PERMISSION_CODE = 1303;
    Boolean isPermissionsAccessible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen_5);
        ButterKnife.bind(this);

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isPermissionsAccessible) {
                    Intent intent = new Intent(WelcomeActivity5.this, WelcomeActivity6.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(WelcomeActivity5.this, "Location Access Permission required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAskPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT < 23) {
                    Toast.makeText(WelcomeActivity5.this, "Location Access Permission granted. Tap \"Next\" to continue.", Toast.LENGTH_SHORT).show();
                    isPermissionsAccessible = true;
                } else {
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(WelcomeActivity5.this, new String[]{
                                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
                    }
                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionsAccessible = true;
                Toast.makeText(this, "Đã cấp quyền truy cập vị trí. Ấn nút Tiếp để hoàn thành phần hướng dẫn cơ bản cho EmerCall", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bạn cần cấp quyền truy cập vị trí để EmerCall hoạt động bình thường", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
