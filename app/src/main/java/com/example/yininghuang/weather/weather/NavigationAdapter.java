package com.example.yininghuang.weather.weather;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yininghuang.weather.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class NavigationAdapter extends RecyclerView.Adapter<NavigationAdapter.ViewHolder> {

    private List<String> cities;
    private OnNavigationItemClickListener mOnNavigationItemClickListener;

    public NavigationAdapter(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String name = cities.get(position);
        holder.getCityNameText().setText(name);
        ImageView deleteBtn = holder.getDeleteBtn();
        if (position == 0) {
            deleteBtn.setImageResource(R.drawable.ic_location_on_grey_700_24dp);
            deleteBtn.setClickable(false);
        } else {
            deleteBtn.setImageResource(R.drawable.ic_close_grey_700_24dp);
            deleteBtn.setClickable(true);
            holder.getDeleteBtn().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnNavigationItemClickListener.onDrawerItemDelete(name);
                }
            });
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnNavigationItemClickListener.onDrawerItemClick(name);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public void setOnNavigationItemClickListener(OnNavigationItemClickListener listener) {
        if (mOnNavigationItemClickListener == null) {
            this.mOnNavigationItemClickListener = listener;
        }
    }

    interface OnNavigationItemClickListener {

        void onDrawerItemClick(String name);

        void onDrawerItemDelete(String name);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cityNameText)
        TextView cityNameText;

        @BindView(R.id.deleteCity)
        ImageView deleteBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public TextView getCityNameText() {
            return cityNameText;
        }

        public ImageView getDeleteBtn() {
            return deleteBtn;
        }
    }
}
