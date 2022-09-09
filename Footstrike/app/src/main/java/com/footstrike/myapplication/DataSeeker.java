package com.footstrike.myapplication;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DataSeeker extends androidx.appcompat.widget.AppCompatSeekBar {

    private ExecutorService playService = Executors.newSingleThreadExecutor();
    private boolean isPlaying = true;

    private int currentFrame = 0;
    private int frames = 100;

    private boolean shouldRepeat = true;
    private long repeatDelay = 100L;

    private long startedSleeping;
    private long currentSleepTime;
    private long sleepTimeLeft;

    private IDataAccessor dataAccessor;

    public DataSeeker(@NonNull Context context) {
        super(context);
        setup();
    }

    public DataSeeker(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public DataSeeker(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup()
    {
        this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if (b) {


                    if (isPlaying) {

                        playService.shutdownNow();
                        try {
                            playService.awaitTermination(10, TimeUnit.MILLISECONDS);
                            currentFrame = i;
                            sleepTimeLeft = 0;
                            resetService();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        currentFrame = i;
                        sleepTimeLeft = 0;
                    }

                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void init(IDataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
        this.frames = dataAccessor.getTotalFrames();
        this.setMax(this.frames - 1);
        playService.submit(this::playbackLoop);

    }

    private void playbackLoop() {
        try {
            Thread.sleep(sleepTimeLeft);
        } catch (InterruptedException e) {
            isPlaying = false;
        }
        while (isPlaying) {

            dataAccessor.displayFrame(currentFrame);

            this.setProgress(currentFrame);
            long currentFrameTime = dataAccessor.getTimeStamp(currentFrame);
            int next = (currentFrame + 1) % frames;
            long nextFrameTime = dataAccessor.getTimeStamp(next);

            long delta = nextFrameTime - currentFrameTime;

            // Repeat on negative deltas or next frame being the first one
            if (delta < 0 || next == 0) {
                if (!shouldRepeat) {
                    isPlaying = false;
                    break;
                } else delta = repeatDelay;
            }

            try {
                startedSleeping = System.currentTimeMillis();
                currentSleepTime = delta;
                Thread.sleep(delta);
            } catch (InterruptedException e) {
                isPlaying = false;
            }

            currentFrame = next;
        }
    }

    public void playPause() {
        isPlaying = !isPlaying;

        if (!isPlaying) {
            playService.shutdownNow();
            sleepTimeLeft = (startedSleeping + currentSleepTime) - System.currentTimeMillis();
            try {
                playService.awaitTermination(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (sleepTimeLeft < 0)
                sleepTimeLeft = 0;


        } else {
            resetService();
        }
    }

    private void resetService() {
        playService = Executors.newSingleThreadExecutor();
        isPlaying = true;
        playService.submit(this::playbackLoop);
    }

    public boolean isPlaying() {
        return isPlaying;
    }


    public void setShouldRepeat(boolean shouldRepeat) {
        this.shouldRepeat = shouldRepeat;
    }

    public void setRepeatDelay(long repeatDelay) {
        this.repeatDelay = repeatDelay;
    }

    public interface IDataAccessor {
        public int getTotalFrames();

        public long getTimeStamp(int i);

        public void displayFrame(int i);
    }


}