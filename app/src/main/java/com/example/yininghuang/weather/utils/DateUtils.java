package com.example.yininghuang.weather.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class DateUtils {

    private static final String[] week = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    public static Date getTime(String text) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return new Date(dateFormat.parse(text).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Date getTime2(String text) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            return new Date(dateFormat.parse(text).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Boolean isToday(Date yourDate) {
        Calendar yourCalendar = Calendar.getInstance();
        yourCalendar.setTime(yourDate);
        Calendar currentCalendar = Calendar.getInstance();
        return yourCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                yourCalendar.get(Calendar.DAY_OF_MONTH) == currentCalendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Boolean isYesterday(Date yourDate) {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DAY_OF_MONTH, -1);
        Calendar date = Calendar.getInstance();
        date.setTime(yourDate);
        return yesterday.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_MONTH) == date.get(Calendar.DAY_OF_MONTH);
    }

    public static Boolean isThisWeek(Date yourDate) {
        Calendar currentCalendar = Calendar.getInstance();
        Calendar yourCalendar = Calendar.getInstance();
        yourCalendar.setTime(yourDate);
        return yourCalendar.get(Calendar.WEEK_OF_YEAR) == currentCalendar.get(Calendar.WEEK_OF_MONTH) &&
                yourCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR);
    }

    public static Boolean isThisYear(Date yourDate) {
        Calendar yourCalendar = Calendar.getInstance();
        yourCalendar.setTime(yourDate);
        Calendar currentCalendar = Calendar.getInstance();
        return yourCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR);
    }

    public static String getWeekOfYear(Date yourDate) {
        Calendar yourCalendar = Calendar.getInstance();
        yourCalendar.setTime(yourDate);
        int dayOfWeek = yourCalendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0)
            dayOfWeek = 0;
        return week[dayOfWeek];
    }

    public static String format(Date yourDate) {
        long interval = System.currentTimeMillis() - yourDate.getTime();
        if (interval < 1000 * 60) {
            return "刚刚";
        } else if (interval < 1000 * 60 * 60) {
            return (int) (interval / 60000) + "分钟前";
        } else if (isToday(yourDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            return dateFormat.format(yourDate);
        } else if (isYesterday(yourDate)) {
            return "昨天";
        } else if (isThisYear(yourDate)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM月dd日");
            return dateFormat.format(yourDate);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            return dateFormat.format(yourDate);
        }
    }
}
