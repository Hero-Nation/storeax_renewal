package net.heronattion.solowin.util;

/**
 * Created by hero on 2017-06-15.
 */

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Brant on 2017-04-10.
 * PHP에서 받아온 데이터를 파싱해주는 클래스
 * [[a,b,c,[d,g,t]], [a,b,c,[d,g,t]]], [a,b,c] 와 같은 형식을 파싱해줌
 */

public class ParseData {

    private String mToParsingData;
    private String[][] mParsedDoubleArrayData;
    private String[] mParsedArrayData;
    private ArrayList<String[]> mParsedMergeDataList;

    public ParseData() {

    }

    //    [[a,b,c,[d,g,t]] 형식을 a,b,c,[d,g,t] 2차배열 형식으로 파싱하여 반환함
//    인자 : 2차 배열 JSON Array (String)
    public String[][] getDoubleArrayData(String toParsingData) {

        mToParsingData = toParsingData;

        try {

            JSONArray jsonArray;
            jsonArray = new JSONArray(mToParsingData);

            mParsedDoubleArrayData = new String[jsonArray.length()][jsonArray.getJSONArray(0).length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                for (int j = 0; j < jsonArray.getJSONArray(i).length(); j++) {

                    mParsedDoubleArrayData[i][j] = jsonArray.getJSONArray(i).getString(j);
                    System.out.println("DoubleArrayData ==>>>>>>>>> " + mParsedDoubleArrayData[i][j]);
                }
            }

        } catch (JSONException e) {

            mParsedDoubleArrayData = new String[1][1];
            mParsedDoubleArrayData[0][0] = "";
            System.out.println("DoubleArrayData ==>>>>>>>>> " + mParsedDoubleArrayData[0][0]);
//            e.printStackTrace();

        }

        return mParsedDoubleArrayData;
    }

//    [d,g,t] 형식의 JSON String을 d,g,t 가 들어가 있는 배열형태로 반환해줌

    public String[] getArrayData(String parseData) {

        try {

            JSONArray json = new JSONArray(parseData);
            mParsedArrayData = new String[json.length()];
            System.out.println(json.length());
            for (int i = 0; i < json.length(); i++) {

                mParsedArrayData[i] = json.getString(i);
                System.out.println("ArrayData ==>>>>>>>>> " + mParsedArrayData[i]);
            }

        } catch (JSONException e) {
            mParsedArrayData = new String[1];
            mParsedArrayData[0] = "";
//            e.printStackTrace();
        }

        return mParsedArrayData;
    }


//  2중배열, 배열이 있는 요소 index
//  mProductListItem[i][6]에 스타일 배열이 있음 ["모던", "트렌디" ...]
//  다른 위치에 있는 배열들을 모아서 합친 후 리스트에 뿌려줌

    public ArrayList<String[]> getMergeArrayList(String[][] parsingData, int position) {

        System.out.println("parsingData ==>>> " + parsingData.length);

        mParsedMergeDataList = new ArrayList<>();

        for (int i = 0; i < parsingData.length; i++)
            mParsedMergeDataList.add(i, getArrayData(parsingData[i][position]));

        return mParsedMergeDataList;
    }
}
