package com.example.yininghuang.weather.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class SharedPrefrencesHelper {

    private static final String FILE_NAME = "default";

    public static void setValue(Context context, String name, Object object) {
        SharedPreferences preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        if (object instanceof Boolean) {
            preferences.edit().putBoolean(name, (Boolean) object).apply();
        } else if (object instanceof Integer) {
            preferences.edit().putInt(name, (Integer) object).apply();
        } else if (object instanceof Long) {
            preferences.edit().putLong(name, (Long) object).apply();
        } else if (object instanceof Float) {
            preferences.edit().putFloat(name, (Float) object).apply();
        } else if (object instanceof String) {
            preferences.edit().putString(name, (String) object).apply();
        } else {
            throw new IllegalArgumentException("null or type mismatch");
        }
    }
    
}
