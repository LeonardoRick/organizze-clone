package com.example.organizze_clone.helper;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class CustomDate {

    public static final String pattern = "dd/MM/yyyy";
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    /**
     *
     * @return date of today. By the way, today is 25/06/2020
     */
    public static String getCurrentDate() {
        long date = System.currentTimeMillis();
        return simpleDateFormat.format(date);
    }

    /**
     *
     * @param date user input as dd/MM/yyyy
     * @return formated string to be used as node on database as MMyyyy (032019 or 072020 for ex)
     *
     * be sure to call this method with a formated date, use ValidateDate to know that
     */
    public static String getMonthDatabaseChildNode(String date) {

        if(validateDate(date)) {
            String[] splitedDate = date.split("/"); // [day] [month] [year]
            String month = splitedDate[1];
            String year = splitedDate[2];
            return month + year;
        }
        Log.e("CustomDate", "getMonthDatabaseChildNode: invalid date");
        return null;
    };

    /**
     *
     * @param date any string like "shaudhfaous" or "02/07/2020"
     * @return true for a string that is a date on format dd/MM/yyyy and false for the rest
     */
    public static Boolean validateDate(String date) {
        // Check if date is null
        if(date == null || date.trim().equals("")) {
            return false;
        } else {
            simpleDateFormat.setLenient(false); // to block java trying to discover a weird input
            try {
               simpleDateFormat.parse(date); // tries to parse data correctly
            } catch (ParseException e) {
               return false; // if catch exception, returns false
            }
        }
        return true;
    }

    public static Boolean verifyEqualMonth(String date1, String date2) {
        if(validateDate(date1) && validateDate(date2)) {
            String[] splitedDate1 = date1.split("/");
            String monthDate1 = splitedDate1[1];

            String[] splitedDate2 = date2.split("/");
            String monthDate2 = splitedDate2[1];

            Log.d("TAG", "verifyEqualMonth1: " + monthDate1);
            Log.d("TAG", "verifyEqualMonth2: " + monthDate2);

            if(monthDate1.equals(monthDate2)) return true;
            return false;
        }
        Log.e("CustomDate", "verifyEqualMonth: Wrong date format");
        return false;
    }

    /**
     *
     * @param date on format: CalendarDay{2020-6-1}
     * @return date on format dd/MM/yyyy (01/06/2020)
     */
    public static String convertCalendarViewDate(String date) {
        if(!date.startsWith("CalendarDay")) {
            Log.e("CustomDate", "convertCalendarViewDate: Input a date on format: CalendarDay{yyy-M-d[d]}");
            return null;
        }
        String[] cleanedDate = date.split("(\\{|\\})"); // [CalendarDay], [2020-6-1]
        String[] splitedDate = cleanedDate[1].split("-"); // [2020], [6], [1]

        StringBuilder formatedDate = new StringBuilder();
        formatedDate.append(validateTwoDigits(splitedDate[2]) + "/"); // [01]
        formatedDate.append(validateTwoDigits(splitedDate[1]) + "/"); // [01/06/]
        formatedDate.append(splitedDate[0]); //[01/06/2020]

        return formatedDate.toString(); // 01/06/2020
    }

    /**
     *
     * @param dayOrMoth on format 6 or 12
     * @return formated date with two digits (if its 6, returns 06)
     */
    private static String validateTwoDigits(String dayOrMoth) {
        if(dayOrMoth.length() == 1) return "0" + dayOrMoth;
        return dayOrMoth;
    }
}
