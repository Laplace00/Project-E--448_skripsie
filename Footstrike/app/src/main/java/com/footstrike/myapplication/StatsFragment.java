package com.footstrike.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
// import gatthandler
import com.footstrike.myapplication.GattHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;


public class StatsFragment extends Fragment {

    public  TextView archVal;
    public  TextView met5Val;
    public  TextView met3Val;
    public  TextView met1Val;
    public  TextView heelRVal;
    public  TextView heelLVal;
    public  TextView halluxVal;
    public  TextView toesVal;
    ArrayList<DataStore> dataList;
    public boolean recordData = false;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View out = inflater.inflate(R.layout.fragment_stats, container, false);

        //GattHandler.init(getActivity());
        out.findViewById(R.id.btnConnect).setOnClickListener((View v)->{
            GattHandler.init(getActivity().getApplicationContext());
        });

        out.findViewById(R.id.btnSave).setOnClickListener((View v)->{
            if(!recordData) {
                recordData = true;
                dataList = new ArrayList<>();
                Toast.makeText(out.getContext(), "Started recording data", Toast.LENGTH_SHORT).show();
            }else{
                recordData = false;

                try {
                    File outfile = new File(MainActivity.file, "sample");
                    PrintStream writer = new PrintStream(outfile);
                    for (DataStore data: dataList
                         ) {
                        writer.println(data);
                    }
                    writer.flush();
                    writer.close();
                    Toast.makeText(out.getContext(), "Stopped recording data", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        });

        GattHandler.runnableTxt = () -> {
            archVal.setText(String.valueOf(GattHandler.data.archVal));
            met5Val.setText(String.valueOf(GattHandler.data.met5Val));
            met3Val.setText(String.valueOf(GattHandler.data.met3Val));
            met1Val.setText(String.valueOf(GattHandler.data.met1Val));
            heelRVal.setText(String.valueOf(GattHandler.data.heelrVal));
            heelLVal.setText(String.valueOf(GattHandler.data.heellVal));
            halluxVal.setText(String.valueOf(GattHandler.data.halluxVal));
            toesVal.setText(String.valueOf(GattHandler.data.toesVal));
            if(recordData) {
                dataList.add(GattHandler.data.copy());
            }


        };
        archVal = out.findViewById(R.id.txtArch);
        met5Val = out.findViewById(R.id.txtMet5);
        met3Val = out.findViewById(R.id.txtMet3);
        met1Val = out.findViewById(R.id.txtMet1);
        heelRVal = out.findViewById(R.id.txtHeelR);
        heelLVal = out.findViewById(R.id.txtHeelL);
        halluxVal = out.findViewById(R.id.txtHallux);
        toesVal = out.findViewById(R.id.txtToes);


        // Inflate the layout for this fragment
        return out;
    }
}