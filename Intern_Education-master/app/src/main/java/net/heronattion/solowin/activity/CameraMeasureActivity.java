package net.heronattion.solowin.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by hero on 2017-06-09.
 */


public class CameraMeasureActivity extends BaseActivity {
    private String CategoryPKey, CategoryName;
    private String[] mOptionListItem;
    private String[][] sizeTypeName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_measure);

        setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("카메라 측정");

        CategoryPKey = "4";
        CategoryName ="";

//        CategoryPKey = getIntent().getExtras().getString("PKey");
//        CategoryName = getIntent().getExtras().getString("Name");


        bindViews();
        setupEvents();
    }


    @Override
    public void bindViews() {

    }

    @Override
    public void setupEvents() {
        parsingOptions();

    }

    private void parsingOptions() {

        //productList.php로 보낼 post 변수 설정, 스크롤 플래그가 필요함
        RequestParams params = new RequestParams();
        params.put("CategoryPKey", CategoryPKey);

        //앱에서는 tryFilter 만 사용함. 값이 없으면 ProductList와 똑같기 때문에.
        HttpClient.post("android/cameraMeasure.php", params, new AsyncHttpResponseHandler() {
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
//
                sizeTypeName = parse.getDoubleArrayData(mOptionListItem[0]);

                for(int i=0; i<sizeTypeName.length; i++){
                    log.i("이름1:", sizeTypeName[i][0]);
                }
//                //동적할당을 위한 체크박스 배열에 생성할 갯수만큼 초기화
//                chStyleArray = new CheckBox[mStyleItem.length];
//                chBrandArray = new CheckBox[mBrandItem.length];
//                chColorArray = new CheckBox[mColorItem.length];
//                chCategoryArray = new CheckBox[mCategoryItem.length];
//
//                //체크박스 디자인 및 동적할당 => 레이아웃에 집어넣음
//                createDynamicCheckBox(chCategoryArray, linCategory, mCategoryItem);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO : 통신에 실패했을 경우

                Toast.makeText(mContext, "죄송합니다. 서버와 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
