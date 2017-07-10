package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class UpdatePassword extends BaseActivity {

    private android.widget.EditText updateOriginalPassword;
    private android.widget.EditText updateNewPassword;
    private android.widget.EditText updateNewPassword2;
    private android.widget.TextView nextButton2;
    private TextView updatePasswordButton;
    private TextView withdrawButton;

    private String Passwrod_PATTERN = "^.*(?=.{8,20})(?=.*[0-9])(?=.*[a-zA-Z]).*$";
    private String userID;
    private android.widget.LinearLayout updateNewPasswordLayout;
    private android.widget.LinearLayout updateNewPassword2Layout;
    private android.widget.LinearLayout updateOriginalPasswordLayout;

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        titleTxt.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
        backBtn.setImageResource(R.drawable.backbutton);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        bindViews();
        setCustomActionBar();
        setupEvents();
    }

    @Override
    public void setupEvents() {
        super.setupEvents();

        updateOriginalPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    updateOriginalPasswordLayout.setBackgroundResource(R.drawable.inputtext_focus);
                else
                    updateOriginalPasswordLayout.setBackgroundResource(R.drawable.inputtext);
            }
        });

        updateNewPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    updateNewPasswordLayout.setBackgroundResource(R.drawable.inputtext_focus);
                else
                    updateNewPasswordLayout.setBackgroundResource(R.drawable.inputtext);
            }
        });

        updateNewPassword2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    updateNewPassword2Layout.setBackgroundResource(R.drawable.inputtext_focus);
                else
                    updateNewPassword2Layout.setBackgroundResource(R.drawable.inputtext);
            }
        });

        updatePasswordButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(updateOriginalPassword.getText().toString().length() == 0 || updateNewPassword.getText().toString().length() == 0 || updateNewPassword2.getText().toString().length() == 0){
                    Toast.makeText(UpdatePassword.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(!PasswrodValidate(updateNewPassword.getText().toString())){
                    Toast.makeText(UpdatePassword.this, "비밀번호는 영문, 숫자 조합 8~20자로 설정하셔야 합니다.", Toast.LENGTH_SHORT).show();
                }
                else if(!updateNewPassword.getText().toString().equals(updateNewPassword2.getText().toString())){
                    Toast.makeText(UpdatePassword.this, "새 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    updatePasswordButtonEvent();
                }
            }
        });

        withdrawButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                withdraw();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
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
        super.bindViews();

        this.withdrawButton = (TextView) findViewById(R.id.withdrawButton);
        this.updatePasswordButton = (TextView) findViewById(R.id.updatePasswordButton);
        this.updateOriginalPasswordLayout = (LinearLayout) findViewById(R.id.updateOriginalPasswordLayout);
        this.updateNewPassword2Layout = (LinearLayout) findViewById(R.id.updateNewPassword2Layout);
        this.updateNewPassword2 = (EditText) findViewById(R.id.updateNewPassword2);
        this.updateNewPasswordLayout = (LinearLayout) findViewById(R.id.updateNewPasswordLayout);
        this.updateNewPassword = (EditText) findViewById(R.id.updateNewPassword);
        this.updateOriginalPassword = (EditText) findViewById(R.id.updateOriginalPassword);

    }

    private boolean PasswrodValidate(String hex) {
        Pattern pattern = Pattern.compile(Passwrod_PATTERN);
        Matcher matcher = pattern.matcher(hex);
        return matcher.matches();
    }

    private void updatePasswordButtonEvent(){
        //포스트로 넘겨줄 값을 지정해줌
        RequestParams params = new RequestParams();

        //왼쪽 인자는 PHP POST 키값을 나타내고 오른쪽 인자는 보낼 값을 나타냄
        final AsyncHttpClient client = HttpClient.getInstance();
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);

        for(int i=0; i < myCookieStore.getCookies().size(); i++) {
            if(myCookieStore.getCookies().get(i).getName().equals("UserID")) {
                userID = myCookieStore.getCookies().get(i).getValue();
                break;
            }
        }
        userID = userID.replace("%40", "@");
        params.put("userID", userID);
        params.put("originalPassword", updateOriginalPassword.getText().toString());
        params.put("newPassword", updateNewPassword.getText().toString());

        //HttpClient 클래스에 기본 URL이 정해져 있음 http://heronation.net/ 이하의 경로를 적어주면 됨
        HttpClient.post("sizeax/php/updatePassword.php", params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                //TODO : 통신에 성공했을 때 이벤트를 적어주면 됨
                String response = new String(responseBody);
                switch(response) {
                    case "success":
                        Toast.makeText(UpdatePassword.this, "비밀번호가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, FragmentActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case "originalPassword" : // 암호가 다름
                        Toast.makeText(UpdatePassword.this, "기존 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        break;
                    default :
                        Toast.makeText(UpdatePassword.this, "서버 환경이 불안정합니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TODO : 통신에 실패했을 때 이벤트를 적어주면 됨
                //서버와의 통신이 원할하지 않습니다.
                Toast.makeText(UpdatePassword.this, "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void withdraw() {
        Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
    }
}
