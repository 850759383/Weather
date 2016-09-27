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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yininghuang.weather.model.Weather.DailyForecast;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.utils.DateFormat;
import com.example.yininghuang.weather.utils.SharedPreferenceHelper;
import com.squareup.picasso.Picasso;

import java.util.Date;

import rx.functions.Action1;

public class WeatherFragment extends Fragment implements WeatherContract.View, LocationListener {

    private WeatherPresenter presenter;

    private TextView title;
    private ImageView weatherImage;
    private TextView weatherText;
    private TextView updateTimeText;
    private TextView maxTemp;
    private TextView minTemp;
    private TextView currentTemp;
    private TextView feelTemp;
    private TextView humidity;
    private TextView windSpeed;
    private TextView airQuality;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView forecastDate1;
    private ImageView forecastImage1;
    private TextView forecastTemp1;

    private TextView forecastDate2;
    private ImageView forecastImage2;
    private TextView forecastTemp2;

    private TextView forecastDate3;
    private ImageView forecastImage3;
    private TextView forecastTemp3;

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
        title = (TextView) view.findViewById(R.id.cityTitle);
        weatherImage = (ImageView) view.findViewById(R.id.weatherImage);
        weatherText = (TextView) view.findViewById(R.id.weatherText);
        updateTimeText = (TextView) view.findViewById(R.id.updateTime);
        currentTemp = (TextView) view.findViewById(R.id.currentTemp);
        maxTemp = (TextView) view.findViewById(R.id.maxTemp);
        minTemp = (TextView) view.findViewById(R.id.minTemp);
        forecastDate1 = (TextView) view.findViewById(R.id.date1);
        forecastImage1 = (ImageView) view.findViewById(R.id.date1Image);
        forecastTemp1 = (TextView) view.findViewById(R.id.date1Temp);
        forecastDate2 = (TextView) view.findViewById(R.id.date2);
        forecastImage2 = (ImageView) view.findViewById(R.id.date2Image);
        forecastTemp2 = (TextView) view.findViewById(R.id.date2Temp);
        forecastDate3 = (TextView) view.findViewById(R.id.date3);
        forecastImage3 = (ImageView) view.findViewById(R.id.date3Image);
        forecastTemp3 = (TextView) view.findViewById(R.id.date3Temp);
        feelTemp = (TextView) view.findViewById(R.id.feelTemp);
        humidity = (TextView) view.findViewById(R.id.humidity);
        airQuality = (TextView) view.findViewById(R.id.airQuality);
        windSpeed = (TextView) view.findViewById(R.id.windSpeed);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        presenter = new WeatherPresenter(this, getActivity());
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        latestLocation = SharedPreferenceHelper.getStringPreference(getActivity(), PREFERENCE_LOCATION);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestLocation();
            }
        });


        if (latestLocation != null) {
            presenter.init(latestLocation);
        }
        requestLocation();
    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, WeatherFragment.this, Looper.getMainLooper());
        }
    }

    @Override
    public void updateWeather(WeatherList.Weather weather) {
        title.setText(weather.getBasicCityInfo().getCityName());
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
                .load(Constants.getWeatherImage(code, getActivity()))
                .fit()
                .centerInside()
                .into(weatherImage);
        feelTemp.setText("体感温度：" + weather.getNowWeather().getFeelTemperature() + Constants.getDegreeSymbol());
        humidity.setText(weather.getNowWeather().getHumidity() + "%");
        airQuality.setText(weather.getAirQuality().getCity().getQlty());
        windSpeed.setText(weather.getNowWeather().getWind().getSpeed() + "km/h");
        setUpDailyForeCast(weather);
    }

    private void setUpDailyForeCast(WeatherList.Weather weather) {
        DailyForecast forecast1 = weather.getDailyForecasts().get(1);
        DailyForecast forecast2 = weather.getDailyForecasts().get(2);
        DailyForecast forecast3 = weather.getDailyForecasts().get(3);

        Picasso.with(getActivity()).load(Constants.getWeatherImage(forecast1.getWeatherStatus().getWeatherCodeDay(), getActivity()))
                .fit()
                .centerCrop()
                .into(forecastImage1);

        Picasso.with(getActivity()).load(Constants.getWeatherImage(forecast2.getWeatherStatus().getWeatherCodeDay(), getActivity()))
                .fit()
                .centerCrop().
                into(forecastImage2);

        Picasso.with(getActivity()).load(Constants.getWeatherImage(forecast3.getWeatherStatus().getWeatherCodeDay(), getActivity()))
                .fit()
                .centerCrop()
                .into(forecastImage3);

        forecastTemp1.setText(forecast1.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol() + "/"
                + forecast1.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());

        forecastTemp2.setText(forecast2.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol() + "/"
                + forecast2.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());
        forecastTemp3.setText(forecast3.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol() + "/"
                + forecast3.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());

        forecastDate1.setText(DateFormat.getWeekOfYear(DateFormat.getTime2(forecast1.getDate())));
        forecastDate2.setText(DateFormat.getWeekOfYear(DateFormat.getTime2(forecast2.getDate())));
        forecastDate3.setText(DateFormat.getWeekOfYear(DateFormat.getTime2(forecast3.getDate())));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        presenter.getCityName(location).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if (s != null) {
                    presenter.fetchWeather(s);
                    SharedPreferenceHelper.setStringPreference(getActivity(), PREFERENCE_LOCATION, s);
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
                setRefresh(false);
                Toast.makeText(getActivity().getApplicationContext(), "地理位置解析失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setRefresh(final Boolean status) {
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(status);
            }
        });
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
