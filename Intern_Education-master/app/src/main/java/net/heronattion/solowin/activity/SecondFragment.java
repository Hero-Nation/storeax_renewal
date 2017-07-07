package net.heronattion.solowin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.adapter.FavoriteBrandListAdapter;
import net.heronattion.solowin.adapter.MyRecyclerViewAdapter;
import net.heronattion.solowin.adapter.RecyclerItemClickListener;
import net.heronattion.solowin.data.BrandList;
import net.heronattion.solowin.data.BrandProductItem;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;
import static java.lang.Integer.parseInt;

/**
 * Created by heronation on 2017-06-08.
 */

public class SecondFragment extends Fragment {
    private LinearLayout tab_brand;
    private LinearLayout tab_cloth;
    private FrameLayout brandView;
    private FrameLayout clothView;
    private LinearLayout layout;
    private ListView brandListView;
    private LinearLayout brandEmptyView;
    private LinearLayout clothEmptyView;

    private boolean brandIsData = false;
    private boolean clothIsdata = false;
    public static int changeSearchBtn = 0;

    private RecyclerView clothListView;
    List<BrandProductItem> listViewItems;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private String mProductStyle;
    private String[] wishList;
    String[][] parseDataList;
    private int pastVisibleItems;
    MyRecyclerViewAdapter rcAdapter;
    private int scrollFlag;
    private int userID;

    ArrayList<BrandList> brandList = new ArrayList<BrandList>();
    FavoriteBrandListAdapter adapter;

    private String[][] mBrandListItem;

    public SecondFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_second, container, false);
        bindViews();
        setValues();
        setupEvents();
        return layout;
    }

    //탭 공통
    public void setTabEvent() {
        // tab 선택
        tab_brand.setSelected(true);

        tab_brand.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                brandView.setVisibility(View.VISIBLE);
                clothView.setVisibility(View.GONE);
                tab_brand.setSelected(true);
                tab_cloth.setSelected(false);
                changeSearchBtn = 0;
            }
        });
        tab_cloth.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                log.e("is", "" + clothIsdata);
                brandView.setVisibility(View.GONE);
                clothView.setVisibility(View.VISIBLE);
                tab_brand.setSelected(false);
                tab_cloth.setSelected(true);
                changeSearchBtn = 1;
            }
        });
    }

    public  void setIsDataEvent() {
        log.d("brandIsData", brandIsData + "");
        if(brandIsData) {
            brandListView.setVisibility(View.VISIBLE);
            brandEmptyView.setVisibility(View.GONE);
        } else {
            brandListView.setVisibility(View.GONE);
            brandEmptyView.setVisibility(View.VISIBLE);
        }

        if(clothIsdata) {
            clothListView.setVisibility(View.VISIBLE);
            clothEmptyView.setVisibility(View.GONE);
        } else {
            clothListView.setVisibility(View.GONE);
            clothEmptyView.setVisibility(View.VISIBLE);
        }
    }


    // 브랜드 탭
    public void setClickedListItem(){
        brandListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrandList item = (BrandList) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DetailBrandActivity.class);
                intent.putExtra("brandKey", item.getBrandKey());
                intent.putExtra("name", item.getName());
                intent.putExtra("logo", item.getLogo());
                startActivity(intent);
//                getActivity().finish();
            }
        });
    }
    public void setFavoriteBrandData() {
        RequestParams params = new RequestParams();
        params.put("UserPKey", userID);
        adapter = new FavoriteBrandListAdapter(getContext());
        HttpClient.post("android/getFavoriteBrand.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                log.d("response", response);

                if(response.equals("0")) {
                    brandIsData = false;
                } else {
                    brandIsData = true;
                    // BrandListItem 'name' 'logo' + 'favoriteFlag(T/F)'
                    ParseData parse = new ParseData();
                    mBrandListItem = parse.getDoubleArrayData(response);

                    for (int i=0; i< mBrandListItem.length; i++) {
                        adapter.addItem(mBrandListItem[i][0], mBrandListItem[i][1], mBrandListItem[i][2], true);
                        brandListView.setAdapter(adapter);
                    }

                }

                setIsDataEvent();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    //찜한옷 탭
    private void setProductItem() {
        RequestParams params = new RequestParams();
        params.put("ScrollFlag", scrollFlag);
        params.put("UserPKey", userID);
        HttpClient.post("android/getWishListItem.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                log.d("responseW=>>", response);
                if(response.equals("0")) {
                    Log.e("responseW1=>>", response);
                    clothIsdata = false;
                }else {
                    Log.e("responseW2=>>", response);

                ParseData parse = new ParseData();
                parseDataList = parse.getDoubleArrayData(response);

                for (int i=0; i< parseDataList.length; i++) {
                    listViewItems.add(new BrandProductItem(parseDataList[i][1], parseDataList[i][2], parseDataList[i][4], parseDataList[i][5], parseInt(parseDataList[i][0]), true));
                    clothIsdata = true;
                }

//                Log.i("listViewItems for문", listViewItems.size()+"");
                ////////////////////////////////////

                List<String[]> mParsedStyleDataList = new ArrayList<>();
                mParsedStyleDataList  = parse.getMergeArrayList(parseDataList, 6);

                for (int i = 0; i < parseDataList.length; i++) {

                    mProductStyle = "";

                    for (int j = 0; j < mParsedStyleDataList.get(i).length; j++) {

                        mProductStyle += mParsedStyleDataList.get(i)[j] + ", ";
                    }
                    log.d("style", mProductStyle);
                }
                clothListView.setAdapter(rcAdapter);
                }
                setIsDataEvent();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    public void setClickedProductItem(){
        clothListView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), clothListView, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {
                Context context = getContext();
                String url = parseDataList[position][5];
                Log.e("POSITION", url);
                Intent intent = new Intent(context, LoadDetailActivity.class);
                intent.putExtra("URL", url);
                startActivity(intent);
            }
            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    public void setupEvents(){
        setTabEvent();
        setFavoriteBrandData();
        setProductItem();
        setClickedListItem();
//        setClickedProductItem();
    }

    public void setValues(){
        scrollFlag = 0;
        userID = FragmentActivity.userID;
        changeSearchBtn = 0;
    }

    public void bindViews(){
        tab_brand = (LinearLayout)layout.findViewById(R.id.favoritebrandBtn);
        tab_cloth = (LinearLayout)layout.findViewById(R.id.favoriteclothBtn);

        brandView = (FrameLayout) layout.findViewById(R.id.favoritebrandPage);
        clothView = (FrameLayout) layout.findViewById(R.id.favoriteclothPage);

        brandListView = (ListView) layout.findViewById(R.id.lvSecondFlagmentBrand);

        brandEmptyView = (LinearLayout) layout.findViewById(R.id.emptyfavoritBrand);
        clothEmptyView = (LinearLayout) layout.findViewById(R.id.emptyfavoritCloth);

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
        clothListView = (RecyclerView)layout.findViewById(R.id.recycler_view);
        listViewItems = new ArrayList<BrandProductItem>();
        clothListView.setHasFixedSize(true);
        clothListView.setLayoutManager(gaggeredGridLayoutManager);
        rcAdapter = new MyRecyclerViewAdapter(getContext(), listViewItems);
    }
}
