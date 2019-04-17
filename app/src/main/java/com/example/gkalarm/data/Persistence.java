package com.example.gkalarm.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Damsith Karunaratna
 *
 * Persistence for arrays in shared preferences
 * See <a href="https://stackoverflow.com/a/7361989">SO answer</a>
 */
public class Persistence {

    private static final String KEY_ALARM_ARRAY = "com.example.gkalarm.data.KEY_ALARM_ARRAY";
    private static final String SHARED_PREF_FILE = "com.example.gkalarm.data.SHARED_PREF_FILE";

    public Persistence() {
    }

    /**
     * Method to convert ArrayList to json string and store in shared preferences
     *
     * @param context context to get shared prefs
     * @param list list to be stored
     * @return Gson parsed list of values
     */
    public static String storeListInSharedPreferences(Context context, List<AlarmData.AlarmItem> list) {

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor = sharedPreferences.edit();
        editor.remove(KEY_ALARM_ARRAY).commit();
        editor.putString(KEY_ALARM_ARRAY, json);
        editor.apply();

        return json;
    }

    /**
     * Method to convert stored json string into an ArrayList
     *
     * @param context to get shared preferences
     * @return
     */
    public static ArrayList<AlarmData.AlarmItem> getListFromSharedPreferences(Context context) {

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response=sharedPreferences.getString(KEY_ALARM_ARRAY , "");

        return gson.fromJson(response,
                new TypeToken<List<AlarmData.AlarmItem>>(){}.getType());
    }
}
