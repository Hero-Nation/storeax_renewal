package net.heronattion.solowin.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;


import net.heronattion.solowin.R;
import net.heronattion.solowin.data.BrandProductItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heronation on 2017-06-14.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolders> {

    private ArrayList<BrandProductItem> itemList;
    private Context context;

    public MyRecyclerViewAdapter(Context context, ArrayList<BrandProductItem> itemList) {
        this.itemList = itemList;
        this.context = context;
    }

    // create new view
    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        // set the view's size, margins, paddings and layout parameters

        MyViewHolders rcv = new MyViewHolders(layoutView);
        return rcv;
    }

    // getView
    @Override
    public void onBindViewHolder(MyViewHolders holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.Title.setText(itemList.get(position).getTitle());
        holder.Title.setTag(itemList.get(position).getUrl());
        holder.Price.setText(itemList.get(position).getPrice());
        holder.Price.setTag(itemList.get(position).getProductID());
        holder.Favorite.setTag(itemList.get(position).getFavorite());

        if(itemList.get(position).getFavorite()) holder.Favorite.setImageResource(R.mipmap.selectedproductbutton);
        else holder.Favorite.setImageResource(R.mipmap.favoritproductbutton);


        Glide.with(context).load("http://" + itemList.get(position).getPhoto()).placeholder(R.drawable.white_background).crossFade().into(holder.Photo);
    }

    @Override
    public int getItemCount() {
        return null!=itemList? itemList.size():0;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}