package com.frank.filters;

import com.frank.filters.camera.CameraEngine;
import com.frank.filters.filter.helper.FilterType;
import com.frank.filters.helper.SavePictureTask;
import com.frank.filters.utils.FilterParams;
import com.frank.filters.widget.FilterCameraView;
import com.frank.filters.widget.base.FilterBaseView;

import java.io.File;

/**
 * Created by Frank on 2016/2/25.
 */
public class FrankFilterEngine {
    private static FrankFilterEngine frankFilterEngine;

    private FrankFilterEngine(Builder builder) {

    }

    public static FrankFilterEngine getInstance() {
        if (frankFilterEngine == null) {
            throw new NullPointerException("FrankFilterEngine must be built first");
        } else {
            return frankFilterEngine;
        }
    }

    public void setFilter(FilterType type) {
        FilterParams.filterBaseView.setFilter(type);
    }

    public void savePicture(File file, SavePictureTask.OnPictureSaveListener listener) {
        SavePictureTask savePictureTask = new SavePictureTask(file, listener);
        FilterParams.filterBaseView.savePicture(savePictureTask);
    }

    public void startRecord() {
        if (FilterParams.filterBaseView instanceof FilterCameraView) {
//            ((FilterCameraView) FilterParams.filterBaseView).changeRecordingState(true);
//            CameraEngine.startRecord();
        }
    }

    public void stopRecord() {
        if (FilterParams.filterBaseView instanceof FilterCameraView) {
//            ((FilterCameraView) FilterParams.filterBaseView).changeRecordingState(false);
//            CameraEngine.stopRecordVideo();
        }
    }

    public void setBeautyLevel(int level) {
        if (FilterParams.filterBaseView instanceof FilterCameraView && FilterParams.beautyLevel != level) {
            FilterParams.beautyLevel = level;
            ((FilterCameraView) FilterParams.filterBaseView).onBeautyLevelChanged();
        }
    }

    public void switchCamera() {
        CameraEngine.switchCamera();
    }

    public static class Builder {

        public FrankFilterEngine build(FilterBaseView filterBaseView) {
            FilterParams.context = filterBaseView.getContext();
            FilterParams.filterBaseView = filterBaseView;
            return new FrankFilterEngine(this);
        }

        public Builder setVideoPath(String path) {
            FilterParams.videoPath = path;
            return this;
        }

        public Builder setVideoName(String name) {
            FilterParams.videoName = name;
            return this;
        }

    }
}
