package com.example.yininghuang.weather.search;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Yining Huang on 2016/10/14.
 */

@Module
public class SearchPresenterModule {

    private SearchContract.View mView;

    public SearchPresenterModule(SearchContract.View view){
        this.mView = view;
    }

    @Provides
    SearchContract.View provideSearchContractView() {
        return mView;
    }
}
