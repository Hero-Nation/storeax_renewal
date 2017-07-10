package net.heronattion.solowin.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.activity.FragmentActivity;
import net.heronattion.solowin.data.BrandList;
import net.heronattion.solowin.network.HttpClient;

import java.util.ArrayList;
import java.util.Comparator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Brant on 2017-02-21.
 */

public class BrandListAdapter extends BaseAdapter {

    private LayoutInflater minflater;
    private ArrayList<BrandList> brandListItems = new ArrayList<BrandList>();
    private ArrayList<BrandList> temp = new ArrayList<BrandList>();
    private BrandList brandListItem;
    private int userID;
    private boolean favoritebrandFlag;
    private Boolean levelFlag = true;
    private int levelTxt = 1;
    private int levelTxtFlag = 0;

    public BrandListAdapter(Context context) {
        minflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Context context = parent.getContext();
        final ViewHolder viewHolder;

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.brand_item, parent, false);

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            viewHolder = new ViewHolder();
            viewHolder.brandll = (LinearLayout) convertView.findViewById(R.id.brandll);
            viewHolder.brandLevel = (TextView) convertView.findViewById(R.id.brandlevel);
            viewHolder.brandLogo = (ImageView) convertView.findViewById(R.id.brandLogo);
            viewHolder.brandName = (TextView) convertView.findViewById(R.id.brandName);
            viewHolder.favoritebrandBtn = (ImageView) convertView.findViewById(R.id.favoritebrandBtn);
            convertView.setTag(viewHolder);

            final BrandList brandListItem = brandListItems.get(position);
            if(brandListItem != null){
                //현재 클릭한 아이템이 해당 객체로 들어감
                viewHolder.favoritebrandBtn.setTag(brandListItem);

                // level 순위
                if(levelTxtFlag < getCount()) {
                    viewHolder.brandLevel.setText(String.valueOf(levelTxt)+"등");
                    levelTxt ++;
                    levelTxtFlag ++;
                }

                // 첫번째 level 버튼만 색보이게
                if(levelFlag) {
                    viewHolder.brandLevel.setBackgroundResource(R.drawable.brand_level_button_one);
                    levelFlag = false;
                }

                // favorite 버튼 표시!
                if(brandListItem.getFavorite()) {
                    viewHolder.favoritebrandBtn.setImageResource(R.mipmap.selectedfavoritebutton);
                } else {
                    viewHolder.favoritebrandBtn.setImageResource(R.mipmap.favoritebutton);
                }
            }

            // 아이템 내 각 위젯에 데이터 반영
//            System.out.println("Adapter TEST =>>>>> " + brandListitem.getLogo());
            Glide.with(context).load("http://" + brandListItem.getLogo()).placeholder(R.drawable.white_background).crossFade().into(viewHolder.brandLogo);
            viewHolder.brandName.setText(brandListItem.getName());

            // 맨 마지막 리스트 아래쪽 마진 없애줌
            if(levelTxtFlag == getCount()) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0,0,0,0);

                viewHolder.brandll.setLayoutParams(layoutParams);
            }

            viewHolder.favoritebrandBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BrandList selectedItem = (BrandList) v.getTag();

                    // 즐겨찾기 해제
                    if(selectedItem.getFavorite()) {
                        // Favorite 정보 바꾸기
                        selectedItem.setFavorite(false);
                        viewHolder.favoritebrandBtn.setImageResource(R.mipmap.favoritebutton);

                        temp.clear();

                        for(int q=0; q < brandListItems.size(); q++) {
                            if(brandListItems.get(q).getFavorite()) {
                                temp.add(brandListItems.get(q));
//                                brandListItems.remove(brandListItems.get(q));
                            }
                        }

                        // 맨 앞으로 이동
//                        brandListItems.addAll(0, temp);

//                        notifyDataSetChanged();
                        Toast.makeText(context,"즐겨찾기가 해제 되었습니다.", Toast.LENGTH_SHORT).show();

                    // 즐겨찾기 등록
                    } else {
                        selectedItem.setFavorite(true);
                        viewHolder.favoritebrandBtn.setImageResource(R.mipmap.selectedfavoritebutton);

                        temp.clear();

//                        Collections.sort(brandListItems, cmpAsc);

                        for(int q=0; q <temp.size(); q++) {
                            if(brandListItems.get(q).getFavorite()) {
                                temp.add(brandListItems.get(q));
//                                brandListItems.remove(brandListItems.get(q));
                            }
                        }

                        // 맨 앞으로 이동 (즐겨찾기 한게 젤 위에)
//                        brandListItems.addAll(0, temp);

//                        notifyDataSetChanged();
                        Toast.makeText(context,"즐겨찾기가 등록 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                    notifyDataSetChanged();
                    // 통신
                    RequestParams params = new RequestParams();
                    userID = FragmentActivity.userPkey;
                    params.put("UserPKey", userID);
                    params.put("BrandKey", selectedItem.getBrandKey());

                    HttpClient.post("android/insertBrand.php", params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String response = new String(responseBody);
                            Log.e("response", response);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return brandListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return brandListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(String brandKey, String name, String logo, Boolean favorite) {

        BrandList item = new BrandList(brandKey, name, logo, favorite);
        brandListItems.add(item);
    }

    // 데이터 정렬 -> 나중에 level 값 넣어서 순서대로..?
    public  static Comparator<BrandList> cmpAsc = new Comparator<BrandList>() {
        @Override
        public int compare(BrandList o1, BrandList o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    public class ViewHolder {
        public int number;
        LinearLayout brandll;
        TextView brandLevel;
        ImageView brandLogo;
        TextView brandName;
        ImageView favoritebrandBtn;
    }
}
