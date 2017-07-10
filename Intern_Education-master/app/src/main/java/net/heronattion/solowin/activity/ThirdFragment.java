package net.heronattion.solowin.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.adapter.MyRecyclerViewAdapter;
import net.heronattion.solowin.data.BrandProductItem;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;
import static java.lang.Integer.parseInt;
import static net.heronattion.solowin.activity.FragmentActivity.mContext;

/**
 * Created by heronation on 2017-06-08.
 */

public class ThirdFragment extends Fragment {

    private Button changeStyleBtn;
    RecyclerView recyclerView;
    List<BrandProductItem> listViewItems;
//    ArrayList<BrandProductItem> listViewItems;
    LinearLayout layout;
    LinearLayout detailLayout;
    private StaggeredGridLayoutManager gaggeredGridLayoutManager;
    private String mProductStyle;
    private String[] wishList;
    private String[][] parseDataList;
    private int pastVisibleItems;
    private MyRecyclerViewAdapter rcAdapter;
    private int scrollFlag;
    private int userID;
    private boolean isWish;

    public ThirdFragment()
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
        detailLayout = (LinearLayout) inflater.inflate(R.layout.product_detail, container, false);
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_third, container, false);
        bindViews();
        setValues();
        setupEvents();
        getWishList();

        return layout;
    }

    private void getWishList() {
        RequestParams params = new RequestParams();
        params.put("UserPKey", userID);

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
        params.put("ScrollFlag", scrollFlag);

        HttpClient.post("mall/php/tryFilter.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                log.d("response=>>", response);
                ParseData parse = new ParseData();
                parseDataList = parse.getDoubleArrayData(response);

                if(isWish)
                for (int i=0; i< parseDataList.length; i++) {
                    if(parseDataList[i][0].equals(wishList[i]))
                        listViewItems.add(new BrandProductItem(parseDataList[i][1], parseDataList[i][2], parseDataList[i][4], parseDataList[i][5], parseInt(parseDataList[i][0]), true));
                    else
                        listViewItems.add(new BrandProductItem(parseDataList[i][1], parseDataList[i][2], parseDataList[i][4], parseDataList[i][5], parseInt(parseDataList[i][0]), false));
                }

                Log.i("listViewItems for문", listViewItems.size()+"");
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
                setStaggeredGridLayout();
//                rcAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    // StaggeredGridView
    public void setStaggeredGridLayout() {
            recyclerView.setAdapter(rcAdapter);
    }

    public void setupEvents() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalItemCount = gaggeredGridLayoutManager.getItemCount();
                int[] lastVisibleItems = gaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                if (lastVisibleItems != null && lastVisibleItems.length > 0) {
                    pastVisibleItems = lastVisibleItems[0];
                }
                Log.e("total",""+totalItemCount);
                Log.e("past",""+pastVisibleItems);
                Log.e("scroll",""+scrollFlag);

                if (pastVisibleItems==totalItemCount-1) {
                    Toast.makeText(mContext, "test", Toast.LENGTH_SHORT).show();
                    scrollFlag ++;
                    setProductItem();
                }
            }
        });
        changeStyleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "서비스 준비 중입니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setValues() {
        scrollFlag = 0;
        userID = FragmentActivity.userPkey;
        isWish = false;
    }

    public void bindViews() {
        gaggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView = (RecyclerView)layout.findViewById(R.id.recycler_view);
        listViewItems = new ArrayList<BrandProductItem>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(gaggeredGridLayoutManager);
        rcAdapter = new MyRecyclerViewAdapter(getContext(), listViewItems);
        changeStyleBtn = (Button) layout.findViewById(R.id.changeStyleBtn);
    }
}
