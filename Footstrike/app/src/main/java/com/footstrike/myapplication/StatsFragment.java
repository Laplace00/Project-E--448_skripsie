package com.footstrike.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
// import gatthandler
import com.footstrike.myapplication.GattHandler;


public class StatsFragment extends Fragment {

    public static TextView archVal;
    public static TextView met5Val;
    public static TextView met3Val;
    public static TextView met1Val;
    public static TextView heelRVal;
    public static TextView heelLVal;
    public static TextView halluxVal;
    public static TextView toesVal;

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
        GattHandler.runnableTxt = () -> {
            archVal.setText(String.valueOf(GattHandler.arch));
            met5Val.setText(String.valueOf(GattHandler.met5));
            met3Val.setText(String.valueOf(GattHandler.met3));
            met1Val.setText(String.valueOf(GattHandler.met1));
            heelRVal.setText(String.valueOf(GattHandler.heelR));
            heelLVal.setText(String.valueOf(GattHandler.heelL));
            halluxVal.setText(String.valueOf(GattHandler.hallux));
            toesVal.setText(String.valueOf(GattHandler.toes));
        };
        //GattHandler.init(getActivity());
        out.findViewById(R.id.btnConnect).setOnClickListener((View v)->{
            GattHandler.init(getActivity().getApplicationContext());
        });
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