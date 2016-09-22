package com.example.yininghuang.weather.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class CityList {
    @SerializedName("city_info")
    private List<City> cityList;

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }
}
