package com.example.yininghuang.weather.net;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class Constants {

    private static final String KEY = "41cb48fa8c78499dbe18697c895456f2";

    private static final String BASE_URL = "https://api.heweather.com";

    private static final int CITY_COUNT = 2567;

    private static final char DEGREE_SYMBOL = 0x00B0;

    public static String getKey() {
        return KEY;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }

    public static int getCityCount() {
        return CITY_COUNT;
    }

    public static String getWeatherImage(String weatherCode) {
        return "http://files.heweather.com/cond_icon/" + weatherCode + ".png";
    }

    public static char getDegreeSymbol() {
        return DEGREE_SYMBOL;
    }


}
