package com.jiuguo.module.video;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.jiuguo.app.bean.Video;
import com.jiuguo.app.core.AppContext;
import com.jiuguo.app.db.DatabaseManager;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import de.greenrobot.event.EventBus;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leonard on 2015/5/20.
 */
public class VideoPlayActivity extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private final static String TAG = "VideoPlayActivity";

    private static final int MODE_NET = 0;
    private static final int MODE_LOCAL = 1;
    private static final int MODE_LIVE = 2;

    private Video video;
    private int mode;
    private String mUri;

    private int mScreenWidth;
    private int mScreenHeight;

    private ImageButton ibBack;
    private ImageButton ibComment;
    private TextView tvTitle;
    private TextView tvBattery;

    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView;
    private TextView loadRateView;

    private MediaController mController;
    private AudioManager audioManager;

    private DatabaseManager dbManager;

    private BroadcastReceiver batteryReceiver;
    private IntentFilter batteryFilter;

    private AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!LibsChecker.checkVitamioLibs(this)) {
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        appContext = (AppContext) this.getApplicationContext();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        dbManager = new DatabaseManager(appContext);

        int layoutId = UZResourcesIDFinder.getResLayoutID("mo_video_play_activity_layout");
        if(layoutId > 0){
            setContentView(layoutId);
        } else {
            Log.e(TAG, "名为：mo_video_play_activity_layout的布局文件不存在!请检查代码");
            return;
        }

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        initView();
        initVideo();
        handleIntent();

        monitorBatteryState();
    }

    private void initView() {
        int videoViewId = UZResourcesIDFinder.getResIdID("video_view");
        if(videoViewId > 0) {
            mVideoView = (VideoView) findViewById(videoViewId);
        } else {
            Log.e(TAG, "名为：video_view的VideoView不存在!请检查代码");
        }

        int controllerId = UZResourcesIDFinder.getResIdID("video_controller");
        if(controllerId > 0) {
            mController = (MediaController) findViewById(controllerId);
            mController.setOnHiddenListener(new MediaController.OnHiddenListener(){

                @Override
                public void onHidden() {
//                resListView.setVisibility(View.GONE);
//                if(!isLocked){
//                    lockImage.setVisibility(View.GONE);
//                }
                }
            });
            mController.setOnShownListener(new MediaController.OnShownListener(){

                @Override
                public void onShown() {
//                lockHandler.removeMessages(1);
//                lockImage.setVisibility(View.VISIBLE);
                }

            });
            mController.setOnTouchUpListener(new MediaController.OnTouchUpListener(){
                @Override
                public void onTouchUp(){
//                mHandler.removeMessages(HideVL);
//                mHandler.sendEmptyMessage(HideVL);
                }
            });

            mController.setOnLightChangedListener(new MediaController.OnLightChangeListener() {

                @Override
                public void ligthChanged(int curVolume, int maxVolume) {
//                float bright = preferences.getFloat("bright", (float) 0.5);
//                bright = bright + ((float)curVolume)/100;
//                if (bright < 0.05) {
//                    if (bright < 0) {
//                        bright = 0;
//                    }
//                    lp.screenBrightness = (float) 0.05;
//                }else if(bright > 1) {
//                    bright = 1;
//                    lp.screenBrightness = 1;
//                }else{
//                    lp.screenBrightness = bright;
//                }
//                getWindow().setAttributes(lp);
//                videoLVLL.setVisibility(View.VISIBLE);
//                mHandler.removeMessages(HideVL);
//                mHandler.sendEmptyMessageDelayed(HideVL, 1000);
//                videoLVIV.setImageResource(R.drawable.video_icon_brightness);
//                videoLVTV.setText((int)(bright * 100) + "%");
//                SharedPreferences.Editor editor = preferences.edit();
//                editor.putFloat("bright", bright);
//                editor.commit();
                }
            });
        } else {
            Log.e(TAG, "名为：video_controller的MediaController不存在!请检查代码");
        }

        int tvBatteryId = UZResourcesIDFinder.getResIdID("video_battery_level");
        if(tvBatteryId > 0) {
            tvBattery = (TextView) findViewById(tvBatteryId);
        } else {
            Log.e(TAG, "名为：video_battery_level的TextView不存在!请检查代码");
        }

//        mSeekBar = (SeekBar) findViewById(R.id.mediacontroller_seekbar);

        int tvDownloadRateId = UZResourcesIDFinder.getResIdID("download_rate");
        if(tvDownloadRateId > 0) {
            downloadRateView = (TextView) findViewById(tvDownloadRateId);
        } else {
            Log.e(TAG, "名为：download_rate的TextView不存在!请检查代码");
        }
        int tvLoadRateId = UZResourcesIDFinder.getResIdID("load_rate");
        if(tvLoadRateId > 0) {
            loadRateView = (TextView) findViewById(tvLoadRateId);
        } else {
            Log.e(TAG, "名为：load_rate的TextView不存在!请检查代码");
        }
        int pbId = UZResourcesIDFinder.getResIdID("probar");
        if(pbId > 0) {
            pb = (ProgressBar) findViewById(pbId);
        } else {
            Log.e(TAG, "名为：probar的ProgressBar不存在!请检查代码");
        }

        int ibBackId = UZResourcesIDFinder.getResIdID("video_back");
        ibBack = (ImageButton) findViewById(ibBackId);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                recordTime();
                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
                VideoPlayActivity.this.finish();
            }
        });
