package com.alen.weatherapp;

import static com.alen.weatherapp.MainActivity.tempSymbol;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alen.weatherapp.databinding.ActivityDailyForecastBinding;
import com.alen.weatherapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Collection;

public class DailyForecastActivity extends AppCompatActivity {
    private ArrayList<Day> dayList = new ArrayList<>();
    private Weather weather;
    private ActivityDailyForecastBinding binding;
    private DailyAdapter dailyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDailyForecastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dailyAdapter = new DailyAdapter(dayList);
        binding.dailyRecycler.setAdapter(dailyAdapter);
        binding.dailyRecycler.setLayoutManager(new LinearLayoutManager(this));

        dayList.clear();
        Intent intent = getIntent();
        if (intent.hasExtra("WEATHER")) {
            weather = (Weather) intent.getSerializableExtra("WEATHER");
            if (weather != null && !weather.getDays().isEmpty()) {
                this.dayList.addAll(weather.getDays());
                dailyAdapter.notifyItemRangeChanged(0, dayList.size());
            }
        }

        if (intent.hasExtra("CITYNAME")) {
            String cityName = intent.getStringExtra("CITYNAME");
            binding.dailyTitle.setText(cityName + " 15-Day Forecast");
        }
    }
}