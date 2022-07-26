package com.footstrike.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GattHandler.init(this);
        findViewById(R.id.btnConnect).setOnClickListener((View v)->{
           GattHandler.init(getApplicationContext());
        });

        findViewById(R.id.btnSend).setOnClickListener((View v)->{
            GattHandler.write(2);
        });

    }

}
// 70:74:95:CF:0D:53