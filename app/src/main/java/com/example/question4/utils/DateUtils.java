package com.example.question4.utils;

import java.util.Calendar;

public class DateUtils {
    
    public static long getStartOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }
    
    public static long getEndOfDay(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTimeInMillis();
    }
    
    public static long[] getTodayRange() {
        long now = System.currentTimeMillis();
        return new long[]{getStartOfDay(now), getEndOfDay(now)};
    }
    
    public static long[] getWeekRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        long startOfWeek = getStartOfDay(calendar.getTimeInMillis());
        
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        long endOfWeek = getEndOfDay(calendar.getTimeInMillis());
        
        return new long[]{startOfWeek, endOfWeek};
    }
    
    public static long[] getMonthRange() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startOfMonth = getStartOfDay(calendar.getTimeInMillis());
        
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long endOfMonth = getEndOfDay(calendar.getTimeInMillis());
        
        return new long[]{startOfMonth, endOfMonth};
    }
}