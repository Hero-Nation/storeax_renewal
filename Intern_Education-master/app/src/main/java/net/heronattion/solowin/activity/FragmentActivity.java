package net.heronattion.solowin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;

import org.w3c.dom.Text;

import cz.msebera.android.httpclient.Header;

import static java.lang.Integer.parseInt;


public class FragmentActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Context mContext;
    public static BaseActivity mainActivity;
    private pagerAdapter vpAdapter;
    private ViewPager vp;

    private NavigationView navigationView;
    private TextView navigationUserID;
    private TextView navigationLogout;
    private TextView signUpButton;
    private TextView myPageButton;
    private TextView mySizeButton;
    private TextView myStyleButton;
    private TextView contactButton;
    private TextView termButton;
    private TextView inquiryButton;


    private LinearLayout ll;
    ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerView;
    private DrawerLayout dlDrawer;
    private ImageView searchBtn;

    private boolean DrawerState;
    private int changeSearchBtn = 0;
    private int getChangeSearchBtn;

    public static int userPkey;
    private String userID;
    private String[] parseUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);


        setFragmentActionBar();
        mainActivity = this;
        mContext = this;
        bindViews();
        setValues();
        setupEvents();

        LinearLayout tab_first = (LinearLayout) findViewById(R.id.page1Btn);
        LinearLayout tab_second = (LinearLayout) findViewById(R.id.page2Btn);
        LinearLayout tab_third = (LinearLayout) findViewById(R.id.page3Btn);
        LinearLayout tab_fourth = (LinearLayout) findViewById(R.id.page4Btn);

        /*
        *   메뉴 버튼 drawer toggle
        */
        drawerToggle=new ActionBarDrawerToggle(this,dlDrawer,R.mipmap.hambutton,R.string.open,R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        dlDrawer.setDrawerListener(drawerToggle);
        navigationView.setNavigationItemSelectedListener(this);
        /*
        *   Viewpase Adapter 설정
        */

        vpAdapter = new pagerAdapter(getSupportFragmentManager());
        vp.setAdapter(vpAdapter);
        vp.setCurrentItem(0); // 앱실행시 첫번째 화면

        /*
        *   버튼 클릭 시 페이지 이동
        *   movePageListener 에서 페이지 이동 시 이벤트 구현
        */

        tab_first.setOnClickListener(movePageListener);
        tab_first.setTag(0);
        tab_second.setOnClickListener(movePageListener);
        tab_second.setTag(1);
        tab_third.setOnClickListener(movePageListener);
        tab_third.setTag(2);
        tab_fourth.setOnClickListener(movePageListener);
        tab_fourth.setTag(3);

        tab_first.setSelected(true);

        // 탭 클릭 page 변화!!! 이벤트
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }

            @Override
            public void onPageSelected(int position)
            {
                Fragment f = vpAdapter.getItem(position);
                if (position==3 || position==2 || position == 1 || position == 0)
                    vpAdapter.notifyDataSetChanged();

                int i = 0;
                while(i<4)
                {
                    if(position==i)
                    {
                        ll.findViewWithTag(i).setSelected(true);
                    }
                    else
                    {
                        ll.findViewWithTag(i).setSelected(false);
                    }
                    i++;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });

        /*
        *   검색 Page 이동
        *   FristFragment 일때만 브랜드검색
        *   movePageListener 에서 changeSearchBtn Flag 조절
        */
        searchBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
