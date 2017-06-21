package net.heronattion.solowin.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.bubbleseekbar.BubbleSeekBar;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MyPageActivity extends BaseActivity {

    private android.widget.TextView mypageWomanButton;
    private android.widget.TextView mypageManButton;
    private android.widget.Spinner mypageYear;
    private android.widget.Spinner mypageMonth;
    private android.widget.Spinner mypageDay;
    private android.widget.TextView nextButton2;
    private android.widget.LinearLayout mypageStyleLinear;
    private BubbleSeekBar bubbleSeekBar1;

    private String seekValue = "";

    private int manFlag = 0;
    private int womanFlag = 0;

    private String[] mOptionListItem;
    private String[][] mStyleItem;
    private CheckBox[] chStyleArray;
    private String strStylePkey;
    private android.widget.ScrollView scSignupAdd2;
    private BubbleSeekBar demo3seekbar1;
    private TextView initialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        bubbleSeekBar1 = (BubbleSeekBar) findViewById(R.id.demo_3_seek_bar_1);


        manFlag = 0;
        womanFlag = 0;

        strStylePkey = "";

        bindViews();
        setupEvents();

    }

    @Override
    public void bindViews() {
        this.nextButton2 = (TextView) findViewById(R.id.nextButton2);
        this.scSignupAdd2 = (ScrollView) findViewById(R.id.scSignupAdd2);
        this.mypageStyleLinear = (LinearLayout) findViewById(R.id.mypageStyleLinear);
        this.mypageDay = (Spinner) findViewById(R.id.mypageDay);
        this.mypageMonth = (Spinner) findViewById(R.id.mypageMonth);
        this.mypageYear = (Spinner) findViewById(R.id.mypageYear);
        this.mypageManButton = (TextView) findViewById(R.id.mypageManButton);
        this.mypageWomanButton = (TextView) findViewById(R.id.mypageWomanButton);
        this.initialButton = (TextView) findViewById(R.id.initialButton);
    }

    @Override
    public void setupEvents() {

        parsingOptions();

        bubbleSeekBar1.getConfigBuilder()
                .min(1)
                .max(5)
                .progress(3)
                .sectionCount(4)
                .showSectionText()
                .sectionTextSize(15)
                .showThumbText()
                .thumbTextSize(20)
                .bubbleTextSize(15)
                .showSectionMark()
                .seekBySection()
                .autoAdjustSectionMark()
                .trackColor(ContextCompat.getColor(this, R.color.colorDot))
                .secondTrackColor(ContextCompat.getColor(this, R.color.colorBackground))
                //.thumbTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                //.sectionTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
                //.bubbleColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .sectionTextPosition(BubbleSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();

        bubbleSeekBar1.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
                        @Override
            public void getProgressOnFinally(int progress, float progressFloat) {
                seekValue = String.valueOf(progress);
            }
        });

        initialButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                log.i("d", seekValue);
            }
        });

        mypageManButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(womanFlag == 0) {
                    if(manFlag == 0){
                        mypageManButton.setBackgroundResource(R.drawable.sex_button_click);
                        mypageManButton.setTextColor(Color.parseColor("#7623D7"));
                        manFlag = 1;
                    } else {
                        mypageManButton.setBackgroundResource(R.drawable.sex_button);
                        mypageManButton.setTextColor(Color.parseColor("#999999"));
                        manFlag = 0;
                    }
                }
                // womanFlag == 1 일때 여성 클릭 해제하고 남성클릭
                else if (womanFlag==1 && manFlag==0){
                    mypageManButton.setBackgroundResource(R.drawable.sex_button_click);
                    mypageManButton.setTextColor(Color.parseColor("#7623D7"));
                    manFlag = 1;

                    mypageWomanButton.setBackgroundResource(R.drawable.sex_button);
                    mypageWomanButton.setTextColor(Color.parseColor("#999999"));
                    womanFlag = 0;
                }
            }
        });

        mypageWomanButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(manFlag == 0){
                    if(womanFlag == 0){
                        mypageWomanButton.setBackgroundResource(R.drawable.sex_button_click);
                        mypageWomanButton.setTextColor(Color.parseColor("#7623D7"));
                        womanFlag = 1;
                    } else {
                        mypageWomanButton.setBackgroundResource(R.drawable.sex_button);
                        mypageWomanButton.setTextColor(Color.parseColor("#999999"));
                        womanFlag = 0;
                    }
                }
                else if (manFlag==1 && womanFlag==0){
                    mypageWomanButton.setBackgroundResource(R.drawable.sex_button_click);
                    mypageWomanButton.setTextColor(Color.parseColor("#7623D7"));
                    womanFlag = 1;

                    mypageManButton.setBackgroundResource(R.drawable.sex_button);
                    mypageManButton.setTextColor(Color.parseColor("#999999"));
                    manFlag = 0;
                }
            }
        });

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
                createDynamicCheckBox(chStyleArray, mypageStyleLinear, mStyleItem);
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
            if(itemArray[i][2].equals("0")){
                checkBoxes[i].setBackgroundResource(R.drawable.signup_border);
                checkBoxes[i].setTextColor(Color.parseColor("#999999"));
            }
            else {
                checkBoxes[i].setBackgroundResource(R.drawable.signup_border_check);
                checkBoxes[i].setTextColor(Color.parseColor("#7623D7"));
                strStylePkey += itemArray[i][1] + "/";
            }
            checkBoxes[i].setGravity(Gravity.CENTER);
            checkBoxes[i].setButtonDrawable(null);
            checkBoxes[i].setLayoutParams(param);
//            checkBoxes[i].setTypeface(null, Typeface.BOLD);

            // 스타일 클릭 시 이벤트
            checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        buttonView.setBackgroundResource(R.drawable.signup_border_check);
                        buttonView.setTextColor(Color.parseColor("#7623D7"));
                        strStylePkey += buttonView.getId() + "/";
                    }
                    else {
                        buttonView.setBackgroundResource(R.drawable.signup_border);
                        buttonView.setTextColor(Color.parseColor("#999999"));
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
            p.setMargins(0, 0, 0, 25);
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
}
