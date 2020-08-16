package com.xxun.filter.advanced;

import android.opengl.GLES20;

import com.frank.filtercamera.R;
import com.xxun.filter.base.gpuimage.GPUImageFilter;
import com.xxun.utils.OpenGlUtils;

public class SketchFilter extends GPUImageFilter {

    private int mSingleStepOffsetLocation;
    //0.0 - 1.0
    private int mStrengthLocation;

    public SketchFilter() {
        super(NO_FILTER_VERTEX_SHADER, OpenGlUtils.readShaderFromRawResource(R.raw.sketch));
    }

    @Override
    protected void onInit() {
        super.onInit();
        mSingleStepOffsetLocation = GLES20.glGetUniformLocation(getProgram(), "singleStepOffset");
        mStrengthLocation = GLES20.glGetUniformLocation(getProgram(), "strength");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setTexelSize(final float w, final float h) {
        setFloatVec2(mSingleStepOffsetLocation, new float[]{1.0f / w, 1.0f / h});
    }

    @Override
    protected void onInitialized() {
        super.onInitialized();
        setFloat(mStrengthLocation, 0.5f);
    }

    @Override
    public void onInputSizeChanged(final int width, final int height) {
        super.onInputSizeChanged(width, height);
        setTexelSize(width, height);
    }
}
