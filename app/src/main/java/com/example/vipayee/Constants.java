


package com.example.vipayee;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
    public static final String BASE_URL = "http://192.168.179.142:5234/";
    public static final String PREF_NAME = "UserPref";
    public static String USER_ID = null;

   // Initialize as null to avoid premature access

    // Set the USER_ID and persist it in SharedPreferences
    public static void setUserUUID(Context context, String userIdString) {
        USER_ID = userIdString;
        if (context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString("USER_ID", userIdString).apply();
        }
    }

    // Get the USER_ID, loading from SharedPreferences if not already set
    public static String getUserUUID(Context context) {
        if (USER_ID == null && context != null) {
            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            USER_ID = prefs.getString("USER_ID", null);
        }
        return USER_ID;
    }
}
