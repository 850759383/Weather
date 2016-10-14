package com.example.yininghuang.weather.search;

import com.example.yininghuang.weather.AppModule;

import dagger.Component;

/**
 * Created by Yining Huang on 2016/10/14.
 */
@Component(modules = {SearchPresenterModule.class, AppModule.class})
public interface SearchComponent {

    void inject(SearchFragment fragment);
}
