package com.liveonsolutions.binance;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrefUtil {

    public static final String PREFS_BOX_LIST = "__PREFS_STICKERS_LIST__" ;
    public static final String PREFS_MY_BOX_LIST = "PREFS_MY_BOX_LIST" ;
    public static final String PREFS_OPEN__BOX_LIST = "PREFS_OPEN__BOX_LIST" ;
    public static final String PREFS_STATEMENT = "PREFS_STATEMENT" ;

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        if(context != null){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            final SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, value);
            editor.apply();
        }
    }

    public static void saveToPrefs(Context context, String key, long value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static String getFromPrefs(Context context, String key) {
        String defaultValue = "";
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    // MÉTODO USADO PARA PERSISTIR LISTAS DE OBJETOS NO SHARED PREFERENCES (DE QUALQUER TIPO)
    public static <T> void saveToPrefs (Context context, String key, List<T> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static <T> List<T> getToPrefs(Context context, String key, Class<T[]> clazz){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        T[] arr = new Gson().fromJson(json, clazz);
        return arr != null ? Arrays.asList(arr) : new ArrayList<T>();
    }

    public static long getFromLongPrefs(Context context, String key) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getLong(key, 0);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean getFromBooleanPrefs(Context context, String key) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getBoolean(key, false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}