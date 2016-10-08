package com.example.yininghuang.weather.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yininghuang.weather.R;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.net.Constants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<WeatherList.Weather> weathers = new ArrayList<>();
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        if (mOnItemClickListener == null) {
            this.mOnItemClickListener = listener;
        }
    }

    public SearchResultAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final WeatherList.Weather weather = weathers.get(position);
        holder.bindData(weather, context);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(weather);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return weathers.size();
    }

    public void addWeather(WeatherList.Weather weather) {
        weathers.clear();
        weathers.add(weather);
        notifyDataSetChanged();
    }

    interface OnItemClickListener {
        void onItemClick(WeatherList.Weather weather);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cityNameText)
        TextView cityNameText;

        @BindView(R.id.cityTempText)
        TextView cityTempText;

        @BindView(R.id.citWeatherImage)
        ImageView cityWeatherImage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindData(WeatherList.Weather weather, Context context) {
            cityNameText.setText(weather.getBasicCityInfo().getCityName());
            cityTempText.setText(context.getResources().getString(R.string.degree, weather.getNowWeather().getTemperature()));
            cityWeatherImage.setImageResource(Constants.getWeatherImage(weather.getNowWeather().getWeatherStatus().getWeatherCode(), context));
        }
    }
}
