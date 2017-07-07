package net.heronattion.solowin.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import net.heronattion.solowin.R;

import org.florescu.android.rangeseekbar.RangeSeekBar;

/**
 * Created by heronation on 2017-06-08.
 */

public class SearchProductActivity extends BaseActivity {


    private TextView title;
    private RangeSeekBar rangeSeekbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        bindViews();
        setValues();
        setupEvents();

        title = (TextView) findViewById(R.id.searchTitleTxt);

        Intent intent = getIntent();
        String titleTxt = intent.getStringExtra("Search_bar_title");
        title.setText(titleTxt);
    }

    public void bindViews() {
        rangeSeekbar = (RangeSeekBar) findViewById(R.id.rangeSeekbar);
    }

    public void setupEvents() {
        setSearchActionBar();
        setSeekbar();
        setSpinner();
        setColorList();

    }

    public void setValues() {

    }

    public void setSeekbar() {
        rangeSeekbar.setNotifyWhileDragging(true);
        rangeSeekbar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {

                Toast.makeText(getApplicationContext(), "Min Value- " + minValue + " & " + "Max Value- " + maxValue, Toast.LENGTH_SHORT).show();
            }
        });

        final RangeSeekBar<Integer> seekBar = new RangeSeekBar<Integer>(this);
        seekBar.setRangeValues(0, 5);

        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer maxValue) {


            }
        });
    }

    // 내 옷장 보기 스피너 설정
    public void setSpinner() {
        Spinner spinner = (Spinner)findViewById(R.id.myclothset_spinner);

        // 스피너 어댑터 설정
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this,R.array.myclothset_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 스피너 이벤트 발생
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO write click event

                if (position ==0) {
//                    ((TextView) findViewById(R.id.category_item)).setText("");
                }


                Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void setColorList() {
        TextView colorWhite = (TextView) findViewById(R.id.colorWhite);
        TextView colorGray = (TextView) findViewById(R.id.colorGray);
        TextView colorBlack = (TextView) findViewById(R.id.colorBlack);
        TextView colorRed = (TextView) findViewById(R.id.colorRed);
        TextView colorGreen = (TextView) findViewById(R.id.colorGreen);
        TextView colorBlue = (TextView) findViewById(R.id.colorBlue);


        colorWhite.setBackgroundResource(R.drawable.search_color_button);
        GradientDrawable bgWhite = (GradientDrawable)colorWhite.getBackground().getCurrent();
        bgWhite.setColor(Color.parseColor("#ffffff"));

        colorGray.setBackgroundResource(R.drawable.search_color_button);
        GradientDrawable bgGray = (GradientDrawable)colorGray.getBackground().getCurrent();
        bgGray.setColor(Color.parseColor("#eeeeee"));

        colorBlack.setBackgroundResource(R.drawable.search_color_button);
        GradientDrawable bgBlack = (GradientDrawable)colorBlack.getBackground().getCurrent();
        bgBlack.setColor(Color.parseColor("#000000"));

        colorRed.setBackgroundResource(R.drawable.search_color_button);
        GradientDrawable bgRed = (GradientDrawable)colorRed.getBackground().getCurrent();
        bgRed.setColor(Color.parseColor("#ff0000"));

        colorGreen.setBackgroundResource(R.drawable.search_color_button);
        GradientDrawable bgGreen = (GradientDrawable)colorGreen.getBackground().getCurrent();
        bgGreen.setColor(Color.parseColor("#00ff00"));

        colorBlue.setBackgroundResource(R.drawable.search_color_button);
        GradientDrawable bgBlue = (GradientDrawable)colorBlue.getBackground().getCurrent();
        bgBlue.setColor(Color.parseColor("#0000ff"));


    }


    // backBtn 클릭 시 Main page로 이동동
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };
}
