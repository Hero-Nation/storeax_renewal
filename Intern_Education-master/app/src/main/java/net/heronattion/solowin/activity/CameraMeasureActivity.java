package net.heronattion.solowin.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.camera.Show2Activity;
import net.heronattion.solowin.data.SizeTypeIDAndFlagData;
import net.heronattion.solowin.network.HttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static net.heronattion.solowin.activity.SignupInformation2Activity.name;
import static net.heronattion.solowin.activity.SignupInformation2Activity.strCategoryPkey;
import static net.heronattion.solowin.activity.SignupInformation3Activity.autoCheckFlag;
import static net.heronattion.solowin.activity.SignupInformation3Activity.sizeEdit;

/**
 * Created by hero on 2017-06-09.
 */


public class CameraMeasureActivity extends BaseActivity {

    private LinearLayout sizeTypeTab1;
    private LinearLayout sizeTypeTab2;
    private LinearLayout sizeTypeTab3;
    private LinearLayout sizeTypeTab4;
    private LinearLayout sizeTypeTab5;

    private TextView sizeTypeNameTxt1;
    private TextView sizeTypeNameTxt2;
    private TextView sizeTypeNameTxt3;
    private TextView sizeTypeNameTxt4;
    private TextView sizeTypeNameTxt5;

    private ImageView sizeTypeCheckImg1;
    private ImageView sizeTypeCheckImg2;
    private ImageView sizeTypeCheckImg3;
    private ImageView sizeTypeCheckImg4;
    private ImageView sizeTypeCheckImg5;

    private TextView selectedSizeTypeNameTxt;
    private TextView skipBtn;
    private TextView nextBtn;

    private LinearLayout[] sizeTypeTabArray;
    private TextView[] sizeTypeNameArray;
    private ImageView[] sizeTypeCheckArray;

    private RequestParams params;
    static public EditText detailSizeET;

    private String selectedID;

    String[][] sizeTypeAndSize;
    private TextView cautionTxt;

    int[] minimum_restrict;
    int[] maximum_restrict;
    String[] sizeTypeNameArr;

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


        minimum_restrict = new int[]{10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10};
        maximum_restrict = new int[]{150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150, 150};
        sizeTypeNameArr =
                new String[]{"어깨", "가슴", "소매", "상의총기장", "원피스총기장", "소매통", "허리", "허벅지", "밑위", "밑단", "하의총기장", "힙", "발길이", "발볼", "굽"};

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

                        SizeTypeIDAndFlagData tag = new SizeTypeIDAndFlagData();
                        tag.setSizeTypeID(sizeTypeID);

                        if (i == 0) {
                            tag.setFlag(0);
                            selectedID = sizeTypeID;
                        } else {
                            tag.setFlag(1);
                        }

                        sizeTypeNameArray[i].setText(sizeName);
                        sizeTypeTabArray[i].setTag(tag);
                        sizeTypeCheckArray[i].setVisibility(View.VISIBLE);

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
//                for (final LinearLayout ll : sizeTypeTabArray) {

