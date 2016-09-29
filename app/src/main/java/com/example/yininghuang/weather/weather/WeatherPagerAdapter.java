package com.example.yininghuang.weather.weather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class WeatherPagerAdapter extends FragmentStatePagerAdapter {

    private final List<String> cities;

    public WeatherPagerAdapter(FragmentManager fm, List<String> cityList) {
        super(fm);
        this.cities = cityList;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return WeatherFragment.newInstance(cities.get(position), true);
        }
        return WeatherFragment.newInstance(cities.get(position), false);
    }

    @Override
    public int getCount() {
        return cities.size();
    }

}
