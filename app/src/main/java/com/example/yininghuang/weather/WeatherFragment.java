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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yininghuang.weather.model.Weather.DailyForecast;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.utils.DateFormat;
import com.example.yininghuang.weather.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.Date;

public class WeatherFragment extends Fragment implements WeatherContract.View, LocationListener {

    private WeatherPresenter presenter;

    private ImageView weatherImage;
    private TextView weatherText;
    private TextView updateTimeText;
    private TextView maxTemp;
    private TextView minTemp;
    private TextView currentTemp;

    private TextView date1;
    private ImageView image1;
    private TextView temp1;

    private TextView date2;
    private ImageView image2;
    private TextView temp2;

    private TextView date3;
    private ImageView image3;
    private TextView temp3;

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
        initView(view);
    }

    private void initView(View view) {
        setHasOptionsMenu(true);
        weatherImage = (ImageView) view.findViewById(R.id.weatherImage);
        weatherText = (TextView) view.findViewById(R.id.weatherText);
        updateTimeText = (TextView) view.findViewById(R.id.updateTime);
        currentTemp = (TextView) view.findViewById(R.id.currentTemp);
        maxTemp = (TextView) view.findViewById(R.id.maxTemp);
        minTemp = (TextView) view.findViewById(R.id.minTemp);
        date1 = (TextView) view.findViewById(R.id.date1);
        image1 = (ImageView) view.findViewById(R.id.date1Image);
        temp1 = (TextView) view.findViewById(R.id.date1Temp);
        date2 = (TextView) view.findViewById(R.id.date2);
        image2 = (ImageView) view.findViewById(R.id.date2Image);
        temp2 = (TextView) view.findViewById(R.id.date2Temp);
        date3 = (TextView) view.findViewById(R.id.date3);
        image3 = (ImageView) view.findViewById(R.id.date3Image);
        temp3 = (TextView) view.findViewById(R.id.date3Temp);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        presenter = new WeatherPresenter(this, getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        latestLocation = SharedPreferenceHelper.getStringPreference(getActivity(), PREFERENCE_LOCATION);

        if (latestLocation != null) {
            presenter.init(latestLocation);
        }

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, WeatherFragment.this, Looper.getMainLooper());
        }
    }

    @Override
    public void updateWeather(WeatherList.Weather weather) {
        Date time = DateFormat.getTime(weather.getBasicCityInfo().getUpdateTime().getLocalTime());
        if (time != null) {
            updateTimeText.setText("更新时间：" + DateFormat.format(time));
        }
        DailyForecast today = weather.getDailyForecasts().get(0);
        currentTemp.setText(weather.getNowWeather().getTemperature());
        maxTemp.setText(today.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol());
        minTemp.setText(today.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());
        weatherText.setText(weather.getNowWeather().getWeatherStatus().getWeatherName());
        String code = weather.getNowWeather().getWeatherStatus().getWeatherCode();
        Picasso.with(getActivity())
                .load(Constants.getWeatherImage(code))
                .fit()
                .centerInside()
                .into(weatherImage);
        setUpDailyForeCast(weather);
    }

    private void setUpDailyForeCast(WeatherList.Weather weather) {
        DailyForecast day1 = weather.getDailyForecasts().get(1);
        DailyForecast day2 = weather.getDailyForecasts().get(2);
        DailyForecast day3 = weather.getDailyForecasts().get(3);

        Picasso.with(getActivity()).load(Constants.getWeatherImage(day1.getWeatherStatus().getWeatherCodeDay())).fit().centerCrop().into(image1);
        Picasso.with(getActivity()).load(Constants.getWeatherImage(day2.getWeatherStatus().getWeatherCodeDay())).fit().centerCrop().into(image2);
        Picasso.with(getActivity()).load(Constants.getWeatherImage(day3.getWeatherStatus().getWeatherCodeDay())).fit().centerCrop().into(image3);

        temp1.setText(day1.getTemperature().getMaxTemperature() + "/" + day1.getTemperature().getMinTemperature());
        temp2.setText(day2.getTemperature().getMaxTemperature() + "/" + day2.getTemperature().getMinTemperature());
        temp3.setText(day3.getTemperature().getMaxTemperature() + "/" + day3.getTemperature().getMinTemperature());

        date1.setText(DateFormat.getWeekOfYear(DateFormat.getTime2(day1.getDate())));
        date2.setText(DateFormat.getWeekOfYear(DateFormat.getTime2(day2.getDate())));
        date3.setText(DateFormat.getWeekOfYear(DateFormat.getTime2(day3.getDate())));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
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
