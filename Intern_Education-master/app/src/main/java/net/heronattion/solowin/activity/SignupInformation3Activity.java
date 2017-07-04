package net.heronattion.solowin.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.Data.DotData;

import java.util.ArrayList;

/**
 * Created by hero on 2017-06-08.
 */

public class SignupInformation3Activity extends BaseActivity {

    public TextView addSize;
    public static android.widget.EditText sizeEdit;
    private TextView nextButton3;

    private String CategoryPKey;
    private String CategoryName;

    static public BaseActivity signupInfoActivity3;

    //최초 측정 이후, 다시 점을 찍기 위해 저장하는 전역 리스트
    //여기서는 선언만 한다.
    public static ArrayList<DotData> savedCardDotList ;

    //수동측정인지, 자동측정인지, 클릭을 안했는지 확인하는 플래그 변수
    // 0이면 클릭 X, 1이면 수동측정, 2이면 자동측정
    public static int autoCheckFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupinformation3);
        signupInfoActivity3 = this;
        setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("추가정보 입력");

        CategoryPKey = getIntent().getExtras().getString("PKey");
        CategoryName = getIntent().getExtras().getString("Name");

        bindViews();
        setValues();
        setupEvents();

    }
    @Override
    public void setValues() {
        super.setValues();
        savedCardDotList = new ArrayList<DotData>();
        autoCheckFlag = 0; // 클릭안한 상태

    }
    @Override
    public void setupEvents() {
        sizeEdit.setOnClickListener(new View.OnClickListener() { // ImageButton을 Click시 AlertDialog가 생성되도록 아래과 같이 설계
            @Override
            public void onClick(View v) {
                // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.signup_dialog, null);
                TextView customTitle = (TextView) view.findViewById(R.id.customtitle);
                customTitle.setText("어떤 방식으로 측정하시겠습니까?");
                customTitle.setTextColor(Color.BLACK);
                ImageView customIcon = (ImageView) view.findViewById(R.id.customdialogicon);
                AlertDialog.Builder builder = new AlertDialog.Builder(SignupInformation3Activity.this);
                builder.setView(view);
                builder.setPositiveButton("카메라 측정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        autoCheckFlag = 2;
                        Intent intent = new Intent(mContext, CameraCautionActivity.class);
                        startActivity(intent);

                    }
                });
                builder.setNegativeButton("수동 측정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        autoCheckFlag = 1;
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        addSize.setOnClickListener(new View.OnClickListener() { // ImageButton을 Click시 AlertDialog가 생성되도록 아래과 같이 설계
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CameraMeasureActivity.class);
                intent.putExtra("CategoryPKey", CategoryPKey);
                intent.putExtra("CategoryName", CategoryName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void bindViews() {
        this.nextButton3 = (TextView) findViewById(R.id.nextButton3);
        this.sizeEdit = (EditText) findViewById(R.id.sizeEdit);
        this.addSize = (TextView) findViewById(R.id.addSize);
    }


}
