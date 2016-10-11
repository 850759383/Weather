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

    private static DataBaseManager INSTANCE;
    private SQLiteDatabase database;
    private DataBaseHelper helper;

    public static final String TABLE_SAVED = "saved";
    public static final String TABLE_AUTO_LOCATION = "auto_location";

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

    public void addOrUpdateCity(City city, String tableName) {
        City cache = queryCity(city.getName(), tableName);
        if (cache == null) {
            insertCity(city, tableName);
        } else {
            updateCity(city, tableName);
        }
    }

    public void insertCity(City city, String tableName) {
        database.beginTransaction();
        database.execSQL("INSERT INTO " + tableName + " VALUES(?, ?, ?)",
                new Object[]{city.getName(), city.getUpdateTime(), city.getWeather()});
        database.setTransactionSuccessful();
        database.endTransaction();
    }

    public void updateCity(City city, String tableName) {
        ContentValues cv = new ContentValues();
        cv.put("name", city.getName());
        cv.put("updateTime", city.getUpdateTime());
        cv.put("weather", city.getWeather());
        database.update(tableName, cv, "name = ?", new String[]{city.getName()});
    }

    public void deleteCity(String name, String tableName) {
        database.delete(tableName, "name = ?", new String[]{name});
    }

    public City queryCity(String name, String tableName) {
        if (name == null)
            return null;

        Cursor cursor = database.rawQuery("SELECT * FROM " + tableName + " WHERE name = ?", new String[]{name});
        City city = null;
        if (cursor.getCount() != 0) {
            city = new City();
            cursor.moveToFirst();
            city.setName(cursor.getString(cursor.getColumnIndex("name")));
            city.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
            city.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
        }
        cursor.close();
        return city;
    }

    public List<City> queryCityList(String tableName) {
        List<City> cityList = new ArrayList<>();
        Cursor cursor = database.query(tableName, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setName(cursor.getString(cursor.getColumnIndex("name")));
                city.setUpdateTime(cursor.getString(cursor.getColumnIndex("updateTime")));
                city.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
                cityList.add(city);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cityList;
    }

}
