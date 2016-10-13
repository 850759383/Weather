package com.example.yininghuang.weather.weather;

import android.content.Context;
import android.location.LocationManager;

import com.example.yininghuang.weather.utils.DataBaseManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yining Huang on 2016/10/13.
 */

@Module
public class WeatherPresenterModule {

    private final Context mContext;
    private final WeatherContract.View mView;

    public WeatherPresenterModule(WeatherContract.View view, Context context) {
        mView = view;
        mContext = context;
    }

    @Provides
    LocationManager provideLocationManager() {
        return (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    @Provides
    DataBaseManager provideDataBaseManager() {
        return DataBaseManager.getInstance();
    }

    @Provides
    WeatherContract.View provideWeatherContractView() {
        return mView;
    }

}
