package com.example.yininghuang.weather;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yininghuang.weather.dao.CityDao;
import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.model.CityList;
import com.example.yininghuang.weather.net.CityService;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.utils.DaoHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class WeatherFragment extends Fragment implements LocationListener {

    public WeatherFragment() {

    }

    public static WeatherFragment newInstance() {
        return new WeatherFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RetrofitHelper.createRetrofit(CityService.class)
                .getCityList(Constants.getKey())
                .doOnNext(new Action1<CityList>() {
                    @Override
                    public void call(CityList cityList) {
                        System.out.println("开始:" + System.currentTimeMillis());
                        CityDao cityDao = DaoHelper.getInstance().getCityDao();
                        Long cityCount = DaoHelper.getInstance().getCityCount();
                        if (cityCount > 2000) {
                            return;
                        } else if (cityCount > 0) {
                            cityDao.deleteAll();
                        }
                        for (City city : cityList.getCityList()) {
                            cityDao.insert(new com.example.yininghuang.weather.dao.City(city.getId(), city.getCity(), city.getProvince()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<CityList>() {
                    @Override
                    public void call(CityList cityList) {
                        System.out.println("结束:" + System.currentTimeMillis());
                        Intent intent = new Intent(getActivity(), WeatherService.class);
                        getActivity().startService(intent);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });

        return inflater.inflate(R.layout.fragment_weather, container, false);
    }


    @Override
    public void onLocationChanged(Location location) {

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
