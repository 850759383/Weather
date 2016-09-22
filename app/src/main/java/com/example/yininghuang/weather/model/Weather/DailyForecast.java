package com.example.yininghuang.weather.model.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class DailyForecast {

    /**
     * code_d : 300
     * code_n : 302
     * txt_d : 阵雨
     * txt_n : 雷阵雨
     */

    @SerializedName("cond")
    private WeatherStatus weatherStatus;
    /**
     * astro : {"sr":"06:02","ss":"18:11"}
     * cond : {"code_d":"300","code_n":"302","txt_d":"阵雨","txt_n":"雷阵雨"}
     * date : 2016-09-22
     * hum : 46
     * pcpn : 3.8
     * pop : 47
     * pres : 1013
     * tmp : {"max":"26","min":"17"}
     * vis : 10
     * wind : {"deg":"200","dir":"无持续风向","sc":"微风","spd":"1"}
     */

    private String date;
    @SerializedName("hum")
    private String humidity;
    /**
     * max : 26
     * min : 17
     */

    @SerializedName("tmp")
    private TmpBean temperature;

    public WeatherStatus getWeatherStatus() {
        return weatherStatus;
    }

    public void setWeatherStatus(WeatherStatus weatherStatus) {
        this.weatherStatus = weatherStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public TmpBean getTemperature() {
        return temperature;
    }

    public void setTemperature(TmpBean temperature) {
        this.temperature = temperature;
    }

    public static class WeatherStatus {
        @SerializedName("code_d")
        private String weatherCodeDay;
        @SerializedName("code_n")
        private String weatherCodeNight;
        @SerializedName("txt_d")
        private String weatherNameDay;
        @SerializedName("txt_n")
        private String weatherNameNight;

        public String getWeatherCodeDay() {
            return weatherCodeDay;
        }

        public void setWeatherCodeDay(String weatherCodeDay) {
            this.weatherCodeDay = weatherCodeDay;
        }

        public String getWeatherCodeNight() {
            return weatherCodeNight;
        }

        public void setWeatherCodeNight(String weatherCodeNight) {
            this.weatherCodeNight = weatherCodeNight;
        }

        public String getWeatherNameDay() {
            return weatherNameDay;
        }

        public void setWeatherNameDay(String weatherNameDay) {
            this.weatherNameDay = weatherNameDay;
        }

        public String getWeatherNameNight() {
            return weatherNameNight;
        }

        public void setWeatherNameNight(String weatherNameNight) {
            this.weatherNameNight = weatherNameNight;
        }
    }

    public static class TmpBean {
        @SerializedName("max")
        private String maxTemperature;
        @SerializedName("min")
        private String minTemperature;

        public String getMaxTemperature() {
            return maxTemperature;
        }

        public void setMaxTemperature(String maxTemperature) {
            this.maxTemperature = maxTemperature;
        }

        public String getMinTemperature() {
            return minTemperature;
        }

        public void setMinTemperature(String minTemperature) {
            this.minTemperature = minTemperature;
        }
    }
}
