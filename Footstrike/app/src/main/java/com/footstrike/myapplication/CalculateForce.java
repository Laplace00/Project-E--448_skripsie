package com.footstrike.myapplication;

public class CalculateForce {
    // method that calculates the force from the ARDUINO ADC readings
    public static float calculateForceArduino(float value) {
        final double a;
        final double b;
        final double c;
        a = 1060.84;
        b = 2937.21;
        c = -0.0117044;
        return (float) Math.max(0, Math.log((value - a) / b) / c);
    }
    // method that calculates the force from the ARDUINO external ADS ADC readings
    public static float calculateForceADS(float value) {
        final double a;
        final double b;
        final double c;
        a = 1519.06;
        b = 2545.44;
        c = -0.0101873;
        return (float) Math.max(0, Math.log((value - a) / b) / c);
    }
}
