package com.jiuguo.module.video;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.jiuguo.module.R;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.io.File;

/**
 * Created by leonard on 2015/5/20.
 */
public class VideoPlayActivity extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private final static String TAG = "VideoPlayActivity";

    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView;
    private TextView loadRateView;

    private boolean isMediaCtlShow = true;
    private MediaController mController;
    private AudioManager audioManager;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!LibsChecker.checkVitamioLibs(this)) {
            return;
        }

        mContext = this;

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int layoutId = UZResourcesIDFinder.getResLayoutID("mo_video_play_activity_layout");
        if(layoutId > 0){
            setContentView(layoutId);
        } else {
            Log.e(TAG, "名为：mo_video_play_activity_layout的布局文件不存在!请检查代码");
            return;
        }

        initView();
        initVideo();
    }

    private void initView() {
        int videoViewId = UZResourcesIDFinder.getResIdID("video_view");
        if(videoViewId > 0) {
            mVideoView = (VideoView) findViewById(R.id.video_view);
        } else {
            Log.e(TAG, "名为：video_view的VideoView不存在!请检查代码");
        }

        int controllerId = UZResourcesIDFinder.getResIdID("video_controller");
        if(controllerId > 0) {
            mController = (MediaController) findViewById(R.id.video_controller);
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
//        batterLevel = (TextView) findViewById(R.id.video_battery_level);

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

//        mSeekBar = (SeekBar) findViewById(R.id.mediacontroller_seekbar);
//        pb = (ProgressBar) findViewById(R.id.probar);

        if(UZResourcesIDFinder.getResIdID("download_rate") > 0) {
            downloadRateView = (TextView) findViewById(R.id.download_rate);
        } else {
            Log.e(TAG, "名为：download_rate的TextView不存在!请检查代码");
        }
        if(UZResourcesIDFinder.getResIdID("load_rate") > 0) {
            loadRateView = (TextView) findViewById(R.id.load_rate);
        } else {
            Log.e(TAG, "名为：load_rate的TextView不存在!请检查代码");
        }
        if(UZResourcesIDFinder.getResIdID("probar") > 0) {
            pb = (ProgressBar) findViewById(R.id.probar);
        } else {
            Log.e(TAG, "名为：probar的ProgressBar不存在!请检查代码");
        }
//
//        mBackImageButton = (ImageButton) findViewById(R.id.video_back);
//        mBackImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                recordTime();
//                try{
//                    Thread.sleep(100);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//                VideoPlay.this.finish();
//            }
//        });
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
//                mp.setWakeMode(getCon, PowerManager.SCREEN_BRIGHT_WAKE_LOCK);
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
            String videoUrl = getIntent().getStringExtra("videoUrl");
            Uri mUri = Uri.parse(videoUrl);
            mVideoView.setVideoURI(mUri);
//        }else{
//            mVideoView.setVideoURI(mUris, appContext.getSaveCachePath());
//        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.i(TAG,"onInfo");
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                Log.v(TAG, "buffering");
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
                Log.v(TAG, "start");
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
}