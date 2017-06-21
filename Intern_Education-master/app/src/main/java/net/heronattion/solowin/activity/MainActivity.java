package net.heronattion.solowin.activity;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import net.heronattion.solowin.R;
import net.heronattion.solowin.adapter.StoryAdapter;
import net.heronattion.solowin.data.StoryData;
import net.heronattion.solowin.data.UserData;
import net.heronattion.solowin.util.BackPressCloseHandler;
import net.heronattion.solowin.util.ContextUtil;
import net.heronattion.solowin.util.DateTime;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    UserData mUserData = null;
    StoryAdapter mStoryAdapter = null;
    ArrayList<StoryData> mStoryDataList = new ArrayList<>();

    private BackPressCloseHandler backPressCloseHandler;

    private android.widget.FrameLayout viewPage1;
    private android.widget.ListView storyListView;
    private android.widget.ImageView floatingBtn;
    private FrameLayout viewPage2;
    private android.support.v4.view.ViewPager viewPager;
    private android.widget.LinearLayout page1Btn;
    private android.widget.LinearLayout page2Btn;
    private android.widget.LinearLayout page3Btn;
    private android.widget.LinearLayout page4Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setCustomActionBar();
        bindViews();
        setupEvents();
        setValues();

    }

    @Override
    public void setupEvents() {
        super.setupEvents();

        View.OnClickListener pageListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                movePage(Integer.parseInt(v.getTag().toString()));
            }
        };

        page1Btn.setOnClickListener(pageListener);
        page2Btn.setOnClickListener(pageListener);
        page3Btn.setOnClickListener(pageListener);
        page4Btn.setOnClickListener(pageListener);

        
        floatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(mContext, "플로팅 버튼을 띄웁시다.", Toast.LENGTH_SHORT).show();
                
            }
        });
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

    void  movePage(int index) {
        viewPager.setCurrentItem(index);
    }

    @Override
    public void setValues() {
        super.setValues();


        backPressCloseHandler = new BackPressCloseHandler(MainActivity.this);

        titleTxt.setText("메인화면");
        backBtn.setVisibility(View.GONE);

        mUserData = ContextUtil.getMyUserData(mContext);

        Log.d("mUserData", mUserData.PKey + "");
        Log.d("mUserData", mUserData.userName);

        StoryData temp = new StoryData();
        temp.storyDate = new DateTime();
        temp.title = "1번객체";
        temp.location = "1번 장소";
        mStoryDataList.add(temp);

        StoryData temp2 = new StoryData();
        temp2.storyDate = new DateTime();
        temp2.title = "2번객체";
        temp2.location = "2번 장소";
        mStoryDataList.add(temp2);

        StoryData temp3 = new StoryData();
        temp3.storyDate = new DateTime();
        temp3.title = "3번객체";
        temp3.location = "3번 장소";
        mStoryDataList.add(temp3);


        StoryData temp4 = new StoryData();
        temp4.storyDate = new DateTime();
        temp4.title = "4번객체";
        temp4.location = "4번 장소";
        mStoryDataList.add(temp4);


        StoryData temp5 = new StoryData();
        temp5.storyDate = new DateTime();
        temp5.title = "5번객체";
        temp5.location = "5번 장소";
        mStoryDataList.add(temp5);

        mStoryAdapter = new StoryAdapter(mContext, mStoryDataList);
        storyListView.setAdapter(mStoryAdapter);


        viewPager.setAdapter(new WizardPagerAdapter());
        viewPager.setOffscreenPageLimit(4);

    }


    class WizardPagerAdapter extends PagerAdapter {

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.viewPage1;
                    break;
                case 1:
                    resId = R.id.viewPage2;
                    break;
                case 2:
                    resId = R.id.viewPage1;
                    break;
                case 3:
                    resId = R.id.viewPage2;
                    break;
            }
            return findViewById(resId);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }
    }


    @Override
    public void bindViews() {
        super.bindViews();

        this.page4Btn = (LinearLayout) findViewById(R.id.page4Btn);
        this.page3Btn = (LinearLayout) findViewById(R.id.page3Btn);
        this.page2Btn = (LinearLayout) findViewById(R.id.page2Btn);
        this.page1Btn = (LinearLayout) findViewById(R.id.page1Btn);
        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        this.viewPage2 = (FrameLayout) findViewById(R.id.viewPage2);
        this.floatingBtn = (ImageView) findViewById(R.id.floatingBtn);
        this.storyListView = (ListView) findViewById(R.id.storyListView);
        this.viewPage1 = (FrameLayout) findViewById(R.id.viewPage1);
    }
}
