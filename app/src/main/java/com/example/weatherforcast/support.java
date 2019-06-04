package com.example.weatherforcast;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class support {
    public static String DATE_FORMATE = "dd/MM/yyyy";

    public static String convertDate(String dateInMilliseconds) {
        return DateFormat.format(DATE_FORMATE, Long.parseLong(dateInMilliseconds) * 1000).toString();
    }
    public static String getFormattedDate(String dateInMilliseconds){
        return DateFormat.format("dd-MMM", Long.parseLong(dateInMilliseconds) * 1000).toString();
    }

    public static String getTodayDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
        String formattedDate = df.format(c);
        return formattedDate;
    }

    public static String getWeekNameFromDate(String input_date_string) {
        String dayFromDate = "";
        SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMATE);
        Date date;
        try {
            date = dateformat.parse(input_date_string);
            SimpleDateFormat dayFormate = new SimpleDateFormat("EEEE");
            dayFromDate = dayFormate.format(date);
            Log.d("asd", "----------:: " + dayFromDate);
            return dayFromDate;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }

    public static String getWeekNameFromMillies(String input_date_string) {
        String dayFromDate = "";
        String datee=DateFormat.format(DATE_FORMATE, Long.parseLong(input_date_string) * 1000).toString();
        SimpleDateFormat dateformat = new SimpleDateFormat(DATE_FORMATE);
        Date date;
        try {
            date = dateformat.parse(datee);
            SimpleDateFormat dayFormate = new SimpleDateFormat("EEEE");
            dayFromDate = dayFormate.format(date);
            Log.d("asd", "----------:: " + dayFromDate);
            return dayFromDate;

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }
    }
    public static float convertFahrenheitToCelcius(float fahrenheit) {
        return fahrenheit - 273.15f;
    }
    public static String setWeatherIcon(int actualId, int hourOfDay, Context context) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            if (hourOfDay >= 7 && hourOfDay < 20) {
                icon = context.getString(R.string.weather_sunny);
            } else {
                icon = context.getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = context.getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = context.getString(R.string.weather_drizzle);
                    break;
                case 7:
                    icon = context.getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = context.getString(R.string.weather_cloudy);
                    break;
                case 6:
                    icon = context.getString(R.string.weather_snowy);
                    break;
                case 5:
                    icon = context.getString(R.string.weather_rainy);
                    break;
            }
        }
        return icon;
    }
}
