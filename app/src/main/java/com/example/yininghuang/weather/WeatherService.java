package com.example.yininghuang.weather;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.example.yininghuang.weather.dao.CityDao;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.RemoteWeatherService;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.utils.DaoHelper;
import com.example.yininghuang.weather.utils.RxBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class WeatherService extends Service implements LocationListener {

    private LocationManager locationManager;

    private String latestLocation;
    private List<String> mLocations;

    private Subscription weatherSub;
    private Timer timer;

    private static final Long UPDATE_TIME_DISTANCE = 1000 * 60 * 20L;

    @Override
    public void onCreate() {
        super.onCreate();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        weatherSub.unsubscribe();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String cityName = addresses.get(0).getLocality();
            if (!cityName.equals(latestLocation)) {
                latestLocation = cityName;
                updateWeather();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void updateWeather() {
        if (weatherSub != null)
            weatherSub.unsubscribe();

        String code = getCityCode(latestLocation);
        if (code != null) {
            weatherSub = RetrofitHelper.createRetrofit(RemoteWeatherService.class)
                    .getWeatherWithId(code, Constants.getKey())
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
                            System.out.println("天气：" + weather.getNowWeather().getWeatherStatus().getWeatherName());
                            broadCastWeather(weather);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            throwable.printStackTrace();
                        }
                    });
        }
    }

    private String getCityCode(String name) {
        if (name == null || name.isEmpty())
            return null;

        String last = name.substring(name.length() - 1);
        if (last.equals("县") || last.equals("市") || last.equals("区")) {
            name = name.substring(0, name.length() - 1);
        }
        com.example.yininghuang.weather.dao.City city = DaoHelper.getInstance()
                .getCityDao()
                .queryBuilder()
                .where(CityDao.Properties.CityName.eq(name))
                .unique();

        if (city == null)
            return null;

        return city.getId();
    }

    private void startTimer() {
        if (timer == null)
            timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateWeather();
                System.out.println("自动更新");
            }
        }, 0, UPDATE_TIME_DISTANCE);
    }

    private void broadCastWeather(WeatherList.Weather weather) {
        RxBus.getInstance().send(weather);
    }
}