//                getChangeSearchBtn = SecondFragment.changeSearchBtn;
//                Intent brandIntent = new Intent(FragmentActivity.this, SearchBrandActivity.class);
//                Intent productIntent = new Intent(FragmentActivity.this, SearchProductActivity.class);
//                switch (changeSearchBtn){
//                    case 0:
//                        brandIntent.putExtra("Search_bar_title", "브랜드검색");
//                        startActivity(brandIntent);
//                        break;
//                    case 1:
//                        productIntent.putExtra("Search_bar_title", "상품검색");
//                        startActivity(productIntent);
//                        break;
//                    case 2:
//                        if(getChangeSearchBtn==0){
//                            brandIntent.putExtra("Search_bar_title", "브랜드검색");
//                            startActivity(brandIntent);
//                        }else{
//                            productIntent.putExtra("Search_bar_title", "상품검색");
//                            startActivity(productIntent);
//                        }
//                        break;
//                }
            }
        });
    }
    @Override
    public void bindViews() {
        drawerView = (RelativeLayout) findViewById(R.id.drawer);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchBtn = (ImageView) findViewById(R.id.searchBtn);
        vp = (ViewPager)findViewById(R.id.vp);
        ll = (LinearLayout)findViewById(R.id.ll);

        navigationView = (NavigationView) findViewById(R.id.design_navigation_view);
        navigationUserID = (TextView) findViewById(R.id.navigationUserID);
        navigationLogout = (TextView) findViewById(R.id.navigationLogout);
        signUpButton = (TextView) findViewById(R.id.signUpButton);
        myPageButton = (TextView) findViewById(R.id.myPageButton);
        mySizeButton = (TextView) findViewById(R.id.mySizeButton);
        myStyleButton = (TextView) findViewById(R.id.myStyleButton);
        contactButton = (TextView) findViewById(R.id.contactButton);
        termButton = (TextView) findViewById(R.id.termButton);
        inquiryButton = (TextView) findViewById(R.id.inquiryButton);
    }

    @Override
    public void setupEvents() {
        navigationLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams params = new RequestParams();

                //HttpClient 클래스에 기본 URL이 정해져 있음 http://heronation.net/ 이하의 경로를 적어주면 됨
                HttpClient.post("android/logout.php", params, new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        //TODO : 통신에 성공했을 때 이벤트를 적어주면 됨
                        String response = new String(responseBody);
                        if(response.equals("logout")){
                            Intent intent = new Intent(mContext, SignSelectionActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            Log.d("Response", "서버통신에러");
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //TODO : 통신에 실패했을 때 이벤트를 적어주면 됨
                        //서버와의 통신이 원할하지 않습니다.

                        Toast.makeText(mContext, "서버와 통신이 원활하지 않습니다.", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        myPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, UpdatePassword.class);
                startActivity(intent);
                finish();
//                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        mySizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        myStyleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.parse("http://www.heronation.net/");
                intent.setData(uri);
                startActivity(intent);
//                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        termButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
        inquiryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //activity는 넘겨 놓은 상태 추후 주석처리만 해제하면 바로 사용가능 ContactActivity에서 작업필요
//                Intent intent = new Intent(mContext, ContactActivity.class);
//                startActivity(intent);
//                finish();
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setValues() {
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        for(int i=0; i < myCookieStore.getCookies().size(); i++) {
            if(myCookieStore.getCookies().get(i).getName().equals("UserPKEY"))
                userPkey = parseInt(myCookieStore.getCookies().get(i).getValue());
            else if(myCookieStore.getCookies().get(i).getName().equals("UserID"))
                userID = myCookieStore.getCookies().get(i).getValue();
        }
        userID = userID.replace("%40", "@");
        parseUserID = userID.split("[@]");
        Log.d("UserID", parseUserID[0]);
        navigationUserID.setText(parseUserID[0]);
    }


    // 프래그먼트간 페이지 이동
    View.OnClickListener movePageListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            int tag = (int) v.getTag();
            vp.setCurrentItem(tag);


            int i = 0;
            while(i<4)
            {
                if(tag==i)
                {
                    ll.findViewWithTag(i).setSelected(true);

                    if(i==0){
                        searchBtn.setVisibility(View.VISIBLE);
                        changeSearchBtn = 0;
                    }
                    else if(i==1){
                        searchBtn.setVisibility(View.VISIBLE);
                        changeSearchBtn = 2;
                    }
                    else if(i==2){
                        searchBtn.setVisibility(View.VISIBLE);
                        changeSearchBtn = 1;
                    }
                    else if(i==3) {
                        searchBtn.setVisibility(View.INVISIBLE);
                    }
                    else{
                        searchBtn.setVisibility(View.VISIBLE);
                        changeSearchBtn = 1;
                    }
                }
                else
                {
                    ll.findViewWithTag(i).setSelected(false);
                }
                i++;

            }
        }
    };

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }

    // 프래그먼트 어댑터
    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        SparseArray< View > views = new SparseArray< View >();
        public pagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new FirstFragment();
                case 1:
                    return new SecondFragment();
                case 2:
                    return new ThirdFragment();
                case 3:
                    return new FourthFragment();
                default:
                    return null;
            }
        }
        // 총 프래그먼트 수
        @Override
        public int getCount()
        {
            return 4;
        }

        @Override
        public int getItemPosition(Object object) { return POSITION_NONE; }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

}