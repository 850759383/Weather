package com.example.yininghuang.weather.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yininghuang.weather.R;
import com.example.yininghuang.weather.model.Weather.WeatherList;
import com.example.yininghuang.weather.utils.Utils;
import com.example.yininghuang.weather.weather.WeatherActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Yining Huang on 2016/9/29.
 */

public class SearchFragment extends Fragment implements SearchContract.View, SearchResultAdapter.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.searchText)
    EditText searchText;

    @BindView(R.id.resultRec)
    RecyclerView resultRec;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private SearchPresenter presenter;
    private SearchResultAdapter adapter;

    public static SearchFragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void setRefreshStatus(final Boolean active) {
        swipeRefreshLayout.setRefreshing(active);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        super.onViewCreated(view, savedInstanceState);
    }

    private void initView() {
        ((SearchActivity) getActivity()).setSupportActionBar(toolbar);
        ((SearchActivity) getActivity()).getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        ((SearchActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout.setEnabled(false);
        setHasOptionsMenu(true);
        adapter = new SearchResultAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        resultRec.setLayoutManager(new LinearLayoutManager(getActivity()));
        resultRec.setAdapter(adapter);
        presenter = new SearchPresenter(this);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search: {
                performSearch();
                break;
            }
            case android.R.id.home: {
                getActivity().onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void performSearch() {
        String city = Utils.formatCityName(searchText.getText().toString());
        if (!TextUtils.isEmpty(city) && !swipeRefreshLayout.isRefreshing()) {
            presenter.search(city);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onStop();
    }

    @Override
    public void setSearchResult(@Nullable WeatherList.Weather weather) {
        if (weather != null) {
            adapter.addWeather(weather);
        } else {
            Toast.makeText(getActivity(), "未找到城市", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(WeatherList.Weather weather) {
        presenter.saveToDB(weather);
        Intent intent = new Intent(getActivity(), WeatherActivity.class);
        intent.putExtra("city", weather.getBasicCityInfo().getCityName());
        startActivity(intent);
        getActivity().finish();
    }
}
