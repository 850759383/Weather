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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WeatherActivity extends AppCompatActivity implements NavigationAdapter.OnNavigationItemClickListener {

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        pagerLocation.addAll(getCityNameList());
        recLocation.addAll(pagerLocation);
        weatherPagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), pagerLocation);
        navigationAdapter = new NavigationAdapter(recLocation);
        navigationAdapter.setOnNavigationItemClickListener(this);
        navRec.setLayoutManager(new LinearLayoutManager(this));
        navRec.setAdapter(navigationAdapter);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        else {
            viewPager.setAdapter(weatherPagerAdapter);
        }
    }

    private List<String> getCityNameList() {
        List<String> name = new ArrayList<>();
        List<City> cityList = DataBaseManager.getInstance().queryCityList();
        if (!cityList.isEmpty()) {
            for (City city : cityList) {
                name.add(city.getName());
            }
        } else {
            name.add("正在定位");
        }
        return name;
    }

    public void updateDrawerRec() {
        recLocation.clear();
        recLocation.addAll(getCityNameList());
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
        if (!pagerLocation.contains(location)) {
            pagerLocation.add(location);
            weatherPagerAdapter.notifyDataSetChanged();
        }
        if (!recLocation.contains(location)){
            recLocation.add(location);
            navigationAdapter.notifyDataSetChanged();
        }
        int index = pagerLocation.indexOf(location);
        viewPager.setCurrentItem(index, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            viewPager.setAdapter(weatherPagerAdapter);
        } else {
            finish();
        }
    }

    @Override
    public void onItemClick(String name) {
        int index = pagerLocation.indexOf(name);
        viewPager.setCurrentItem(index, false);
        drawerLayout.closeDrawers();
    }

    @Override
    public void onItemDelete(String name) {
        pagerLocation.remove(name);
        weatherPagerAdapter.notifyDataSetChanged();
        navigationAdapter.notifyDataSetChanged();
        DataBaseManager.getInstance().deleteCity(name);
    }
}
