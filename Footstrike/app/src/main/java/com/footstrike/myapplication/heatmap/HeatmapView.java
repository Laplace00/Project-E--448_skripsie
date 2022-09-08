package com.footstrike.myapplication.heatmap;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class HeatmapView extends GLSurfaceView {

    private HeatmapRenderer mRenderer;

    public HeatmapView(Context context, AttributeSet attr) {
        super(context, attr);
        inner(context);
    }

    public HeatmapView(Context context) {
        super(context);
        inner(context);

    }
    private void inner(Context context)
    {
        setEGLContextClientVersion(2);
        super.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        getHolder().setFormat(PixelFormat.TRANSLUCENT);
        setZOrderOnTop(true);

        mRenderer = new HeatmapRenderer(context.getResources(), this);
        setRenderer(mRenderer);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public HeatMap getHeatmap()
    {
        return mRenderer.getHeatmap();
    }

    public void dataChanged()
    {
        mRenderer.getHeatmap().dataChanged();

        requestRender();
    }

}
