package com.example.yininghuang.weather.net;

import com.example.yininghuang.weather.model.AMapGeoCode;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Yining Huang on 2016/9/30.
 */

public interface LocationAnalysisService {

    @GET("/v3/geocode/regeo")
    Observable<AMapGeoCode> analysis(@Query("key") String key, @Query("location") String LngAndLat);
}
