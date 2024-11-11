package com.alen.weatherapp;

import androidx.recyclerview.widget.RecyclerView;

import com.alen.weatherapp.databinding.DailyWeatherListEntryBinding;

public class DailyViewHolder extends RecyclerView.ViewHolder {

    DailyWeatherListEntryBinding binding;

    public DailyViewHolder(DailyWeatherListEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
