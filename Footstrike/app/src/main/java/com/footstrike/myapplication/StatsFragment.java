package com.footstrike.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
    public  TextView stepsVal;
    public static int steps = 0;
    public static ArrayList<DataStore> dataList;
    public boolean recordData = false;
    private String m_Text = "";
    public boolean down =false;


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
        archVal = out.findViewById(R.id.txtArch);
        met5Val = out.findViewById(R.id.txtMet5);
        met3Val = out.findViewById(R.id.txtMet3);
        met1Val = out.findViewById(R.id.txtMet1);
        heelRVal = out.findViewById(R.id.txtHeelR);
        heelLVal = out.findViewById(R.id.txtHeelL);
        halluxVal = out.findViewById(R.id.txtHallux);
        toesVal = out.findViewById(R.id.txtToes);
        stepsVal = out.findViewById(R.id.txtSteps);



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
                            Toast.makeText(out.getContext(), "Stopped recording data and saved to text file", Toast.LENGTH_SHORT).show();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GattHandler.data.steps = 0;
                        Toast.makeText(out.getContext(), "Stopped recording data and file not saved", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });

                builder.show();



            }

        });

        GattHandler.runnableTxt = this::run;



        // Inflate the layout for this fragment
        return out;
    }

    private void run() {
        archVal.setText("Arch:" + GattHandler.data.archVal);
        met5Val.setText("Met5:" + GattHandler.data.met5Val);
        met3Val.setText("Met3:" + GattHandler.data.met3Val);
        met1Val.setText("Met1:" + GattHandler.data.met1Val);
        heelRVal.setText("HeelR:" + GattHandler.data.heelrVal);
        heelLVal.setText("HeelL:" + GattHandler.data.heellVal);
        halluxVal.setText("Hallux:" + GattHandler.data.halluxVal);
        toesVal.setText("Toes:" + GattHandler.data.toesVal);
        stepsVal.setText("Steps:" + GattHandler.data.steps);
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