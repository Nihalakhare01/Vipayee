//package com.example.vipayee;
//
//import static android.content.Context.MODE_PRIVATE;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.speech.tts.TextToSpeech;
//import android.util.Log;
//
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.UUID;
//import java.util.logging.Logger;
//
//import okhttp3.Call;
//import okhttp3.Callback;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.Response;
//
//public class Constants {
//
//    private TextToSpeech textToSpeech;
//    public static final String BASE_URL = "http://192.168.236.85:5234/";
//    public static final String PREF_NAME = "UserPref";
////    public static final UUID USER_ID = UUID.fromString("0BFB1683-94A8-4F60-8B93-286C61CC6248");
//
//    // Static variable to store phone number
//
//    public static String UserId ;
//    public static String PHONE_NUMBER = "";
//
//
//
////    public static void savePhoneNumber(Context context, String phoneNumber) {
////        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
////        SharedPreferences.Editor editor = sharedPreferences.edit();
////        editor.putString("PHONE_NUMBER", phoneNumber);
////        editor.apply();
////        PHONE_NUMBER = phoneNumber;  // Store it in static variable
////        Log.d("Constants", "Phone Number Saved: " + PHONE_NUMBER);
////    }
////
////    public static void loadPhoneNumber(Context context) {
////        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
////        PHONE_NUMBER = sharedPreferences.getString("PHONE_NUMBER", "");
////        Log.d("Constants", "Constants Phone Number: " + PHONE_NUMBER);
////    }
//
//
//
//    public String msg = "your USerID" + UserId;
//    private void speakMessage(String msg) {
//        if (textToSpeech != null) {
//            textToSpeech.speak(msg, TextToSpeech.QUEUE_FLUSH, null, null);
//        }
//    }
//
//    public static UUID USER_ID = UUID.fromString(UserId);
//
//
//
//
//}



package com.example.vipayee;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.UUID;

public class Constants {

    public static final String BASE_URL = "http://192.168.179.142:5234/";
    public static final String PREF_NAME = "UserPref";
//    private static final String USER_ID_KEY = "USER_ID";

//    public static String userId = ""; // Store user ID as a string
//    public static final UUID USER_ID = UUID.fromString("0BFB1683-94A8-4F60-8B93-286C61CC6248");
//    public static final UUID USER_UUID = UUID.fromString("0BFB1683-94A8-4F60-8B93-286C61CC6248");
//

// Store as UUID
public static final String USER_ID = "0BFB1683-94A8-4F60-8B93-286C61CC6248";


    // Save the fetched userId in SharedPreferences and convert it to UUID
//    public static void setUserId(Context context, String userIdString) {
//        try {
//            // Store in shared preferences
//            SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString(USER_ID_KEY, userIdString);
//            editor.apply();
//
//            // Store in static variable
//            userId = userIdString;
//            USER_UUID = UUID.fromString(userIdString);
//
//            Log.d("Constants", "User ID Saved: " + USER_UUID.toString());
//        } catch (Exception e) {
//            Log.e("Constants", "Error saving User ID", e);
//        }
//    }
//
//    // Retrieve the stored User ID and convert it back to UUID
//    public static void loadUserId(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
//        userId = sharedPreferences.getString(USER_ID_KEY, "");
//
//        if (!userId.isEmpty()) {
//            try {
//                USER_UUID = UUID.fromString(userId);
//                Log.d("Constants", "User ID Loaded: " + USER_UUID.toString());
//            } catch (Exception e) {
//                Log.e("Constants", "Error loading User ID", e);
//            }
//        }
//    }


}

