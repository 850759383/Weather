package com.example.yininghuang.weather.weather;

import com.example.yininghuang.weather.model.Weather.WeatherList;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class WeatherContract {

    interface Presenter {

        void init(String district, String city, Boolean isAutoLocation);

        void update();

        void onStop();

    }

    interface View {

        void updateWeather(WeatherList.Weather weather, String updateTime);

        void setBottomRefresh(Boolean status);

        void showMessage(String msg);
    }
}
