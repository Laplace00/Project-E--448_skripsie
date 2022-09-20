package com.footstrike.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomTableLayout extends LinearLayout {
    LinearLayout position;
    LinearLayout forceNewton;
    LinearLayout forcePercent;

    TextView[] txtPositions = new TextView[9];
    public TextView[] txtForceNewtonVals = new TextView[9];
    public TextView[] txtForcePercentVals = new TextView[9];
    String[] positionNames = new String[]{"Position","Arch:","Met5:","Met3:","Met1:","HeelR:","HeelL:","Hallux:","Toes:"};

    public CustomTableLayout(@NonNull Context context) {
        super(context);
        createView();

    }

    public CustomTableLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        createView();
    }

    public CustomTableLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        createView();
    }

    void createView(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,1/3f);
        position = new LinearLayout(getContext());
        position.setOrientation(VERTICAL);
        addView(position);
        //position.addView(new Button(getContext()));
        position.setLayoutParams(params);

        forceNewton = new LinearLayout(getContext());
        forceNewton.setOrientation(VERTICAL);
        addView(forceNewton);
        //forceNewton.addView(new Button(getContext()));
        forceNewton.setLayoutParams(params);



        forcePercent = new LinearLayout(getContext());
        forcePercent.setOrientation(VERTICAL);
        addView(forcePercent);
        //forcePercent.addView(new Button(getContext()));
        forcePercent.setLayoutParams(params);


        for (int i = 0; i < 9; i++) {
            txtPositions[i] = new TextView(getContext());
            txtPositions[i].setText(positionNames[i]);
            position.addView(txtPositions[i]);

            txtForceNewtonVals[i] = new TextView(getContext());
            txtForceNewtonVals[i].setText("0");
            forceNewton.addView(txtForceNewtonVals[i]);

            txtForcePercentVals[i] = new TextView(getContext());
            txtForcePercentVals[i].setText("0");
            forcePercent.addView(txtForcePercentVals[i]);
        }
        txtForceNewtonVals[0].setText("Force (n)");
        txtForcePercentVals[0].setText("Force (%)");
    }

    void updateData(){
        float sum = 0;
        for (int i = 1; i < 9; i++) {
            sum = sum + GattHandler.data.getForce(i - 1);
        }
        for (int i = 1; i < 9; i++) {
            float tmp = ((GattHandler.data.getForce(i-1))/sum)*100;
            txtForcePercentVals[i].setText(String.format("%.2f", tmp));
            txtForceNewtonVals[i].setText(String.format("%.2f", GattHandler.data.getForce(i - 1)));
        }
    }
}
