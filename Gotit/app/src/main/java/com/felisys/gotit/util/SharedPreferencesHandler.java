package com.felisys.gotit.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.util.Log;

import java.text.MessageFormat;

/**
 * Created by gauravkanade on 1/21/17.
 */

public class SharedPreferencesHandler {

    final static String FILENAME = "GotItPreferences";
    final static String EMAIL_TAG = "userEmail";
    final static String NAME_TAG = "userName";
    final static String LAST_USED_TIME = "lastUsedTime";
    final static String USER_ID = "userId";

    static SharedPreferences sharedPreferences;

    public static boolean isUserLoggedIn(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME, 0);
        if (sharedPreferences.contains(EMAIL_TAG)) {
            return true;
        }
        return false;
    }

    public static void saveUserInfo(Context context, String userId, String userName, String email) {
        Log.i(Utility.LOG_TAG, MessageFormat.format(">>SharedPreferenceHandler.saveUserInfo() - userName={0}, userId={1}, email={2}", userName, userId, email));
        sharedPreferences = context.getSharedPreferences(FILENAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_ID, userId);
        editor.putString(EMAIL_TAG, email);
        editor.putString(NAME_TAG, userName);
        editor.putLong(LAST_USED_TIME, System.currentTimeMillis());
        editor.commit();
    }

    public static String getStoredUserId(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME, 0);
        return sharedPreferences.getString(USER_ID, null);
    }
}
