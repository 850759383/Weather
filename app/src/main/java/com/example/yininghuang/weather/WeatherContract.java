package com.example.yininghuang.weather;

import android.location.Location;

import com.example.yininghuang.weather.model.Weather.WeatherList;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class WeatherContract {

    interface Presenter {

        void init(String cityName);

        void fetchCityList();

        void fetchWeather(String cityName);

        String getCityName(Location location);

        void onStop();

    }

    interface View {

        void updateWeather(WeatherList.Weather weather);
    }
}
