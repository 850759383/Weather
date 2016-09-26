package com.example.yininghuang.weather;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.text.TextUtils;

import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.RemoteWeatherService;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class WeatherPresenter implements WeatherContract.Presenter {

    private final WeatherContract.View weatherView;

    private final String cacheDir;

    private final Context context;

    public WeatherPresenter(WeatherContract.View weatherView, Context context) {
        this.weatherView = weatherView;
        this.context = context.getApplicationContext();
        cacheDir = context.getCacheDir().getAbsolutePath();
    }

    @Override
    public void init(String cityName) {
        String name = formatCityName(cityName);
        try {
            WeatherList.Weather cache = loadFromCache(name);
            if (cache != null)
                weatherView.updateWeather(cache);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fetchWeather(name);
        }
    }

    @Override
    public void fetchCityList() {

    }

    @Override
    public void fetchWeather(String cityName) {
        String name = formatCityName(cityName);
        if (TextUtils.isEmpty(name))
            return;

        RetrofitHelper.createRetrofit(RemoteWeatherService.class)
                .getWeatherWithName(name, Constants.getKey())
                .map(new Func1<WeatherList, WeatherList.Weather>() {
                    @Override
                    public WeatherList.Weather call(WeatherList weatherList) {
                        return weatherList.getWeatherList().get(0);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeatherList.Weather>() {
                    @Override
                    public void call(WeatherList.Weather weather) {
                        System.out.println("更新天气");
                        weatherView.updateWeather(weather);
                        try {
                            saveToCache(weather);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
    }

    private String formatCityName(String name) {
        if (TextUtils.isEmpty(name))
            return null;

        String last = name.substring(name.length() - 1);
        if (last.equals("县") || last.equals("市") || last.equals("区")) {
            name = name.substring(0, name.length() - 1);
        }
        return name;
    }

    private void saveToCache(WeatherList.Weather weather) throws IOException {
        File file = new File(cacheDir, weather.getBasicCityInfo().getCityName());
        if (!file.exists())
            file.createNewFile();

        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(new Gson().toJson(weather).getBytes());
        fos.close();
    }

    private WeatherList.Weather loadFromCache(String cityName) throws IOException {
        String name = formatCityName(cityName);
        if (TextUtils.isEmpty(name))
            return null;

        File file = new File(cacheDir, cityName);
        JsonReader reader = new JsonReader(new FileReader(file));
        return new Gson().fromJson(reader, WeatherList.Weather.class);
    }

    @Override
    public String getCityName(Location location) {
        try {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onStop() {

    }
}
