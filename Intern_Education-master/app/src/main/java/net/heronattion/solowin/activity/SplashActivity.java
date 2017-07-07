package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
    private String userID = "";
    private String userPassword = "";
    private String userPkey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setupEvents();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                moveToProperActivity();
            }
        }, 1500);
    }



    @Override
    public void onBackPressed() {
    }

    void moveToProperActivity() {
        if (userID.length() == 0) {
            // TODO - 로그인 화면으로 이동해야함
            Intent intent = new Intent(mContext, SignSelectionActivity.class);
            startActivity(intent);
        }
        else {
            // 메인화면으로 이동
            Intent intent = new Intent(mContext, FragmentActivity.class);
            startActivity(intent);
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
//
//        CheckLogin();
    }

}
