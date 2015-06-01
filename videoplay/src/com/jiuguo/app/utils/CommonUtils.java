package com.jiuguo.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by leonard on 2015/6/1.
 */
public class CommonUtils {

    /**
     * 读取SharedPreferences
     * @param context
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    /**
     * 设置SharedPreferences
     * @param context
     * @param key
     * @param value
     * @return
     */
    public static void setPrefs(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * 删除SharedPreferences
     * @param context
     * @param key
     * @return
     */
    public static void removePrefs(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(key, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }
}
