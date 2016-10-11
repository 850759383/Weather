package com.example.yininghuang.weather.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Yining Huang on 2016/9/28.
 */

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "location.db";
    private static final int DB_VERSION = 3;

    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS saved(name TEXT PRIMARY KEY, updateTime TEXT, weather TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS auto_location(name TEXT PRIMARY KEY, updateTime TEXT, weather TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
