package com.xxun.camera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.view.SurfaceView;

import java.io.IOException;

public class CameraEngine {
    private static Camera camera = null;
    private static int cameraID = 0;
    private static SurfaceTexture surfaceTexture;
    private static SurfaceView surfaceView;

    public static Camera getCamera() {
        return camera;
    }

    public static boolean openCamera() {
        if (camera == null) {
            try {
                camera = Camera.open(cameraID);
                setDefaultParameters();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    public static boolean openCamera(int id) {
        if (camera == null) {
            try {
                camera = Camera.open(id);
                cameraID = id;
                setDefaultParameters();
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        }
        return false;
    }

    public static void releaseCamera() {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public static void switchCamera() {
        releaseCamera();
        cameraID = cameraID == 0 ? 1 : 0;
        openCamera(cameraID);
        startPreview(surfaceTexture);
    }

    private static void setDefaultParameters() {
        Parameters parameters = camera.getParameters();
        if (parameters.getSupportedFocusModes().contains(
                Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
//        Size previewSize = CameraUtils.getLargePreviewSize(camera);
        parameters.setPreviewSize(240, 240);
//        Size pictureSize = CameraUtils.getLargePictureSize(camera);
        parameters.setPictureSize(240, 240);
        parameters.setRotation(0);
        camera.setParameters(parameters);
    }

    private static Size getPreviewSize() {
        return camera.getParameters().getPreviewSize();
    }

    private static Size getPictureSize() {
        return camera.getParameters().getPictureSize();
    }

    public static void startPreview(SurfaceTexture surfaceTexture) {
        if (camera != null) {
            try {
                camera.setPreviewTexture(surfaceTexture);
                CameraEngine.surfaceTexture = surfaceTexture;
                camera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void startPreview() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    public static void stopPreview() {
        camera.stopPreview();
    }

    public static void setRotation(int rotation) {
        Parameters params = camera.getParameters();
        params.setRotation(rotation);
        camera.setParameters(params);
    }

    public static void takePicture(Camera.ShutterCallback shutterCallback, Camera.PictureCallback rawCallback,
                                   Camera.PictureCallback jpegCallback) {
        camera.takePicture(shutterCallback, rawCallback, jpegCallback);
    }

    public static com.xxun.camera.utils.CameraInfo getCameraInfo() {
        com.xxun.camera.utils.CameraInfo info = new com.xxun.camera.utils.CameraInfo();
//        Size size = getPreviewSize();
        CameraInfo cameraInfo = new CameraInfo();
//        Camera.getCameraInfo(cameraID, cameraInfo);
        info.previewWidth = 240;
        info.previewHeight = 240;
        info.orientation = cameraInfo.orientation;
        info.isFront = /*cameraID == 1 ? true : */false;
//        size = getPictureSize();
        info.pictureWidth = 240;
        info.pictureHeight = 240;
        return info;
    }

    public void resumeCamera() {
        openCamera();
    }

    public Parameters getParameters() {
        if (camera != null) {
            camera.getParameters();
        }
        return null;
    }

    public void setParameters(Parameters parameters) {
        camera.setParameters(parameters);
    }
}