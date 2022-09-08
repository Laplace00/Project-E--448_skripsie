package com.footstrike.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static File file;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        GattHandler.init(this);
//        GattHandler.init(getApplicationContext());

        file = new File(MainActivity.this.getFilesDir(),"records");
        file.mkdir();


        ViewPager2 viewPager2 = findViewById(R.id.viewPager2);

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new StatsFragment();
                    case 1:
                        return new HeatmapFragment(viewPager2);
                }
                return null;
            }

            @Override
            public int getItemCount() {
                return 2;
            }
        };
        viewPager2.setAdapter(adapter);
    }

    public static void runOnUIThread(Runnable runnable)
    {
        (new Handler(Looper.getMainLooper())).post(() -> runnable.run());

    }

}
// 70:74:95:CF:0D:53