package com.servicenow.processmining.extensions.pm.simulation.workflow;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class TimeTest
{
    @Test
    public void test1() throws ParseException
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        String dateInString1 = "2024-01-30 15:34:17";
        Date date1 = formatter.parse(dateInString1);
        String dateInString2 = "2024-02-19 18:41:44";
        Date date2 = formatter.parse(dateInString2);
        // System.out.println("d1: (" + date1.getTime() + "), d2: (" + date2.getTime() + "). Diff: (" + (date2.getTime()-date1.getTime()) + ")");
        Assert.assertEquals(1739247000, (date2.getTime()-date1.getTime()));
    }
}
