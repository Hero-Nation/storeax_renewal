package net.heronattion.solowin.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.heronattion.solowin.R;

/**
 * Created by HERO NATION2 on 2017-03-09.
 */

public class CameraDesc4 extends AppCompatActivity {

    Button btn;
    ImageView iv;
    private TextView desc4;
    private LinearLayout activitymain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_desc4);
        this.activitymain = (LinearLayout) findViewById(R.id.activity_main);
        this.desc4 = (TextView) findViewById(R.id.desc4);

        btn = (Button) findViewById(R.id.btn);
        iv = (ImageView) findViewById(R.id.iv);

        if(CameraInit.flag.equals("1")) {
            iv.setPadding(0, 150, 0, 0);
            iv.setImageResource(R.drawable.camera_top_desc4);
            desc4.setText(R.string.camera_press_right_shoulder);
        }
        else {
            iv.setPadding(0, 150, 0, 0);
            iv.setImageResource(R.drawable.camera_btm_desc4);
            desc4.setText(R.string.camera_press_right_waist);
        }
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent next_intent = new Intent(
                        getApplicationContext(), // 현재 화면의 제어권자
                        CameraActivity.class); // 다음 넘어갈 클래스 지정
                startActivity(next_intent);
                finish();

            }
        });
    } // end onCreate()

}
