package com.example.yininghuang.weather.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Yining Huang on 2016/9/21.
 */

public class City {

    private String city;

    private String id;

    @SerializedName("prov")
    private String province;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
