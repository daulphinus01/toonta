package com.toonta.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Guillaume on 22/05/2016.
 */
public class ToontaSharedPreferences extends Application{

    private static String TAG = "ToontaSharedPreferences";

    private static String FIRST_USE_SLIDER_VALIDATION = "FIRST_USE_SLIDER_VALIDATION";
    private static String LOGGED_IN = "LOGGED_IN";

    private static String REQUEST_TOKEN = "REQUEST_TOKEN";
    public String requestToken;

    private static String USER_ID = "USER_ID";
    public String userId;

    public static ToontaSharedPreferences toontaSharedPreferences;

    private Context context;

    public static void init(Context context) {
        toontaSharedPreferences = new ToontaSharedPreferences();
        toontaSharedPreferences.context = context;
        if (isLoggedIn()) {
            toontaSharedPreferences.loadUserInformations();
        }
    }

    public static void validateFirstUseSlider() {
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(FIRST_USE_SLIDER_VALIDATION, true);
        editor.apply();
    }

    public static boolean isFistUseSliderValidated() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(FIRST_USE_SLIDER_VALIDATION, false);
    }

    public static void validateLoggedIn(String token, String userId) {
        toontaSharedPreferences.requestToken = token;
        toontaSharedPreferences.userId = userId;
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOGGED_IN, true);
        editor.putString(REQUEST_TOKEN, token);
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public static boolean isLoggedIn() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(LOGGED_IN, false);
    }

    private void loadUserInformations() {
        SharedPreferences sharedPreferences = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        toontaSharedPreferences.requestToken = sharedPreferences.getString(REQUEST_TOKEN, "");
        toontaSharedPreferences.userId = sharedPreferences.getString(USER_ID, "");
    }

    public static void logOut() {
        toontaSharedPreferences.requestToken = "";
        toontaSharedPreferences.userId = "";
        SharedPreferences.Editor editor = toontaSharedPreferences.context.getSharedPreferences(TAG, Context.MODE_PRIVATE).edit();
        editor.putBoolean(LOGGED_IN, false);
        editor.putString(REQUEST_TOKEN, null);
        editor.putString(USER_ID, null);
        editor.apply();
    }
}
