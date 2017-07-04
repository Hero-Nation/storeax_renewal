package net.heronattion.solowin.camera;
/**
 * Created by HERO NATION2 on 2017-03-10.
 */


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import net.heronattion.solowin.R;

public class CameraInit extends AppCompatActivity {

    public static String flag = "0";
    public static float sCameraShoulderSize = 0;
    public static float sCameraWaistSize = 0;

    private Activity mainActivity = this;
    private static final int REQUEST_CAMERA = 1;

    Button btn;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_init);

        Intent intent = getIntent();
        flag = intent.getStringExtra("mypage_flag");

        btn = (Button) findViewById(R.id.btn);
        iv = (ImageView) findViewById(R.id.iv);

        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCamera == PackageManager.PERMISSION_DENIED || permissionStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);

            if (flag.equals("1")) {
                iv.setPadding(0, 150, 0, 0);
                iv.setImageResource(R.drawable.camera_top);
            } else {
                iv.setPadding(0, 150, 0, 0);
                iv.setImageResource(R.drawable.camera_btm);
            }

            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

            setup();
        } else {
            if (flag.equals("1")) {
                iv.setPadding(0, 200, 0, 0);
                iv.setImageResource(R.drawable.camera_top);
            } else {

                iv.setPadding(0, 100, 0, 0);
                iv.setImageResource(R.drawable.camera_btm);
            }

            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

            setup();
        }

    }

    private void setup() {
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CameraDesc3.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 0) {
            Intent next_intent = new Intent(
                    getApplicationContext(), // 현재 화면의 제어권자
                    CameraDesc3.class); // 다음 넘어갈 클래스 지정
            startActivity(next_intent);
            finish();
        }
    }
}