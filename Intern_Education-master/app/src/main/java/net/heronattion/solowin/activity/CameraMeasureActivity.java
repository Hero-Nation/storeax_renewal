package net.heronattion.solowin.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.Show2Activity;
import net.heronattion.solowin.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static net.heronattion.solowin.activity.SignupInformation2Activity.strCategoryPkey;
import static net.heronattion.solowin.activity.SignupInformation3Activity.autoCheckFlag;
import static net.heronattion.solowin.activity.SignupInformation3Activity.sizeEdit;

/**
 * Created by hero on 2017-06-09.
 */


public class CameraMeasureActivity extends BaseActivity {

    private android.widget.LinearLayout sizeTypeTab1;
    private android.widget.LinearLayout sizeTypeTab2;
    private android.widget.LinearLayout sizeTypeTab3;
    private android.widget.LinearLayout sizeTypeTab4;
    private android.widget.LinearLayout sizeTypeTab5;

    private TextView sizeTypeNameTxt1;
    private TextView sizeTypeNameTxt2;
    private TextView sizeTypeNameTxt3;
    private TextView sizeTypeNameTxt4;
    private TextView sizeTypeNameTxt5;

    private android.widget.ImageView sizeTypeCheckImg1;
    private android.widget.ImageView sizeTypeCheckImg2;
    private android.widget.ImageView sizeTypeCheckImg3;
    private android.widget.ImageView sizeTypeCheckImg4;
    private android.widget.ImageView sizeTypeCheckImg5;

    private TextView selectedSizeTypeNameTxt;
    private TextView skipBtn;
    private TextView nextBtn;

    private LinearLayout[] sizeTypeTabArray;
    private TextView[] sizeTypeNameArray;
    private ImageView[] sizeTypeCheckArray;

    private RequestParams params;
    static public android.widget.EditText detailSizeET;

    private String selectedID;