//        mTitleTextView = (TextView) findViewById(R.id.video_title);
//        mTitleTextView.setText(videoTitle);

    }

    private void initVideo() {
        audioManager.requestAudioFocus(new AudioManager.OnAudioFocusChangeListener(){
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS||focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Pause playback
                    if (mVideoView != null) {
                        try {
                            mVideoView.pause();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mVideoView.setMediaController(mController);
        mVideoView.setBufferSize(512 * 1024);
        mVideoView.requestFocus();
        mVideoView.setHardwareDecoder(true);
        String cachePath = Environment.getExternalStorageDirectory() + File.separator + "jiuguo" + File.separator + "cache" + File.separator;
        mVideoView.setCacheDirectory(cachePath);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                Log.i(TAG,"onPrepared");
//                errorTime = 0;
                mediaPlayer.setPlaybackSpeed(1.0f);
//                mController.show(-1);
//                mLoadingView.setVisibility(View.GONE);
            }
        });
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                recordTime();
//                isComplete = true;
//                VideoPlay.this.finish();

            }
        });

        mVideoView.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {

            @Override
            public void onSeekComplete(MediaPlayer mp) {

            }
        });
        mVideoView.setLifecycleListener(new VideoView.LifeCycleListener() {

            @Override
            public void onStop(VideoView view, MediaPlayer mp, long duration) {
                Log.i(TAG,"onStop");
//                isFirstStart = false;
//                sendBarrageChangeEvent(STATE_STOP);
            }

            @Override
            public void onStart(VideoView view, MediaPlayer mp, long duration) {
                Log.i(TAG,"onStart");
//
//                Log.i(TAG,"onStart videoPlay + isSeekToRecord : " + isSeekToRecord);
//
//                if(isMediaCtlShow){
//                    if(mController!=null){
//                        mController.hide();
//                        isMediaCtlShow = false;
//                    }
//                }
//                videoPause = false;
//                if (playMode != MODE_LIVE&&isSeekToRecord) {
//                    isSeekToRecord = false;
//                    try {
//                        Video video = dbManager.getRecordVideo(videoId, false);
//                        if (video != null) {
//                            int posttime = video.getRecordTime();
//                            if (posttime > 0 && posttime != Integer.MAX_VALUE && posttime*1000 < mp.getDuration()) {
//                                mp.seekTo(posttime * 1000);
//                                isMediaCtlShow = true;
//                                mController.show(-1);
//                                appContext.toast("你上次已看到" + formatHumanTime(posttime) + ",正在跳转...", Toast.LENGTH_SHORT);
//                            }else if(posttime==Integer.MAX_VALUE){
//                                appContext.toast("该片已经看完，正从头开始播放", Toast.LENGTH_SHORT);
//                            }
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//                sendBarrageChangeEvent(STATE_START);
//                isFirstStart = false;
//                mHandler.postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        isFirstStart = true;
//                    }
//
//                }, 2000);
            }

            @Override
            public void onSeekTo(VideoView view, MediaPlayer mp, long duration) {
                Log.i(TAG,"onSeekTo");
//                isFirstStart = false;
//                sendBarrageChangeEvent(STATE_SEEK);
//                if(isMediaCtlShow){
//                    if(mController!=null){
//                        mController.hide();
//                        isMediaCtlShow = false;
//                    }
//                }
            }

            @Override
            public void onPause(VideoView view, MediaPlayer mp, long duration) {
//                videoPause = true;
//                isFirstStart = false;
//                sendBarrageChangeEvent(STATE_PAUSE);
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener(){

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.i(TAG,"onError");
//                if(playMode == MODE_LIVE){
//                    Log.e("cyj","onError");
//                    if(errorTime <5){
//                        try {
//                            Thread.sleep(500);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        mVideoView.stopPlayback();
//                        mVideoView.setVideoURI(mUri);
//                        if (errorTime == 0) {
//                            appContext.toast("主播家的网络有点卡，正在刷新！", Toast.LENGTH_SHORT);
//                        }
//                        errorTime++;
//                    } else {
//                        appContext.toast("视频出错啦，请稍后再试", Toast.LENGTH_SHORT);
//                        VideoPlay.this.finish();
//                        errorTime = 0;
//                    }
//                }else {
//                    if(errorTime < 5&&what==1&&extra==-5){
//                        try {
//                            Thread.sleep(300);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        mVideoView.stopPlayback();
//                        mVideoView.setVideoURI(mUris,appContext.getSaveCachePath());
//                        errorTime ++;
//                    }else{
//                        appContext.toast("视频出错啦，请稍后再试", Toast.LENGTH_SHORT);
//                        VideoPlay.this.finish();
//                        errorTime = 0;
//                    }
//                }
                return true;
            }});
//        if(mUris == null){
//            String videoUrl = getIntent().getStringExtra("videoUrl");
//            Uri mUri = Uri.parse(videoUrl);
//            mVideoView.setVideoURI(mUri);
//        }else{
//            mVideoView.setVideoURI(mUris, appContext.getSaveCachePath());
//        }
//        mVideoView.setVideoURI(Uri.parse(appContext.getPrefs("url", "")));
    }

    private void handleIntent() {
        video = (Video) getIntent().getSerializableExtra("video");
        mode = getIntent().getIntExtra("mode", MODE_NET);

        if(video != null) {
            appContext.toast("无视频信息", Toast.LENGTH_SHORT);
            VideoPlayActivity.this.finish();
            return;
        }

        switch (mode) {
            case MODE_NET: {
                getNetVideoUrl();
                break;
            }
            case MODE_LOCAL: {
                getLocalVideoUrl();
                break;
            }
            case MODE_LIVE: {
                getLiveVideoUrl();
                break;
            }
        }
    }

    private void getNetVideoUrl() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = sdf.parse(video.getPostDate());
            Date today = new Date();
            int diff = (int) ((today.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
            if (diff < 1) {
                url = AppClientV2.getNewYoukuUrl(appContext, video.getCheckId(), format);
            } else {
                AppClientV2.getYoukuUrl(appContext,
                        video.getId(), video.getCheckId(),
                        appContext.getLoginId(),
                        appContext.getLoginToken());
            }
        } catch (Exception e) {
            Log.e(TAG, "get exception when getNetVideoUrl, cause: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getLocalVideoUrl() {

    }

    private void getLiveVideoUrl() {
        new Thread() {
            @Override
            public void run() {
//                try {

//                    NewVideoUrl zhanqiUrl = AppClientV2.getLiveUrl(mContext, tmp.getId());
//                    if (zhanqiUrl != null) {
//                        zhanqiUrl.setVideoId(tmp.getId());
//                        zhanqiUrl.setTitle(tmp.getTitle());
//                        Message msg = Message.obtain();
//                        msg.what = JGConstant.LiveVideo;
//                        Bundle bundle = new Bundle();
//                        bundle.putSerializable("url", zhanqiUrl);
//                        bundle.putString("imageUrl", tmp.getImageUrl());
//                        msg.setData(bundle);
//                        handler.sendMessage(msg);
//                    } else {
//                        handler.post(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                mContext.toast("主播可能正在休息哦，不信再点一下", Toast.LENGTH_SHORT);
//                            }
//                        });
//                    }
//                } catch (Exception e) {
//                    // TODO: handle exception
//                    handler.post(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            mContext.toast(R.string.server_connect_failed, Toast.LENGTH_SHORT);
//                        }
//                    });
//                }
            }

        }.start();
    }

    private void monitorBatteryState() {
        batteryReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT != health) {
                    switch (status) {
                        case BatteryManager.BATTERY_STATUS_UNKNOWN:
                            break;
                        case BatteryManager.BATTERY_STATUS_CHARGING:
                            tvBattery.setText("电量:" + level + "%");
                            break;
                        case BatteryManager.BATTERY_STATUS_DISCHARGING:
                        case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                        case BatteryManager.BATTERY_STATUS_FULL:
                            tvBattery.setText("电量:" + level + "%");
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, batteryFilter);
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.i(TAG,"onInfo");
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        Log.i(TAG,"onBufferingUpdate");
        loadRateView.setText(percent + "%");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(batteryReceiver != null){
            unregisterReceiver(batteryReceiver);
        }
        EventBus.getDefault().unregister(this);
    }
}
