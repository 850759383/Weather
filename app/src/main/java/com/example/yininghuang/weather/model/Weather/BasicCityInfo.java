package com.example.yininghuang.weather.model.Weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class BasicCityInfo {

    @SerializedName("city")
    private String cityName;

    @SerializedName("id")
    private String cityId;

    @SerializedName("cnty")
    private String country;

    @SerializedName("update")
    private UpdateTime updateTime;

    @SerializedName("loc")
    private String updateLocalTime;

    @SerializedName("utc")
    private String updateUTCTime;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UpdateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(UpdateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateLocalTime() {
        return updateLocalTime;
    }

    public void setUpdateLocalTime(String updateLocalTime) {
        this.updateLocalTime = updateLocalTime;
    }

    public String getUpdateUTCTime() {
        return updateUTCTime;
    }

    public void setUpdateUTCTime(String updateUTCTime) {
        this.updateUTCTime = updateUTCTime;
    }

    public static class UpdateTime {

        @SerializedName("loc")
        private String localTime;
        @SerializedName("utc")
        private String UTCTime;

        public String getLocalTime() {
            return localTime;
        }

        public void setLocalTime(String localTime) {
            this.localTime = localTime;
        }

        public String getUTCTime() {
            return UTCTime;
        }

        public void setUTCTime(String UTCTime) {
            this.UTCTime = UTCTime;
        }
    }
}