    String[][] sizeTypeAndSize;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_measure);

        bindViews();
        setCustomActionBar();
        setValues();
        setupEvents();
    }

    @Override
    public void setValues() {
        super.setValues();
        sizeTypeTabArray = new LinearLayout[]{
                sizeTypeTab1,
                sizeTypeTab2,
                sizeTypeTab3,
                sizeTypeTab4,
                sizeTypeTab5
        };

        sizeTypeNameArray = new TextView[]{
                sizeTypeNameTxt1,
                sizeTypeNameTxt2,
                sizeTypeNameTxt3,
                sizeTypeNameTxt4,
                sizeTypeNameTxt5
        };

        sizeTypeCheckArray = new ImageView[]{
                sizeTypeCheckImg1,
                sizeTypeCheckImg2,
                sizeTypeCheckImg3,
                sizeTypeCheckImg4,
                sizeTypeCheckImg5
        };

        params = new RequestParams();
        params.add("SizeTypeID", strCategoryPkey); // 지금은 static 변수로 받고 있지만, 추후 클래스로 바꿔보자

        if (sizeEdit.getText().toString().length() != 0) {
            detailSizeET.setText(sizeEdit.getText().toString());
        }

    }

    @Override
    public void setupEvents() {

        // 버튼 동적할당
        HttpClient.post("android/CHS/getSizeTypeName.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    sizeTypeAndSize = new String[jsonArray.length()][2];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String sizeTypeID = item.getString("SizeTypeID");
                        String sizeName = item.getString("SizeName");

                        Log.d("sizeTypeID", sizeTypeID);
                        Log.d("sizeName", sizeName);

                        sizeTypeNameArray[i].setText(sizeName);
                        sizeTypeTabArray[i].setTag(sizeTypeID);
                        if(i==0){
                            selectedID = sizeTypeID;
                        }
                        sizeTypeAndSize[i][0] = sizeTypeID;
                        sizeTypeAndSize[i][1] = "";
                    }
                    selectedSizeTypeNameTxt.setText(sizeTypeNameArray[0].getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < sizeTypeAndSize.length; i++) {
                    Log.d("sizeTypeID", sizeTypeAndSize[i][0]);
                    Log.d("size->null만 나올것", sizeTypeAndSize[i][1]);
                }

                // 버튼 클릭 이벤트
                for (final LinearLayout ll : sizeTypeTabArray) {
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            // 배열에 저장된 사이즈 출력
                            for (int j = 0; j < sizeTypeAndSize.length; j++) {
                                if(sizeEdit.getText().toString().length()!=0) {
                                    if (ll.getTag().toString().equals(sizeTypeAndSize[j][0])) {
                                        detailSizeET.setText(sizeTypeAndSize[j][1]);
                                        break;
                                    }
                                }
                            }

                            // 탭 클릭할때마다 이전의 사이즈 배열에 저장
                            for (int j = 0; j < sizeTypeAndSize.length; j++) {
                                if (selectedID.equals(sizeTypeAndSize[j][0])) {
                                    sizeTypeAndSize[j][1] = detailSizeET.getText().toString();
                                    break;
                                }
                            }
                            selectedID = ll.getTag().toString();

                            for(int j = 0 ; j < sizeTypeAndSize.length ; j++){

                                System.out.println("ID : " + sizeTypeAndSize[j][0] + " Size : "+ sizeTypeAndSize[j][1]) ;

                            }

                            for (int i = 0; i < sizeTypeTabArray.length; i++) {
                                sizeTypeTabArray[i].setBackgroundColor(Color.WHITE);
                                sizeTypeNameArray[i].setTextColor(Color.rgb(137, 137, 137));
//                        sizeTypeCheckArray[i].setBackgroundDrawable(getResources().getDrawable(R.drawable.check_grey));
                                sizeTypeCheckArray[i].setImageResource(0);
                                sizeTypeCheckArray[i].setImageResource(R.drawable.check_grey);
                            }

                            TextView tv = (TextView) (ll.getChildAt(1));
                            ImageView iv = (ImageView) (ll.getChildAt(2));

                            v.setBackgroundColor(Color.rgb(181, 173, 222));
                            tv.setTextColor(Color.WHITE);
                            iv.setImageResource(0);
                            iv.setImageResource(R.drawable.check_white);
//                    iv.setBackgroundDrawable(getResources().getDrawable(R.drawable.check_white));

                            selectedSizeTypeNameTxt.setText(tv.getText());


                        }
                    });

                }

                // 첫번째칸 트리거
                sizeTypeTabArray[0].performClick();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });


        detailSizeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (autoCheckFlag) {
                    case 0: {// 선택 안함
                        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
                        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                        View view = inflater.inflate(R.layout.signup_dialog, null);
                        TextView customTitle = (TextView) view.findViewById(R.id.customtitle);
                        customTitle.setText("어떤 방식으로 측정하시겠습니까?");
                        customTitle.setTextColor(Color.BLACK);
                        ImageView customIcon = (ImageView) view.findViewById(R.id.customdialogicon);
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                    break;
                    case 1: { // 수동 측정

                    }
                    break;
                    case 2: {
                        Intent intent = new Intent(mContext, Show2Activity.class);
                        intent.putExtra("caseFlag", "2");
                        startActivity(intent);
                    }
                    break;
                }

            }
        });
    }


    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("카메라 측정");
    }

    @Override
    public void bindViews() {
        this.nextBtn = (TextView) findViewById(R.id.nextBtn);
        this.skipBtn = (TextView) findViewById(R.id.skipBtn);
        this.detailSizeET = (EditText) findViewById(R.id.detailSizeET);
        this.selectedSizeTypeNameTxt = (TextView) findViewById(R.id.selectedSizeTypeNameTxt);
        this.sizeTypeTab5 = (LinearLayout) findViewById(R.id.sizeTypeTab5);
        this.sizeTypeCheckImg5 = (ImageView) findViewById(R.id.sizeTypeCheckImg5);
        this.sizeTypeNameTxt5 = (TextView) findViewById(R.id.sizeTypeNameTxt5);
        this.sizeTypeTab4 = (LinearLayout) findViewById(R.id.sizeTypeTab4);
        this.sizeTypeCheckImg4 = (ImageView) findViewById(R.id.sizeTypeCheckImg4);
        this.sizeTypeNameTxt4 = (TextView) findViewById(R.id.sizeTypeNameTxt4);
        this.sizeTypeTab3 = (LinearLayout) findViewById(R.id.sizeTypeTab3);
        this.sizeTypeCheckImg3 = (ImageView) findViewById(R.id.sizeTypeCheckImg3);
        this.sizeTypeNameTxt3 = (TextView) findViewById(R.id.sizeTypeNameTxt3);
        this.sizeTypeTab2 = (LinearLayout) findViewById(R.id.sizeTypeTab2);
        this.sizeTypeCheckImg2 = (ImageView) findViewById(R.id.sizeTypeCheckImg2);
        this.sizeTypeNameTxt2 = (TextView) findViewById(R.id.sizeTypeNameTxt2);
        this.sizeTypeTab1 = (LinearLayout) findViewById(R.id.sizeTypeTab1);
        this.sizeTypeCheckImg1 = (ImageView) findViewById(R.id.sizeTypeCheckImg1);
        this.sizeTypeNameTxt1 = (TextView) findViewById(R.id.sizeTypeNameTxt1);

    }


}
