package com.example.yininghuang.weather.weather;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yining Huang on 2016/10/13.
 */

@Module
public class WeatherPresenterModule {

    private final WeatherContract.View mView;

    public WeatherPresenterModule(WeatherContract.View view) {
        mView = view;
    }

    @Provides
    WeatherContract.View provideWeatherContractView() {
        return mView;
    }

}
