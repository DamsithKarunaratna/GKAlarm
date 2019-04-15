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
 * Persistence for arrays in shared preferences
 * See <a href="https://stackoverflow.com/a/7361989">SO answer</a>
 */
public class Persistence {

    public static final String KEY_ALARM_ARRAY = "com.example.gkalarm.data.KEY_ALARM_ARRAY";
    public static final String SHARED_PREF_FILE = "com.example.gkalarm.data.SHARED_PREF_FILE";

    public Persistence() {
    }

    public static void setStringArrayPref(Context context, String key, ArrayList<String> values) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty()) {
            editor.putString(key, a.toString());
        } else {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    public static String storeListInSharedPreferences(Context context, List<AlarmData.AlarmItem> list) {

        SharedPreferences sharedPreferences;
        SharedPreferences.Editor editor;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(list);

        editor = sharedPreferences.edit();
        editor.remove(KEY_ALARM_ARRAY).commit();
        editor.putString(KEY_ALARM_ARRAY, json);
        editor.commit();

        return json;
    }

    public static ArrayList<AlarmData.AlarmItem> getListFromSharedPreferences(Context context) {

        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_FILE, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String response=sharedPreferences.getString(KEY_ALARM_ARRAY , "");
        ArrayList<AlarmData.AlarmItem> alarmItemArrayList = gson.fromJson(response,
                new TypeToken<List<AlarmData.AlarmItem>>(){}.getType());

        return alarmItemArrayList;
    }
}
