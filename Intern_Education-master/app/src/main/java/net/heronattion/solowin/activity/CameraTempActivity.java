package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import net.heronattion.solowin.R;

//import static net.heronattion.solowin.activity.SignupInformation2Activity.name;
//import static net.heronattion.solowin.activity.SignupInformation2Activity.strCategoryPkey;

public class CameraTempActivity extends BaseActivity {

    private android.widget.Button tempBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_temp);
        this.tempBtn = (Button) findViewById(R.id.tempBtn);

        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SignupInformation3Activity.signupInfoActivity3.finish();
                Intent intent = new Intent(mContext, SignupInformation3Activity.class);
//                intent.putExtra("PKey", strCategoryPkey);
//                intent.putExtra("Name", name);
                startActivity(intent);
                finish();
            }
        });

    }

}
