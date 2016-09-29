package com.example.yininghuang.weather.model;

/**
 * Created by Yining Huang on 2016/9/28.
 */

public class City {

    private String name;
    private String updateTime;
    private String weather;
    private int positioning;

    public City(String name, String update, String weather, int positioning) {
        this.name = name;
        this.updateTime = update;
        this.weather = weather;
        this.positioning = positioning;
    }

    public City() {

    }

    public int getPositioning() {
        return positioning;
    }

    public void setPositioning(int positioning) {
        this.positioning = positioning;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
