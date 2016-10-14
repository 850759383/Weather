package com.example.yininghuang.weather;

import android.content.Context;

import com.example.yininghuang.weather.net.RetrofitHelper;
import com.example.yininghuang.weather.utils.DataBaseManager;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yining Huang on 2016/10/14.
 */

@Module
public class AppModule {

    private Context mContext;

    public AppModule(Context context){
        this.mContext = context.getApplicationContext();
    }

    @Provides
    Context provideContext() {
        return mContext;
    }

    @Provides
    DataBaseManager provideDataBaseManager() {
        return DataBaseManager.getInstance();
    }

    @Provides
    RetrofitHelper provideRetrofitHelper(){
        return RetrofitHelper.getInstance();
    }
}
