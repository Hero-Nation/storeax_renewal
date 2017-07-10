package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.heronattion.solowin.R;

public class ContactActivity extends BaseActivity {
    private LinearLayout emailInquiry;
    private LinearLayout phoneInquiry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        setCustomActionBar();
        bindViews();
        setupEvents();

//        setCustomActionBar();
//        TextView title = (TextView) findViewById(R.id.title);
//        title.setText("여니씨커스텀바");
    }

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        titleTxt.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.VISIBLE);
        backBtn.setImageResource(R.drawable.backbutton);
    }

    @Override
    public void bindViews() {
        emailInquiry = (LinearLayout) findViewById(R.id.emailInquiry);
        phoneInquiry = (LinearLayout) findViewById(R.id.phoneInquiry);
    }

    @Override
    public void setupEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, FragmentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        emailInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });

        phoneInquiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Intent intent = new Intent(mContext, FragmentActivity.class);
        startActivity(intent);
        finish();
    }
}
