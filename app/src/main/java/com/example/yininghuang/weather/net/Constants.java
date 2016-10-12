package com.example.yininghuang.weather.net;

import android.content.Context;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class Constants {

    public static final String WEATHER_KEY = "41cb48fa8c78499dbe18697c895456f2";

    public static final String AMAP_KEY = "942b2443eaa7e261747a1aadb93cbaef";

    public static final String WEATHER_BASE_URL = "https://api.heweather.com";

    public static final String AMAP_BASE_URL = "http://restapi.amap.com";

    public static int getWeatherImage(String weatherCode, Context context) {
        return context.getResources().getIdentifier("img_" + weatherCode, "drawable", "com.example.yininghuang.weather");
    }

}
