package com.example.yininghuang.weather;

import android.app.Application;

import com.example.yininghuang.weather.utils.DataBaseManager;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class WeatherApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DataBaseManager.init(this);
    }
}
