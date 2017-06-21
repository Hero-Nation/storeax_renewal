package net.heronattion.solowin.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by hero on 2017-06-08.
 */

public class SignupInformationActivity extends BaseActivity {


    private android.widget.Spinner spinner1;
    private TextView nextButton;
    private android.widget.LinearLayout linSignupStyle;

    private String userPkey;
    private int manFlag;
    private int womanFlag;

    private String[] mOptionListItem;
    private String[][] mStyleItem;
    private String[][] mBrandItem;
    private String Birthday;

    private CheckBox[] chStyleArray, chBrandArray;

    private String strStylePkey, strBrandPkey;
    public static String[] sStyleList;

    private String[] selStylePkey, selBrandPkey;
    private android.widget.ScrollView scSignupAdd2;
    private Spinner Year;
    private Spinner Month;
    private Spinner Day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupinformation);



//        manFlag = getIntent().getIntExtra("manFlag", 0);
//        womanFlag = getIntent().getIntExtra("womanFlag", 0);
        manFlag = 1;
        womanFlag = 0;

        strStylePkey = "";
        selStylePkey = new String[]{};
        sStyleList = new String[]{};

        setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("추가정보 입력");

        bindViews();
        setupEvents();

    }

    @Override
    public void setupEvents() {
        final AsyncHttpClient client = HttpClient.getInstance();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);

        for(int i=0; i < myCookieStore.getCookies().size(); i++) {
            if(myCookieStore.getCookies().get(i).getName().equals("UserPKEY"))
                userPkey = myCookieStore.getCookies().get(i).getValue();
        }
        log.i("PKey: ", userPkey);
        parsingOptions();


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFilter();
                RequestParams params = new RequestParams();

                // 생년 월일 put, 스타일 put 해야함
                Birthday = Year.getSelectedItem().toString() + "-" + Month.getSelectedItem().toString() + "-" + Day.getSelectedItem().toString();
                params.put("date_string", Birthday);
                params.put("style", selStylePkey);
                for(int i = 0; i < selStylePkey.length; i++){
                    selStylePkey[i].replace("null", "");
                    Log.d("name1",selStylePkey[i]);
                }
                params.put("userID", userPkey);

                Toast.makeText(SignupInformationActivity.this, "3", Toast.LENGTH_SHORT).show();
                HttpClient.post("android/signupAdd.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        //TODO : 통신에 성공했을 때 이벤트를 적어주면 됨
                        String response = new String(responseBody);
                        switch(response) {
                            case "success" :
                                Toast.makeText(SignupInformationActivity.this, "정보가 저장되었습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(mContext, SignupInformation2Activity.class);
                                intent.putExtra("manFlag", manFlag);
                                intent.putExtra("womanFlag", womanFlag);
                                startActivity(intent);
                                finish();
                                break;
                            case "server_connect_fail" :
                                Toast.makeText(SignupInformationActivity.this, "서버 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(SignupInformationActivity.this, "서버 환경이 불안정합니다.", Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(SignupInformationActivity.this, "서버 환경이 불안정합니다.", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

    @Override
    public void bindViews() {
        this.nextButton = (TextView) findViewById(R.id.nextButton);
        this.scSignupAdd2 = (ScrollView) findViewById(R.id.scSignupAdd2);
        this.linSignupStyle = (LinearLayout) findViewById(R.id.linSignupStyle);
        this.Day = (Spinner) findViewById(R.id.Day);
        this.Month = (Spinner) findViewById(R.id.Month);
        this.Year = (Spinner) findViewById(R.id.Year);


    }

    private void parsingOptions() {

        //productList.php로 보낼 post 변수 설정, 스크롤 플래그가 필요함
        RequestParams params = new RequestParams();
        params.put("lang", "kor");
        params.put("default", 0);

        HttpClient.post("android/getFilter.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //TODO : 통신에 성공했을 경우

//              POST 방식으로 데이터를 보내고 PHP로부터 받아온 데이터
                String response = new String(responseBody);
                System.out.println("response ==>>>>>>>>>>>>>>" + response);

                ParseData parse = new ParseData();

                // 스타일, 브랜드, 컬러, 카테고리 순으로 배열 요소를 나누어줌
                mOptionListItem = parse.getArrayData(response);

                mStyleItem = parse.getDoubleArrayData(mOptionListItem[0]);

                //동적할당을 위한 체크박스 배열에 생성할 갯수만큼 초기화
                chStyleArray = new CheckBox[mStyleItem.length];

                //체크박스 디자인 및 동적할당 => 레이아웃에 집어넣음
                createDynamicCheckBox(chStyleArray, linSignupStyle, mStyleItem);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO : 통신에 실패했을 경우
                Toast.makeText(mContext, "서버 연결에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void createDynamicCheckBox(final CheckBox[] checkBoxes, LinearLayout linLayout, String[][] itemArray) {

        // 버튼 디자인위한 Param 선언
        Log.i("START.................", "  ");
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        param.rightMargin = Math.round(5 * dm.density);

        param.width = LinearLayout.LayoutParams.MATCH_PARENT;
        param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        param.weight = 1;

        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox(this);

            checkBoxes[i].setText(itemArray[i][1]);
            // 버튼 디자인 부분 //
            checkBoxes[i].setTextSize(13);
            checkBoxes[i].setId(Integer.parseInt(itemArray[i][0].toString()));
            checkBoxes[i].setBackgroundResource(R.drawable.signup_border);
            checkBoxes[i].setButtonDrawable(null);
//            checkBoxes[i].setHeight(120);
            checkBoxes[i].setGravity(Gravity.CENTER);
            checkBoxes[i].setLayoutParams(param);
            checkBoxes[i].setTextColor(Color.parseColor("#5b6fa2"));
//            checkBoxes[i].setTypeface(null, Typeface.BOLD);

            // 스타일 클릭 시 이벤트
            checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buttonView.setBackgroundResource(R.drawable.signup_border_check);
                        buttonView.setTextColor(Color.parseColor("#000000"));
                        strStylePkey += buttonView.getId() + "/";
                    }
                    else {
                        buttonView.setBackgroundResource(R.drawable.signup_border);
                        buttonView.setTextColor(Color.parseColor("#5b6fa2"));
                        String temp = strStylePkey;
                        temp = temp.replace(buttonView.getId() + "/", "");
                        strStylePkey = temp;
                    }
                }
            });
        }

        // 레이아웃에 추가
        linLayout.removeAllViews();
        int count = 0;
        while (count < checkBoxes.length) {
            LinearLayout linOptionLayout = new LinearLayout(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            p.setMargins(0, 25, 0, 0);
            linOptionLayout.setLayoutParams(p);
            linOptionLayout.setOrientation(LinearLayout.HORIZONTAL);


            for (int j = 0; j < 3; j++) {
                if (count == checkBoxes.length) {
                    break;
                } // 4개보다 안되는 경우
                else {
                    linOptionLayout.addView(checkBoxes[count]);
                    count++;
                }
            }
            linLayout.addView(linOptionLayout);

        }
    }

    private void setFilter() {
        if (!strStylePkey.equals("")) {
            selStylePkey = strStylePkey.split("/");
            sStyleList = selStylePkey;
        }
    }
}
