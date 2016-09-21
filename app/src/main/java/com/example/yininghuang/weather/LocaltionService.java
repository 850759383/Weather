package com.example.yininghuang.weather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.example.yininghuang.weather.model.City;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Yining Huang on 2016/9/21.
 */

class LocationService implements LocationListener {

    private static LocationService locationService = null;
    private String latestLocation = null;
    private Context context;

    static LocationService getInstance(Context context) {
        if (locationService == null)
            locationService = new LocationService(context);
        return locationService;
    }

    private LocationService(Context context) {
        this.context = context.getApplicationContext();
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
    }

    public String getLatestLocation() {
        return this.latestLocation;
    }


    @Override
    public void onLocationChanged(Location location) {
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            System.out.println("位置:" + addresses.get(0).getLocality());
            this.latestLocation = addresses.get(0).getLocality();
            new Retrofit.Builder()
                    .baseUrl("https://api.heweather.com").build().create(CityService.class)
                    .getCityList().enqueue(new Callback<List<City>>() {
                @Override
                public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                    System.out.println("size:" + response.body().size());
                }

                @Override
                public void onFailure(Call<List<City>> call, Throwable t) {

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
