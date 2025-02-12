package com.alen.weatherapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import java.util.Locale;

public class ColorMaker {

    public static void setColorGradient(View view, double tempIn, String unitLetter) {
        double temp = tempIn;
        if (unitLetter.equalsIgnoreCase("C")) {
            temp = (tempIn * 9 / 5) + 32;
        }
        int[] colorStart = getStartTemperatureColor((int) temp);
        int[] colorEnd = getEndTemperatureColor((int) temp);

        String startColorString = String.format(Locale.getDefault(), "#FF%02x%02x%02x", colorStart[0], colorStart[1], colorStart[2]);
        int startColor = Color.parseColor(startColorString);
        String endColorString = String.format(Locale.getDefault(), "#99%02x%02x%02x", colorEnd[0], colorEnd[1], colorEnd[2]);
        int endColor = Color.parseColor(endColorString);
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, endColor});
        view.setBackground(gd);
    }

    public static int[] getStartTemperatureColor(int temperature) {
        int[] rgb = new int[3];
        if (temperature < 40) {
            // Dark Blue gradient
            rgb[0] = 63;
            rgb[1] = 101;
            rgb[2] = 163;
        } else if (temperature <= 81) {
            // Dark Green gradient
            rgb[1] = 82;
            rgb[0] = 164;
            rgb[2] = 218;
        } else {
            // Dark Red gradient
            rgb[0] = 130;
            rgb[1] = 16;
            rgb[2] = 98;
        }
        return rgb;
    }

    public static int[] getEndTemperatureColor(int temperature) {
        int[] rgb = new int[3];
        if (temperature < 40) {
            // Dark Blue gradient
            rgb[0] = 207;
            rgb[1] = 171;
            rgb[2] = 168;
        } else if (temperature <= 81) {
            // Dark Green gradient
            rgb[1] = 82;
            rgb[0] = 164;
            rgb[2] = 218;
        } else {
            // Dark Red gradient
            rgb[0] = 251;
            rgb[1] = 81;
            rgb[2] = 61;
        }
        return rgb;
    }
}