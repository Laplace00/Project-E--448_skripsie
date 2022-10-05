package com.footstrike.myapplication.heatmap;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

//This class contains more GSLS biolerplate code
public class HeatmapRenderer implements GLSurfaceView.Renderer {

    private final HeatMap heatMap;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private final GLSurfaceView surfaceView;

    public HeatmapRenderer(Resources res, GLSurfaceView surfaceView) {
        this.surfaceView = surfaceView;
        this.heatMap = new HeatMap(res);
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        this.heatMap.init(surfaceView.getWidth(), surfaceView.getHeight());

        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0f);


    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw heatmap
        heatMap.draw(mMVPMatrix);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        float ratio = (float) width / height;
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    public static int loadShader(int type, String shaderCode) {

        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public HeatMap getHeatmap() {
        return heatMap;
    }
}