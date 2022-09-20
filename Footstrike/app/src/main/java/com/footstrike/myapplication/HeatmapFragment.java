package com.footstrike.myapplication;



import static android.os.Looper.getMainLooper;
import static com.footstrike.myapplication.MainActivity.runOnUIThread;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.footstrike.myapplication.heatmap.HeatMap;
import com.footstrike.myapplication.heatmap.HeatmapView;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HeatmapFragment extends Fragment {


    ViewPager2 viewPager;
    Switch sw;


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
        DataSeeker dataSeeker = view.findViewById(R.id.dataSeeker);
        HeatmapView heatMapper = view.findViewById(R.id.heatmapView);
        TextView time = view.findViewById(R.id.txtTime);



        view.findViewById(R.id.btnloadFile).setOnClickListener((View v)->{
            FilePickDialog d = new FilePickDialog(getContext());
            d.setFileSelectedHandler(file -> {
                try {
                    Scanner s = new Scanner(file);
                    StatsFragment.dataList = new ArrayList<DataStore>();
                    while(s.hasNextLine()){
                        StatsFragment.dataList.add(new DataStore(s.nextLine()));
                    }
                    dataSeeker.init(new DataSeeker.IDataAccessor() {
                        @Override
                        public int getTotalFrames() {
                            return StatsFragment.dataList.size();
                        }

                        @Override
                        public long getTimeStamp(int i) {
                            return StatsFragment.dataList.get(i).timeStamp;
                        }

                        @Override
                        public void displayFrame(int i ,long timeStart) {
                            GattHandler.data.copyFrom(StatsFragment.dataList.get(i));
                            runOnUIThread(()->{
                                heatMapper.dataChanged();
                                time.setText(String.valueOf(timeStart));


                            });
                        }
                    });


                    if(!s.hasNext()) return;



                    //Toast.makeText(this, s.next(), Toast.LENGTH_LONG).show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }



            });
            d.show();
        });
        view.findViewById(R.id.btnStartStop).setOnClickListener((View v)->{
            dataSeeker.playPause();
        });



        // OpenGL heatmap init
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

        final CustomTableLayout tableForce = view.findViewById(R.id.tableForce);
        // switch that toggles live mode
        sw = view.findViewById(R.id.swtLive);
        sw.setOnClickListener((View v) ->{
            if (sw.isChecked()){
                GattHandler.runnable = ()->{
                    heatMapper.dataChanged();
                    tableForce.updateData();
                };
                Toast.makeText(getContext(), "Live mode is now ON ", Toast.LENGTH_SHORT).show();

            }else{
                GattHandler.runnable = ()->{};
                Toast.makeText(getContext(), "Live mode is now OFF ", Toast.LENGTH_SHORT).show();
            }
        });



        // Inflate the layout for this fragment
        return view;

    }
}