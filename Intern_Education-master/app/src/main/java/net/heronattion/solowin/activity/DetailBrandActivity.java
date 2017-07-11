package net.heronattion.solowin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.adapter.BrandListAdapter;
import net.heronattion.solowin.adapter.MyRecyclerViewAdapter;
import net.heronattion.solowin.adapter.RecyclerItemClickListener;
import net.heronattion.solowin.data.BrandProductItem;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static java.lang.Integer.parseInt;


/**
 * Created by heronation on 2017-06-12.
 */

public class DetailBrandActivity extends BaseActivity {

    RecyclerView recyclerView;
    BrandListAdapter adapter;
    ArrayList<BrandProductItem> listViewItems;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private MyRecyclerViewAdapter rcAdapter;
    private String mProductStyle;
    private String[] wishList;
    private String[][] parseDataList;
    private String[][] brandCategory;
    private int userID;
    private int categoryID;
    private int scrollFlag;
    private ImageView backBtn;
    private ImageView favoritebrandBtn;
    private TextView brand_set_price;
    private ImageView detailLogo;
    private TextView detailName;

    private String mbrandKey, mTitle, mLogo;
    private boolean mFavorite;
    private boolean isWish;

    private boolean loading = true;
    private boolean lastProducdt = false;
    private int pastVisibleItems, visibleItemCount, totalItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_brand);
        bindViews();
        setValues();
        setupEvents();
    }


    public void setupEvents() {
        super.setupEvents();
        hideActionBar();
        setBrandCategory();
        setFavoriteBtn();
        getWishList();
        setClickedDialog();
        setClickedbackBtn();
        setScrollRecyclerView();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), FragmentActivity.class);
        getWindow().setWindowAnimations(android.R.style.Animation_Toast);
        finish();
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void setValues() {
        adapter = new BrandListAdapter(getApplicationContext());
        scrollFlag = 0;
        Intent intent = getIntent();
        userID = FragmentActivity.userPkey;
        mbrandKey = intent.getStringExtra("brandKey");
        mTitle = intent.getStringExtra("name");
        mLogo = intent.getStringExtra("logo");
        categoryID = 0;
        isWish = false;
        detailName.setText(mTitle);

        Glide.with(this)
                .load("http://" + mLogo)
                .bitmapTransform(new CropCircleTransformation(this))
                .thumbnail( 0.1f )
//                .placeholder(R.drawable.white_background)
                .into(detailLogo);
        setBrandInfo();
    }

    // Clicked backBtn
    public void setClickedbackBtn() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), FragmentActivity.class);
            getWindow().setWindowAnimations(android.R.style.Animation_Toast);
            finish();
            startActivity(intent);
            overridePendingTransition(0,0);
            }
        });
    }


    // Clicked Dialog
    public void setClickedDialog() {
        brand_set_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDialog();
            }
        });
    }
    public void setScrollRecyclerView(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                gaggeredGridLayoutManager.invalidateSpanAssignments();
                visibleItemCount = gaggeredGridLayoutManager.getChildCount();
                totalItemCount = gaggeredGridLayoutManager.getItemCount();
                int[] firstVisibleItems = null;
                firstVisibleItems = gaggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];
                }
                if (loading) {
                    if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                        loading = false;
                        scrollFlag++;
                        setProductItem();
                    }
                }
                if(lastProducdt){
                    lastProducdt = false;
                    Toast.makeText(mContext,"상품이 없습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // Set price Dialog
    public void setDialog() {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(DetailBrandActivity.this);
    }

    // 리스트뷰에 데이터 뿌림
    private void setBrandInfo() {
        RequestParams params = new RequestParams();
        params.put("UserPKey",userID);
        params.put("BrandID", mbrandKey);
        HttpClient.post("android/getBrandDetail.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.e("Favorite",response);
                adapter = new BrandListAdapter(getApplicationContext());

                if(response.equals("true")) {
                    mFavorite = true;
                    favoritebrandBtn.setImageResource(R.mipmap.selectedfavoritebutton);
                } else {
                    mFavorite = false;
                    favoritebrandBtn.setImageResource(R.mipmap.favoritebutton);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void getWishList() {
        RequestParams params = new RequestParams();
        params.put("UserPKey", userID);
        params.put("BrandID", mbrandKey);

        HttpClient.post("android/getWishList.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                ParseData parse = new ParseData();
//                Log.d("내용",""+response);
                wishList = parse.getArrayData(response);
//                for (int i=0; i< wishList.length; i++) {
//                        log.d("내용", wishList[i]);
//                }
                if(response.equals("0")){
                    //
                    Log.e("내용", "없음");
                    isWish = false;
                }else{
                    isWish = true;
                }

                setProductItem();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                Log.e("내용",""+response);
            }
        });
    }
    // Product List 뿌리기
    private void setProductItem() {
        RequestParams params = new RequestParams();
        // params.put 브랜드키
        params.put("ScrollFlag", scrollFlag);
        params.put("Brand", mbrandKey);
        HttpClient.post("mall/php/tryFilter.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
//                log.d("response=>>", response);
                ParseData parse = new ParseData();
                parseDataList = parse.getDoubleArrayData(response);

                if(isWish)
                    for (int i=0; i< parseDataList.length; i++) {
                        if(parseDataList[i][0].equals(wishList[i]))
                            listViewItems.add(new BrandProductItem(parseDataList[i][1], parseDataList[i][2], parseDataList[i][4], parseDataList[i][5], parseInt(parseDataList[i][0]), true));
                        else
                            listViewItems.add(new BrandProductItem(parseDataList[i][1], parseDataList[i][2], parseDataList[i][4], parseDataList[i][5], parseInt(parseDataList[i][0]), false));
                    }

                ////////////////////////////////////

                List<String[]> mParsedStyleDataList = new ArrayList<>();
                mParsedStyleDataList  = parse.getMergeArrayList(parseDataList, 6);

                for (int i = 0; i < parseDataList.length; i++) {

                    mProductStyle = "";

                    for (int j = 0; j < mParsedStyleDataList.get(i).length; j++) {

                        mProductStyle += mParsedStyleDataList.get(i)[j] + ", ";

                    }

                }
                if(parseDataList.length==15) {
                    loading = true;
                }else{
                    lastProducdt = true;
                }
                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    // StaggeredGridView

    // 즐겨찾기
    public void setFavoriteBtn() {
        favoritebrandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mFavorite) {
                    favoritebrandBtn.setImageResource(R.mipmap.favoritebutton);
                    Toast.makeText(getApplicationContext(),"즐겨찾기가 해제 되었습니다.", Toast.LENGTH_SHORT).show();
                    mFavorite = false;

                    // 즐겨찾기 등록
                } else {
                    favoritebrandBtn.setImageResource(R.mipmap.selectedfavoritebutton);
                    Toast.makeText(getApplicationContext(),"즐겨찾기가 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                    mFavorite = true;
                }

                // 통신
                RequestParams params = new RequestParams();
                params.put("UserPKey", userID);
                params.put("BrandKey", mbrandKey);

                HttpClient.post("android/insertBrand.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        Log.e("error", response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    }
                });
            }
        });
    }
    private void setBrandCategory(){
        RequestParams params = new RequestParams();
        params.put("BrandID", mbrandKey);

        HttpClient.post("android/getBrandCategory.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.e("res_category", response);
                ParseData parse = new ParseData();
                brandCategory = parse.getDoubleArrayData(response);
                setSpinner();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
    public void setSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.category_spinner);
        ArrayList<String> items =  new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, items);
        adapter.add("카테고리");
        for (int i=0; i< brandCategory.length; i++) {
            adapter.add(brandCategory[i][1]);
        }
        adapter.notifyDataSetChanged();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
//        Spinner spinner = (Spinner)findViewById(R.id.category_spinner);
//
//        // 스피너 어댑터 설정
//        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.category_array, R.layout.spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);

        // 스피너 이벤트 발생
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO write click event

                String categoryName = parent.getItemAtPosition(position).toString();
                for (int i=0; i< brandCategory.length; i++) {
                    if(categoryName.equals(brandCategory[i][1]))
                        categoryID = parseInt(brandCategory[i][0]);
                }
                Log.e("position", ""+categoryID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("position", "nothing");
                // TODO Auto-generated method stub
            }
        });
    }

    // 액션바 숨김
    public void hideActionBar() {
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
        }
    }

    public void bindViews() {
        favoritebrandBtn = (ImageView) findViewById(R.id.detailFavoritebrandBtn);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        brand_set_price = (TextView) findViewById(R.id.brand_set_price);
        detailName = (TextView) findViewById(R.id.detailName);
        detailLogo = (ImageView) findViewById(R.id.detailLogo);
        listViewItems = new ArrayList<BrandProductItem>();

        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,1);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        rcAdapter = new MyRecyclerViewAdapter(DetailBrandActivity.this, listViewItems);
        recyclerView.setAdapter(rcAdapter);
    }
}
