package net.heronattion.solowin.util;

import android.content.Context;
import android.content.SharedPreferences;

import net.heronattion.solowin.data.UserData;

/**
 * Created by Brant on 2017-04-08.
 */

public class ContextUtil {

    private static final String PREFERENCE_NAME = "SolowinPreferences";
    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String APP_INTRO_OK = "APP_INTRO_OK";
    private static final String USER_VALIDATE_KEY = "USER_VALIDATE_KEY";
    private static final String FB_SESSION = "FB_SESSION";


    public static UserData loginUser = null;

    public static boolean isUserLoggedin(Context context) {

        return loginUser != null;
    }


    public static void setUserData(Context context, UserData userData) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        prefs.edit().putInt(USER_ID, userData.PKey).commit();
        prefs.edit().putString(USER_NAME, userData.userName).commit();
        loginUser = userData;
    }

    public static UserData getMyUserData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        if (loginUser == null) {
            loginUser = new UserData();
            loginUser.PKey = prefs.getInt(USER_ID, -1);
            loginUser.userName = prefs.getString(USER_NAME, "");
        }
        return loginUser;
    }

    public static void setAPP_INTRO_OK(Context context, boolean ok) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        prefs.edit().putBoolean(APP_INTRO_OK, ok).commit();
    }

    public static boolean getAPP_INTRO_OK(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);

        return prefs.getBoolean(APP_INTRO_OK, false);
    }
}
