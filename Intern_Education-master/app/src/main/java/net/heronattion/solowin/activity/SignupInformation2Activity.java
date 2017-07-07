package net.heronattion.solowin.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hero on 2017-06-08.
 */

public class SignupInformation2Activity extends BaseActivity {

    private android.widget.EditText textView2;
    private android.widget.TextView nextButton2;
    private LinearLayout linCategory;
    private android.widget.ScrollView scCategory;
    private android.widget.LinearLayout signupInformation2;

    //브랜드, 카테고리, 색상, 스타일 데이터를 파싱하기 위한 변수
    private String[] mOptionListItem, sCategoryList, selCategoryPkey;

    private String[][] mCategoryItem;

    //파싱된 데이터를 활용하여 버튼 동적할당
    private CheckBox[] chCategoryArray;

    static public String strCategoryPkey;
    static public String name;

    private int categoryCount = 0;
    private int categoryFlag = 0;
    private int manFlag;
    private int womanFlag;
//    private int checkboxFlag = 0;
    private int i;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupinformation2);

        setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("추가정보 입력");

        manFlag = getIntent().getIntExtra("manFlag", 0);
        womanFlag = getIntent().getIntExtra("womanFlag", 0);
        sCategoryList = new String[]{};

        bindViews();
        setupEvents();
    }


    @Override
    public void setupEvents() {
        parsingOptions();
        signupInformation2.setFocusableInTouchMode(true);
        signupInformation2.setFocusableInTouchMode(true);
        signupInformation2.setFocusable(true);

        nextButton2.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (textView2.getText().length() == 0) {
//                    Snackbar.make(getWindow().getDecorView().getRootView(), "이름을 입력해주세요", Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                    Toast.makeText(mContext, "이름을 입력해주세요", Toast.LENGTH_SHORT).show();
                } else {

                    Intent intent = new Intent(mContext, SignupInformation3Activity.class);
                    name = textView2.getText().toString();
                    intent.putExtra("PKey", strCategoryPkey);
                    intent.putExtra("Name", name);

                    Log.i("Category Pkey: ", strCategoryPkey);
                    Log.i("textView : ", textView2.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void bindViews() {
        this.nextButton2 = (TextView) findViewById(R.id.nextButton2);
        this.textView2 = (EditText) findViewById(R.id.textView2);
        this.scCategory = (ScrollView) findViewById(R.id.scCategory);
        this.linCategory = (LinearLayout) findViewById(R.id.linCategory);
        this.signupInformation2 = (LinearLayout) findViewById(R.id.signupInformation2);
    }

    private void parsingOptions() {

        //productList.php로 보낼 post 변수 설정, 스크롤 플래그가 필요함
        RequestParams params = new RequestParams();
        params.put("lang", "kor");
        params.put("default", 0);

        //앱에서는 tryFilter 만 사용함. 값이 없으면 ProductList와 똑같기 때문에.
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

                /*
                파싱 완료.

                스 타 일 요소 : Pkey, Name, isSelect
                브 랜 드 요소 : Pkey, Name, isSelect
                컬    러 요소 : Pkey, RGBCode, isSelect
                카테고리 요소 : Pkey, Name_Lang, ParentID
                 */


                mCategoryItem = parse.getDoubleArrayData(mOptionListItem[3]);

                //동적할당을 위한 체크박스 배열에 생성할 갯수만큼 초기화

                chCategoryArray = new CheckBox[mCategoryItem.length];

                //체크박스 디자인 및 동적할당 => 레이아웃에 집어넣음
                createDynamicCheckBox(chCategoryArray, linCategory, mCategoryItem);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO : 통신에 실패했을 경우

                Toast.makeText(mContext, "죄송합니다. 서버와 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createDynamicCheckBox(final CheckBox[] checkBoxes, LinearLayout linLayout, String[][] itemArray) {

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

        for (i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox(this);
            manFlag = 0;
            womanFlag = 1;
            if (manFlag == 1 && itemArray[i][2].toString().equals("1")) {
                categoryFlag = 1;
                categoryCount++;

                checkBoxes[i].setText(itemArray[i][1].toString());
//                checkBoxes[i].setText(itemArray[i][1].toString() + "(여)");

                /*
                버튼 세팅
                 */
                checkBoxes[i].setTextSize(13);
                checkBoxes[i].setId(Integer.parseInt(itemArray[i][0]));
                checkBoxes[i].setBackgroundResource(R.drawable.signup_border);
                checkBoxes[i].setButtonDrawable(null);
                checkBoxes[i].setGravity(Gravity.CENTER);
                checkBoxes[i].setLayoutParams(param);
                checkBoxes[i].setTextColor(Color.parseColor("#999999"));
                checkBoxes[i].setTag(0); // 0이면 선택 안됨, 1이면 선택됨
//                checkBoxes[i].setHeight(120);


                checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && (int)buttonView.getTag() == 0) {
                            for (int j = 0; j < checkBoxes.length; j++) {
                                checkBoxes[j].setTag(0);
                                checkBoxes[j].setBackgroundResource(R.drawable.signup_border);
                                checkBoxes[j].setTextColor(Color.parseColor("#999999"));

                            }
                            buttonView.setTag(1);
                            buttonView.setBackgroundResource(R.drawable.signup_border_check);
                            buttonView.setTextColor(Color.parseColor("#7623D7"));
                            strCategoryPkey = buttonView.getId() + "";
                        } else {
                            buttonView.setTag(0);
                            buttonView.setBackgroundResource(R.drawable.signup_border);
                            buttonView.setTextColor(Color.parseColor("#999999"));

//                            String temp = strCategoryPkey;
//                            temp = temp.replace(buttonView.getId() + "/", "");
//                            strCategoryPkey = temp;
                        }
                    }
                });
            } else if (womanFlag == 1 && itemArray[i][2].toString().equals("2")) {
                categoryFlag = 2;
                categoryCount++;

                checkBoxes[i].setText(itemArray[i][1].toString());
//                checkBoxes[i].setText(itemArray[i][1].toString() + "(여)");

                /*
                버튼 세팅
                 */
                checkBoxes[i].setTextSize(15);
                checkBoxes[i].setId(Integer.parseInt(itemArray[i][0]));
                checkBoxes[i].setBackgroundResource(R.drawable.signup_border);
                checkBoxes[i].setButtonDrawable(null);
                checkBoxes[i].setGravity(Gravity.CENTER);
                checkBoxes[i].setLayoutParams(param);
                checkBoxes[i].setTextColor(Color.parseColor("#999999"));
                checkBoxes[i].setHeight(120);
                checkBoxes[i].setTag(0);

                checkBoxes[i].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked && (int)buttonView.getTag() == 0) {
                            for (int j = 0; j < checkBoxes.length; j++) {
                                checkBoxes[j].setTag(0);
                                checkBoxes[j].setBackgroundResource(R.drawable.signup_border);
                                checkBoxes[j].setTextColor(Color.parseColor("#999999"));

                            }
                            buttonView.setTag(1);
                            buttonView.setBackgroundResource(R.drawable.signup_border_check);
                            buttonView.setTextColor(Color.parseColor("#7623D7"));
                            strCategoryPkey = buttonView.getId() + "";
                        } else {
                            buttonView.setTag(0);
                            buttonView.setBackgroundResource(R.drawable.signup_border);
                            buttonView.setTextColor(Color.parseColor("#999999"));
                        }
                    }
                });
            }
        }

        // 레이아웃에 추가
        linLayout.removeAllViews();
        int count = 0;


        if (categoryFlag == 1) {
            while (count < 6) {
                LinearLayout linOptionLayout = new LinearLayout(this);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(0, 25, 0, 0);
                linOptionLayout.setLayoutParams(p);
                linOptionLayout.setOrientation(LinearLayout.HORIZONTAL);
                // 4개씩만 출력하기 위해 리니어 레이아웃에 버튼이 4개 들어가면 새로운 레이아웃 생성 후 삽입
                // 한국어일때 4개, 나머지는 3개씩 출력

                // 그와중에 카테고리이면 3개씩 출력
                if (checkBoxes == chCategoryArray) {
                    for (int j = 0; j < 3; j++) {
                        if (count == categoryCount) {
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
        } else if (categoryFlag == 2) {
            count = 6;
            while (count < 13) {
                LinearLayout linOptionLayout = new LinearLayout(this);
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                p.setMargins(0, 25, 0, 0);
                linOptionLayout.setLayoutParams(p);
                linOptionLayout.setOrientation(LinearLayout.HORIZONTAL);
                // 4개씩만 출력하기 위해 리니어 레이아웃에 버튼이 4개 들어가면 새로운 레이아웃 생성 후 삽입
                // 한국어일때 4개, 나머지는 3개씩 출력

                // 그와중에 카테고리이면 3개씩 출력
                if (checkBoxes == chCategoryArray) {
                    for (int j = 0; j < 3; j++) {
                        if (count == categoryCount + 6) {
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
    }

    private void setFilter() {
        if (!strCategoryPkey.equals("")) {
            selCategoryPkey = strCategoryPkey.split("/");
            sCategoryList = selCategoryPkey;
        }
    }
}
