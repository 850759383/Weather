package com.example.yininghuang.weather.utils;

import android.content.Context;

import com.example.yininghuang.weather.dao.CityDao;
import com.example.yininghuang.weather.dao.DaoMaster;
import com.example.yininghuang.weather.dao.DaoSession;

/**
 * Created by Yining Huang on 2016/9/22.
 */

public class DaoHelper {

    private static DaoHelper INSTANCE = null;

    private DaoSession session = null;
    private CityDao cityDao = null;

    public static DaoHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DaoHelper();
        }
        return INSTANCE;
    }

    private DaoHelper() {
    }

    public void init(Context context) {
        session = new DaoMaster(new DaoMaster.DevOpenHelper(context, "city", null)
                .getWritableDatabase())
                .newSession();

        cityDao = session.getCityDao();
    }

    public DaoSession getSession() {
        return session;
    }

    public CityDao getCityDao() {
        return cityDao;
    }

    public Long getCityCount() {
        return cityDao.count();
    }
}
