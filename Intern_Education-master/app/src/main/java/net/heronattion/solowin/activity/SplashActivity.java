package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ContextUtil;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;
import static java.lang.Integer.parseInt;

public class SplashActivity extends BaseActivity {
    Button loginButton;
    Button signupButton;
    private String userID = "";
    private String userPassword = "";
    private String userPkey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupEvents();
        loginButton = (Button) findViewById(R.id.loginButton);
        signupButton = (Button) findViewById(R.id.signupButton);

        loginButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        signupButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });


//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                moveToProperActivity();
//            }
//        }, 1500);
    }



    @Override
    public void onBackPressed() {
    }

    void moveToProperActivity() {
        if (ContextUtil.getMyUserData(mContext).PKey == -1) {
            // TODO - 로그인 화면으로 이동해야함
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
        else {
            // 메인화면으로 이동
//            Intent intent = new Intent(mContext, MainActivity.class);
//            startActivity(intent);
        }
        finish();
    }

    @Override
    public void setupEvents() {
        final AsyncHttpClient client = HttpClient.getInstance();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);

        for(int i=0; i < myCookieStore.getCookies().size(); i++) {
            if(myCookieStore.getCookies().get(i).getName().equals("UserID"))
                userID = myCookieStore.getCookies().get(i).getValue();
            else if(myCookieStore.getCookies().get(i).getName().equals("UserPWD"))
                userPassword = myCookieStore.getCookies().get(i).getValue();
            else if(myCookieStore.getCookies().get(i).getName().equals("UserPKEY"))
                userPkey = myCookieStore.getCookies().get(i).getValue();
        }

            // get cookie 에서 받아오는 값의 특수 문자 값이 디코딩처리가 안됨.. 추후에 처리해야함.
            // 우선 해당 특수문자에 대한 ASCII를 변환해주었음.
            userID = userID.replace("%40", "@");
            userPassword = userPassword.replace("%7E", "~");
            userPassword = userPassword.replace("%60", "`");
            userPassword = userPassword.replace("%21", "!");
            userPassword = userPassword.replace("%40", "@");
            userPassword = userPassword.replace("%23", "#");
            userPassword = userPassword.replace("%24", "$");
            userPassword = userPassword.replace("%25", "%");
            userPassword = userPassword.replace("%5E", "^");
            userPassword = userPassword.replace("%2A", "*");
            userPassword = userPassword.replace("%28", "(");
            userPassword = userPassword.replace("%29", ")");
            userPassword = userPassword.replace("%2D", "-");
            userPassword = userPassword.replace("%5F", "_");
            userPassword = userPassword.replace("%2B", "+");
            userPassword = userPassword.replace("%3D", "=");
            userPassword = userPassword.replace("%7C", "|");
            userPassword = userPassword.replace("%5C", "\\");

            log.i("id: ", userID);
            log.i("pw: ", userPassword);
            log.i("pkey: ", userPkey);

        client.setCookieStore(myCookieStore);

        CheckLogin();
    }

    private void CheckLogin() {
        //포스트로 넘겨줄 값을 지정해줌
        RequestParams params = new RequestParams();

        //왼쪽 인자는 PHP POST 키값을 나타내고 오른쪽 인자는 보낼 값을 나타냄
        params.put("userId", userID);
        params.put("userPassword", userPassword);

        //HttpClient 클래스에 기본 URL이 정해져 있음 http://heronation.net/ 이하의 경로를 적어주면 됨
        HttpClient.post("sizeax/php/tryLogin.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //TODO : 통신에 성공했을 때 이벤트를 적어주면 됨
                String response = new String(responseBody);
                Log.d("responseLogin",response);
                switch(response) {
                    case "id_pw_empty" :
//                        Toast.makeText(SplashActivity.this, "로그인 실패.", Toast.LENGTH_SHORT).show();
                    case "fail" :
//                        Toast.makeText(SplashActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case "server_connect_fail":
                        Toast.makeText(SplashActivity.this, "서버 연결이 불안정합니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        Toast.makeText(SplashActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(SplashActivity.this, "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
