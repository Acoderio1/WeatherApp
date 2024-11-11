package com.alen.weatherapp;

import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeMap;

public class WeatherLoaderVolley {
    private static final String TAG = "WeatherLoaderRunnable";
    private static final String DATA_URL = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    private static final String APIKey = "9RRXRRNQQTR7YAVGU8WAS94YH";
    private static RequestQueue queue;
    private static MainActivity mainActivity;
    public static String timezone;

    public static void getSourceData(MainActivity mainActivityIn, String locationString, String unit) {
        mainActivity = mainActivityIn;

        queue = Volley.newRequestQueue(mainActivityIn);

        Uri.Builder buildURL = Uri.parse(DATA_URL).buildUpon();
        buildURL.appendPath(locationString);
        buildURL.appendQueryParameter("unitGroup", unit);
        buildURL.appendQueryParameter("key", APIKey);

        String urlToUse = buildURL.build().toString();

        Response.Listener<JSONObject> listener =
                response -> parseJSON(response.toString());

        Response.ErrorListener error =
                error1 -> mainActivity.updateData(null);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse,
                        null, listener, error);

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }


    private static void parseJSON(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.downloadFailed();
            return;
        }
        try {
            JSONObject jObjMain = new JSONObject(s);
            Log.d(TAG, "got weather results" + jObjMain);

            double  latitude = jObjMain.getDouble("latitude");
            double longitude = jObjMain.getDouble("longitude");
            String resolvedAddress = jObjMain.getString("resolvedAddress");
            String timeZone = jObjMain.getString("timezone");
            timezone = timeZone;
            JSONArray days = jObjMain.getJSONArray("days");
            ArrayList<Day> dayArrayList = new ArrayList<>();
            TreeMap<String, Double> timeTempValues = new TreeMap<>();

            ZoneId zoneId = ZoneId.of(timeZone);
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
            long currentEpoch = zonedDateTime.toEpochSecond();

            for (int i = 0; i < days.length(); i++) {
                JSONObject day = (JSONObject) days.get(i);

                Long datetimeEpoch = day.getLong("datetimeEpoch");
                double tempmax = day.getDouble("tempmax");
                double tempmin = day.getDouble("tempmin");
                Integer preciprob = day.getInt("precipprob");
                Integer uvindex = day.getInt("uvindex");
                String conditions = day.getString("conditions");
                String description = day.getString("description");
                String icon = day.getString("icon");

                JSONArray hours = day.getJSONArray("hours");
                ArrayList<Hour> hourArrayList = new ArrayList<>();
                for (int j = 0; j < hours.length(); j++){
                    JSONObject hour = (JSONObject) hours.get(j);

                    String datetime = hour.getString("datetime");
                    long hdatetimeEpoch = hour.getLong("datetimeEpoch");
                    double temp = hour.getDouble("temp");
                    double feelslike = hour.getDouble("feelslike");
                    double humidity = hour.getDouble("humidity");
                    double windgust = 0.0;
                    if (hour.has("windgust") && !hour.isNull("windgust")) {
                        windgust = hour.getDouble("windgust");
                    }
                    double windspeed = hour.getDouble("windspeed");
                    double winddir = hour.getDouble("winddir");
                    double visibility = hour.getDouble("visibility");
                    double cloudcover = hour.getDouble("cloudcover");
                    int huvindex = hour.getInt("uvindex");
                    String hconditions = hour.getString("conditions");
                    String hicon = hour.getString("icon");

                    if (i < 1) {
                        timeTempValues.put(datetime, temp);
                    }

                    if (currentEpoch < hdatetimeEpoch) {
                        hourArrayList.add(new Hour(datetime, hdatetimeEpoch, temp, feelslike,
                                humidity, windgust, windspeed,
                                winddir, visibility, cloudcover,
                                huvindex, hconditions, hicon));
                    }
                }
                if (currentEpoch < datetimeEpoch) {
                    dayArrayList.add(new Day(datetimeEpoch, tempmax, tempmin,
                            preciprob, uvindex, conditions,
                            description, icon, hourArrayList));
                }
            }

            JSONObject currentConditions = jObjMain.getJSONObject("currentConditions");
            long datetimeEpoch = currentConditions.getLong("datetimeEpoch");
            double temp = currentConditions.getLong("temp");
            double feelslike = currentConditions.getDouble("feelslike");
            double humidity = currentConditions.getDouble("humidity");
            double windgust = 0.0;
            if (currentConditions.has("windgust") && !currentConditions.isNull("windgust")) {
                windgust = currentConditions.getDouble("windgust");
            }
            double windspeed = currentConditions.getDouble("windspeed");
            double winddir = currentConditions.getDouble("winddir");
            double visibility = currentConditions.getDouble("visibility");
            double cloudcover = currentConditions.getDouble("cloudcover");
            int uvindex = currentConditions.getInt("uvindex");
            String conditions = currentConditions.getString("conditions");
            String icon = currentConditions.getString("icon");
            long sunriseEpoch = currentConditions.getLong("sunriseEpoch");
            long sunsetEpoch = currentConditions.getLong("sunsetEpoch");
            CurrentConditions currentConditions1 = new CurrentConditions(datetimeEpoch, temp, feelslike,
                                                                        humidity, windgust, windspeed, winddir,
                                                                        visibility, cloudcover, uvindex, conditions,
                                                                        icon, sunriseEpoch, sunsetEpoch);

            Weather weather = new Weather(latitude, longitude,
                                            resolvedAddress, timeZone, dayArrayList,
                                            currentConditions1, timeTempValues);
            mainActivity.updateData(weather);
            Log.d(TAG, "parseJSON: " +  weather);

        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());

        }
    }
}
