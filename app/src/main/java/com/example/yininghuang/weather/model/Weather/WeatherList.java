package com.example.yininghuang.weather.model.Weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class WeatherList {

    @SerializedName("HeWeather data service 3.0")
    private List<Weather> weatherList;

    public List<Weather> getWeatherList() {
        return weatherList;
    }

    public void setWeatherList(List<Weather> weatherList) {
        this.weatherList = weatherList;
    }

    public static class Weather {

        @SerializedName("basic")
        private BasicCityInfo basicCityInfo;

        @SerializedName("daily_forecast")
        private List<DailyForecast> dailyForecasts;

        @SerializedName("now")
        private NowWeather nowWeather;

        @SerializedName("aqi")
        private AirQuality airQuality;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        private String status;

        public AirQuality getAirQuality() {
            return airQuality;
        }

        public void setAirQuality(AirQuality airQuality) {
            this.airQuality = airQuality;
        }

        public BasicCityInfo getBasicCityInfo() {
            return basicCityInfo;
        }

        public void setBasicCityInfo(BasicCityInfo basicCityInfo) {
            this.basicCityInfo = basicCityInfo;
        }

        public List<DailyForecast> getDailyForecasts() {
            return dailyForecasts;
        }

        public void setDailyForecasts(List<DailyForecast> dailyForecasts) {
            this.dailyForecasts = dailyForecasts;
        }

        public NowWeather getNowWeather() {
            return nowWeather;
        }

        public void setNowWeather(NowWeather nowWeather) {
            this.nowWeather = nowWeather;
        }
    }
}
