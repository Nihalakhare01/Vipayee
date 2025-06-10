//package com.example.vipayee;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import java.util.UUID;
//
//public class Constants {
//
//    public static final String BASE_URL = "http://192.168.179.142:5234/";
//    public static final String PREF_NAME = "UserPref";
//
////    private static final String USER_ID_KEY = "USER_ID";
//
////    public static String userId = ""; // Store user ID as a string
////    public static final UUID USER_ID = UUID.fromString("0BFB1683-94A8-4F60-8B93-286C61CC6248");
////    public static final UUID USER_UUID = UUID.fromString("0BFB1683-94A8-4F60-8B93-286C61CC6248");
////
//
//// Store as UUID
//public static final String USER_ID = "0BFB1683-94A8-4F60-8B93-286C61CC6248";
//
//
//    // Save the fetched userId in SharedPreferences and convert it to UUID
////    public static void setUserId(Context context, String userIdString) {
////        try {
////            // Store in shared preferences
////            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
////            SharedPreferences.Editor editor = sharedPreferences.edit();
////            editor.putString(USER_ID_KEY, userIdString);
////            editor.apply();
////
////            // Store in static variable
////            userId = userIdString;
////            USER_UUID = UUID.fromString(userIdString);
////
////            Log.d("Constants", "User ID Saved: " + USER_UUID.toString());
////        } catch (Exception e) {
////            Log.e("Constants", "Error saving User ID", e);
////        }
////    }
////
////    // Retrieve the stored User ID and convert it back to UUID
////    public static void loadUserId(Context context) {
////        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
////        userId = sharedPreferences.getString(USER_ID_KEY, "");
////
////        if (!userId.isEmpty()) {
////            try {
////                USER_UUID = UUID.fromString(userId);
////                Log.d("Constants", "User ID Loaded: " + USER_UUID.toString());
////            } catch (Exception e) {
////                Log.e("Constants", "Error loading User ID", e);
////            }
////        }
////    }
//
//
//}
//


package com.example.vipayee;

import android.content.Context;
import android.content.SharedPreferences;

public class Constants {
    public static final String BASE_URL = "http://192.168.190.142:5234/";
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
