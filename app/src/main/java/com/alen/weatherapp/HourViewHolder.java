package com.alen.weatherapp;

import androidx.recyclerview.widget.RecyclerView;

import com.alen.weatherapp.databinding.HourlyWeatherListEntryBinding;

public class HourViewHolder extends RecyclerView.ViewHolder {

    HourlyWeatherListEntryBinding binding;

    public HourViewHolder(HourlyWeatherListEntryBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
