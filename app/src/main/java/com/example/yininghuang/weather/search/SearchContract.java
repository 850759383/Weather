package com.example.yininghuang.weather.search;

import android.support.annotation.Nullable;

import com.example.yininghuang.weather.model.Weather.WeatherList;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class SearchContract {

    interface Presenter {

        void search(String city);

        void saveToDB(WeatherList.Weather weather);

        void onStop();

    }

    interface View {

        void setSearchResult(@Nullable WeatherList.Weather weather);

        void setRefreshStatus(Boolean active);

    }
}
