package com.example.yininghuang.weather.weather;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class NavgationAdapter extends RecyclerView.Adapter<NavgationAdapter.ViewHolder> {

    private List<String> cities;

    public NavgationAdapter(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
