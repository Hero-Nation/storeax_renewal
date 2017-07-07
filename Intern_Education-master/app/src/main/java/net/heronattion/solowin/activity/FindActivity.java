package net.heronattion.solowin.activity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;

import cz.msebera.android.httpclient.Header;

/**
 * Created by hero on 2017-06-08.
 */

public class FindActivity extends BaseActivity{

    TextView cancelButton;
    private android.widget.EditText findID;
    private Button findButton;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        setCustomActionBar();

        bindViews();
        setCustomActionBar();
        backButton = (ImageView) findViewById(R.id.backButton);
        backButton.setImageResource(R.drawable.backbutton);
        setupEvents();


    }

    @Override
    public void setupEvents() {
        super.setupEvents();

        // 로그인 버튼 클릭시
        findButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                findButtonEvent();
            }
        });

        findID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    findID.setBackgroundResource(R.drawable.inputtext_focus);
                else
                    findID.setBackgroundResource(R.drawable.inputtext);
            }
        });

        backButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(mContext, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void bindViews() {
        super.bindViews();

        this.findButton = (Button) findViewById(R.id.findButton);
        this.findID = (EditText) findViewById(R.id.findID);
    }


    private void findButtonEvent() {
        //포스트로 넘겨줄 값을 지정해줌
        RequestParams params = new RequestParams();

        //왼쪽 인자는 PHP POST 키값을 나타내고 오른쪽 인자는 보낼 값을 나타냄
        params.put("checkEmail", findID.getText().toString());

        //HttpClient 클래스에 기본 URL이 정해져 있음 http://heronation.net/ 이하의 경로를 적어주면 됨
        HttpClient.post("sizeax/php/sendCheckEmail.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //TODO : 통신에 성공했을 때 이벤트를 적어주면 됨
                String response = new String(responseBody);

                switch(response) {
                    case "input_empty":
                        Toast.makeText(FindActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        break;

                    case "success":
                        Toast.makeText(FindActivity.this, "임시 비밀번호가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "notselect" :
                        Toast.makeText(FindActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "server_connect_fail":
                        Toast.makeText(FindActivity.this, "서버 연결에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    default :
                        Toast.makeText(FindActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO : 통신에 실패했을 때 이벤트를 적어주면 됨
                //서버와의 통신이 원할하지 않습니다.
                Toast.makeText(FindActivity.this, "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
