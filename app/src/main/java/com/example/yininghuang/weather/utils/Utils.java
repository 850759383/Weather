package com.example.yininghuang.weather.utils;

import android.text.TextUtils;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class Utils {

    public static String formatCityName(String name) {
        if (TextUtils.isEmpty(name))
            return null;

        String last = name.substring(name.length() - 1);
        if (last.equals("县") || last.equals("市") || last.equals("区")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }
}
