package com.example.yininghuang.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.yininghuang.weather.utils.SharedPrefrencesHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportFragmentManager().findFragmentById(R.id.mainFrameLayout) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFrameLayout, WeatherFragment.newInstance())
                    .commit();
        }

        SharedPrefrencesHelper.setValue(1);
        SharedPrefrencesHelper.setValue(1L);
        SharedPrefrencesHelper.setValue(true);
    }
}
