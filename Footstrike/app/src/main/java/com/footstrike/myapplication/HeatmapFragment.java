package com.footstrike.myapplication;



import static android.os.Looper.getMainLooper;
import static com.footstrike.myapplication.MainActivity.runOnUIThread;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.footstrike.myapplication.heatmap.HeatMap;
import com.footstrike.myapplication.heatmap.HeatmapView;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HeatmapFragment extends Fragment {


    ViewPager2 viewPager;
    public static float newArch;
    public static float newMet5;
    public static float newMet3;
    public static float newMet1;
    public static float newHeelR;
    public static float newHeelL;
    public static float newHallux;
    public static float newToes;
    public static int counter;


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

        HeatmapView heatMapper = view.findViewById(R.id.heatmapView);



        HeatMap heatMap = heatMapper.getHeatmap();
        heatMap.pointRadius = 0.3f;
        heatMap.heatMax = 2048;
        heatMap.addPoint(0.43519f,1.67130f, GattHandler.data);
        heatMap.addPoint(0.62037f,1.63657f, GattHandler.data);
        heatMap.addPoint(0.62269f,1.08565f, GattHandler.data);
        heatMap.addPoint(0.24769f,0.58565f, GattHandler.data);
        heatMap.addPoint(0.45139f,0.61111f, GattHandler.data);
        heatMap.addPoint(0.65278f,0.66435f, GattHandler.data);
        heatMap.addPoint(0.27315f,0.28704f, GattHandler.data);
        heatMap.addPoint(0.50000f,0.32639f, GattHandler.data);



//        HeatMap.IHeatMappable p = (i) ->
//        {
//
//            return (float) (5 * (1 + Math.cos(i*Math.PI/6f+(counter) / 20f)));
//
//        };
//
//        ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();
//        s.scheduleAtFixedRate(() -> {
//
//                    counter++;
//                    new Handler(getMainLooper()).post(() -> {
//                        heatMapper.dataChanged();
//                    });}, 0, 10, TimeUnit.MILLISECONDS);
//
//            heatMap.addPoint(0.43519f, 1.67130f, p);
//            heatMap.addPoint(0.62037f, 1.63657f, p);
//            heatMap.addPoint(0.62269f, 1.08565f, p);
//            heatMap.addPoint(0.24769f, 0.58565f, p);
//            heatMap.addPoint(0.45139f, 0.61111f, p);
//            heatMap.addPoint(0.65278f, 0.66435f, p);
//            heatMap.addPoint(0.27315f, 0.28704f, p);
//            heatMap.addPoint(0.50000f, 0.32639f, p);



        GattHandler.runnable = heatMapper::dataChanged;



        // Inflate the layout for this fragment
        return view;

    }
}