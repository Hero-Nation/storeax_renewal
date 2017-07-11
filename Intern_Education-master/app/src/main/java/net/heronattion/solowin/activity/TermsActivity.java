package net.heronattion.solowin.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import net.heronattion.solowin.R;

/**
 * Created by Administrator on 2017-07-11.
 */

public class TermsActivity extends BaseActivity{
    TextView termsContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindViews();
        setCustomActionBar();
        setupEvents();

    }

    @Override
    public void bindViews() {
        termsContent = (TextView) findViewById(R.id.termsContent);
    }

    @Override
    public void setupEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void setCustomActionBar() {
        super.setCustomActionBar();
        titleTxt.setText("이용 약관");
        backBtn.setImageResource(R.drawable.backbutton);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
