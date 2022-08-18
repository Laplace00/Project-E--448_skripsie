package com.footstrike.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {



    public static TextView archVal;
    public static TextView met5Val;
    public static TextView met3Val;
    public static TextView met1Val;
    public static TextView heelRVal;
    public static TextView heelLVal;
    public static TextView halluxVal;
    public static TextView toesVal;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        archVal = findViewById(R.id.txtArch);
        met5Val = findViewById(R.id.txtMet5);
        met3Val = findViewById(R.id.txtMet3);
        met1Val = findViewById(R.id.txtMet1);
        heelRVal = findViewById(R.id.txtHeelR);
        heelLVal = findViewById(R.id.txtHeelL);
        halluxVal = findViewById(R.id.txtHallux);
        toesVal = findViewById(R.id.txtToes);


        GattHandler.init(this);
        findViewById(R.id.btnConnect).setOnClickListener((View v)->{
           GattHandler.init(getApplicationContext());
        });








    }

    public static void runOnUIThread(Runnable runnable)
    {
        (new Handler(Looper.getMainLooper())).post(() -> runnable.run());

    }

}
// 70:74:95:CF:0D:53