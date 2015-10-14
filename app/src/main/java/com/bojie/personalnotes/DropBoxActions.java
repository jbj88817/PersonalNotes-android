package com.bojie.personalnotes;

import android.content.Context;
import android.content.SharedPreferences;

import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;

/**
 * Created by bojiejiang on 10/13/15.
 */
public class DropBoxActions {

    public static void loadAuth(AndroidAuthSession session, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.ACCOUNT_PREFS_NAME, 0);
        String key = preferences.getString(AppConstant.ACCESS_KEY_NAME, null);
        String secret = preferences.getString(AppConstant.ACCESS_SECRET_NAME, null);
        if (key == null || secret == null || key.length() == 0 || secret.length() == 0) {
            return;
        }
        if (key.equals("oath2:")) {
            session.setOAuth2AccessToken(secret);
        } else {
            session.setAccessTokenPair(new AccessTokenPair(key, secret));
        }
    }

    public static void storeAuth(AndroidAuthSession session, Context context) {
        String oAuth2AccessToken = session.getOAuth2AccessToken();
        if (oAuth2AccessToken != null) {
            saveAuth(context, "oauth2:", oAuth2AccessToken);
            return;
        }
        AccessTokenPair oAuth1AccessToken = session.getAccessTokenPair();
        if (oAuth1AccessToken != null){
            saveAuth(context, oAuth1AccessToken.key, oAuth1AccessToken.secret);
        }

    }

    private static void saveAuth(Context context, String accessKey, String accessSecret) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(AppConstant.ACCESS_KEY_NAME, accessKey);
        editor.putString(AppConstant.ACCESS_SECRET_NAME, accessSecret);
        editor.apply();
    }

    public static void clearKeys(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConstant.ACCOUNT_PREFS_NAME, 0);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }

    public static AndroidAuthSession buildSession(Context context) {
        AppKeyPair appKeyPair = new AppKeyPair(AppConstant.APP_KEY, AppConstant.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
        loadAuth(session, context);
        return session;
    }
}
