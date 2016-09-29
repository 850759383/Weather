package com.example.yininghuang.weather.search;

import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.RemoteWeatherService;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.utils.DataBaseManager;
import com.google.gson.Gson;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class SearchPresenter implements SearchContract.Presenter {

    private Subscription subscription;
    private SearchContract.View searchView;

    public SearchPresenter(SearchContract.View searchView) {
        this.searchView = searchView;
    }

    @Override
    public void search(String city) {
        searchView.setRefreshStatus(true);
        subscription = RetrofitHelper.createRetrofit(RemoteWeatherService.class)
                .getWeatherWithName(city, Constants.getKey())
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
                        searchView.setSearchResult(weather);
                        searchView.setRefreshStatus(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        searchView.setSearchResult(null);
                        searchView.setRefreshStatus(false);
                    }
                });
    }

    @Override
    public void saveToDB(WeatherList.Weather weather) {
        City c = new City(weather.getBasicCityInfo().getCityName(),
                Long.toString(System.currentTimeMillis()),
                new Gson().toJson(weather),
                DataBaseManager.UN_POSITIONING);
        City city = DataBaseManager.getInstance().queryCity(weather.getBasicCityInfo().getCityName());
        if (city == null) {
            DataBaseManager.getInstance().insertCity(c);
        } else if (city.getPositioning() != DataBaseManager.POSITIONING) {
            DataBaseManager.getInstance().updateCity(c);
        }
    }

    @Override
    public void onStop() {
        if (subscription != null)
            subscription.unsubscribe();
    }
}
