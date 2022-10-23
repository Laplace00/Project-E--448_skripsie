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
    // Coordinates of the plane on which to draw the heatmap
    private static final float[] squareCoords = {
            -1, 1, 0.0f,   // top left
            -1, -1, 0.0f,   // bottom left
            1, -1, 0.0f,   // bottom right
            1, 1, 0.0f,  // top right
    };

    // Point coordinates packed into a 1d array.
    static float[] points = new float[0];
    // order to draw vertices
    private final short[] drawOrder = {0, 1, 2, 0, 2, 3}; // order to draw vertices
    // Width and height of the heatmap map view
    private float width;
    private float height;
    // List of points to plot
    private final List<HeatMapPoint> heatMapPoints = new ArrayList<>();

    // Radius and maximum value for each point
    public float pointRadius = 0.3f;
    public float heatMax = 10f;

    // GSLS code for vertices and colours
    private final String vertexShaderCode;
    private final String fragmentShaderCode;

    // Constructor requires Resources to access shader and vertex code
    public HeatMap(Resources res) {
        vertexShaderCode = (new Scanner(res.openRawResource(R.raw.heatmap_v))).useDelimiter("\\A").next();
        fragmentShaderCode = (new Scanner(res.openRawResource(R.raw.heatmap_f))).useDelimiter("\\A").next();
    }
    // Initialize the heatmap with the width and height of the view size
    public void init(float width, float height) {
        this.height = height;
        this.width = width;

        // GLSL initialization code
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

        // Actually load the shader code and retrieve their OpenGL handles
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

    // Add a new point to the heatmap at the specified coordinates
    public void addPoint(float x, float y, IHeatMappable heatMappable) {
        heatMapPoints.add(new HeatMapPoint(x, y, heatMappable));
        initPoints();
    }

    // Actually draw the heatmap using OpenGL
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

        //Load constants to GL, reuse the id for memory efficiency
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

    // Interface for retrieving the value point
    public interface IHeatMappable {
        float getHeat(int index);
    }
    // Class that represents a single point on the heatmap
    private static class HeatMapPoint {
        private final float x, y;
        private final IHeatMappable heatMappable;

        public HeatMapPoint(float x, float y, IHeatMappable heatMappable) {
            this.x = x;
            this.y = y;
            this.heatMappable = heatMappable;
        }
    }
    // Initializes the the 1d point array from the HeatMapPoint list
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

    // This function should be called every time the data in heatMapPoints changes
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