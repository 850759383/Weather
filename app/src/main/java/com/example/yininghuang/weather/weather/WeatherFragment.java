package com.example.yininghuang.weather.weather;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yininghuang.weather.R;
import com.example.yininghuang.weather.model.Weather.DailyForecast;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;
import com.example.yininghuang.weather.utils.DateUtils;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherFragment extends Fragment implements WeatherContract.View {

    @BindView(R.id.cityTitle)
    TextView cityTitle;
    @BindView(R.id.weatherImage)
    ImageView weatherImage;
    @BindView(R.id.weatherText)
    TextView weatherText;
    @BindView(R.id.updateTime)
    TextView updateTimeText;
    @BindView(R.id.maxTemp)
    TextView maxTemp;
    @BindView(R.id.minTemp)
    TextView minTemp;
    @BindView(R.id.currentTemp)
    TextView currentTemp;
    @BindView(R.id.feelTemp)
    TextView feelTemp;
    @BindView(R.id.humidity)
    TextView humidity;
    @BindView(R.id.windSpeed)
    TextView windSpeed;
    @BindView(R.id.airQuality)
    TextView airQuality;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.bottomRefresh)
    LinearLayout bottomRefresh;
    @BindView(R.id.updateMessage)
    TextView updateMessage;

    @BindView(R.id.date1)
    TextView forecastDate1;
    @BindView(R.id.date1Image)
    ImageView forecastImage1;
    @BindView(R.id.date1Temp)
    TextView forecastTemp1;

    @BindView(R.id.date2)
    TextView forecastDate2;
    @BindView(R.id.date2Image)
    ImageView forecastImage2;
    @BindView(R.id.date2Temp)
    TextView forecastTemp2;

    @BindView(R.id.date3)
    TextView forecastDate3;
    @BindView(R.id.date3Image)
    ImageView forecastImage3;
    @BindView(R.id.date3Temp)
    TextView forecastTemp3;

    private WeatherPresenter presenter;
    private Boolean isRefresh = false;
    private long updateTime = -1;

    public static WeatherFragment newInstance(String cityName, Boolean positioning) {
        Bundle bundle = new Bundle();
        bundle.putString("city", cityName);
        bundle.putBoolean("positioning", positioning);
        WeatherFragment fragment = new WeatherFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle argument = getArguments();
        String name = argument.getString("city");
        if (argument.getBoolean("positioning")) {
            presenter = new WeatherPresenter(this, getActivity());
        } else {
            presenter = new WeatherPresenter(this, getActivity(), name);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefresh)
                    presenter.update();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        presenter.init();
    }

    @Override
    public void updateWeather(WeatherList.Weather weather, String updateTime) {
        cityTitle.setText(weather.getBasicCityInfo().getCityName());
        this.updateTime = Long.valueOf(updateTime);
        updateTimeText.setText("更新时间：" + DateUtils.format(new Date(this.updateTime)));
        DailyForecast today = weather.getDailyForecasts().get(0);
        currentTemp.setText(weather.getNowWeather().getTemperature());
        maxTemp.setText(today.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol());
        minTemp.setText(today.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());
        weatherText.setText(weather.getNowWeather().getWeatherStatus().getWeatherName());
        weatherImage.setImageResource(Constants.getWeatherImage(weather.getNowWeather().getWeatherStatus().getWeatherCode(), getActivity()));
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

        forecastImage1.setImageResource(Constants.getWeatherImage(forecast1.getWeatherStatus().getWeatherCodeDay(), getActivity()));
        forecastImage2.setImageResource(Constants.getWeatherImage(forecast2.getWeatherStatus().getWeatherCodeDay(), getActivity()));
        forecastImage3.setImageResource(Constants.getWeatherImage(forecast3.getWeatherStatus().getWeatherCodeDay(), getActivity()));

        forecastTemp1.setText(forecast1.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol() + "/"
                + forecast1.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());
        forecastTemp2.setText(forecast2.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol() + "/"
                + forecast2.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());
        forecastTemp3.setText(forecast3.getTemperature().getMaxTemperature() + Constants.getDegreeSymbol() + "/"
                + forecast3.getTemperature().getMinTemperature() + Constants.getDegreeSymbol());

        forecastDate1.setText(DateUtils.getWeekOfYear(DateUtils.getTime2(forecast1.getDate())));
        forecastDate2.setText(DateUtils.getWeekOfYear(DateUtils.getTime2(forecast2.getDate())));
        forecastDate3.setText(DateUtils.getWeekOfYear(DateUtils.getTime2(forecast3.getDate())));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (updateTime != -1)
            updateTimeText.setText("更新时间：" + DateUtils.format(new Date(this.updateTime)));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.onStop();
    }

    @Override
    public void setBottomRefresh(Boolean status, String msg) {
        if (msg != null)
            updateMessage.setText(msg);
        isRefresh = status;
        if (status) {
            updateTimeText.setVisibility(View.GONE);
            bottomRefresh.setVisibility(View.VISIBLE);
        } else {
            updateTimeText.setVisibility(View.VISIBLE);
            bottomRefresh.setVisibility(View.GONE);
        }
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

}
