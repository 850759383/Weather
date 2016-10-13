package com.example.yininghuang.weather.weather;

import dagger.Component;

/**
 * Created by Yining Huang on 2016/10/13.
 */

@Component(modules = {WeatherPresenterModule.class})
public interface WeatherComponent {

    void inject(WeatherFragment fragment);
}
