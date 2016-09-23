package com.example.yininghuang.weather.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class DateFormat {

    public static Long getTime(String text) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.parse(text).getTime();
    }
}
