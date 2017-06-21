package net.heronattion.solowin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.heronattion.solowin.R;
import net.heronattion.solowin.data.StoryData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Brant on 2017-04-08.
 */

public class StoryAdapter extends ArrayAdapter<StoryData> {

    Context mContext = null;
    ArrayList<StoryData> mList = null;
    LayoutInflater inf = null;

    public StoryAdapter(Context context, ArrayList<StoryData> list) {

        super(context, R.layout.story_list_item, R.id.titleTxt, list);

        mContext = context;
        mList = list;
        inf = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row = convertView;

        if (row == null) {

            row = inf.inflate(R.layout.story_list_item, null);
        }

        TextView dateTxt = (TextView) row.findViewById(R.id.dateTxt);
        TextView titleTxt = (TextView) row.findViewById(R.id.titleTxt);
        TextView locationTxt = (TextView) row.findViewById(R.id.locationTxt);
        ImageView backImgeView = (ImageView) row.findViewById(R.id.backImgeView);


        StoryData data = mList.get(position);

        dateTxt.setText(data.storyDate.toDisplayDateTimeString());
        titleTxt.setText(data.title);
        locationTxt.setText(data.location);


        return row;
    }
}
