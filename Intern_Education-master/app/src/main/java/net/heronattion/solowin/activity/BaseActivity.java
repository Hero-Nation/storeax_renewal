package net.heronattion.solowin.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.heronattion.solowin.R;

/**
 * Created by Brant on 2017-04-08.
 */

public class BaseActivity extends AppCompatActivity {

    public Context mContext = null;

    public ImageView backBtn;
    public TextView titleTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext  = this;
    }

    public void bindViews() {

    }

    public void setCustomActionBar() {

        ActionBar myActionBar = getSupportActionBar();
        myActionBar.setDisplayShowHomeEnabled(false);
        myActionBar.setDisplayHomeAsUpEnabled(false);
        myActionBar.setDisplayShowTitleEnabled(false);
        myActionBar.setDisplayShowCustomEnabled(true);


        LayoutInflater inf = LayoutInflater.from(mContext);
        View customBarView = inf.inflate(R.layout.custom_action_bar, null);

        myActionBar.setCustomView(customBarView);
        myActionBar.setDisplayShowCustomEnabled(true);

        Toolbar parent = (Toolbar) customBarView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        getSupportActionBar().setElevation(0);

//        backBtn = (ImageView) customBarView.findViewById(R.id.backBtn);
        titleTxt = (TextView) customBarView.findViewById(R.id.title);

    }

    public void setupEvents() {

    }

    public void setValues() {

    }
}
