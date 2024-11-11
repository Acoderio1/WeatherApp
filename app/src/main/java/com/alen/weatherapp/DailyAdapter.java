package com.alen.weatherapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alen.weatherapp.databinding.DailyWeatherListEntryBinding;

import static com.alen.weatherapp.MainActivity.getId;
import static com.alen.weatherapp.MainActivity.tempSymbol;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DailyAdapter extends RecyclerView.Adapter<DailyViewHolder> {
    private static final String TAG = "DailyAdapter";
    private final ArrayList<Day> dayList;

    public DailyAdapter(ArrayList<Day> dayList) {
        this.dayList = dayList;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: MAKING NEW");

        DailyWeatherListEntryBinding binding =
                DailyWeatherListEntryBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);

        return new DailyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        Day day = dayList.get(position);

        ColorMaker.setColorGradient(holder.binding.dailyWeather, day.getTempmax(), tempSymbol.substring(1) );
        ColorMaker.setColorGradient(holder.binding.dayAndDate, day.getTempmax(), tempSymbol.substring(1) );

        Date date = new Date(day.getDatetimeEpoch() * 1000); // Convert seconds to milliseconds
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM", Locale.US);
        String formattedDate = sdf.format(date);
        holder.binding.dayAndDate.setText(formattedDate);

        String icon = day.getIcon().replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        holder.binding.iconImage.setImageResource(iconID);

        holder.binding.minmaxTemp.setText(Math.round(day.getTempmax()) + tempSymbol + "/" + Math.round(day.getTempmin()) + tempSymbol);
        holder.binding.description.setText(day.getDescription());
        holder.binding.precipProb.setText("(" + day.getPrecipprob() + "% precip.)");
        holder.binding.uvIndexDaily.setText("UV Index: " + day.getUvindex());

        long hdatetimeEpoch = day.getDatetimeEpoch();
        long currentEpoch = Instant.now().getEpochSecond();

        if (currentEpoch < hdatetimeEpoch) {
            holder.binding.morningTemp.setText(Math.round(day.getHours().get(8).getTemp()) + tempSymbol);
            holder.binding.afternoonTemp.setText(Math.round(day.getHours().get(13).getTemp()) + tempSymbol);
            holder.binding.eveningTemp.setText(Math.round(day.getHours().get(23).getTemp()) + tempSymbol);
            holder.binding.nightTemp.setText(Math.round(day.getHours().get(17).getTemp()) + tempSymbol);
        }
    }

    @Override
    public int getItemCount() {
        return dayList.size();
    }
}
