package com.xxun;

import android.Manifest;
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.xxun.camera.FilterAdapter;
import com.frank.filtercamera.R;
import com.xxun.utils.XunFilterEngine;
import com.xxun.filter.helper.FilterType;
import com.xxun.widget.FilterCameraView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Frank on 2016/3/17.
 */
public class CameraActivity extends Activity {
    private final int MODE_PIC = 1;
    private final int MODE_VIDEO = 2;
    private final FilterType[] types = new FilterType[]{
            FilterType.NONE,
            FilterType.FAIRYTALE, // 童话
            FilterType.SUNRISE, // 日出
            FilterType.SUNSET, //日落
            FilterType.WHITECAT, // 白猫
            FilterType.BLACKCAT, // 黑猫
            FilterType.SKINWHITEN,  // 美白
            FilterType.HEALTHY, // 健康
            FilterType.SWEETS, // 甜品
            FilterType.ROMANCE, // 浪漫
            FilterType.SAKURA, // 樱花
            FilterType.WARM, //温暖
            FilterType.ANTIQUE, // 复古
            FilterType.NOSTALGIA, // 怀旧
            FilterType.CALM, // 冰冷
            FilterType.LATTE, // 拿铁
            FilterType.TENDER, // 温柔
            FilterType.COOL, // 冰冷
            FilterType.EMERALD, // 祖母绿
            FilterType.EVERGREEN, // 常青
            FilterType.CRAYON, //蜡笔
            FilterType.SKETCH, // 素描
    };
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private XunFilterEngine xunFilterEngine;
    private boolean isRecording = false;
    private int mode = MODE_PIC;
    private ImageView btn_shutter;
    private ImageView btn_mode;
    private ObjectAnimator animator;
    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener() {

        @Override
        public void onFilterChanged(FilterType filterType) {
            xunFilterEngine.setFilter(filterType);
        }
    };
    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_camera_mode:
                    switchMode();
                    break;
                case R.id.btn_camera_shutter:
                    if (PermissionChecker.checkSelfPermission(CameraActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(CameraActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                v.getId());
                    } else {
                        if (mode == MODE_PIC) {
                            takePhoto();
                        } else {
                            takeVideo();
                        }
                    }
                    break;
                case R.id.btn_camera_filter:
                    showFilters();
                    break;
                // 只有一个摄像头，去掉切换功能
//                case R.id.btn_camera_switch:
//                    xunFilterEngine.switchCamera();
//                    break;
                case R.id.btn_camera_beauty:
//                    new AlertDialog.Builder(CameraActivity.this)
//                            .setSingleChoiceItems(new String[]{"关闭", "1", "2", "3", "4", "5"}, FilterParams.beautyLevel,
//                                    new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            xunFilterEngine.setBeautyLevel(which);
//                                            dialog.dismiss();
//                                        }
//                                    })
//                            .setNegativeButton("取消", null)
//                            .show();
                    break;
                case R.id.btn_camera_closefilter:
                    hideFilters();
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        XunFilterEngine.Builder builder = new XunFilterEngine.Builder();
        xunFilterEngine = builder
                .build((FilterCameraView) findViewById(R.id.glsurfaceview_camera));
        initView();
    }

    private void initView() {
        mFilterLayout = (LinearLayout) findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);

        btn_shutter = (ImageView) findViewById(R.id.btn_camera_shutter);
        btn_mode = (ImageView) findViewById(R.id.btn_camera_mode);

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_shutter).setOnClickListener(btn_listener);
//        findViewById(R.id.btn_camera_switch).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_mode).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_beauty).setOnClickListener(btn_listener);

//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        mFilterListView.setLayoutManager(gridLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        animator = ObjectAnimator.ofFloat(btn_shutter, "rotation", 0, 360);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        FilterCameraView cameraView = (FilterCameraView) findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        cameraView.setLayoutParams(params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (mode == MODE_PIC) {
                takePhoto();
            } else {
                takeVideo();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void switchMode() {
        if (mode == MODE_PIC) {
            mode = MODE_VIDEO;
            btn_mode.setImageResource(R.drawable.icon_camera);
        } else {
            mode = MODE_PIC;
            btn_mode.setImageResource(R.drawable.icon_video);
        }
    }

    private void takePhoto() {
        xunFilterEngine.savePicture(getOutputMediaFile(), null);
    }

    private void takeVideo() {
        if (isRecording) {
            animator.end();
            xunFilterEngine.stopRecord();
        } else {
            animator.start();
            xunFilterEngine.startRecord();
        }
        isRecording = !isRecording;
    }

    private void showFilters() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", mFilterLayout.getHeight(), 0);
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                findViewById(R.id.btn_camera_shutter).setClickable(false);
                mFilterLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        animator.start();
    }

    private void hideFilters() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(mFilterLayout, "translationY", 0, mFilterLayout.getHeight());
        animator.setDuration(200);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                // TODO Auto-generated method stub
                mFilterLayout.setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_camera_shutter).setClickable(true);
            }
        });
        animator.start();
    }

    public File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                "DCIM"), "Camera");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "FIMG_" + timeStamp + ".jpg");

        return mediaFile;
    }
}
