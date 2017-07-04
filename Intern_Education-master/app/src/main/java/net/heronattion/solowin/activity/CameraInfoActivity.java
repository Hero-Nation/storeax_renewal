package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.CameraInit;

public class CameraInfoActivity extends BaseActivity {


    private android.widget.TextView nextButton;

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
    }


    @Override
    public void setupEvents() {
        super.setupEvents();
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CameraInit.class);
                intent.putExtra("mypage_flag","1");
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
