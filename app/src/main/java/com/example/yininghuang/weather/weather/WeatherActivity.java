package com.example.yininghuang.weather.weather;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.yininghuang.weather.R;
import com.example.yininghuang.weather.model.City;
import com.example.yininghuang.weather.search.SearchActivity;
import com.example.yininghuang.weather.utils.DataBaseManager;
import com.example.yininghuang.weather.utils.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends AppCompatActivity implements NavigationAdapter.OnNavigationItemClickListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.viewPager)
    ViewPager viewPager;

    @BindView(R.id.navRec)
    RecyclerView navRec;

    @BindView(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    public static final int PERMISSION_CODE = 1;
    private WeatherPagerAdapter weatherPagerAdapter;
    private NavigationAdapter navigationAdapter;
    private List<String> pagerLocation = new ArrayList<>();
    private List<String> recLocation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        pagerLocation.add(getAutoLocationName("正在定位"));
        pagerLocation.addAll(getSavedCityName());
        recLocation.addAll(pagerLocation);
        weatherPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), pagerLocation);
        navigationAdapter = new NavigationAdapter(this, recLocation);
        navigationAdapter.setOnNavigationItemClickListener(this);
        navRec.setLayoutManager(new LinearLayoutManager(this));
        navRec.setAdapter(navigationAdapter);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        else {
            viewPager.setAdapter(weatherPagerAdapter);
            viewPager.addOnPageChangeListener(this);
        }
    }

    private List<String> getSavedCityName() {
        List<String> nameList = new ArrayList<>();
        List<City> cityList = DataBaseManager.getInstance().queryCityList(DataBaseManager.TABLE_SAVED);
        for (City city : cityList) {
            nameList.add(city.getName());
        }
        return nameList;
    }

    private String getAutoLocationName(String defaultLocation) {
        String d = SharedPreferenceHelper.getStringPreference(this, WeatherPresenter.PREFERENCE_DISTRICT);
        String c = SharedPreferenceHelper.getStringPreference(this, WeatherPresenter.PREFERENCE_CITY);
        City d1 = DataBaseManager.getInstance().queryCity(d, DataBaseManager.TABLE_AUTO_LOCATION);
        if (d1 != null)
            return d1.getName();
        City c1 = DataBaseManager.getInstance().queryCity(c, DataBaseManager.TABLE_AUTO_LOCATION);
        if (c1 != null)
            return c1.getName();
        return defaultLocation;
    }

    public void updateDrawerRec() {
        recLocation.clear();
        recLocation.add(getAutoLocationName("正在定位"));
        recLocation.addAll(getSavedCityName());
        navigationAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_location: {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            }
            case android.R.id.home: {
                if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    drawerLayout.closeDrawers();
                } else {
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String location = intent.getStringExtra("city");
        pagerLocation.clear();
        recLocation.clear();
        pagerLocation.add(getAutoLocationName(getString(R.string.positioning)));
        pagerLocation.addAll(getSavedCityName());
        recLocation.addAll(pagerLocation);
        weatherPagerAdapter.notifyDataSetChanged();
        navigationAdapter.notifyDataSetChanged();
        int index = 0;
        for (String name : pagerLocation) {
            index++;
            if (index == 0)
                continue;
            if (name.equals(location))
                break;
        }
        viewPager.setCurrentItem(index, false);
        navigationAdapter.setSelectedIndex(index);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewPager.setAdapter(weatherPagerAdapter);
            viewPager.addOnPageChangeListener(this);
        } else {
            finish();
        }
    }

    @Override
    public void onDrawerItemClick(int index) {
        viewPager.setCurrentItem(index, false);
        navigationAdapter.setSelectedIndex(index);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onDrawerItemDelete(int index) {
        DataBaseManager.getInstance().deleteCity(pagerLocation.get(index), DataBaseManager.TABLE_SAVED);
        pagerLocation.remove(index);
        recLocation.remove(index);
        weatherPagerAdapter.notifyDataSetChanged();
        navigationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPageSelected(int position) {
        navigationAdapter.setSelectedIndex(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
