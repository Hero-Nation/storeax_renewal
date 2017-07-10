package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class LoginActivity extends BaseActivity {

    private TextView findButton;
    private Button loginButton;
//    private ImageView backButton;
    private android.widget.EditText loginID;
    private android.widget.EditText loginPW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.loginButton = (Button) findViewById(R.id.loginButton);
        this.loginPW = (EditText) findViewById(R.id.loginPW);
        this.loginID = (EditText) findViewById(R.id.loginID);
        this.findButton = (TextView) findViewById(R.id.findButton);


        bindViews();
        setCustomActionBar();


        setupEvents();
    }

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        titleTxt.setVisibility(View.VISIBLE);
        logo.setVisibility(View.INVISIBLE);
        backBtn.setImageResource(R.drawable.backbutton);
    }

    @Override
    public void setupEvents() {
        super.setupEvents();

        // 로그인 버튼 클릭시
        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginButtonEvent();
            }
        });
        // 비밀번호 찾기 버튼 클릭 시
        findButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, FindActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
             @Override
             public void onFocusChange(View v, boolean hasFocus) {
                 if(hasFocus)
                     loginID.setBackgroundResource(R.drawable.inputtext_focus);
                 else
                     loginID.setBackgroundResource(R.drawable.inputtext);
             }
        });

        loginPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    loginPW.setBackgroundResource(R.drawable.inputtext_focus);
                else
                    loginPW.setBackgroundResource(R.drawable.inputtext);
            }
        });

        backBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SignSelectionActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginID.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginID.setBackgroundResource(R.drawable.inputtext_focus);
            }
        });
    }

    @Override
    public void bindViews() {
        super.bindViews();

        this.loginPW = (EditText) findViewById(R.id.loginPW);
        this.loginID = (EditText) findViewById(R.id.loginID);
        this.findButton = (TextView) findViewById(R.id.findButton);
        this.loginButton = (Button) findViewById(R.id.loginButton);

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(mContext, SignSelectionActivity.class);
        startActivity(intent);
        finish();
    }

    private void loginButtonEvent() {
        //포스트로 넘겨줄 값을 지정해줌
        RequestParams params = new RequestParams();

        //왼쪽 인자는 PHP POST 키값을 나타내고 오른쪽 인자는 보낼 값을 나타냄
        params.put("userId", loginID.getText().toString());
        params.put("userPassword", loginPW.getText().toString());

        //HttpClient 클래스에 기본 URL이 정해져 있음 http://heronation.net/ 이하의 경로를 적어주면 됨
        HttpClient.post("sizeax/php/tryLogin.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //TODO : 통신에 성공했을 때 이벤트를 적어주면 됨
                String response = new String(responseBody);
                Log.e("testLogin", response);
                switch (response) {
                    case "id_pw_empty":
                        Toast.makeText(LoginActivity.this, "아이디가 비었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "fail":
                        Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "server_connect_fail":
                        Toast.makeText(LoginActivity.this, "서버 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                        break;

                    default:
                        Intent intent = new Intent(mContext, FragmentActivity.class);
                        intent.putExtra("UserPKey",response);
                        startActivity(intent);
                        finish();
                        break;
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO : 통신에 실패했을 때 이벤트를 적어주면 됨
                //서버와의 통신이 원할하지 않습니다.

                Toast.makeText(LoginActivity.this, "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

}
