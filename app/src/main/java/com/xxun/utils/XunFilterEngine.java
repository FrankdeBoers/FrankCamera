package com.xxun.utils;

import com.xxun.camera.CameraEngine;
import com.xxun.filter.helper.FilterType;
import com.xxun.widget.FilterCameraView;
import com.xxun.widget.base.FilterBaseView;

import java.io.File;

//import com.xxun.morefilter.camera.CameraEngine;

/**
 * Created by Frank on 2016/2/25.
 */
public class XunFilterEngine {
    private static XunFilterEngine xunFilterEngine;

    private XunFilterEngine(Builder builder) {

    }

    public static XunFilterEngine getInstance() {
        if (xunFilterEngine == null) {
            throw new NullPointerException("XunFilterEngine must be built first");
        } else {
            return xunFilterEngine;
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
            ((FilterCameraView) FilterParams.filterBaseView).changeRecordingState(true);
        }
    }

    public void stopRecord() {
        if (FilterParams.filterBaseView instanceof FilterCameraView) {
            ((FilterCameraView) FilterParams.filterBaseView).changeRecordingState(false);
        }
    }

//    public void setBeautyLevel(int level) {
//        if (FilterParams.filterBaseView instanceof FilterCameraView && FilterParams.beautyLevel != level) {
//            FilterParams.beautyLevel = level;
//            ((FilterCameraView) FilterParams.filterBaseView).onBeautyLevelChanged();
//        }
//    }

    public void switchCamera() {
        CameraEngine.switchCamera();
    }

    public static class Builder {

        public XunFilterEngine build(FilterBaseView filterBaseView) {
            FilterParams.context = filterBaseView.getContext();
            FilterParams.filterBaseView = filterBaseView;
            return new XunFilterEngine(this);
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
