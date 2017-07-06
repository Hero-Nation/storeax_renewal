package net.heronattion.solowin.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.CameraDesc3;
import net.heronattion.solowin.camera.CameraInit;

public class CameraInfoActivity extends BaseActivity {


    private android.widget.TextView nextButton;
    private Activity mainActivity = this;
    private static final int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_info);

        bindViews();
        setCustomActionBar();
        setValues();
        setupEvents();
    }


    @Override
    public void setValues() {
        super.setValues();
        int permissionCamera = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        int permissionStorage = ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionCamera == PackageManager.PERMISSION_DENIED || permissionStorage == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA);
        }
    }


    @Override
    public void setupEvents() {
        super.setupEvents();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(mContext, CameraInit.class);
//                intent.putExtra("mypage_flag", "1");
                Intent intent = new Intent(mContext, CameraDesc3.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        titleTxt.setText("카메라 측정");
    }


    @Override
    public void bindViews() {
        super.bindViews();
        this.nextButton = (TextView) findViewById(R.id.nextButton);
    }

}
