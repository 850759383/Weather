package com.example.yininghuang.weather;

import com.example.yininghuang.weather.model.City;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Yining Huang on 2016/9/21.
 */

public interface CityService {
    @GET("/x3/citylist?search=allchina&key=41cb48fa8c78499dbe18697c895456f2")
    Call<List<City>> getCityList();
}
