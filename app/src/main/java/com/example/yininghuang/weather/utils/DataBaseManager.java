package com.example.yininghuang.weather.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.yininghuang.weather.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yining Huang on 2016/9/28.
 */

public class DataBaseManager {

    public static final int POSITIONING = 0;
    public static final int UN_POSITIONING = 1;

    private static DataBaseManager INSTANCE;
    private SQLiteDatabase database;
    private DataBaseHelper helper;

    private DataBaseManager(Context context) {
        helper = new DataBaseHelper(context);
        database = helper.getWritableDatabase();

    }

    public static void init(Context context) {
        if (INSTANCE == null)
            INSTANCE = new DataBaseManager(context);
    }

    public static DataBaseManager getInstance() {
        if (INSTANCE == null) {
            throw new IllegalStateException("call init() method first");
        }
        return INSTANCE;
    }

    public void addOrUpdateCity(City city) {
        City cache = queryCity(city.getName());
        if (cache == null) {
            insertCity(city);
        } else {
            updateCity(city);
        }
    }

    public void insertCity(City city) {
        database.beginTransaction();
        database.execSQL("INSERT INTO city VALUES(?, ?, ?, ?)",
                new Object[]{city.getName(), city.getUpdateTime(), city.getWeather(), city.getPositioning()});
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void updateCity(City city) {
        ContentValues cv = new ContentValues();
        cv.put("updateTime", city.getUpdateTime());
        cv.put("weather", city.getWeather());
        database.update("city", cv, "name = ?", new String[]{city.getName()});
    }

    public City queryCity(String name) {
        Cursor cursor = database.rawQuery("SELECT * FROM city WHERE name = ?", new String[]{name});
        City city = null;
        if (cursor.getCount() != 0) {
            city = new City();
            cursor.moveToFirst();
            city.setName(cursor.getString(cursor.getColumnIndex("name")));
            city.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
            city.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
            city.setPositioning(cursor.getInt(cursor.getColumnIndex("positioning")));
        }
        cursor.close();
        return city;
    }

    public List<City> queryCityList() {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = database.query("city", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setName(cursor.getString(cursor.getColumnIndex("name")));
                city.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
                city.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
                city.setPositioning(cursor.getInt(cursor.getColumnIndex("positioning")));
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cityList;
    }

}
