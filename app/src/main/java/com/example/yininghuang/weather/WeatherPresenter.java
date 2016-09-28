package com.example.yininghuang.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.RemoteWeatherService;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.utils.DataBaseManager;
import com.example.yininghuang.weather.utils.DateUtils;
import com.example.yininghuang.weather.utils.SharedPreferenceHelper;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class WeatherPresenter implements WeatherContract.Presenter, LocationListener {

    private final WeatherContract.View weatherView;
    private LocationManager locationManager;
    private String latestLocation;
    private final String cacheDir;
    private final Context context;
    private WeatherList.Weather weather;
    private Boolean isAutoLocation = true;
    private SubscriptionList subscriptionList = new SubscriptionList();

    private static final String PREFERENCE_LOCATION = "location";
    private static final Long UPDATE_TIME_INTERVAL = 1000L * 60 * 60;

    public WeatherPresenter(WeatherContract.View weatherView, Context context) {
        this.weatherView = weatherView;
        this.context = context.getApplicationContext();
        cacheDir = context.getCacheDir().getAbsolutePath();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public WeatherPresenter(WeatherContract.View weatherView, Context context, String requireLocation) {
        this(weatherView, context);
        latestLocation = requireLocation;
        isAutoLocation = false;
    }

    @Override
    public void init() {
        if (isAutoLocation) {
            latestLocation = SharedPreferenceHelper.getStringPreference(context, PREFERENCE_LOCATION);
        }

        String name = formatCityName(latestLocation);
        City city = queryFromDB(name);
        if (city != null) {
            weather = new Gson().fromJson(city.getWeather(), WeatherList.Weather.class);
            weatherView.updateWeather(weather, city.getUpdateTime());
        }

        if (isAutoLocation) {
            requestLocationUpdate();
        }
        if (shouldUpdate()) {
            fetchWeather();
        }
    }

    @Override
    public void update() {
        if (isAutoLocation) {
            requestLocationUpdate();
        } else {
            fetchWeather();
        }
    }

    private void requestLocationUpdate() {
        if (!isAutoLocation)
            return;

        weatherView.setBottomRefresh(true, "正在定位");
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
        }
    }

    private void fetchWeather() {
        String name = formatCityName(latestLocation);
        if (TextUtils.isEmpty(name))
            return;

        weatherView.setBottomRefresh(true, "正在更新");
        Subscription subscription = RetrofitHelper.createRetrofit(RemoteWeatherService.class)
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
                        weatherView.setBottomRefresh(false, null);
                        String currentTime = Long.toString(System.currentTimeMillis());
                        weatherView.updateWeather(weather, currentTime);
                        WeatherPresenter.this.weather = weather;
                        saveToDB(weather, currentTime);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        weatherView.setBottomRefresh(false, null);
                        weatherView.showMessage("网络错误");
                        throwable.printStackTrace();
                    }
                });
        subscriptionList.add(subscription);
    }

    @Override
    public void onStop() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.removeUpdates(this);
        subscriptionList.unsubscribe();
    }

    @Override
    public void onLocationChanged(final Location location) {
        Subscription subscription = locationToCityName(location).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if (s != null) {
                    latestLocation = s;
                    SharedPreferenceHelper.setStringPreference(context, PREFERENCE_LOCATION, s);
                    fetchWeather();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                weatherView.setBottomRefresh(false, null);
                weatherView.showMessage("地理位置解析失败");
            }
        });
        subscriptionList.add(subscription);
    }

    private Observable<String> locationToCityName(final Location location) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Geocoder gcd = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    subscriber.onNext(addresses.get(0).getLocality());
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    private void saveToDB(WeatherList.Weather weather, String updateTime) {
        DataBaseManager.getInstance().addOrUpdateCity(
                new City(
                        weather.getBasicCityInfo().getCityName(),
                        updateTime,
                        new Gson().toJson(weather)
                )
        );
    }

    private City queryFromDB(String cityName) {
        String name = formatCityName(cityName);
        if (TextUtils.isEmpty(name))
            return null;

        return DataBaseManager.getInstance().queryCity(name);
    }

    private Boolean shouldUpdate() {
        if (weather != null) {
            Date weatherTime = DateUtils.getTime(weather.getBasicCityInfo().getUpdateTime().getLocalTime());
            if (weatherTime != null && System.currentTimeMillis() - weatherTime.getTime() > UPDATE_TIME_INTERVAL) {
                return true;
            }
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