                for(int k = 0 ; k < sizeTypeAndSize.length ; k++){
                    final LinearLayout ll = sizeTypeTabArray[k];
                    ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            detailSizeET.setTag(ll.getTag()); // detailSizeET에도 ll의 태그(sizeTypeID)를 똑같이 먹여준다.
                            // 탭 클릭할때마다 이전의 사이즈 배열에 저장
                            for (int j = 0; j < sizeTypeAndSize.length; j++) {
                                if (selectedID.equals(sizeTypeAndSize[j][0])) {
                                    sizeTypeAndSize[j][1] = detailSizeET.getText().toString();
                                    break;
                                }
                            }
                            SizeTypeIDAndFlagData tagdata = (SizeTypeIDAndFlagData) ll.getTag();
                            selectedID = tagdata.getSizeTypeID() + "";

                            for (int k = 0; k < sizeTypeAndSize.length; k++) {
                                if ((tagdata.getSizeTypeID() + "").equals(sizeTypeAndSize[k][0])) {
                                    detailSizeET.setText(sizeTypeAndSize[k][1]);
                                    break;
                                }
                            }


                            for (int j = 0; j < sizeTypeAndSize.length; j++) {

                                System.out.println("ID : " + sizeTypeAndSize[j][0] + " Size : " + sizeTypeAndSize[j][1]);

                            }


                            // 색 전부 바꾸자

                            for (int i = 0; i <  sizeTypeAndSize.length; i++) {
                                SizeTypeIDAndFlagData tagdata2 = (SizeTypeIDAndFlagData) sizeTypeTabArray[i].getTag();
                                sizeTypeTabArray[i].setBackgroundColor(Color.WHITE);
                                sizeTypeNameArray[i].setTextColor(Color.rgb(137, 137, 137));
                                sizeTypeCheckArray[i].setImageResource(R.drawable.check_grey);
                                if (tagdata2.getFlag() == 2) { // 트루 상태
                                    sizeTypeCheckArray[i].setImageResource(0);
                                    sizeTypeCheckArray[i].setImageResource(R.drawable.check_green);
                                } else if (tagdata2.getFlag() == 0) {
                                    sizeTypeCheckArray[i].setImageResource(0);
                                    sizeTypeCheckArray[i].setImageResource(R.drawable.check_red);
                                } else {
                                    sizeTypeCheckArray[i].setImageResource(0);
                                    sizeTypeCheckArray[i].setImageResource(R.drawable.check_grey);
                                }

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

        detailSizeET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 입력하기 전에
                limitConditionFunc();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 입력되는 텍스트 변화있을때
                limitConditionFunc();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝났을떄
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean totalFlag = false;


                int finalSelectedIDlocation = 0;

                for (int i = 0; i < sizeTypeAndSize.length; i++) {
                    SizeTypeIDAndFlagData tagdata = (SizeTypeIDAndFlagData) sizeTypeTabArray[i].getTag();
                    if ((tagdata.getSizeTypeID() + "").equals(selectedID)) {
                        finalSelectedIDlocation = i;
                    }
                }
                sizeTypeAndSize[finalSelectedIDlocation][1] = detailSizeET.getText().toString();


                //최종 플래그 검사
                for (int i = 0; i < sizeTypeAndSize.length; i++) {
                    SizeTypeIDAndFlagData tagdata = (SizeTypeIDAndFlagData) sizeTypeTabArray[i].getTag();
                    Log.i("flag 검사 결과 " + i, tagdata.getFlag() + "");
                    if (tagdata.getFlag() == 1 || tagdata.getFlag() == 2) { // 유효성 ok
                        totalFlag = true;
                    } else {
                        totalFlag = false;
                        break;
                    }
                }

                RequestParams params = new RequestParams();
                params.put("UserPKey", "2087");
                params.put("CategoryID", strCategoryPkey);
//                params.put("Name",name);
                params.put("Name", name);
                params.put("SizetypeAndSize", sizeTypeAndSize);
                Log.i("totalFlag", totalFlag + "");
                if (totalFlag) {

                    HttpClient.post("/sizeax/CHS/php/insertusersize.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            System.out.println("Result >>>>>> " + new String(responseBody));

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
                            System.out.println("Result >>>>>> ERROR");
                            Toast.makeText(mContext, "죄송합니다. 네트워크 상의 에러가 있습니다. "  , Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
                }


            }
        });


        skipBtn.setOnClickListener(new View.OnClickListener() { // 취소 버튼
            @Override
            public void onClick(View v) {
                // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.signup_dialog, null);
                TextView customTitle = (TextView) view.findViewById(R.id.customtitle);
                ImageView custonImage = (ImageView)view.findViewById(R.id.customdialogicon);
                custonImage.setVisibility(View.GONE);
                customTitle.setText("입력하신 내용이 저장되지 않습니다. \n 계속하시겠습니까?");
                customTitle.setTextColor(Color.BLACK);
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setView(view);
                builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                finish();
                        CameraMeasureActivity.super.onBackPressed();

                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });
    }


    public void limitConditionFunc() {
        SizeTypeIDAndFlagData tagdata_et = (SizeTypeIDAndFlagData) (detailSizeET.getTag());
        int sizetypeid = tagdata_et.getSizeTypeID();
        float size = 0f;
        if (!detailSizeET.getText().toString().equals("")) {
            size = Float.parseFloat(detailSizeET.getText().toString());
        }


        String alertText = sizeTypeNameArr[sizetypeid - 1] + "크기는 최소 " + minimum_restrict[sizetypeid - 1] + "cm에서 " + maximum_restrict[sizetypeid - 1] + "cm로 기입하셔야합니다.";

        if (sizetypeid == 1 || sizetypeid == 7) { // 필수입력값
            if (detailSizeET.getText().length() == 0) { // 공백
                tagdata_et.setFlag(0);
                cautionTxt.setVisibility(View.VISIBLE);
                cautionTxt.setText(sizeTypeNameArr[sizetypeid - 1] + "크기는 반드시 입력하셔야합니다.");
            } else if (size < minimum_restrict[sizetypeid - 1]) {
                tagdata_et.setFlag(0);
                cautionTxt.setVisibility(View.VISIBLE);
                cautionTxt.setText(alertText);
            } else if (size > maximum_restrict[sizetypeid - 1]) {
                tagdata_et.setFlag(0);
                cautionTxt.setVisibility(View.VISIBLE);
                cautionTxt.setText(alertText);
            } else {
                tagdata_et.setFlag(2);
                cautionTxt.setVisibility(View.INVISIBLE);
            }
        } else {
            if (detailSizeET.getText().length() == 0) {
                tagdata_et.setFlag(1);
                cautionTxt.setVisibility(View.INVISIBLE);
            } else if (size < minimum_restrict[sizetypeid - 1]) {
                tagdata_et.setFlag(0);
                cautionTxt.setVisibility(View.VISIBLE);
                cautionTxt.setText(alertText);
            } else if (size > maximum_restrict[sizetypeid - 1]) {
                tagdata_et.setFlag(0);
                cautionTxt.setVisibility(View.VISIBLE);
                cautionTxt.setText(alertText);
            } else {
                tagdata_et.setFlag(2);
                cautionTxt.setVisibility(View.INVISIBLE);
            }
        }
        for (int i = 0; i < sizeTypeAndSize.length; i++) {
            SizeTypeIDAndFlagData tagdata_tab = (SizeTypeIDAndFlagData) sizeTypeTabArray[i].getTag();
            if (tagdata_tab.getSizeTypeID() == sizetypeid) {
                sizeTypeTabArray[i].setTag(tagdata_tab);
                break;
            }
        }

    }

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("카메라 측정");
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.signup_dialog, null);
        TextView customTitle = (TextView) view.findViewById(R.id.customtitle);
        ImageView custonImage = (ImageView)view.findViewById(R.id.customdialogicon);
        custonImage.setVisibility(View.GONE);
        customTitle.setText("입력하신 내용이 저장되지 않습니다. \n 계속하시겠습니까?");
        customTitle.setTextColor(Color.BLACK);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
                CameraMeasureActivity.super.onBackPressed();

            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

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
        this.cautionTxt = (TextView) findViewById(R.id.cautionTxt);

    }


}