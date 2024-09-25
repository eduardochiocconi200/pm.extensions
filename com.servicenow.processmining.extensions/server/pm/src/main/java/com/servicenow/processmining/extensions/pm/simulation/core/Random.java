package com.servicenow.processmining.extensions.pm.simulation.core;

public class Random {
    public static double exponential(double mean) {
        return -mean * Math.log(Math.random());
    }
}