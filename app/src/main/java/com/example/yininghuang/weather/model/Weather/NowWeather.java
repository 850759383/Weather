package com.example.yininghuang.weather.model.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class NowWeather {

    @SerializedName("tmp")
    private String temperature;

    @SerializedName("fl")
    private String feelTemperature;

    @SerializedName("cond")
    private WeatherStatus weatherStatus;

    @SerializedName("hum")
    private String humidity;

    @SerializedName("wind")
    private Wind wind;

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public WeatherStatus getWeatherStatus() {
        return weatherStatus;
    }

    public void setWeatherStatus(WeatherStatus weatherStatus) {
        this.weatherStatus = weatherStatus;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getFeelTemperature() {
        return feelTemperature;
    }

    public void setFeelTemperature(String feelTemperature) {
        this.feelTemperature = feelTemperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public static class WeatherStatus {

        @SerializedName("code")
        private String weatherCode;

        @SerializedName("txt")
        private String weatherName;

        public String getWeatherCode() {
            return weatherCode;
        }

        public void setWeatherCode(String weatherCode) {
            this.weatherCode = weatherCode;
        }

        public String getWeatherName() {
            return weatherName;
        }

        public void setWeatherName(String weatherName) {
            this.weatherName = weatherName;
        }
    }

    public static class Wind {

        /**
         * deg : 70
         * dir : 东北风
         * sc : 5-6
         * spd : 27
         */

        @SerializedName("deg")
        private String degree;
        @SerializedName("dir")
        private String direction;
        @SerializedName("sc")
        private String level;
        @SerializedName("spd")
        private String speed;

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }

        public String getLevel() {
            return level;
        }

        public void setLevel(String level) {
            this.level = level;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }
    }
}
