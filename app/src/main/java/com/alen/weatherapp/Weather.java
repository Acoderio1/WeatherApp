package com.alen.weatherapp;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.TreeMap;

public class Weather implements Serializable {

    private double latitude;
    private double longitude;
    private String resolvedAddress;
    private String timeZone;
    private ArrayList<Day> days;
    private CurrentConditions currentConditions;
    private TreeMap<String, Double> timeTempValues;

    public Weather(double latitude, double longitude, String resolvedAddress, String timeZone, ArrayList<Day> days, CurrentConditions currentConditions, TreeMap<String, Double> timeTempValues) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.resolvedAddress = resolvedAddress;
        this.timeZone = timeZone;
        this.days = days;
        this.currentConditions = currentConditions;
        this.timeTempValues = timeTempValues;

    }

    public TreeMap<String, Double> getTimeTempValues() {
        return timeTempValues;
    }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }

    public String getResolvedAddress() { return resolvedAddress; }

    public String getTimeZone() {
        return timeZone;
    }

    public ArrayList<Day> getDays() { return days; }

    public CurrentConditions getCurrentConditions() { return currentConditions; }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

class Day implements Serializable{

    private long datetimeEpoch;
    private double tempmax;
    private double tempmin;
    private int precipprob;
    private int uvindex;
    private String conditions;
    private String description;
    private String icon;
    private ArrayList<Hour> hours;

    public Day(long datetimeEpoch, double tempmax, double tempmin,
               int precipprob, int uvindex, String conditions,
               String description, String icon, ArrayList<Hour> hours) {
        this.datetimeEpoch = datetimeEpoch;
        this.tempmax = tempmax;
        this.tempmin = tempmin;
        this.precipprob = precipprob;
        this.uvindex = uvindex;
        this.conditions = conditions;
        this.description = description;
        this.icon = icon;
        this.hours = hours;
    }

    public long getDatetimeEpoch() {
        return datetimeEpoch;
    }

    public double getTempmax() {
        return tempmax;
    }

    public double getTempmin() {
        return tempmin;
    }

    public int getPrecipprob() {
        return precipprob;
    }

    public int getUvindex() {
        return uvindex;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public ArrayList<Hour> getHours() {
        return hours;
    }

    @NonNull
    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}

class Hour implements Serializable{
    private String datetime;
    private long hdatetimeEpoch;
    private double temp;
    private double feelslike;
    private double humidity;
    private double windgust;
    private double windspeed;
    private double winddir;
    private double visibility;
    private double cloudcover;
    private int uvindex;
    private String conditions;
    private String icon;

    public Hour(String datetime, long datetimeEpoch, double temp, double feelslike,
                double humidity, double windgust, double windspeed,
                double winddir, double visibility, double cloudcover,
                int uvindex, String conditions, String icon) {
        this.datetime = datetime;
        this.hdatetimeEpoch = datetimeEpoch;
        this.temp = temp;
        this.feelslike = feelslike;
        this.humidity = humidity;
        this.windgust = windgust;
        this.windspeed = windspeed;
        this.winddir = winddir;
        this.visibility = visibility;
        this.cloudcover = cloudcover;
        this.uvindex = uvindex;
        this.conditions = conditions;
        this.icon = icon;
    }

    public String getDatetime() {
        return datetime;
    }

    public long getDatetimeEpoch() {
        return hdatetimeEpoch;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelslike() {
        return feelslike;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindgust() {
        return windgust;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public double getWinddir() {
        return winddir;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getCloudcover() {
        return cloudcover;
    }

    public int getUvindex() {
        return uvindex;
    }

    public String getConditions() {
        return conditions;
    }

    public String getIcon() {
        return icon;
    }
}

class CurrentConditions implements Serializable{
    private long datetimeEpoch;
    private double temp;
    private double feelslike;
    private double humidity;
    private double windgust;
    private double windspeed;
    private double winddir;
    private double visibility;
    private double cloudcover;
    private int uvindex;
    private String conditions;
    private String icon;
    private long sunriseEpoch;
    private long sunsetEpoch;

    public CurrentConditions(long datetimeEpoch, double temp, double feelslike,
                             double humidity, double windgust, double windspeed,
                             double winddir, double visibility, double cloudcover,
                             int uvindex, String conditions, String icon, long sunriseEpoch,
                             long sunsetEpoch) {
        this.datetimeEpoch = datetimeEpoch;
        this.temp = temp;
        this.feelslike = feelslike;
        this.humidity = humidity;
        this.windgust = windgust;
        this.windspeed = windspeed;
        this.winddir = winddir;
        this.visibility = visibility;
        this.cloudcover = cloudcover;
        this.uvindex = uvindex;
        this.conditions = conditions;
        this.icon = icon;
        this.sunriseEpoch = sunriseEpoch;
        this.sunsetEpoch = sunsetEpoch;
    }

    public long getDatetimeEpoch() {
        return datetimeEpoch;
    }

    public double getTemp() {
        return temp;
    }

    public double getFeelslike() {
        return feelslike;
    }

    public double getHumidity() {
        return humidity;
    }

    public double getWindgust() {
        return windgust;
    }

    public double getWindspeed() {
        return windspeed;
    }

    public double getWinddir() {
        return winddir;
    }

    public double getVisibility() {
        return visibility;
    }

    public double getCloudcover() {
        return cloudcover;
    }

    public int getUvindex() {
        return uvindex;
    }

    public String getConditions() {
        return conditions;
    }

    public String getIcon() {
        return icon;
    }

    public long getSunriseEpoch() {
        return sunriseEpoch;
    }

    public long getSunsetEpoch() {
        return sunsetEpoch;
    }
}
