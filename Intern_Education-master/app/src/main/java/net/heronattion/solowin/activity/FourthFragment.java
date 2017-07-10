package net.heronattion.solowin.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import net.heronattion.solowin.R;
import net.heronattion.solowin.network.HttpClient;
import net.heronattion.solowin.util.ParseData;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.EncodingUtils;

import static com.loopj.android.http.AsyncHttpClient.log;
import static java.lang.Integer.parseInt;

/**
 * Created by heronation on 2017-06-08.
 */

public class FourthFragment extends Fragment {
    LinearLayout layout;
    WebView webView;
    LinearLayout addMyClothe;
    Context context;

    private String[][] parseDataList;
    private int tempCategoryID;
    private int categoryID;
    private int userID;
    private int gender;
    private int manFlag;
    private int womanFlag;

    public FourthFragment()
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
        layout = (LinearLayout) inflater.inflate(R.layout.fragment_fourth, container, false);
        gender = 1;
        final PersistentCookieStore myCookieStore = new PersistentCookieStore(getContext());
        for(int i=0; i < myCookieStore.getCookies().size(); i++) {
            if(myCookieStore.getCookies().get(i).getName().equals("UserGENDER"))
                gender = parseInt(myCookieStore.getCookies().get(i).getValue());
        }
        Log.e("gender", ""+gender);
        bindViews();
        setupEvents();
        goURL(layout);
        return layout;
    }

    public void goURL(View view){
        String url = "http://heronation.net/android/webView/myCloset.html".toString();
        userID = FragmentActivity.userPkey;
        String postData = "UserPKey="+userID+"&CategoryID="+tempCategoryID;
        if(categoryID != 0) postData = "UserPKey="+userID+"&CategoryID="+categoryID;

//        Log.i("URL","Opening URL :"+url);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.postUrl(url, EncodingUtils.getBytes(postData, "BASE64"));
    }

    public void setupEvents(){
        setClickedAddClothe();
        getCategory();
    }
    public void bindViews(){
        webView = (WebView)layout.findViewById(R.id.webView);
        addMyClothe = (LinearLayout)layout.findViewById(R.id.addMyClothe);
        context = getContext();
        categoryID = 0;
    }

    private void setClickedAddClothe(){
        addMyClothe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gender==1){
                    manFlag=1;
                    womanFlag=0;
                }else if(gender==2){
                    manFlag=0;
                    womanFlag=1;
                }else{
                    manFlag=1;
                    womanFlag=0;
                }
                Intent intent = new Intent(getActivity(),SignupInformation2Activity.class);

                intent.putExtra("manFlag",manFlag);
                intent.putExtra("womanFlag",womanFlag);
                startActivity(intent);

                Log.d("addClothes","addClothes");
            }
        });
    }
    private void getCategory(){
        RequestParams params = new RequestParams();
        params.put("Gender", gender);
        HttpClient.post("android/getCategory.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                log.d("responseC=>>", response);
                ParseData parse = new ParseData();
                parseDataList = parse.getDoubleArrayData(response);
                setSpinner();
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String response = new String(responseBody);
                log.d("response=>>", response);
            }
        });
    }
    private void setSpinner() {
        Spinner spinner = (Spinner)layout.findViewById(R.id.myClosetSpinner);
        ArrayList<String> items =  new ArrayList<String>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, items);
        tempCategoryID = parseInt(parseDataList[0][0]);
        for (int i=0; i< parseDataList.length; i++) {
            adapter.add(parseDataList[i][1]);
        }
        adapter.notifyDataSetChanged();

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // 스피너 이벤트 발생
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO write click event
                String categoryName = parent.getItemAtPosition(position).toString();
                for (int i=0; i< parseDataList.length; i++) {
                    if(categoryName.equals(parseDataList[i][1]))
                        categoryID = parseInt(parseDataList[i][0]);
                }
                Log.e("position", ""+categoryID);
                goURL(layout);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("position", "nothing");
                // TODO Auto-generated method stub
            }
        });
    }
}
