package net.heronattion.solowin.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Brant on 2017-04-08.
 */

public class UserData implements Serializable {

    public int PKey = -1;
    public String userId = null;
    public String userName = null;

    public static UserData getUserDataFromJson(JSONObject jsonObject) {
        UserData data =  new UserData();

        try {
            data.PKey = jsonObject.getInt("PKey");
            data.userId = jsonObject.getString("userId");
            data.userName = jsonObject.getString("userName");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }

}
