/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.game.ms.func;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

/**
 *
 * @author wanggang
 */
public class FuncUtils {

    private static Random randomSeed = new Random(new Date().getTime());

    public static double randomInRange(double base, double range) {
        return randomSeed.nextBoolean() == true ? base + randomSeed.nextDouble() * range : base - randomSeed.nextDouble() * range;
    }

    public static double randomInPersentRange(double base, int persentRange) {
        double range = base * persentRange / 100;
        return randomInRange(base, range);
    }

    private static final double precision = 0.001;

    public static int numberCompare(double a, double b) {
        if (a - b > precision) {
            return 1;
        } else if (b - a > precision) {
            return -1;
        }
        return 0;
    }

    public static String todayString() {
        Calendar day = Calendar.getInstance();
        TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
        day.setTimeZone(zone);
        Date currentTime = day.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    public static String yestoryString() {
        Calendar day = Calendar.getInstance();
        TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
        day.setTimeZone(zone);
        day.add(Calendar.DATE, -1);
        Date currentTime = day.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(currentTime);
    }

    public static List<String> list7dayString() {
        List<String> list = new ArrayList<>();
        Calendar day = Calendar.getInstance();
        TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
        day.setTimeZone(zone);
        {
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        {
            day.add(Calendar.DATE, -1);
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        {
            day.add(Calendar.DATE, -1);
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        {
            day.add(Calendar.DATE, -1);
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        {
            day.add(Calendar.DATE, -1);
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        {
            day.add(Calendar.DATE, -1);
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        {
            day.add(Calendar.DATE, -1);
            Date currentTime = day.getTime();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            list.add(formatter.format(currentTime));
        }
        return list;
    }

    public static Date todayStartTime() {
        Calendar today = Calendar.getInstance();
        TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
        today.setTimeZone(zone);
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        return today.getTime();
    }

    public static Date todayEndTime() {
        Calendar today = Calendar.getInstance();
        TimeZone zone = TimeZone.getTimeZone("GMT+8:00");
        today.setTimeZone(zone);
        today.set(Calendar.HOUR_OF_DAY, 23);
        today.set(Calendar.MINUTE, 59);
        today.set(Calendar.SECOND, 59);
        today.set(Calendar.MILLISECOND, 999);
        return today.getTime();
    }

}
