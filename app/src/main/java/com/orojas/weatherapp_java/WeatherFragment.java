package com.orojas.weatherapp_java;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WeatherFragment extends Fragment {

    private TextView cityTextView;
    private TextView tempTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weather, container, false);
        cityTextView = view.findViewById(R.id.city_text);
        tempTextView = view.findViewById(R.id.temp_text);
        return view;
    }

    public void updateWeather(String city, String temp) {
        cityTextView.setText(city);
        tempTextView.setText(temp);
    }
}
