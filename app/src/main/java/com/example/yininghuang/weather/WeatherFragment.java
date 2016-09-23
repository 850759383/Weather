package com.example.yininghuang.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.utils.DateFormat;
import com.example.yininghuang.weather.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class WeatherFragment extends Fragment implements WeatherContract.View, LocationListener {

    private WeatherPresenter presenter;

    private ImageView weatherImage;
    private TextView weatherText;
    private TextView updateTimeText;

    private String latestLocation;
    public static final String PREFERENCE_LOCATION = "location";
    private LocationManager locationManager;

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
        return inflater.inflate(R.layout.fragment_weather, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view, savedInstanceState);
    }

    private void initView(View view, Bundle savedInstanceState) {
        weatherImage = (ImageView) view.findViewById(R.id.weatherImage);
        weatherText = (TextView) view.findViewById(R.id.weatherText);
        updateTimeText = (TextView) view.findViewById(R.id.updateTime);
        presenter = new WeatherPresenter(this, getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        latestLocation = SharedPreferenceHelper.getStringPreference(getActivity(), PREFERENCE_LOCATION);

        if (latestLocation != null) {
            presenter.init(latestLocation);
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.getMainLooper());
        }
    }

    @Override
    public void updateWeather(WeatherList.Weather weather) {
        String code = weather.getNowWeather().getWeatherStatus().getWeatherCode();
        Picasso.with(getActivity()).load("http://files.heweather.com/cond_icon/" + code + ".png").into(weatherImage);
        weatherText.setText(weather.getNowWeather().getWeatherStatus().getWeatherName());
        updateTimeText.setText(weather.getBasicCityInfo().getUpdateTime().getLocalTime());

    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 60, 0, this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.removeUpdates(this);
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onStop();
        super.onDestroyView();
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("更新位置");
        String name = presenter.getCityName(location);
        if (name != null && !name.equals(latestLocation)) {
            presenter.fetchWeather(name);
            SharedPreferenceHelper.setStringPreference(getActivity(), PREFERENCE_LOCATION, name);
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
}
