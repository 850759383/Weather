package com.example.yininghuang.weather.search;

import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.net.WeatherService;
import com.example.yininghuang.weather.utils.DataBaseManager;
import com.google.gson.Gson;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class SearchPresenter implements SearchContract.Presenter {

    private Subscription mSubscription;
    private SearchContract.View mSearchView;
    private DataBaseManager mDataBaseManager;
    private RetrofitHelper mRetrofitHelper;

    @Inject
    public SearchPresenter(SearchContract.View searchView, DataBaseManager dataBaseManager, RetrofitHelper retrofitHelper) {
        this.mSearchView = searchView;
        this.mDataBaseManager = dataBaseManager;
        this.mRetrofitHelper = retrofitHelper;
    }

    @Override
    public void search(String city) {
        mSearchView.setRefreshStatus(true);
        mSubscription = mRetrofitHelper.createRetrofit(WeatherService.class, Constants.WEATHER_BASE_URL)
                .getWeatherWithName(city, Constants.WEATHER_KEY)
                .map(new Func1<WeatherList, WeatherList.Weather>() {
                    @Override
                    public WeatherList.Weather call(WeatherList weatherList) {
                        return weatherList.getWeatherList().get(0);
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeatherList.Weather>() {
                    @Override
                    public void call(WeatherList.Weather weather) {
                        if (weather.getStatus().equals("ok")) {
                            mSearchView.setSearchResult(weather);
                            mSearchView.setRefreshStatus(false);
                        } else {
                            mSearchView.setSearchResult(null);
                            mSearchView.setRefreshStatus(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mSearchView.setSearchResult(null);
                        mSearchView.setRefreshStatus(false);
                    }
                });
    }

    @Override
    public void saveToDB(WeatherList.Weather weather) {
        City c = new City(weather.getBasicCityInfo().getCityName(),
                Long.toString(System.currentTimeMillis()),
                new Gson().toJson(weather));
        mDataBaseManager.addOrUpdateCity(c, DataBaseManager.TABLE_SAVED);
    }

    @Override
    public void onStop() {
        if (mSubscription != null)
            mSubscription.unsubscribe();
    }
}
