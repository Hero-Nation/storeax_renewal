package net.heronattion.solowin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.activity.FragmentActivity;
import net.heronattion.solowin.data.BrandProductItem;
import net.heronattion.solowin.network.HttpClient;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * RecyclerViewAdapter + ViewHolder
 * Created by Jake on 2017-06-28.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private Context context;
    private ArrayList<BrandProductItem> items;
    private int lastPosition = -1;


    private int productID;
    private boolean isFavorite;
    private int userID;

    public RecyclerViewAdapter(ArrayList<BrandProductItem> items, Context context) {
        this.items = items;
        this.context = context;
        userID = FragmentActivity.userPkey;

    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView Title;
        public TextView Price;
        public ImageView Photo;
        public ImageView Favorite;

        public ViewHolder(View itemView) {
            super(itemView);

            Title = (TextView) itemView.findViewById(R.id.bp_title);
            Price = (TextView) itemView.findViewById(R.id.bp_price);
            Photo = (ImageView) itemView.findViewById(R.id.bp_photo);
            Favorite = (ImageView) itemView.findViewById(R.id.favoriteProductBtn);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Clicked Position = " + getPosition(), Toast.LENGTH_SHORT).show();
                }
            });
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

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.Title.setText(items.get(position).getTitle());
        holder.Price.setText(items.get(position).getPrice());
        holder.Price.setTag(items.get(position).getProductID());
        holder.Favorite.setTag(items.get(position).getFavorite());
        Glide.with(context).load("http://" + items.get(position).getPhoto()).placeholder(R.drawable.white_background).crossFade().into(holder.Photo);

        if(items.get(position).getFavorite()) holder.Favorite.setImageResource(R.mipmap.selectedproductbutton);
        else holder.Favorite.setImageResource(R.mipmap.favoritproductbutton);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
