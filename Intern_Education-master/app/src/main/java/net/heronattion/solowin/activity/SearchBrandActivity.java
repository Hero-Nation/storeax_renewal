package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.heronattion.solowin.R;


/**
 * Created by heronation on 2017-06-08.
 */

public class SearchBrandActivity extends BaseActivity {
    Button saveBrandButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_brand);

        setValues();
        bindViews();
        setupEvents();
        // 액션바
        setSearchActionBar();
        TextView title = (TextView) findViewById(R.id.searchTitleTxt);

        Intent intent = getIntent();
        String titleTxt = intent.getStringExtra("Search_bar_title");

        title.setText(titleTxt);

        // 나이대 선택
        final TextView sexWomen = (TextView) findViewById(R.id.brandsexwomen);
        final TextView sexMen = (TextView) findViewById(R.id.brandsexman);
        sexWomen.setSelected(true);

        sexWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexWomen.setSelected(true);
                sexMen.setSelected(false);
            }
        });

        sexMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sexWomen.setSelected(false);
                sexMen.setSelected(true);
            }
        });

        // 연령대 선택
        TextView age10 = (TextView) findViewById(R.id.brandage10);
        age10.setSelected(true);

    }

    public void bindViews() {
        saveBrandButton = (Button)findViewById(R.id.saveBrandButton);
    }

    public void setupEvents() {
        saveBrandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "clicked save");
            }
        });
    }

    public void setValues() {

    }


    // backBtn 클릭 시 Main page로 이동동
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };
}
