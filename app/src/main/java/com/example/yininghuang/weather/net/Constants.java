package com.example.yininghuang.weather.net;

import android.content.Context;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class Constants {

    private static final String WEATHER_KEY = "41cb48fa8c78499dbe18697c895456f2";

    private static final String AMAP_KEY = "942b2443eaa7e261747a1aadb93cbaef";

    private static final String WEATHER_BASE_URL = "https://api.heweather.com";

    private static final String AMAP_BASE_URL = "http://restapi.amap.com";

    public static String getAmapBaseUrl() {
        return AMAP_BASE_URL;
    }

    public static String getWeatherKey() {
        return WEATHER_KEY;
    }

    public static String getWeatherBaseUrl() {
        return WEATHER_BASE_URL;
    }

    public static String getAmapKey() {
        return AMAP_KEY;
    }

    public static int getWeatherImage(String weatherCode, Context context) {
        return context.getResources().getIdentifier("img_" + weatherCode, "drawable", "com.example.yininghuang.weather");
    }

}
