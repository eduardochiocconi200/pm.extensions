package com.servicenow.processmining.extensions.sn.core;

public class TimeUtility
{
    public static String secondsToMinutes(final String seconds)
    {
        Long secondsLong = Long.valueOf(seconds) / 60;

        return secondsLong.toString();
    }

    public static int secondsToMinutes(final int seconds)
    {
        return seconds / 60;
    }

    public static String secondsToHours(final String seconds)
    {
        Long secondsLong = Long.valueOf(seconds) / 60 / 60;

        return secondsLong.toString();
    }

    public static int secondsToHours(final int seconds)
    {
        return seconds / 60 / 60;
    }

    public static String secondsToDays(final String seconds)
    {
        Long secondsLong = Long.valueOf(seconds) / 60 / 60 / 24;

        return secondsLong.toString();
    }

    public static int secondsToDays(final int seconds)
    {
        return seconds  / 60 / 60 / 24;
    }

    public static double millisecondsToDays(final double millis)
    {
        long millisecondsInADay = 1000L * 60 * 60 * 24;
        double d = millis/millisecondsInADay;

        return d;
    }

    public static String secondsToWeeks(final String seconds)
    {
        Long secondsLong = Long.valueOf(seconds) / 60 / 60 / 24 / 7;

        return secondsLong.toString();
    }

    public static int secondsToWeeks(final int seconds)
    {
        return seconds / 60 / 60 / 24 / 7;
    }
}
