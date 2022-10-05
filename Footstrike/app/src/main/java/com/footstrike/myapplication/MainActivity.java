package com.footstrike.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.footstrike.myapplication.heatmap.HeatMap;
import com.footstrike.myapplication.heatmap.HeatmapView;
import com.hbisoft.hbrecorder.HBRecorder;
import com.hbisoft.hbrecorder.HBRecorderListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity  implements HBRecorderListener {

    Switch sw;

    public static Runnable defaultrunnable;;
    HBRecorder hbRecorder;
    private static final int SCREEN_RECORD_REQUEST_CODE = 777;
    public static ArrayList<DataStore> dataList;
    public boolean recordData = false;
    private String m_Text = "";
    public boolean stepDone = true;
    public TextView stepsView;
    Button recordButton;
    Button connectButton;
    Button loadButton;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);

        setContentView(R.layout.activity_main);

//        GattHandler.init(this);
//        GattHandler.init(getApplicationContext());
        FileHelper.init(this);
        DataSeeker dataSeeker = findViewById(R.id.dataSeeker);

        //Init HBRecorder
        hbRecorder = new HBRecorder(this, this);

        // OpenGL heatmap init
        HeatmapView heatMapper = findViewById(R.id.heatmapView);
        HeatMap heatMap = heatMapper.getHeatmap();
        heatMap.pointRadius = 0.3f;
        heatMap.heatMax =60;

        heatMap.addPoint(0.62269f,1.08565f, GattHandler.data);
        heatMap.addPoint(0.65278f,0.66435f, GattHandler.data);
        heatMap.addPoint(0.45139f,0.61111f, GattHandler.data);
        heatMap.addPoint(0.24769f,0.58565f, GattHandler.data);
        heatMap.addPoint(0.62037f,1.63657f, GattHandler.data);
        heatMap.addPoint(0.43519f,1.67130f, GattHandler.data);
        heatMap.addPoint(0.27315f,0.28704f, GattHandler.data);
        heatMap.addPoint(0.50000f,0.32639f, GattHandler.data);


       // Views and Buttons
        stepsView = findViewById(R.id.txtSteps);
        connectButton = findViewById(R.id.btnConnectBLE);
        recordButton = findViewById(R.id.btnRecordData);
        loadButton = findViewById(R.id.btnloadFile);
        sw = findViewById(R.id.swtLive);
        TextView time = findViewById(R.id.txtTime);
        final CustomTableLayout tableForce = findViewById(R.id.tableForce);

        connectButton.setOnClickListener((View v)->{
            GattHandler.init(getApplicationContext());
        });


        recordButton.setOnClickListener((View v)->{
            if(!recordData) {
                //Start Recording
                dataList = new ArrayList<>();


                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter file name");

                // Set up the input
                final EditText input = new EditText(this);
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        hbRecorder.setFileName(m_Text);
                        recordData = true;
                        startRecordingScreen();
                        recordButton.setText("Stop Recording");
                        Toast.makeText(getApplicationContext(), "Started recording data", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GattHandler.data.steps = 0;
                        stepsView.setText("Steps: " + GattHandler.data.steps);

                        Toast.makeText(getApplicationContext(), "Recording Cancelled", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                builder.show();

            }else{
                //Stop recording
                recordButton.setText("Start Recording");
                recordData = false;
                if (hbRecorder.isBusyRecording()) {
                    hbRecorder.stopScreenRecording();
                }

                try {
                    File outfile = new File(FileHelper.getRoot(), m_Text + ".csv");
                    File outfileInternal = new File(FileHelper.getinternal(),m_Text + ".csv");

                    PrintStream writer = new PrintStream(outfile);
                    PrintStream writerInternal = new PrintStream(outfileInternal);
                    for (DataStore data: dataList
                    ) {
                        writer.println(data);
                        writerInternal.println(data);
                    }
                    writer.flush();
                    writerInternal.flush();
                    writer.close();
                    writerInternal.close();
                    GattHandler.data.steps = 0;
                    stepsView.setText("Steps: " + GattHandler.data.steps);
                    Toast.makeText(getApplicationContext(), "Stopped recording and saved data", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

        });



        loadButton.setOnClickListener((View v)->{
            FilePickDialog d = new FilePickDialog(this);
            d.setFileSelectedHandler(file -> {
                try {
                    sw.setChecked(false);
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
                                stepsView.setText("Steps: " + GattHandler.data.steps);

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
        findViewById(R.id.btnStartStop).setOnClickListener((View v)->{
            dataSeeker.playPause();
        });



        defaultrunnable = ()->{
            heatMapper.dataChanged();
            tableForce.updateData();
            startRecording();
        };;

        GattHandler.runnable = defaultrunnable;

        // switch that toggles live mode
        sw.setOnClickListener((View v) ->{
            if (sw.isChecked()){
                GattHandler.runnable = defaultrunnable;
                Toast.makeText(this, "Live mode is now ON ", Toast.LENGTH_SHORT).show();

            }else{
                GattHandler.runnable = ()->{

                    startRecording();
                };
                Toast.makeText(this, "Live mode is now OFF ", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public static void runOnUIThread(Runnable runnable)
    {
        (new Handler(Looper.getMainLooper())).post(() -> runnable.run());

    }

    void startRecording(){
        if (recordData) {
            stepsView.setText("Steps: " + GattHandler.data.steps);
            if (stepDone == false && GattHandler.data.met1Val < 50 && GattHandler.data.met3Val < 50) {
                GattHandler.data.steps++;
                stepDone = true;

            }

            if (stepDone == true && GattHandler.data.met1Val > 55 && GattHandler.data.met3Val > 55) {
                stepDone = false;
            }

            dataList.add(GattHandler.data.copy());
        }
    }

    @Override
    public void HBRecorderOnStart() {

    }

    @Override
    public void HBRecorderOnComplete() {

    }

    @Override
    public void HBRecorderOnError(int errorCode, String reason) {

    }
    private void startRecordingScreen() {
        quickSettings();
        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        startActivityForResult(permissionIntent,SCREEN_RECORD_REQUEST_CODE );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SCREEN_RECORD_REQUEST_CODE  ) {
            if (resultCode == RESULT_OK) {
                createFolder();
                hbRecorder.setOutputPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES) +"/Foot Strike Recordings");
                //Start screen recording
                hbRecorder.startScreenRecording(data, resultCode);
                //Toast.makeText(this, "started", Toast.LENGTH_SHORT);
            }
        }
    }

    private void quickSettings() {

        hbRecorder.recordHDVideo(false);
        hbRecorder.isAudioEnabled(false);

    }

    private void createFolder() {
        File f1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "/Foot Strike Recordings");
        if (!f1.exists()) {
            if (f1.mkdirs()) {
                Log.i("Folder ", "created");
            }
        }
    }

    }
// 70:74:95:CF:0D:53