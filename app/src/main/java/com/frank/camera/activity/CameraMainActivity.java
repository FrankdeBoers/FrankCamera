package com.frank.camera.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.CamcorderProfile;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.frank.filtercamera.R;
import com.frank.camera.adapter.FilterAdapter;
import com.frank.camera.helper.FilterTypeHelper;
import com.frank.filters.FrankFilterEngine;
import com.frank.filters.camera.CameraEngine;
import com.frank.filters.filter.helper.FilterType;
import com.frank.filters.widget.FilterCameraView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * Created by Frank on 2016/3/17.
 */
public class CameraMainActivity extends Activity implements View.OnClickListener {

    public static final int STATE_IDLE = 11;        //空闲状态
    public static final int STATE_PRESS = 12;       //按下状态
    public static final int STATE_RECORDERING = 14; //录制状态
    private static final String TAG = "CameraMainActiavity";
    // 视频保存路径
    private static final String VIDEO_FILE_PATH
            = Environment.getExternalStoragePublicDirectory("DCIM").getAbsolutePath() + "/Camera";
    public static final int PREVIEW_SIZE_WIDTH = 1440;
    public static final int PREVIEW_SIZE_HEIGHT = 1080;
    private static final int MSG_DISABLE_BTNPIC = 1;
    private static final int MSG_ENABLE_BTNPIC = 2;
    private static final int MSG_HIDE_TOP = 3;

    private final FilterType[] types = new FilterType[]{
            FilterType.NONE,
            FilterType.WHITECAT, // 白猫
            FilterType.BLACKCAT, // 黑猫
            FilterType.ROMANCE, // 浪漫
            FilterType.SAKURA, // 樱花
            FilterType.ANTIQUE, // 复古
            FilterType.NOSTALGIA, // 怀旧
            FilterType.LATTE, // 拿铁
            FilterType.COOL, // 冰冷
            FilterType.EMERALD, // 祖母绿
            FilterType.EVERGREEN, // 常青
//            FilterType.CRAYON, //蜡笔
//            FilterType.SKETCH, // 素描
    };
    Camera camera = null;
    File videoFile = null;
    private int state = STATE_IDLE;              //当前按钮状态
    private MediaRecorder mMediaRecorder = null;
    private AudioManager mAudioManager;
    private int audioResult = -1;
    private CamcorderProfile mCamcorderProfile = null;
    private String CURRTRT_VIDEO_NAME;
    private long mCurrentStartRecordTime;
    private long mPrePicTime;
    private RelativeLayout gallery_filter;
    private LinearLayout mFilterLayout;
    private RecyclerView mFilterListView;
    private FilterAdapter mAdapter;
    private FrankFilterEngine frankFilterEngine;
    private ImageView btn_shutter;
    private ImageView btnPic = null;
    private Button btnVideo = null;
    private Button btnVideoStop = null;
    private TextView tv_filter_name = null;
    private boolean isQuit = true; // 解决滤镜左划冲突
    private RelativeLayout layout_video_mode = null;
    private Button btn_flick = null;

    // mediaPlayer对象，播放拍照声；  录像时的声音采用系统声音
    private MediaPlayer mp = null;
    private MediaPlayer mp_record = null;

    private PowerManager.WakeLock wakeLock;
    private PowerManager powerManager = null;

    // 计时器控件，用于录像时记录时间
    private Chronometer chronometer = null;
    private Handler mMainHandler = null;
    private FilterAdapter.onFilterChangeListener onFilterChangeListener = new FilterAdapter.onFilterChangeListener() {

        @Override
        public void onFilterChanged(FilterType filterType) {
            frankFilterEngine.setFilter(filterType);
            hideFilters();
            Log.d(TAG, "filterType: " + filterType);
            tv_filter_name.setText(FilterTypeHelper.FilterType2Name(filterType));
        }

        @Override
        public void onFiterSame() {
            hideFilters();
        }
    };
    private View.OnClickListener btn_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_camera_filter:
                    showFilters();
                    break;
                case R.id.btn_camera_gallery:
                    goGallery();
                    break;
//                case R.id.btn_camera_closefilter:
//                    hideFilters();
//                    break;

