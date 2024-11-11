package com.alen.weatherapp;

import static com.alen.weatherapp.MainActivity.getId;
import static com.alen.weatherapp.MainActivity.tempSymbol;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alen.weatherapp.databinding.HourlyWeatherListEntryBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class HourAdapter extends RecyclerView.Adapter<HourViewHolder> {
    private static final String TAG = "HourAdapter";
    private final List<Hour> hourList;

    public HourAdapter(ArrayList<Hour> hourList) {
        this.hourList = hourList;
    }


    @NonNull
    @Override
    public HourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");

        HourlyWeatherListEntryBinding binding =
                HourlyWeatherListEntryBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);

        return new HourViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HourViewHolder holder, int position) {
        Hour hour = hourList.get(position);

        Date date = new Date(hour.getDatetimeEpoch() * 1000); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        TimeZone timeZone = TimeZone.getTimeZone(WeatherLoaderVolley.timezone);
        sdf.setTimeZone(timeZone);
        String formattedDate = sdf.format(date);
        holder.binding.dayTitleText.setText(formattedDate);

        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");  // Input format (24-hour)
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm a");
        try {
            Date date1 = inputFormat.parse(hour.getDatetime());
            String formattedTime = outputFormat.format(date1);
            holder.binding.time.setText(formattedTime);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        String icon = hour.getIcon().replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        holder.binding.hourIcon.setImageResource(iconID);

        holder.binding.hourTemperature.setText(Math.round(hour.getTemp()) + tempSymbol);
        holder.binding.hourForecast.setText(hour.getConditions());
    }

    @Override
    public int getItemCount() {
        return hourList.size();
    }
}
