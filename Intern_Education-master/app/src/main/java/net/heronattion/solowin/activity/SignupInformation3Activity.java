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
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.Data.DotData;
import net.heronattion.solowin.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static net.heronattion.solowin.activity.SignupInformation2Activity.name;
import static net.heronattion.solowin.activity.SignupInformation2Activity.strCategoryPkey;

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
    public static ArrayList<DotData> savedCardDotList;

    //수동측정인지, 자동측정인지, 클릭을 안했는지 확인하는 플래그 변수
    // 0이면 클릭 X, 1이면 수동측정, 2이면 자동측정
    public static int autoCheckFlag;

    RequestParams params;
    int necessaryPartID;
    String necessaryPartName;
    int jsonLength;
    private TextView skipSignup3;

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

        params = new RequestParams();
        params.put("SizeTypeID", strCategoryPkey);
    }

    @Override
    public void setupEvents() {

        // 필수 입력값(어깨, 허리) 구분위한 AsyncTask
        HttpClient.post("android/CHS/getSizeTypeName.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);

                System.out.println(result);
                String necessaryPart = "";

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");

                    jsonLength = jsonArray.length();

                    JSONObject item = jsonArray.getJSONObject(0);
                    necessaryPart = item.getString("SizeTypeID");
                    necessaryPartName = item.getString("SizeName");
                    necessaryPartID = Integer.parseInt(necessaryPart);

                    Log.d("necessaryPartName ", necessaryPartName );
                    Log.d("necessaryPartID ", necessaryPart);
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

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

        nextButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sizeEdit.getText().length()!= 0){

                    String[][] necessarySizeTypeAndSize = new String[jsonLength][2];

                    for (int i = 0; i < necessarySizeTypeAndSize.length; i++) {
                        if (i == 0) {
                            necessarySizeTypeAndSize[0][0] = necessaryPartID + "";
                            necessarySizeTypeAndSize[0][1] = sizeEdit.getText().toString();
                        } else {
                            necessarySizeTypeAndSize[i][0] = "0";
                            necessarySizeTypeAndSize[i][1] = "";
                        }
                    }

                    RequestParams params1 = new RequestParams();
                    params1.put("UserPKey", "2087");
                    params1.put("CategoryID", strCategoryPkey);
                    params1.put("Name", name);
                    params1.put("SizetypeAndSize", necessarySizeTypeAndSize);

                    HttpClient.post("/sizeax/CHS/php/insertusersize.php", params1, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String s = new String(responseBody);
//                            Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
                            if(new String(responseBody).contains("success")){
                                Toast.makeText(mContext, "성공적으로 저장되었습니다." , Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, FragmentActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(mContext, "저장도중 문제가 발생하였습니다." , Toast.LENGTH_SHORT).show();
                            }



                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(SignupInformation3Activity.this, "NETWORK_ERROR", Toast.LENGTH_SHORT).show();
                        }
                    });
                    // Intent 추가 삽입 필요

                }else{
                    Toast.makeText(SignupInformation3Activity.this, necessaryPartName + "길이는 반드시 입력하셔야합니다.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        skipSignup3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FragmentActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void bindViews() {
        this.nextButton3 = (TextView) findViewById(R.id.nextButton3);
        this.skipSignup3 = (TextView) findViewById(R.id.skipSignup3);
        this.addSize = (TextView) findViewById(R.id.addSize);
        this.sizeEdit = (EditText) findViewById(R.id.sizeEdit);
    }


}
