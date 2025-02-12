package com.alen.weatherapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alen.weatherapp.databinding.ActivityMainBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.TimeZone;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_REQUEST = 111;
    private final ArrayList<Hour> hourList = new ArrayList<>();
    private Weather weather;

    private static String locationString = "Unspecified Location";
    public static String tempSymbol = "°F";
    public static String tempUnit = "us";

    public static String locationCoordinates;
    public static String locationName;
    public static String cityName;
    public static String zipCode;
    public static String forecast;
    public static String now;
    public static String humidity;
    public static String winds;
    public static String searchString;

    private FusedLocationProviderClient mFusedLocationClient;
    private HourAdapter hourAdapter;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ActivityMainBinding binding;
    private ConnectivityManager connectivityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mFusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);
        if (savedInstanceState == null) {
            determineLocation();
        }
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleEditResult);
        connectivityManager = getSystemService(ConnectivityManager.class);
        checkNet();

        binding.main.setOnRefreshListener(() -> {
            WeatherLoaderVolley.getSourceData(this, locationString, tempUnit);
            binding.main.setRefreshing(false); // This stops the busy-circle
        });

        hourAdapter = new HourAdapter(hourList);
        binding.hourRecycler.setAdapter(hourAdapter);
        binding.hourRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void handleEditResult(ActivityResult activityResult) {
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putSerializable("WEATHER", weather);
        // Call super last
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        // Call super first
        super.onRestoreInstanceState(savedInstanceState);
        weather = (Weather) savedInstanceState.getSerializable("WEATHER");
        updateData(weather);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST) {
            if (permissions[0].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    determineLocation();
                }
            }
        }
    }

    //Icon bar utilities
    public void goToSelectedLocation(View view) {
        Uri mapUri = Uri.parse("geo:" + locationCoordinates + "?q=" + locationName);

        Intent intent = new Intent(Intent.ACTION_VIEW, mapUri);

        // Check if there is an app that can handle geo intents
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
//            makeErrorAlert("No Application found that handles ACTION_VIEW (geo) intents");
        }
    }

    public void resetLocation (View v) {
        determineLocation();
    }

    public void shareWeather (View v) {
        getDeviceInfo();
        String shareText = "Weather for " + cityName + " " + " (" + zipCode + " )";
        String extraText = String.format(
                shareText +
                        "\nForecast: %s\n" +
                        "Now: %s\n" +
                        "Humidity: %s\n" +
                        "Winds: %s\n" +
                        "%s\n" +
                        "%s\n" +
                        "%s\n" +
                        "%s",
                forecast, now,
                humidity, winds, binding.uvIndex.getText(), binding.sunrise.getText(), binding.sunset.getText(), binding.visibility.getText()
        );

        if (shareText.isEmpty()) {
            return;
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, shareText);
        sendIntent.putExtra(Intent.EXTRA_TEXT, extraText);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, "Share to...");
        startActivity(shareIntent);
    }

    private String getDeviceInfo() {
        return MessageFormat.format("MODEL: {0}\n", Build.MODEL) +
                MessageFormat.format("ID: {0}\n", Build.ID) +
                MessageFormat.format("Manufacturer: {0}\n", Build.MANUFACTURER) +
                MessageFormat.format("Device: {0}\n", Build.DEVICE) +
                MessageFormat.format("Display: {0}\n", Build.DISPLAY) +
                MessageFormat.format("Build Time: {0}\n", new Date(Build.TIME)) +
                MessageFormat.format("Build: {0}\n", Build.VERSION.RELEASE);
    }

    public void unitChange(View v) {
        if (tempSymbol.equals("°F")) {
            tempSymbol = "°C";
            tempUnit = "metric";
            binding.unitIcon.setImageResource(R.drawable.units_c);
            WeatherLoaderVolley.getSourceData(this, locationString, tempUnit);
        } else {
            tempSymbol = "°F";
            tempUnit = "us";
            binding.unitIcon.setImageResource(R.drawable.units_f);
            WeatherLoaderVolley.getSourceData(this, locationString, tempUnit);
        }

    }

    public void openDailyActivity(View v) {
        Intent intent = new Intent(this, DailyForecastActivity.class);
        intent.putExtra("WEATHER", weather);
        intent.putExtra("CITYNAME", cityName);
        activityResultLauncher.launch(intent);
    }

    public void enterLocation(View v) {
        dialog("SETLOCATION");
    }

    public void dialog(String type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);

        if (type.equals("SETLOCATION")) {
            builder.setTitle("Enter a Location");
            builder.setMessage("For US locations, enter as 'City', \n or 'City, State' \n\n " +
                                "For International locations enter as 'City, Country'");
            builder.setNegativeButton("Cancel",(dialog, id) -> {
            });
            et.setInputType(InputType.TYPE_CLASS_TEXT);
            et.setGravity(Gravity.CENTER_HORIZONTAL);
            builder.setView(et);
        }

        if (type.equals("NETWORKERROR")) {
            builder.setIcon(R.drawable.alert);
            builder.setTitle("No Internet Connection");
            builder.setMessage("This app requires an internet\n" +
                    "connection to function properly.\n" +
                    "Please check your connection and try again.");
        }

        if (type.equals("DATAERROR")) {
            builder.setIcon(R.drawable.alert);
            builder.setTitle("Weather Data Error");
            builder.setMessage("There was an error retrieving the\n" +
                    "weather data. Please try again\n" +
                    "later.");
        }

        if (type.equals("LOCATIONERROR")) {
            builder.setIcon(R.drawable.alert);
            builder.setTitle("Location Error");
            builder.setMessage("The specified location\n" +
                     "\"" + searchString + "\"" + " could not be\n" +
                    "resolved. Please try a different\n" +
                    "location.");
        }

        builder.setPositiveButton("Ok",(dialog, id) -> {
            if (type.equals("SETLOCATION")) {
                searchString = et.getText().toString();
                WeatherLoaderVolley.getSourceData(this, searchString, tempUnit);
                hourList.clear();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void determineLocation() {
        // Check for location permission - if not then start the request and return
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
            return;
        }

        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    // Got last known location. In some situations this can be null.
                    if (location != null) {
                        locationString = getPlaceName(location.getLatitude(), location.getLongitude());
                        WeatherLoaderVolley.getSourceData(this, locationString, tempUnit);
                    } else {
                        Log.d(TAG, "determineLocation: NULL LOCATION");
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.d(TAG, "determineLocation: FAILURE");
                    Toast.makeText(MainActivity.this,
                            e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private String getPlaceName(double latitude, double longitude ) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                cityName = addresses.get(0).getLocality();
                zipCode = addresses.get(0).getPostalCode();
                Log.d(TAG, "getPlaceName: " + cityName);
            }
        } catch (IOException e) {
            Log.d(TAG, "getPlaceName: " + e.getMessage());
        }
        return cityName;
    }

    public void updateData(Weather weather) {
        Calendar cal = Calendar.getInstance();
        if (weather == null) {
            dialog("LOCATIONERROR");
            return;
        }

        this.weather = weather;
        locationName = weather.getResolvedAddress();
        locationCoordinates = weather.getLatitude() + "," + weather.getLongitude();
        forecast = weather.getCurrentConditions().getConditions() + " with a high of " + weather.getDays().get(0).getTempmax() + tempSymbol + " and a low of " + weather.getDays().get(0).getTempmin() + tempSymbol;
        now = weather.getCurrentConditions().getTemp() + tempSymbol + ", " + weather.getCurrentConditions().getConditions() + " ( Feels like: " + weather.getCurrentConditions().getFeelslike() + tempSymbol + " )";
        humidity = Math.round(weather.getCurrentConditions().getHumidity()) + "%";
        winds = getDirection(weather.getCurrentConditions().getTemp()) + " at " + weather.getCurrentConditions().getWindspeed() + " mph";
        locationString = getPlaceName(weather.getLatitude(), weather.getLongitude());

        ColorMaker.setColorGradient(binding.main, weather.getCurrentConditions().getTemp(), tempSymbol.substring(1) );
//        ColorMaker.setColorGradient(binding.iconBar, weather.getCurrentConditions().getTemp(), tempSymbol.substring(1) );

        String[] parts = weather.getResolvedAddress().split(",");
        TimeZone timeZone = TimeZone.getTimeZone(weather.getTimeZone());
        cal.setTimeZone(timeZone);

        String resolvedAddress = parts[0] + "";
        binding.resolvedAddress.setText(resolvedAddress);
        binding.dateTime.setText(dateFormatter("EEE dd, h:mm a",timeZone, cal.getTime()));
        binding.temperature.setText((Math.round(weather.getCurrentConditions().getTemp())) + tempSymbol);

        String icon = weather.getCurrentConditions().getIcon().replace("-", "_");
        int iconID = getId(icon, R.drawable.class);
        if (iconID == 0) {
            iconID = R.mipmap.ic_launcher;
        }
        binding.weatherIcon.setImageResource(iconID);

        binding.feelsLike.setText(String.format(this.getString(R.string.feelsLike),
                Math.round(weather.getCurrentConditions().getFeelslike()), tempSymbol));
        binding.weatherDescription.setText(String.format(this.getString(R.string.weatherDescription),
                weather.getCurrentConditions().getConditions(),
                weather.getCurrentConditions().getCloudcover()));
        binding.windDir.setText("");
//        if (weather.getCurrentConditions().getWindgust() == 0.0) {
//            binding.windDir.setText(String.format(
//                    this.getString(R.string.nowindgust),
//                    getDirection(weather.getCurrentConditions().getWinddir()),
//                    weather.getCurrentConditions().getWindspeed()));
//        } else {
//            binding.windDir.setText(String.format(
//                    this.getString(R.string.windDir),
//                    getDirection(weather.getCurrentConditions().getWinddir()),
//                    weather.getCurrentConditions().getWindspeed(),
//                    weather.getCurrentConditions().getWindgust()));
//        }
        binding.humidity.setText(String.format(this.getString(R.string.humidity), weather.getCurrentConditions().getHumidity()));
        binding.uvIndex.setText(String.format(this.getString(R.string.uvIndex), weather.getCurrentConditions().getUvindex()));
        binding.visibility.setText(String.format(this.getString(R.string.visibility), weather.getCurrentConditions().getVisibility()));

        TreeMap<String, Double> tempPoints = weather.getTimeTempValues();
        ChartMaker chartMaker = new ChartMaker(this, binding, timeZone);
        chartMaker.makeChart(tempPoints, System.currentTimeMillis());

        hourList.clear();
        binding.hourRecycler.setBackgroundColor(Color.parseColor("#40FFFFFF"));
        for (int i = 0; i < 3; i++) {
            Day day = weather.getDays().get(i);
            hourList.addAll(day.getHours());
        }
        hourAdapter.notifyDataSetChanged();

        Date sunrisedate = new Date(weather.getCurrentConditions().getSunriseEpoch() * 1000); // Convert seconds to milliseconds
        binding.sunrise.setText(String.format(
                this.getString(R.string.sunrise),
                dateFormatter("h:mm a",timeZone, sunrisedate)
        ));
        Date sunsetdate = new Date(weather.getCurrentConditions().getSunsetEpoch() * 1000);
        binding.sunset.setText(String.format(
                this.getString(R.string.sunset),
                dateFormatter("h:mm a",timeZone, sunsetdate)
        ));
    }

    public Boolean checkNet() {
        Network currentNetwork = connectivityManager.getActiveNetwork();
        if (currentNetwork == null) {
            dialog("NETWORKERROR");
            return false;
        }
        return true;
    }

    private String getDirection(double degrees) {
        if (degrees >= 337.5 || degrees < 22.5)
            return "N";
        if (degrees >= 22.5 && degrees < 67.5)
            return "NE";
        if (degrees >= 67.5 && degrees < 112.5)
            return "E";
        if (degrees >= 112.5 && degrees < 157.5)
            return "SE";
        if (degrees >= 157.5 && degrees < 202.5)
            return "S";
        if (degrees >= 202.5 && degrees < 247.5)
            return "SW";
        if (degrees >= 247.5 && degrees < 292.5)
            return "W";
        if (degrees >= 292.5 && degrees < 337.5)
            return "NW";
        return "X"; // We'll use 'X' as the default if we get a bad value
    }

    public static int getId(String resourceName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resourceName);
            return idField.getInt(idField);
        } catch (Exception e) {
            return 0;
        }
    }


    public String dateFormatter(String pattern,TimeZone timeZone, Date date) {
        SimpleDateFormat  dateFormat = new SimpleDateFormat(pattern);
        dateFormat.setTimeZone(timeZone);
        return dateFormat.format(date);
    }

    public void downloadFailed() {
        Log.d(TAG, "handleResults: Failure in data download");
        dialog("DATAERROR");
//        hourAdapter.notifyItemRangeChanged(0, artWorkList.size());
    }

//    public void displayChartTemp(float time, float tempVal) {
//        SimpleDateFormat sdf =
//                new SimpleDateFormat("h a", Locale.US);
//        Date d = new Date((long) time);
//        new Thread(() -> {
//            try {
//                Thread.sleep(5000);
//                runOnUiThread(() -> binding.chartTemp.setVisibility(View.GONE));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//    }
}