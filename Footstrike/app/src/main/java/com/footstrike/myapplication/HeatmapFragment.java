package com.footstrike.myapplication;



import static android.os.Looper.getMainLooper;
import static com.footstrike.myapplication.MainActivity.runOnUIThread;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.footstrike.myapplication.heatmap.HeatMap;
import com.footstrike.myapplication.heatmap.HeatmapView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class HeatmapFragment extends Fragment {


    ViewPager2 viewPager;
    Switch sw;

    public static Runnable defaultrunnable;;

    public static ArrayList<DataStore> dataList;
    public boolean recordData = false;
    private String m_Text = "";
    public boolean down =false;


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
        final CustomTableLayout tableForce = view.findViewById(R.id.tableForce);

        view.findViewById(R.id.btnConnectBLE).setOnClickListener((View v)->{
            GattHandler.init(getActivity().getApplicationContext());
        });


        view.findViewById(R.id.btnRecordData).setOnClickListener((View v)->{
            if(!recordData) {
                recordData = true;
                dataList = new ArrayList<>();
                Toast.makeText(view.getContext(), "Started recording data", Toast.LENGTH_SHORT).show();
            }else{
                recordData = false;
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter file name");

                // Set up the input
                final EditText input = new EditText(getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        try {
                            File outfile = new File(FileHelper.getRoot(), m_Text + ".footstrike");
                            PrintStream writer = new PrintStream(outfile);
                            for (DataStore data: dataList
                            ) {
                                writer.println(data);
                            }
                            writer.flush();
                            writer.close();
                            GattHandler.data.steps = 0;
                            Toast.makeText(view.getContext(), "Stopped recording data and saved to text file", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GattHandler.data.steps = 0;
                        Toast.makeText(view.getContext(), "Stopped recording data and file not saved", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                builder.show();



            }

        });



        view.findViewById(R.id.btnloadFile).setOnClickListener((View v)->{
            FilePickDialog d = new FilePickDialog(getContext());
            d.setFileSelectedHandler(file -> {
                try {
                    Scanner s = new Scanner(file);
                    dataList = new ArrayList<DataStore>();
                    while(s.hasNextLine()){
                        dataList.add(new DataStore(s.nextLine()));
                    }
                    dataSeeker.init(new DataSeeker.IDataAccessor() {
                        @Override
                        public int getTotalFrames() {
                            return dataList.size();
                        }

                        @Override
                        public long getTimeStamp(int i) {
                            return dataList.get(i).timeStamp;
                        }

                        @Override
                        public void displayFrame(int i ,long timeStart) {
                            GattHandler.data.copyFrom(dataList.get(i));
                            runOnUIThread(()->{
                                heatMapper.dataChanged();
                                time.setText(String.valueOf(timeStart));
                                tableForce.updateData();


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



        defaultrunnable = ()->{
            heatMapper.dataChanged();
            tableForce.updateData();
            startRecording();
        };;

        GattHandler.runnable = defaultrunnable;

        // switch that toggles live mode
        sw = view.findViewById(R.id.swtLive);
        sw.setOnClickListener((View v) ->{
            if (sw.isChecked()){
                GattHandler.runnable = defaultrunnable;
                Toast.makeText(getContext(), "Live mode is now ON ", Toast.LENGTH_SHORT).show();

            }else{
                GattHandler.runnable = ()->{
                    startRecording();
                };
                Toast.makeText(getContext(), "Live mode is now OFF ", Toast.LENGTH_SHORT).show();
            }
        });



        // Inflate the layout for this fragment
        return view;

    }

    void startRecording(){
        if (recordData) {
            if (down == false && GattHandler.data.met1Val < 3000 && GattHandler.data.met3Val < 3000) {
                GattHandler.data.steps++;
                down = true;

            }

            if (down == true && GattHandler.data.met1Val > 3500 && GattHandler.data.met3Val > 3500) {
                down = false;
            }

            dataList.add(GattHandler.data.copy());
        }
    }
}