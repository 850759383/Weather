package com.example.yininghuang.weather.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;

import com.example.yininghuang.weather.R;
import com.example.yininghuang.weather.model.AMapGeoCode;
import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.LocationAnalysisService;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.net.WeatherService;
import com.example.yininghuang.weather.utils.DataBaseManager;
import com.example.yininghuang.weather.utils.DateUtils;
import com.example.yininghuang.weather.utils.SharedPreferenceHelper;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;

import static com.example.yininghuang.weather.utils.Utils.formatCityName;

/**
 * Created by Yining Huang on 2016/9/23.
 */

public class WeatherPresenter implements WeatherContract.Presenter, LocationListener {

    private final WeatherContract.View mWeatherView;
    private final Context context;
    private LocationManager mLocationManager;
    private WeatherList.Weather mWeather;
    private DataBaseManager mDataBaseManager;
    private Boolean isAutoLocation = true;
    private SubscriptionList subscriptionList = new SubscriptionList();
    private Subscription positionTimer;

    private String district;
    private String city;
    private String tableName;

    public static final String PREFERENCE_DISTRICT = "district";
    public static final String PREFERENCE_CITY = "city";
    private static final long MAX_POSITION_INTERVAL = 1000 * 8;
    private static final long UPDATE_TIME_INTERVAL = 1000 * 60 * 60;

    public WeatherPresenter(WeatherContract.View weatherView, Context context, DataBaseManager dataBaseManager, String district, String city, Boolean isAutoLocation) {
        this.mWeatherView = weatherView;
        this.context = context.getApplicationContext();
        this.mDataBaseManager = dataBaseManager;
        this.isAutoLocation = isAutoLocation;
        this.district = formatCityName(district);
        this.city = city;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (isAutoLocation)
            tableName = DataBaseManager.TABLE_AUTO_LOCATION;
        else
            tableName = DataBaseManager.TABLE_SAVED;
    }

    @Override
    public void init() {
        City city = queryFromDB();
        if (city != null) {
            mWeather = new Gson().fromJson(city.getWeather(), WeatherList.Weather.class);
            mWeatherView.updateWeather(mWeather, city.getUpdateTime());
        }
        update();
    }

    @Override
    public void update() {
        if (shouldUpdate()) {
            fetchWeather();
        }
        requestLocationUpdate();
    }

    private void requestLocationUpdate() {
        if (!isAutoLocation)
            return;

        mWeatherView.setBottomRefresh(true);
        if (checkPermission())
            mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
        positionTimer = Observable.timer(MAX_POSITION_INTERVAL, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            if (checkPermission())
                                onLocationChanged(mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                        }
                    });
    }

    private void fetchWeather() {
        if (TextUtils.isEmpty(district))
            return;

        mWeatherView.setBottomRefresh(true);
        Subscription subscription = getWeatherObservable(district)
                .flatMap(new Func1<WeatherList.Weather, Observable<WeatherList.Weather>>() {
                    @Override
                    public Observable<WeatherList.Weather> call(WeatherList.Weather weather) {
                        if (weather.getStatus().equals("unknown city") && city != null)
                            return getWeatherObservable(city);
                        return Observable.just(weather);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeatherList.Weather>() {
                    @Override
                    public void call(WeatherList.Weather weather) {
                        if (weather.getStatus().equals("ok")) {
                            mWeather = weather;
                            mWeatherView.setBottomRefresh(false);
                            String currentTime = Long.toString(System.currentTimeMillis());
                            saveToDB(weather, currentTime);
                            mWeatherView.updateWeather(weather, currentTime);
                        } else {
                            mWeatherView.setBottomRefresh(false);
                            mWeatherView.showMessage(weather.getStatus());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        mWeatherView.setBottomRefresh(false);
                        mWeatherView.showMessage(context.getString(R.string.network_error));
                        throwable.printStackTrace();
                    }
                });
        subscriptionList.add(subscription);
    }

    private Observable<WeatherList.Weather> getWeatherObservable(String cityName) {
        return RetrofitHelper.createRetrofit(WeatherService.class, Constants.WEATHER_BASE_URL)
                .getWeatherWithName(cityName, Constants.WEATHER_KEY)
                .map(new Func1<WeatherList, WeatherList.Weather>() {
                    @Override
                    public WeatherList.Weather call(WeatherList weatherList) {
                        return weatherList.getWeatherList().get(0);
                    }
                });
    }

    @Override
    public void onStop() {
        if (checkPermission())
            mLocationManager.removeUpdates(this);
        subscriptionList.unsubscribe();
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (positionTimer != null) {
            positionTimer.unsubscribe();
            positionTimer = null;
        }
        if (location == null) {
            mWeatherView.setBottomRefresh(false);
            return;
        }

        Subscription subscription = analysisLocation(location)
                .map(new Func1<AMapGeoCode, Map<String, String>>() {
                    @Override
                    public Map<String, String> call(AMapGeoCode amapGeoCode) {
                        if (amapGeoCode.getInfocode().equals("10000")) {
                            Map<String, String> map = new HashMap<>();
                            map.put("district", formatCityName(amapGeoCode.getRegeocode().getAddressComponent().getDistrict()));
                            map.put("city", formatCityName(amapGeoCode.getRegeocode().getAddressComponent().getCity()));
                            return map;
                        }
                        return null;
                    }
                }).subscribe(new Action1<Map<String, String>>() {
                    @Override
                    public void call(Map<String, String> s) {
                        if (s == null)
                            return;

                        city = s.get("city");
                        SharedPreferenceHelper.setStringPreference(context, PREFERENCE_CITY, city);
                        String d = s.get("district");
                        if (d != null && !d.equals(district)) {
                            district = d;
                            fetchWeather();
                            SharedPreferenceHelper.setStringPreference(context, PREFERENCE_DISTRICT, district);
                        } else {
                            mWeatherView.setBottomRefresh(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        mWeatherView.setBottomRefresh(false);
                        mWeatherView.showMessage(context.getString(R.string.analysis_location_failed));
                    }
                });
        subscriptionList.add(subscription);
    }

    private Observable<AMapGeoCode> analysisLocation(Location location) {
        Double lng = location.getLongitude();
        Double lat = location.getLatitude();
        return RetrofitHelper.createRetrofit(LocationAnalysisService.class, Constants.AMAP_BASE_URL)
                .analysis(Constants.AMAP_KEY, lng.toString() + "," + lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void saveToDB(WeatherList.Weather weather, String updateTime) {
        mDataBaseManager.addOrUpdateCity(new City(weather.getBasicCityInfo().getCityName(), updateTime, new Gson().toJson(weather)), tableName);
    }

    private City queryFromDB() {
        if (district != null) {
            City d = mDataBaseManager.queryCity(district, tableName);
            if (d != null)
                return d;
        }
        if (city != null) {
            mDataBaseManager.queryCity(city, tableName);
        }
        return null;
    }

    private Boolean shouldUpdate() {
        return mWeather == null ||
                System.currentTimeMillis() - DateUtils.getTime(mWeather.getBasicCityInfo().getUpdateTime().getLocalTime()).getTime() > UPDATE_TIME_INTERVAL;
    }

    private Boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
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
