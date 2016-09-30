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
import java.util.List;

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
    private LocationManager locationManager;
    private String latestLocation;
    private final Context context;
    private WeatherList.Weather weather;
    private Boolean isAutoLocation = true;
    private SubscriptionList subscriptionList = new SubscriptionList();

    private static final String PREFERENCE_LOCATION = "location";
    private static final Long UPDATE_TIME_INTERVAL = 1000L * 60 * 60;

    public WeatherPresenter(WeatherContract.View weatherView, Context context) {
        this.weatherView = weatherView;
        this.context = context.getApplicationContext();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public WeatherPresenter(WeatherContract.View weatherView, Context context, String requireLocation) {
        this(weatherView, context);
        latestLocation = formatCityName(requireLocation);
        isAutoLocation = false;
    }

    @Override
    public void init() {
        if (isAutoLocation)
            latestLocation = SharedPreferenceHelper.getStringPreference(context, PREFERENCE_LOCATION);
        City city = queryFromDB(latestLocation);
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
        if (TextUtils.isEmpty(latestLocation))
            return;

        weatherView.setBottomRefresh(true, "正在更新");
        Subscription subscription = RetrofitHelper.createRetrofit(WeatherService.class, Constants.getWeatherBaseUrl())
                .getWeatherWithName(latestLocation, Constants.getWeatherKey())
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
                        if (weather.getStatus().equals("ok")) {
                            weatherView.setBottomRefresh(false, null);
                            String currentTime = Long.toString(System.currentTimeMillis());
                            WeatherPresenter.this.weather = weather;
                            saveToDB(weather, currentTime);
                            weatherView.updateWeather(weather, currentTime);
                        } else {
                            weatherView.setBottomRefresh(false, null);
                            weatherView.showMessage(weather.getStatus());
                        }
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
        Subscription subscription = analysisLocation(location).map(new Func1<AMapGeoCode, String>() {
            @Override
            public String call(AMapGeoCode amapGeoCode) {
                if (amapGeoCode.getInfocode().equals("10000")) {
                    return amapGeoCode.getRegeocode().getAddressComponent().getDistrict();
                }
                return null;
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                String city = formatCityName(s);
                if (city != null) {
                    if (city.equals(latestLocation) && shouldUpdate()) {
                        fetchWeather();
                    } else if (!city.equals(latestLocation)) {
                        latestLocation = city;
                        fetchWeather();
                    } else {
                        weatherView.setBottomRefresh(false, null);
                    }
                    if (latestLocation != null)
                        SharedPreferenceHelper.setStringPreference(context, PREFERENCE_LOCATION, latestLocation);
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

    private Observable<AMapGeoCode> analysisLocation(Location location) {
        Double lng = location.getLongitude();
        Double lat = location.getLatitude();
        return RetrofitHelper.createRetrofit(LocationAnalysisService.class, Constants.getAmapBaseUrl())
                .analysis(Constants.getAmapKey(), lng.toString() + "," + lat)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void saveToDB(WeatherList.Weather weather, String updateTime) {
        int positioning = DataBaseManager.UN_POSITIONING;
        if (isAutoLocation)
            positioning = DataBaseManager.POSITIONING;

        City city = new City(weather.getBasicCityInfo().getCityName(), updateTime, new Gson().toJson(weather), positioning);
        List<City> cityList = DataBaseManager.getInstance().queryCityWithPosition(positioning);
        if (isAutoLocation && cityList.size() != 0) {
            DataBaseManager.getInstance().updateCity(city, "positioning");
        } else if (isAutoLocation) {
            DataBaseManager.getInstance().insertCity(city);
        } else {
            DataBaseManager.getInstance().addOrUpdateCity(
                    new City(
                            weather.getBasicCityInfo().getCityName(),
                            updateTime,
                            new Gson().toJson(weather),
                            positioning
                    )
            );
        }
    }

    private City queryFromDB(String cityName) {
        if (cityName == null)
            return null;

        return DataBaseManager.getInstance().queryCity(cityName);
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
