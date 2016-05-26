package com.example.nancy.smartbj.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference存取工具类
 * Created by Nancy on 2016/5/21.
 */
public class SpTools {
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();//提交保存的键值对
    }

    /**
     * @param context      上下我呢
     * @param key          关键字
     * @param defaultValue 设置的默认值
     * @return 从sp取回的boolean值
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defaultValue);
    }

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.CONFIG_FILE_NAME, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();//提交保存的键值对
    }
}
