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

import java.util.Date;
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

    private final WeatherContract.View weatherView;
    private final Context context;
    private LocationManager locationManager;
    private WeatherList.Weather weather;
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

    public WeatherPresenter(WeatherContract.View weatherView, Context context, String district, String city, Boolean isAutoLocation) {
        this.weatherView = weatherView;
        this.context = context.getApplicationContext();
        this.isAutoLocation = isAutoLocation;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.district = formatCityName(district);
        this.city = city;
        if (isAutoLocation)
            tableName = DataBaseManager.TABLE_AUTO_LOCATION;
        else
            tableName = DataBaseManager.TABLE_SAVED;
    }

    @Override
    public void init() {
        City city = queryFromDB();
        if (city != null) {
            weather = new Gson().fromJson(city.getWeather(), WeatherList.Weather.class);
            weatherView.updateWeather(weather, city.getUpdateTime());
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

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            weatherView.setBottomRefresh(true);
            positionTimer = Observable.timer(MAX_POSITION_INTERVAL, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            System.out.println("超时");
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                onLocationChanged(locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER));
                            }
                        }
                    });
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
        }
    }

    private void fetchWeather() {
        if (TextUtils.isEmpty(district))
            return;

        weatherView.setBottomRefresh(true);
        Subscription subscription = getWeatherObserver(district)
                .flatMap(new Func1<WeatherList.Weather, Observable<WeatherList.Weather>>() {
                    @Override
                    public Observable<WeatherList.Weather> call(WeatherList.Weather weather) {
                        if (weather.getStatus().equals("unknown city") && city != null)
                            return getWeatherObserver(city);
                        return Observable.just(weather);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<WeatherList.Weather>() {
                    @Override
                    public void call(WeatherList.Weather weather) {
                        if (weather.getStatus().equals("ok")) {
                            weatherView.setBottomRefresh(false);
                            String currentTime = Long.toString(System.currentTimeMillis());
                            WeatherPresenter.this.weather = weather;
                            saveToDB(weather, currentTime);
                            weatherView.updateWeather(weather, currentTime);
                        } else {
                            weatherView.setBottomRefresh(false);
                            weatherView.showMessage(weather.getStatus());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        weatherView.setBottomRefresh(false);
                        weatherView.showMessage("网络错误");
                        throwable.printStackTrace();
                    }
                });
        subscriptionList.add(subscription);
    }

    private Observable<WeatherList.Weather> getWeatherObserver(String cityName) {
        return RetrofitHelper.createRetrofit(WeatherService.class, Constants.getWeatherBaseUrl())
                .getWeatherWithName(cityName, Constants.getWeatherKey())
                .map(new Func1<WeatherList, WeatherList.Weather>() {
                    @Override
                    public WeatherList.Weather call(WeatherList weatherList) {
                        return weatherList.getWeatherList().get(0);
                    }
                });
    }

    @Override
    public void onStop() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            locationManager.removeUpdates(this);
        subscriptionList.unsubscribe();
    }

    @Override
    public void onLocationChanged(final Location location) {
        if (positionTimer != null) {
            positionTimer.unsubscribe();
            positionTimer = null;
        }
        if (location == null) {
            weatherView.setBottomRefresh(false);
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
                        System.out.println(s);
                        if (d != null && !d.equals(district)) {
                            district = d;
                            fetchWeather();
                            SharedPreferenceHelper.setStringPreference(context, PREFERENCE_DISTRICT, district);
                        } else {
                            weatherView.setBottomRefresh(false);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        weatherView.setBottomRefresh(false);
                        weatherView.showMessage("地理位置解析失败");
                    }
                });
        subscriptionList.add(subscription);
    }

    private Observable<AMapGeoCode> analysisLocation(Location location) {
        Double lng = location.getLongitude();
        Double lat = location.getLatitude();
        return RetrofitHelper.createRetrofit(LocationAnalysisService.class, Constants.getAmapBaseUrl())
                .analysis(Constants.getAmapKey(), lng.toString() + "," + lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void saveToDB(WeatherList.Weather weather, String updateTime) {
        DataBaseManager.getInstance()
                .addOrUpdateCity(new City(weather.getBasicCityInfo().getCityName(), updateTime, new Gson().toJson(weather)), tableName);
    }

    private City queryFromDB() {
        if (district != null) {
            City d = DataBaseManager.getInstance().queryCity(district, tableName);
            if (d != null)
                return d;
        }
        if (city != null) {
            return DataBaseManager.getInstance().queryCity(city, tableName);
        }
        return null;
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
