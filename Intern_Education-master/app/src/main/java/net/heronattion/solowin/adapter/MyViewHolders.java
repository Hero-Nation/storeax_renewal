package net.heronattion.solowin.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.activity.FragmentActivity;
import net.heronattion.solowin.activity.LoadDetailActivity;
import net.heronattion.solowin.network.HttpClient;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class MyViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    LoadDetailActivity loadDetailActivity;

    public TextView Title;
    public TextView Price;
    public ImageView Photo;
    public ImageView Favorite;
    private int productID;
    private boolean isFavorite;
    private int userID;
    private Context context;

    // 뷰 바인딩 부분을 한번만 하도록
    // https://jungwoon.github.io/jungwoon.github.io/Recycler-View-Practice/

    public MyViewHolders(View itemView) {
        super(itemView);
        context = itemView.getContext();
        itemView.setOnClickListener(this);
        bindViews();
        setValues();
        setupEvents();
    }

    @Override
    public void onClick(View view) {
        String getUrl = (String) Title.getTag();
//        Toast.makeText(view.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//        Log.e("url", getUrl);
//        ThirdFragment.loadDetail(getUrl);
//        Context context =
//        String url = parseDataList[position][5];
//        Log.e("POSITION", url);
        Intent intent = new Intent(context, LoadDetailActivity.class);
        intent.putExtra("URL", getUrl);
        context.startActivity(intent);
    }

    private void setValues(){
        userID = FragmentActivity.userID;
    }

    private void bindViews(){
        loadDetailActivity = new LoadDetailActivity();
        Title = (TextView) itemView.findViewById(R.id.bp_title);
        Price = (TextView) itemView.findViewById(R.id.bp_price);
        Photo = (ImageView) itemView.findViewById(R.id.bp_photo);
        Favorite = (ImageView) itemView.findViewById(R.id.favoriteProductBtn);
    }

    private void setupEvents(){
        Favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                productID = (int)Price.getTag();
                isFavorite = (boolean) Favorite.getTag();

                if(isFavorite){
                    Favorite.setImageResource(R.mipmap.favoritproductbutton);
                    Favorite.setTag(false);
                }else{
                    Favorite.setImageResource(R.mipmap.selectedproductbutton);
                    Favorite.setTag(true);
                }

                //http 통신
                RequestParams params = new RequestParams();
                params.put("UserPKey", userID);
                params.put("ProductID", productID);
                HttpClient.post("android/insertWishList.php", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String response = new String(responseBody);
                        log.d("response=>>", response);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        String response = new String(responseBody);
                        log.e("response=>>", response);
                    }
                });
//                Log.e("is",""+ isFavorite);
//                Log.e("id",""+ productID);
            }
        });
    }
}
