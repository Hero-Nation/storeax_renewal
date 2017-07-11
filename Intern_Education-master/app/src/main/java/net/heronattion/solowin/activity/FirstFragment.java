package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.adapter.BrandListAdapter;
import net.heronattion.solowin.data.BrandList;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by heronation on 2017-06-08.
 */

public class FirstFragment extends Fragment {

    LinearLayout layout;
    ArrayList<BrandList> brandList = new ArrayList<BrandList>();
    private LinearLayout brandDetail;
    BrandListAdapter adapter;
    private ListView listView;

    private String[] mOptionListItem;
    private String[][] mBrandListItem;
    private String[][] mFavoriteListItem;

    private int userID;

    public ImageView favoritebrandBtn;
    public boolean nFavorite;

    public FirstFragment()
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
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_first, container, false);

        brandDetail = (LinearLayout) layout.findViewById(R.id.brandDetail);
        listView = (ListView) layout.findViewById(R.id.lvFisrtFlagment);
        favoritebrandBtn = (ImageView) layout.findViewById(R.id.favoritebrandBtn);
        userID = FragmentActivity.userPkey;
        setupEvents();

        return layout;
    }

    public void setupEvents() {
        setProductItem();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BrandList item = (BrandList) parent.getItemAtPosition(position);
                Intent intent = new Intent(getActivity(), DetailBrandActivity.class);
                intent.putExtra("brandKey", item.getBrandKey());
                intent.putExtra("name", item.getName());
                intent.putExtra("logo", item.getLogo());
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    // 리스트뷰에 데이터 뿌림
    private void setProductItem() {
        RequestParams params = new RequestParams();
        params.put("UserPKey",userID);
        adapter = new BrandListAdapter(getContext());

        HttpClient.post("android/getBrand.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                ParseData parse = new ParseData();

                mOptionListItem = parse.getArrayData(response);

                mBrandListItem = parse.getDoubleArrayData(mOptionListItem[0]);
                mFavoriteListItem = parse.getDoubleArrayData(mOptionListItem[1]);



                // BrandListItem 'name' 'logo' + 'favoriteFlag(T/F)'
                for (int i=0; i< mBrandListItem.length; i++) {

                    Boolean Flag = false;

                    for(int j=0; j < mFavoriteListItem.length; j++ ) {
                        if ((mFavoriteListItem[j][0] != null) && (mBrandListItem[i][0].equals(mFavoriteListItem[j][0]))) {
                            adapter.addItem(mBrandListItem[i][0], mBrandListItem[i][1], mBrandListItem[i][2], true);
                            Flag = true;
                            break;
                        } else {
                            continue;
                        }
                    }

                    if(!Flag) {
                        adapter.addItem(mBrandListItem[i][0], mBrandListItem[i][1], mBrandListItem[i][2], false);
                    }
                }

                listView.setAdapter(adapter);

                adapter.notifyDataSetChanged();
//                adapter.refreshAdapter(BrandListAdapter.brandListItems);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    // DetailBrand가 종료되었을 때 호출됨
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==RESULT_OK) // 액티비티가 정상적으로 종료되었을 경우
//        {
//            int x = 0 ;
//            while (requestCode == x) {
//                if(requestCode == x) {
//                    // DetilBrandActivity 에서 호출한 경우에만 처리
//                    int mPos = data.getIntExtra("pos", 0);
//                    boolean mFavorite = data.getBooleanExtra("favorite", false);
//                    log.d("mFavorite", String.valueOf(mFavorite));
//
//                    if (nFavorite != mFavorite) {
//                        if (mFavorite) {
//                            favoritebrandBtn.setImageResource(R.mipmap.selectedfavoritebutton);
//                        } else {
//                            favoritebrandBtn.setImageResource(R.mipmap.favoritebutton);
//                        }
//                    }
//
//                    //                adapter.notifyDataSetChanged();
//                }
//                x ++;
//            }
//        }
//    }

}
