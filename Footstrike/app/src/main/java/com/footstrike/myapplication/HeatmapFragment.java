package com.footstrike.myapplication;



import static com.footstrike.myapplication.MainActivity.runOnUIThread;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import ca.hss.heatmaplib.HeatMap;

public class HeatmapFragment extends Fragment {


    ViewPager2 viewPager;
    public static float newArch = 4096 - GattHandler.arch;
    public static float newMet5 = 4096 -GattHandler.met5;
    public static float newMet3 = 4096 -GattHandler.met3;
    public static float newMet1 = 4096 -GattHandler.met1;
    public static float newHeelR = 4096 -GattHandler.heelR;
    public static float newHeelL = 4096 -GattHandler.heelL;
    public static float newHallux = 4096 -GattHandler.hallux;
    public static float newToes = 4096 -GattHandler.toes;


    public HeatmapFragment(ViewPager2 viewPager) {
        this.viewPager = viewPager;
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_heatmap, container, false);

        HeatMap heatMap = (HeatMap) view.findViewById(R.id.heatmap);

        heatMap.setMinimum(0.0);
        heatMap.setMaximum(2000.0);
        heatMap.setRadius(950);
        HeatMap.DataPoint archPoint = new HeatMap.DataPoint(0.6f, 0.55f, newArch);
        heatMap.addData(archPoint);
        HeatMap.DataPoint met5Point = new HeatMap.DataPoint(0.7f, 0.3f, newMet5);
        heatMap.addData(met5Point);
        HeatMap.DataPoint met3Point = new HeatMap.DataPoint(0.5f, 0.27f, newMet3);
        heatMap.addData(met3Point);
        HeatMap.DataPoint met1Point = new HeatMap.DataPoint(0.3f, 0.24f, newMet1);
        heatMap.addData(met1Point);
        HeatMap.DataPoint heelRPoint = new HeatMap.DataPoint(0.52f, 0.9f, newHeelR);
        heatMap.addData(heelRPoint);
        HeatMap.DataPoint heelLPoint = new HeatMap.DataPoint(0.37f, 0.9f, newHeelL);
        heatMap.addData(heelLPoint);
        HeatMap.DataPoint halluxPoint = new HeatMap.DataPoint(0.37f, 0.11f, newHallux);
        heatMap.addData(halluxPoint);
        HeatMap.DataPoint toesPoint = new HeatMap.DataPoint(0.6f, 0.12f, newToes);
        heatMap.addData(toesPoint);

        GattHandler.runnable = () -> {
            newMet5 = 4096 -GattHandler.met5;
            newArch = 4096 - GattHandler.arch;
            newMet3 = 4096 -GattHandler.met3;
            newMet1 = 4096 -GattHandler.met1;
            newHeelR = 4096 -GattHandler.heelR;
            newHeelL = 4096 -GattHandler.heelL;
            newHallux = 4096 -GattHandler.hallux;
            newToes = 4096 -GattHandler.toes;
            if (archPoint.value - newArch > 100 || archPoint.value - newArch < -100
                    || met5Point.value - newMet5 > 100 || met5Point.value - newMet5 < -100
                    || met3Point.value - newMet3 > 100 || met3Point.value - newMet3 < -100
                    || met1Point.value - newMet1 > 100 || met1Point.value - newMet1 < -100
                    || heelRPoint.value - newHeelR > 100 || heelRPoint.value - newHeelR < -100
                    || heelLPoint.value - newHeelL > 100 || heelLPoint.value - newHeelL < -100
                    || halluxPoint.value - newHallux > 100 || halluxPoint.value - newHallux < -100
                    || toesPoint.value - newToes > 100 || toesPoint.value - newToes < -100) {
                heatMap.forceRefresh();
            }
                archPoint.value =newArch;
                met5Point.value = newMet5;
                met3Point.value = newMet3;
                met1Point.value = newMet1;
                heelRPoint.value = newHeelR;
                heelLPoint.value = newHeelL;
                halluxPoint.value = newHallux;
                toesPoint.value = newToes;
        };



        // Inflate the layout for this fragment
        return view;
    }
}