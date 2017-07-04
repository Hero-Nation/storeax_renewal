package net.heronattion.solowin.camera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.heronattion.solowin.R;
import net.heronattion.solowin.activity.BaseActivity;

public class CameraStartActivity extends BaseActivity {

    private Button cameraBtn;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_STORAGE = 1;

    private String path;

    @Override
    public void setValues() {
        super.setValues();
//        path = "/storage/emulated/0/Pictures/MyCameraApp/IMG_20170415_152357.jpg";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start2);

        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCamera == PackageManager.PERMISSION_DENIED || permissionStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(CameraStartActivity.this, new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
//            ActivityCompat.requestPermissions(CameraStartActivity.this, new String[]{}, REQUEST_STORAGE);
        }

//        int permissionStorage = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        bindViews();
        setupEvents();

    }


    @Override
    public void setupEvents() {
        super.setupEvents();
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 카메라 검사 -> 없으면 예외처리
                if (!checkCameraHardware(mContext)) {
                    Toast.makeText(mContext.getApplicationContext(), "카메라가 없습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CameraStartActivity.this, CameraActivity.class);
                    startActivity(intent);
//                    finish();

                }

            }
        });
//        skipBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent =  new Intent(CameraStartActivity.this, ShowActivity.class);
//                intent.putExtra("FILE_PATH","/storage/emulated/0/Pictures/MyCameraApp/IMG_20170419_112804.jpg");
////                intent.putExtra("FILE_PATH","/storage/emulated/0/Pictures/MyCameraApp/IMG_20170418_113505.jpg");
//                startActivity(intent);
////                finish();
//            }
//        });
    }


    // 카메라 유무 확인 함수
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) { // 카메라 있을때
            return true;
        } else { // 카메라 없을때
            return false;
        }
    }

    @Override
    public void bindViews() {
        super.bindViews();

        this.cameraBtn = (Button) findViewById(R.id.cameraBtn);
    }


}