                default:
                    break;
            }
        }
    };

    private View.OnTouchListener onVideoListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    takeVideo();
                    btnVideo.setVisibility(View.GONE);
                    btnPic.setVisibility(View.GONE);
                    gallery_filter.setVisibility(View.GONE);
                    layout_video_mode.setVisibility(View.VISIBLE);
                    initChronometer();
                    startFlick(btn_flick);
                    chronometer.setVisibility(View.VISIBLE);
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    break;

                default:
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener onVideoListenerStop = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;

                case MotionEvent.ACTION_UP:
                    if (System.currentTimeMillis() - mCurrentStartRecordTime > 2000) {
                        stopRecordVideo();
                        saveRecordVideo();
                    } else {
                        showShortTimeDialog();
                        stopRecordVideo();
                        deleteRecordVideo();
                    }
                    btnVideo.setVisibility(View.VISIBLE);
                    btnPic.setVisibility(View.VISIBLE);
                    gallery_filter.setVisibility(View.VISIBLE);
                    layout_video_mode.setVisibility(View.GONE);
                    stopFlick(btn_flick);
                    state = STATE_IDLE;
                    break;

                default:
                    break;
            }

            return true;
        }
    };

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.d(TAG, "event.getAction(): " + event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: // 0
                    state = STATE_PRESS;
                    //同时延长500启动长按后处理的逻辑Runnable，实现长按录像
//                    mMainHandler.postDelayed(mLongPressRunnable, 500);
                    break;

                case MotionEvent.ACTION_UP: // 1 手抬起时的处理
//                    handlerUnpressByState();
                    if (System.currentTimeMillis() - mPrePicTime > 1400) {
                        playSound();
                        takePhoto();
                    }
                    break;

                default:
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        Log.d(TAG, "onCreate 1");
        FrankFilterEngine.Builder builder = new FrankFilterEngine.Builder();
        Log.d(TAG, "onCreate 2");
        frankFilterEngine = builder
                .build((FilterCameraView) findViewById(R.id.glsurfaceview_camera));
        initView();
        Log.d(TAG, "onCreate 3");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setIsCameraUsing("true");
        // setScreenOffTime(30000);
        setScreenOn(30 * 1000);
        Log.d(TAG, "onResume ..");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause ..");
        // 设置灭屏时间8S
        // setScreenOffTime(8000);
        setIsCameraUsing("false");
        releaseWakeLock();

        if (state == STATE_RECORDERING) {
            stopRecordVideo();
            saveRecordVideo();
            btnVideo.setVisibility(View.VISIBLE);
            btnPic.setVisibility(View.VISIBLE);
            gallery_filter.setVisibility(View.VISIBLE);
            layout_video_mode.setVisibility(View.GONE);
            stopFlick(btn_flick);
            state = STATE_IDLE;
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop ..");
        // 退出时，Camera标志位 false
        setIsCameraUsing("false");

    }

    // 设置系统灭屏时间
    // private void setScreenOffTime(int paramInt) {
    //     Log.d(TAG, "setScreenOffTime " + paramInt);
    //     try {
    //         android.provider.Settings.System.putInt(
    //                 getContentResolver(),
    //                 android.provider.Settings.System.SCREEN_OFF_TIMEOUT,
    //                 paramInt);
    //     } catch (Exception localException) {
    //         localException.printStackTrace();
    //     }
    // }


    // 设置标志位，表明当前已经进入相机界面，远程后台拍照功能无法使用
    private void setIsCameraUsing(String isCameraUsing) {
        Log.d(TAG, "setIsCameraUsing: " + isCameraUsing);
        try {
            android.provider.Settings.System.putString(
                    getContentResolver()
                    , "camera_isRecording"
                    , isCameraUsing);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    private void initView() {
        mMainHandler = new MainHandler(this, getMainLooper());
        mFilterLayout = (LinearLayout) findViewById(R.id.layout_filter);
        mFilterListView = (RecyclerView) findViewById(R.id.filter_listView);
        gallery_filter = (RelativeLayout) findViewById(R.id.gallery_filter);
        // 进入5S后，顶部按钮消失
        mMainHandler.sendEmptyMessageDelayed(MSG_HIDE_TOP, 3 * 1000);
        btn_shutter = (ImageView) findViewById(R.id.btn_camera_shutter);

        btnPic = (ImageView) findViewById(R.id.btn_camera_shutter);

        btnVideo = (Button) findViewById(R.id.btn_camera_video);

        btnVideoStop = (Button) findViewById(R.id.btn_camera_video_stop);

        tv_filter_name = (TextView) findViewById(R.id.show_filter_name);

        layout_video_mode = (RelativeLayout) findViewById(R.id.layout_video_mode);

        btn_flick = (Button) findViewById(R.id.btn_flick);

        findViewById(R.id.btn_camera_filter).setOnClickListener(btn_listener);
//        findViewById(R.id.btn_camera_closefilter).setOnClickListener(btn_listener);
        findViewById(R.id.btn_camera_gallery).setOnClickListener(btn_listener);

        btnPic.setOnTouchListener(onTouchListener);
//        btnVideo.setOnTouchListener(onVideoListener);
//        btnVideoStop.setOnTouchListener(onVideoListenerStop);
        btnVideo.setOnClickListener(this);
        btnVideoStop.setOnClickListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mFilterListView.setLayoutManager(gridLayoutManager);

        mAdapter = new FilterAdapter(this, types);
        mFilterListView.setAdapter(mAdapter);
        mAdapter.setOnFilterChangeListener(onFilterChangeListener);

        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        FilterCameraView cameraView = (FilterCameraView) findViewById(R.id.glsurfaceview_camera);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) cameraView.getLayoutParams();
        params.width = screenSize.x;
        params.height = screenSize.x * 4 / 3;
        cameraView.setLayoutParams(params);
        cameraView.setOnClickListener(this);

        mFilterListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        isQuit = false;
                        break;
                }
                return false;
            }
        });
        initAudioFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy " + isQuit);
        releaseMediaPlayer();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed isQuit " + isQuit);
        if (isQuit) {
            super.onBackPressed();
        } else {
            isQuit = true;
        }
    }

    private void initChronometer() {
        if (chronometer == null) {
            chronometer = (Chronometer) findViewById(R.id.chmeter_time);
        }

        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            /**
             * Notification that the chronometer has changed.
             * 每秒钟执行一次
             *
             * @param chronometer
             */
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                Log.d(TAG, "chronometer.getBase():" + chronometer.getBase());
                // 10S后自动结束录像
                if (SystemClock.elapsedRealtime() - chronometer.getBase() > 10000) {
                    Log.d(TAG, "onChronometerTick >> ");
                    stopRecordVideo();
                    saveRecordVideo();
                    btnVideo.setVisibility(View.VISIBLE);
                    btnPic.setVisibility(View.VISIBLE);
                    gallery_filter.setVisibility(View.VISIBLE);
                    layout_video_mode.setVisibility(View.GONE);
                    stopFlick(btn_flick);
                    state = STATE_IDLE;
                }

            }
        });
    }

    private void takePhoto() {
        Log.d(TAG, "takePhoto ");
        mPrePicTime = System.currentTimeMillis();
        frankFilterEngine.savePicture(getOutputMediaFile(), null);
    }

    private void takeVideo() {
        Log.d(TAG, "takeVideo ");
        if (state == STATE_IDLE) {
            state = STATE_RECORDERING;
            mCurrentStartRecordTime = System.currentTimeMillis();
            camera = CameraEngine.getCamera();
            camera.setDisplayOrientation(180);
            frankFilterEngine.setFilter(FilterType.NONE);
            tv_filter_name.setText(FilterTypeHelper.FilterType2Name(FilterType.NONE));
            mAdapter.setFilters4Activity();
            startRecord(camera);
            playRecordingSound();
        }
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

    public void startRecord(Camera camera) {
        Log.d(TAG, "camera " + camera);
        try {
            // 视频存储的缓存路径
            CURRTRT_VIDEO_NAME = "VID_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance()) + ".mp4";
            File videoTempDir = new File(VIDEO_FILE_PATH);
            if (!videoTempDir.exists()) {
                videoTempDir.mkdirs();
            }
            videoFile = new File(VIDEO_FILE_PATH, CURRTRT_VIDEO_NAME);
            camera.unlock();

            //初始化一个MediaRecorder
            if (mMediaRecorder == null) {
                mMediaRecorder = new MediaRecorder();
            } else {
                mMediaRecorder.reset();
            }
            mMediaRecorder.setCamera(camera);

            // 视频源类型
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setAudioChannels(2);

            mMediaRecorder.setOrientationHint(180);

            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
                Log.e(TAG, "QUALITY_720P");
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
                Log.e(TAG, "QUALITY_1080P");
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                Log.e(TAG, "QUALITY_HIGH");
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW)) {
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
                Log.e(TAG, "QUALITY_LOW");
            }

            if (mCamcorderProfile != null) {
                mCamcorderProfile.audioCodec = MediaRecorder.AudioEncoder.AAC;
                mCamcorderProfile.audioChannels = 1;
                mCamcorderProfile.audioSampleRate = 16000;

                mCamcorderProfile.videoCodec = MediaRecorder.VideoEncoder.H264;
                mMediaRecorder.setProfile(mCamcorderProfile);
            }
            mMediaRecorder.setVideoSize(PREVIEW_SIZE_WIDTH, PREVIEW_SIZE_HEIGHT);
            mMediaRecorder.setOutputFile(videoFile.getAbsolutePath());
            mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
                    if (mMediaRecorder != null) {
                        mMediaRecorder.stop();
                        mMediaRecorder.release();
                        mMediaRecorder = null;
                    }
                    Log.e(TAG, "onError: " + " what:" + what + " extra: " + extra);
                }
            });

            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                    //正在录制...
                }
            });

            // 准备、开始
            mMediaRecorder.prepare();
            mMediaRecorder.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopRecordVideo() {
        if (state == STATE_RECORDERING) {
            if (mMediaRecorder != null) {
                try {
                    mMediaRecorder.setOnErrorListener(null);
                    mMediaRecorder.setOnInfoListener(null);
                    mMediaRecorder.setPreviewDisplay(null);
//            mMediaRecorder.reset();
                    mMediaRecorder.stop();
                    mMediaRecorder.release();
                    mMediaRecorder = null;
                } catch (IllegalStateException e) {
                    Log.e("Exception", Log.getStackTraceString(e));
                } catch (RuntimeException e) {
                    Log.e("Exception", Log.getStackTraceString(e));
                } catch (Exception e) {
                    // TODO: handle exception
                    Log.i("Exception", Log.getStackTraceString(e));
                }
            }

            if (gallery_filter != null) {
                gallery_filter.setVisibility(View.VISIBLE);

            }
            if (chronometer != null) {
                chronometer.stop();
            }
            // 拍照按钮设置为拍照图标
            btnPic.setImageResource(R.drawable.btn_pic_normal);
            playRecordingSound();
        }

    }

    private void saveRecordVideo() {
        if (videoFile != null) {
            scanFile(videoFile.getAbsolutePath());
        } else {
            Log.e(TAG, "videoFile = null !! ");
        }
    }

    private void deleteRecordVideo() {
        if (videoFile.exists()) {
            videoFile.delete();
        }
    }

    /**
     * 扫描文件
     *
     * @param path
     */
    private void scanFile(String path) {
        MediaScannerConnection.scanFile(CameraMainActivity.this, new String[]{path},
                null, new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.e("TAG", "onScanCompleted");
                    }
                });
    }


    private void releaseMediaPlayer() {
        if (mp != null) {
            mp.stop();
            //关键语句
            mp.reset();
            mp.release();
            mp = null;
        }
        if (mp_record != null) {
            mp_record.stop();
            //关键语句
            mp_record.reset();
            mp_record.release();
            mp_record = null;
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera_video:
                takeVideo();
                btnVideo.setVisibility(View.GONE);
                btnPic.setVisibility(View.GONE);
                gallery_filter.setVisibility(View.GONE);
                layout_video_mode.setVisibility(View.VISIBLE);
                initChronometer();
                startFlick(btn_flick);
                chronometer.setVisibility(View.VISIBLE);
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                break;

            case R.id.btn_camera_video_stop:
                if (System.currentTimeMillis() - mCurrentStartRecordTime > 2000) {
                    stopRecordVideo();
                    saveRecordVideo();
                } else {
                    showShortTimeDialog();
                    stopRecordVideo();
                    deleteRecordVideo();
                }
                btnVideo.setVisibility(View.VISIBLE);
                btnPic.setVisibility(View.VISIBLE);
                gallery_filter.setVisibility(View.VISIBLE);
                layout_video_mode.setVisibility(View.GONE);
                stopFlick(btn_flick);
                state = STATE_IDLE;
                break;

            case R.id.glsurfaceview_camera:
                /* 0  VISIBLE    可见
                   4  NVISIBLE    不可见但是占用布局空间
                   8  GONE    不可见也不占用布局空间 */
                if (gallery_filter.getVisibility() == View.VISIBLE) {
                    gallery_filter.setVisibility(View.INVISIBLE);
                } else {
                    gallery_filter.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private static class MainHandler extends Handler {
        final WeakReference<CameraMainActivity> mActivity;

        public MainHandler(CameraMainActivity activity, Looper looper) {
            super(looper);
            mActivity = new WeakReference<CameraMainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CameraMainActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            Log.d(TAG, "msg.what: " + msg.what);
            switch (msg.what) {
                case MSG_ENABLE_BTNPIC:
                    activity.btnPic.setEnabled(true);
                    break;

                case MSG_DISABLE_BTNPIC:
                    activity.btnPic.setEnabled(false);
                    break;

                case MSG_HIDE_TOP:
                    activity.gallery_filter.setVisibility(View.INVISIBLE);
                    break;

                default:
                    break;

            }
        }
    }

    /**
     * 进入相册
     */
    private void goGallery() {

    }

    /**
     * 播放拍照声音，系统的拍照声音无法正常播放，直接在APP里面控制播放
     */
    private void playSound() {
        if (!doesRing()) {
            return;
        }
        if (mp == null) {
            Log.d(TAG, "playSound >> init mp..");
            mp = MediaPlayer.create(this, R.raw.camera_click);
        }
        if (audioResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                mp.start();//开始播放
            } catch (Exception e) {
                e.printStackTrace();//输出异常信息
            }
        }
    }

    /**
     * 播放录像声音，方便音量控制
     */
    private void playRecordingSound() {
        if (!doesRing()) {
            return;
        }
        if (mp_record == null) {
            Log.d(TAG, "playSound >> init mp_record..");
            mp_record = MediaPlayer.create(this, R.raw.video_record);
        }
        if (audioResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            try {
                mp_record.start();//开始播放
            } catch (Exception e) {
                e.printStackTrace();//输出异常信息
            }
        }
    }

    private boolean doesRing() {
        int ringType = mAudioManager.getRingerMode();
        Log.d(TAG, "vibrateType: " + ringType);
        return ringType == AudioManager.RINGER_MODE_NORMAL;
    }

    private void initAudioFocus() {
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioResult = mAudioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
    }

    /**
     * 开启View闪烁效果
     */
    private void startFlick(Button view) {
        if (null == view) {
            return;
        }
        Animation alphaAnimation = new AlphaAnimation(1, 0.4f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(alphaAnimation);
    }

    /*
    * 取消View闪烁效果
    */
    private void stopFlick(Button view) {
        if (null == view) {
            return;
        }
        view.clearAnimation();
    }

    private void showShortTimeDialog() {
        Toast toast = Toast.makeText(this, "录像时间过短！", Toast.LENGTH_SHORT);
        LinearLayout view = (LinearLayout) toast.getView();
        view.setBackgroundColor(getResources().getColor(R.color.transparent));
        TextView textView = (TextView) view.getChildAt(0);
        textView.setTextSize(35);
        toast.setGravity(Gravity.CENTER, 20, 60);
        toast.setView(view);
        toast.show();
    }

    private void setScreenOn(long time) {
        Log.d(TAG, "setScreenOn ");
        if (powerManager == null) {
            powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        wakeLock = powerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "filtercamera");
        wakeLock.acquire(time);
    }

    private void releaseWakeLock() {
        Log.d(TAG, "releaseWakeLock ");
        try {
            wakeLock.release();
            wakeLock = null;
        } catch (Exception e) {
            wakeLock = null;
        }
    }
}