package net.heronattion.solowin.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;

import static java.lang.Integer.parseInt;


public class FragmentActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static Context mContext;
    public static BaseActivity mainActivity;
    private pagerAdapter vpAdapter;
    private ViewPager vp;

    private LinearLayout ll;
    ActionBarDrawerToggle drawerToggle;
    private RelativeLayout drawerView;
    private DrawerLayout dlDrawer;
    private ImageView searchBtn;

    private boolean DrawerState;
    private int changeSearchBtn = 0;
    private int getChangeSearchBtn;

    public static int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        for(int i=0; i < myCookieStore.getCookies().size(); i++) {
            if(myCookieStore.getCookies().get(i).getName().equals("UserPKEY"))
                userID = parseInt(myCookieStore.getCookies().get(i).getValue());
        }
//        Intent intent = getIntent();
//        userID = parseInt(intent.getStringExtra("UserPKey"));
//        userID = 1988;
        /*
        *    CustomActionBar
        *    res - layout - main_actio_bar. xml
        */

        setFragmentActionBar();
        mainActivity = this;
        mContext = this;
//        drawerView = (RelativeLayout) findViewById(R.id.drawer);
        dlDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        searchBtn = (ImageView) findViewById(R.id.searchBtn);

        vp = (ViewPager)findViewById(R.id.vp);
        ll = (LinearLayout)findViewById(R.id.ll);

        LinearLayout tab_first = (LinearLayout) findViewById(R.id.page1Btn);
        LinearLayout tab_second = (LinearLayout) findViewById(R.id.page2Btn);
        LinearLayout tab_third = (LinearLayout) findViewById(R.id.page3Btn);
        LinearLayout tab_fourth = (LinearLayout) findViewById(R.id.page4Btn);

        TextView menuSignUP = (TextView)findViewById(R.id.menuSignUP);

        menuSignUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FragmentActivity.this, "회원가입", Toast.LENGTH_SHORT).show();
                dlDrawer.closeDrawer(GravityCompat.START);
            }
        });

        /*
        *   메뉴 버튼 drawer toggle
        */
        drawerToggle=new ActionBarDrawerToggle(this,dlDrawer,R.mipmap.hambutton,R.string.open,R.string.close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
//                drawerView.bringToFront();
            }
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        dlDrawer.setDrawerListener(drawerToggle);

        NavigationView navigationView = (NavigationView)findViewById(R.id.design_navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

//        drawerToggle.syncState();

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
                    getChangeSearchBtn = SecondFragment.changeSearchBtn;
                    Intent brandIntent = new Intent(FragmentActivity.this, SearchBrandActivity.class);
                    Intent productIntent = new Intent(FragmentActivity.this, SearchProductActivity.class);
                    switch (changeSearchBtn){
                        case 0:
                            brandIntent.putExtra("Search_bar_title", "브랜드검색");
                            startActivity(brandIntent);
                            break;
                        case 1:
                            productIntent.putExtra("Search_bar_title", "상품검색");
                            startActivity(productIntent);
                            break;
                        case 2:
                            if(getChangeSearchBtn==0){
                                brandIntent.putExtra("Search_bar_title", "브랜드검색");
                                startActivity(brandIntent);
                            }else{
                                productIntent.putExtra("Search_bar_title", "상품검색");
                                startActivity(productIntent);
                            }
                            break;
                    }
                }
        });

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

//
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


//        @Override
//        public Object instantiateItem(View container, int position) {
//            View root = ll;
//            ((ViewPager) container).addView(root);
//            views.put(position, root);
//            return root;
//        }
//
//        @Override
//        public void destroyItem(View collection, int position, Object o) {
//            View view = (View)o;
//            ((ViewPager) collection).removeView(view);
//            views.remove(position);
//            view = null;
//        }
//
//        @Override
//        public void notifyDataSetChanged() {
//            int key = 0;
//            for(int i = 0; i < views.size(); i++) {
//                key = views.keyAt(i);
//                View view = views.get(key);
//
//
//            }
//            super.notifyDataSetChanged();
//        }

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