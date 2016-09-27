package com.example.yininghuang.weather;

import android.location.Location;

import com.example.yininghuang.weather.model.Weather.WeatherList;

import rx.Observable;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class WeatherContract {

    interface Presenter {

        void init(String cityName);

        void fetchCityList();

        void fetchWeather(String cityName);

        Observable<String> getCityName(Location location);

        void onStop();

    }

    interface View {

        void setRefresh(Boolean status);

        void updateWeather(WeatherList.Weather weather);
    }
}
