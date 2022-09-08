package com.footstrike.myapplication.heatmap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.footstrike.myapplication.R;

public class HeatMap {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private int mProgram;

    private static final int COORDS_PER_VERTEX = 3;

    private static final float[] squareCoords = {
            -1, 1, 0.0f,   // top left
            -1, -1, 0.0f,   // bottom left
            1, -1, 0.0f,   // bottom right
            1, 1, 0.0f,  // top right
    };


    static float[] points = new float[0];

    private final short[] drawOrder = {0, 1, 2, 0, 2, 3}; // order to draw vertices

    private float width;
    private float height;

    private final List<HeatMapPoint> heatMapPoints = new ArrayList<>();
    public float pointRadius = 0.3f;
    public float heatMax = 10f;

    private final String vertexShaderCode;
    private final String fragmentShaderCode;


    public HeatMap(Resources res) {
        vertexShaderCode = (new Scanner(res.openRawResource(R.raw.heatmap_v))).useDelimiter("\\A").next();
        fragmentShaderCode = (new Scanner(res.openRawResource(R.raw.heatmap_f))).useDelimiter("\\A").next();
    }

    public void init(float width, float height) {
        this.height = height;
        this.width = width;
        ByteBuffer bb = ByteBuffer.allocateDirect(
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        ByteBuffer dlb = ByteBuffer.allocateDirect(
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        int vertexShader = HeatmapRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);

        int fragmentShader = HeatmapRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);

        GLES20.glLinkProgram(mProgram);
    }

    public void addPoint(float x, float y, IHeatMappable heatMappable) {
        heatMapPoints.add(new HeatMapPoint(x, y, heatMappable));
        initPoints();
    }


    public void draw(float[] mvpMatrix) {

        GLES20.glUseProgram(mProgram);

        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        int vertexStride = COORDS_PER_VERTEX * 4;
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);


        // Transformation Matrix
        int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

        //Load constants to GL
        int id = GLES20.glGetUniformLocation(mProgram, "iResolution");
        GLES20.glUniform2f(id, width, height);

        id = GLES20.glGetUniformLocation(mProgram, "PointRadius");
        GLES20.glUniform1f(id, pointRadius);

        id = GLES20.glGetUniformLocation(mProgram, "HEAT_MAX");
        GLES20.glUniform1f(id, heatMax);

        id = GLES20.glGetUniformLocation(mProgram, "PointCount");
        GLES20.glUniform1i(id, points.length / 3);

        id = GLES20.glGetUniformLocation(mProgram, "points");
        GLES20.glUniform3fv(id, points.length / 3, points, 0);

        // Draw all
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


    public interface IHeatMappable {
        float getHeat(int index);
    }

    private static class HeatMapPoint {
        private final float x, y;
        private final IHeatMappable heatMappable;

        public HeatMapPoint(float x, float y, IHeatMappable heatMappable) {
            this.x = x;
            this.y = y;
            this.heatMappable = heatMappable;
        }
    }

    public void initPoints() {
        points = new float[heatMapPoints.size() * 3];
        int i = 0;
        for (HeatMapPoint p : heatMapPoints) {
            int index = i;
            points[i++] = p.x;
            points[i++] = p.y;
            points[i++] = p.heatMappable.getHeat(index);
        }
    }

    public void dataChanged() {
        int index = 0;
        int i = 2;
        for (HeatMapPoint p : heatMapPoints) {

            points[i] = p.heatMappable.getHeat(index);
            i += 3;
            index++;
        }

    }


}