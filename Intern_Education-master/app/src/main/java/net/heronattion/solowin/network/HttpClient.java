package net.heronattion.solowin.network;

/**
 * Created by Brant on 2017-04-07.
 */

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

// GET / POST 방식의 통신을 손쉽게 할 수 있는 클래스
// Login, List 등 모든곳에서 아주 유용하게 사용중이니 참고하여 사용하면 될듯
// 대부분 post방식만 사용함

public class HttpClient {

    private static final String BASE_URL = "http://heronation.net/";
    private static AsyncHttpClient client = new AsyncHttpClient();

    public static AsyncHttpClient getInstance() {

        return HttpClient.client;

    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responHandler) {

        client.get(getAbsoluteUrl(url), params, responHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responHandler) {

        client.post(getAbsoluteUrl(url), params, responHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {

        return BASE_URL + relativeUrl;
    }

}
