package com.apptronix.nitkonschedule;


import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import timber.log.Timber;

/**
 * Created by DevOpsTrends on 7/4/2017.
 */

public class Utils {

    public static String convertToDate(int date){ //say date is 20171214
        int day = date%100; //day is 14
        int month = ((date%10000)-day)/100;
        String monthString;
        //int year = (date-(month*100)-day)/10000;

        switch(month){
            case 1: monthString="JAN";
                break;
            case 2: monthString="FEB";
                break;
            case 3: monthString="MAR";
                break;
            case 4: monthString="APR";
                break;
            case 5: monthString="MAY";
                break;
            case 6: monthString="JUN";
                break;
            case 7: monthString="JUL";
                break;
            case 8: monthString="AUG";
                break;
            case 9: monthString="SEP";
                break;
            case 10: monthString="OCT";
                break;
            case 11: monthString="NOV";
                break;
            case 12: monthString="DEC";
                break;
            default:
                return String.valueOf(date);
        }

        return String.valueOf(day)+ " " + monthString;
    }

    public static int getTodayDate() {

        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) * 10000 + (c.get(Calendar.MONTH)+1) * 100 + c.get(Calendar.DATE);
    }

    public static String setTime(int time) {

        int hours=(time-time%100)/100;
        int mins=time-(hours*100);
        Timber.i("schedule %d %d %d",time,hours,mins);
        return hours+":"+mins;
    }

    public static String timeFromInt(int time){
        int hours=(time-time%100)/100;
        int mins=time-(hours*100);
        String suffix="am";
        if(hours>12){
            hours=hours-12;
            suffix="pm";
        }
        return hours+":"+mins+suffix;
    }

    public static int getDateInt(int startDate, int position) {

        int day = startDate%100; //day is 14
        int month = ((startDate%10000)-day)/100;
        int year = (startDate-(month*100)-day)/10000;

        Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        c.add(Calendar.DAY_OF_MONTH,position-3);

        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        return year*10000+(month)*100+day;
    }

    @NotNull
    public static String getDay(int startDate, int position) {

        position+=4;
        int day = startDate%100; //day is 14
        int month = ((startDate%10000)-day)/100;
        int year = (startDate-(month*100)-day)/10000;

        Calendar c = Calendar.getInstance();
        c.set(year,month,day);
        c.add(Calendar.DAY_OF_MONTH,position+2);

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        return dayFormat.format(c.getTime());

    }


}
