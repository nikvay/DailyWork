package com.nikvay.daily_work.shared_pref;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference {

    private static String SHARED_PREF_NAME = "Fast Connect";
    public static String IS_LOGIN = "IS_LOGIN";
    public static String USER_FULL_NAME = "USER_FULL_NAME";
    public static String USER_NAME = "USER_NAME";
    public static String USER_PASSWORD = "USER_PASSWORD";
    public static String USER_EMAIL = "USER_EMAIL";
    public static String USER_CONTACT = "USER_CONTACT";
    public static String UID = "UID";
    public static String U_TYPE = "U_TYPE";
    private static String DEVICE_TOKEN = "DEVICE_TOKEN";

    public static void putDeviceToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(DEVICE_TOKEN, token)
                .apply();
    }

    public static String getDeviceToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(DEVICE_TOKEN, "");
    }

    public static void putUserID(Context context, String user_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(UID, user_id)
                .apply();
    }


    public static String getUserID(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(UID, "");
    }

 public static void putUserType(Context context, String user_id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                .putString(U_TYPE, user_id)
                .apply();
    }


    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(U_TYPE, "");
    }


}
