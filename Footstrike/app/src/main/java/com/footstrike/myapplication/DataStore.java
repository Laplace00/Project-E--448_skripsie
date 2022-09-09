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
    public long timeStamp;


    @NonNull
    @Override
    public String toString() {
        return timeStamp + "," + archVal + "," + met5Val + "," + met3Val + "," + met1Val + "," + heelrVal + "," + heellVal + "," + halluxVal + "," + toesVal;
    }

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
    }

    public DataStore() {
        timeStamp = System.currentTimeMillis();
    }

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
        return data;
    }

    public void copyFrom(DataStore data){

        this.archVal = data.archVal;
        this.met5Val = data.met5Val;
        this.met3Val = data.met3Val;
        this.met1Val = data.met1Val;
        this.heelrVal = data.heelrVal;
        this.heellVal = data.heellVal;
        this.halluxVal = data.halluxVal;
        this.toesVal = data.toesVal;
    }


    @Override
    public float getHeat(int index) {
        float out = 69;
        switch(index){
            case 0:
                out = heellVal;
                break;

            case 1:
                out = heelrVal;
                break;

            case 2:
                out = archVal;
                break;

            case 3:
                out = met1Val;
                break;

            case 4:
                out = met3Val;
                break;

            case 5:
                out = met5Val;
                break;

            case 6:
                out = halluxVal;
                break;

            case 7:
                out = toesVal;
                break;

        }
        return 4096 - out ;
    }
}


