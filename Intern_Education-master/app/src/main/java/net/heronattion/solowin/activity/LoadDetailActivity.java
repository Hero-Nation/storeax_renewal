package net.heronattion.solowin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import net.heronattion.solowin.R;


/**
 * Created by Administrator on 2017-07-04.
 */

public class LoadDetailActivity extends BaseActivity{
    WebView mWebView;
    private Intent intent;
    private String URL;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);
        bindViews();
        setValues();
        setupEvents();
        goUrl(URL);
    }


    @Override
    public void bindViews() {
        super.bindViews();
        mWebView = (WebView) findViewById(R.id.detailView);
        mWebView.setBackgroundColor(0);
        mWebView.setHorizontalScrollBarEnabled(false); //가로 스크롤
        mWebView.setVerticalScrollBarEnabled(false); //세로 스크롤
        //mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); //스크롤 노출타입

        //HTML을 파싱하여 웹뷰에서 보여주거나 하는 작업에서
        //width , height 가 화면 크기와 맞지 않는 현상이 발생한다
        //이를 잡아주기 위한 코드
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);

        //캐시파일 사용 금지(운영중엔 주석처리 할 것)
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        //zoom 허용
        //mWebView.getSettings().setBuiltInZoomControls(true);
        //mWebView.getSettings().setSupportZoom(true);

        //javascript의 window.open 허용
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        //javascript 허용
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        //meta태그의 viewport사용 가능
        mWebView.getSettings().setUseWideViewPort(true);
    }

    @Override
    public void setupEvents() {
        super.setupEvents();

    }

    @Override
    public void setValues() {
        super.setValues();
        intent = getIntent();
        URL = intent.getStringExtra("URL");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()){
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WishWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            view.loadUrl(url);
            return true;
        }
    }

    public void goUrl(String link){
        String url = link;
        mWebView.loadUrl(url);
        mWebView.setWebViewClient(new WishWebViewClient());
    }
}
