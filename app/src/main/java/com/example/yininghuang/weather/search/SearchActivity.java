package com.example.yininghuang.weather.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.yininghuang.weather.R;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        if (getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout) == null)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, SearchFragment.newInstance())
                    .commit();
    }

}
