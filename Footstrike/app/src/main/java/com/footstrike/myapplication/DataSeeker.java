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
    // ExecutorService creates the animation thread which runs the loop, code
    private ExecutorService playService = Executors.newSingleThreadExecutor();
    public boolean isPlaying = true;
    // range: 0 - dataAccessor.getTotalFrames() -1
    private int currentFrame = 0;
    // Reference point for the time stamp of the first frame
    // In milliseconds
    private long firstFrameTime = 0;
    // Total frames in the current animation
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
        // Listen to the user interaction with the seek bar
        this.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // b == true when human changes bar position
                if (b) {


                    if (isPlaying) {
                        // Stop playback
                        playService.shutdownNow();
                        // Wait for playback to stop
                        try {
                            playService.awaitTermination(10, TimeUnit.MILLISECONDS);
                            // Set the frame to the selected index
                            currentFrame = i;
                            sleepTimeLeft = 0;
                            // set the service tp play from the newly selected point
                            resetService();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else {
                        // no need to stop playback
                        // set frame to selected index
                        currentFrame = i;
                        sleepTimeLeft = 0;
                        // set frame time to display newly selected frame
                        long currentFrameTime = dataAccessor.getTimeStamp(currentFrame);
                        dataAccessor.displayFrame(currentFrame,currentFrameTime-firstFrameTime);
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

    // Use IDataAccessor to get total frames,current frame timestamp
    public void init(IDataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
        // Get the total frames for the animation
        this.frames = dataAccessor.getTotalFrames();
        // Set the max index of the inherited seeker class
        this.setMax(this.frames - 1);
        // Retrieve the first time stamp
        this.firstFrameTime = dataAccessor.getTimeStamp(0);
        // Start the executor service
        playService.submit(this::playbackLoop);

    }
    private void playbackLoop() {
        // Try to sleep the thread for sleepTimeLeft milliseconds, this accounts for
        // time between play/pauses. If it fails stop playing
        try {
            Thread.sleep(sleepTimeLeft);
        } catch (InterruptedException e) {
            isPlaying = false;
        }
        while (isPlaying) {
            // Set the progress of the inherited seeker class
            this.setProgress(currentFrame);
            // Retrieve the time stamp (in milliseconds) of the current frame
            long currentFrameTime = dataAccessor.getTimeStamp(currentFrame);
            // Call the display frame method to display current frame and provide it with
            // which index to display as well as the the time in milliseconds since the
            // start of the animation the frame is
            dataAccessor.displayFrame(currentFrame,currentFrameTime-firstFrameTime);
            // Calculate next frame index, mod to wrap around
            int next = (currentFrame + 1) % frames;
            // Retrieve the time stamp (in milliseconds) of the next frame
            long nextFrameTime = dataAccessor.getTimeStamp(next);
            // Calculate the difference between the next and current frame time stamp
            long delta = nextFrameTime - currentFrameTime;

            // Repeat playback on negative deltas or next frame being the first one
            if (delta < 0 || next == 0) {
                if (!shouldRepeat) {
                    isPlaying = false;
                    break;
                } else delta = repeatDelay;
            }
            // Try to sleep the thread for sleepTimeLeft milliseconds, this accounts for
            // time between play/pauses. If it fails stop playing
            try {
                startedSleeping = System.currentTimeMillis();
                currentSleepTime = delta;
                // Pause the current thread until the next frame needs to be played
                Thread.sleep(delta);
            } catch (InterruptedException e) {
                isPlaying = false;
            }

            currentFrame = next;
        }
    }
    // method to play or pause the playback
    public void playPause() {
        isPlaying = !isPlaying;

        // If the new state is not playing stop the service, but keep track of how many
        // milliseconds to sleep if it is ever restarted to keep consistent timings.
        if (!isPlaying) {
            playService.shutdownNow();
            sleepTimeLeft = (startedSleeping + currentSleepTime) - System.currentTimeMillis();
            try {
                playService.awaitTermination(10, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Enforce positive times
            if (sleepTimeLeft < 0)
                sleepTimeLeft = 0;


        } else {
            resetService();
        }
    }

    private void resetService() {
        // Create the executror service responsible for playing the loop
        playService = Executors.newSingleThreadExecutor();
        isPlaying = true;
        // Tell it which method to run in the backround
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
    // Interface that the DataSeeker class uses to get information on animation
    // length/frame time stamps and uses to advance an animation to another frame
    public interface IDataAccessor {
        public int getTotalFrames();
        // Should return the time stamp of a particular frame
        public long getTimeStamp(int i);
        // This method should display a frame to the gui based on the given index and
        // the milliseconds since the start of the animation
        public void displayFrame(int i ,long timeStart);
    }


}