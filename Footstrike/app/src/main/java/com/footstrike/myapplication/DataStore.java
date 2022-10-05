package com.footstrike.myapplication;

import androidx.annotation.NonNull;

import com.footstrike.myapplication.heatmap.HeatMap;

public class DataStore implements HeatMap.IHeatMappable {
    public float archVal;
    public float met5Val;
    public float met3Val;
    public float met1Val;
    public float heelrVal;
    public float heellVal;
    public float halluxVal;
    public float toesVal;
    public float steps;
    public long timeStamp;


    @NonNull
    @Override
    public String toString() {
        return timeStamp + "," + archVal + "," + met5Val + "," + met3Val + "," + met1Val + "," + heelrVal + "," + heellVal + "," + halluxVal + "," + toesVal + "," + steps;
    }

    // Method to store all the data that is to be written to a file
    public DataStore(String string){
        String[] strings = string.split(",");
        timeStamp = Long.parseLong(strings[0]);
        archVal = Float.parseFloat(strings[1]);
        met5Val = Float.parseFloat(strings[2]);
        met3Val = Float.parseFloat(strings[3]);
        met1Val = Float.parseFloat(strings[4]);
        heelrVal = Float.parseFloat(strings[5]);
        heellVal = Float.parseFloat(strings[6]);
        halluxVal = Float.parseFloat(strings[7]);
        toesVal = Float.parseFloat(strings[8]);
        steps = Float.parseFloat(strings[9]);
    }

    // method to get the current time in milliseconds
    public DataStore() {
        timeStamp = System.currentTimeMillis();
    }

    // method to make a copy of the all ready stored data
    public DataStore copy(){
        DataStore data = new DataStore();
        data.archVal = this.archVal;
        data.met5Val = this.met5Val;
        data.met3Val = this.met3Val;
        data.met1Val = this.met1Val;
        data.heelrVal = this.heelrVal;
        data.heellVal = this.heellVal;
        data.halluxVal = this.halluxVal;
        data.toesVal = this.toesVal;
        data.steps = this.steps;
        return data;
    }

    // method to copy from the all ready stored data
    public void copyFrom(DataStore data){

        this.archVal = data.archVal;
        this.met5Val = data.met5Val;
        this.met3Val = data.met3Val;
        this.met1Val = data.met1Val;
        this.heelrVal = data.heelrVal;
        this.heellVal = data.heellVal;
        this.halluxVal = data.halluxVal;
        this.toesVal = data.toesVal;
        this.steps = data.steps;
    }


    @Override
    public float getHeat(int index) {
        return getForce(index);
    }
    public float getForce(int index) {

        switch(index){
            case 0:
                return archVal ;
            case 1:
                return met5Val ;
            case 2:
                return met3Val;
            case 3:
                return met1Val;
            case 4:
                return heelrVal;
            case 5:
                return heellVal;
            case 6:
                return halluxVal;
            case 7:
            default:
                return toesVal;

        }
    }
//    public float getForce(int index) {
//        final double a;
//        final double b;
//        final double c;
//        if(index == 0 || index == 1){
//            a = 1519.06;
//            b = 2545.44;
//            c = -0.0101873;
//        }else {
//            a = 1060.84;
//            b = 2937.21;
//            c = -0.0117044;
//        }
//        return (float) Math.max(0, Math.log((getADC(index) - a) / b) / c);
//    }
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


