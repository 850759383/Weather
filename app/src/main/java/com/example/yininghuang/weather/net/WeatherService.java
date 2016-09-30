package com.example.yininghuang.weather.net;

import com.example.yininghuang.weather.model.Weather.WeatherList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public interface WeatherService {
    @GET("/x3/weather")
    Observable<WeatherList> getWeatherWithId(@Query("cityid") String cityId, @Query("key") String key);

    @GET("/x3/weather")
    Observable<WeatherList> getWeatherWithName(@Query("city") String cityName, @Query("key") String key);
}
