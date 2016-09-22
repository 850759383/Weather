package com.example.yininghuang.weather.net;

import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.model.CityList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Yining Huang on 2016/9/21.
 */

public interface CityService {
    @GET("/x3/citylist?search=allchina")
    Observable<CityList> getCityList(@Query("key") String key);
}
