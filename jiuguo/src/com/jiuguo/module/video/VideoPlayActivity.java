package com.jiuguo.module.video;

import android.app.Activity;
import android.content.*;
import android.media.AudioManager;
import android.net.Uri;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import com.jiuguo.app.bean.NewVideoUrl;
import com.jiuguo.app.bean.UrlBean;
import com.jiuguo.app.bean.Video;
import com.jiuguo.app.core.AppClient;
import com.jiuguo.app.db.DatabaseManager;
import com.jiuguo.app.utils.CommonUtils;
import com.jiuguo.app.utils.ResolutionUtils;
import com.uzmap.pkg.uzcore.UZResourcesIDFinder;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by leonard on 2015/5/20.
 */
public class VideoPlayActivity extends Activity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private final static String TAG = "VideoPlayActivity";

    private static final int MODE_NET = 0;
    private static final int MODE_LOCAL = 1;
    private static final int MODE_LIVE = 2;

    private static final int APPLE_GET_NUMBER = 0;
    private static final int APPLE_SEND_SUCCESS = 1;
    private static final int APPLE_SEND_FAILED = 2;

    private int userId;
    private String userToken;
    private int resolution;
    private boolean isComplete = false;
    private int videoId;

    private Video video;
    private int mode;
    private String playUrl;
    private String[] playUrls;

    private int mScreenWidth;
    private int mScreenHeight;

    private String tmpResolutionName = "高清";
    private int tmpResolutionNumber = 0 ;

    private ImageButton ibBack;
    private ImageButton ibComment;
    private TextView tvTitle;
    private TextView tvBattery;

    private TextView tvAppleNum;
    private ImageView ivApple;
    private ImageView ivAppleTips;

    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView;
    private TextView loadRateView;

    private MediaController mController;
    private AudioManager audioManager;

    private DatabaseManager dbManager;

    private BroadcastReceiver batteryReceiver;
    private IntentFilter batteryFilter;

    private Context mContext;

    private final static int MSG_GET_LIVE_URL = 0;
    private final static int MSG_GET_NET_URL = 1;
    private final static int MSG_GET_LOCAL_URL = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_NET_URL : {
                    handleNetUrl((NewVideoUrl) msg.obj);
                    break;
                }
                case MSG_GET_LOCAL_URL : {
                    break;
                }
                case MSG_GET_LIVE_URL : {
                    handleLiveUrl((NewVideoUrl) msg.obj);
                    break;
                }
            }
        }
    };

    private Handler appleNumberHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case APPLE_GET_NUMBER : {
                    tvAppleNum.setText("" + msg.obj);
                    ivApple.setClickable(true);
                    break;
                }
                case APPLE_SEND_SUCCESS : {
                    CommonUtils.toast(mContext, "赠送小苹果成功！！", Toast.LENGTH_SHORT);
                    ivAppleTips.setVisibility(View.GONE);
                    getApple();
                    break;
                }
                case APPLE_SEND_FAILED : {
                    CommonUtils.toast(mContext, "赠送小苹果失败，可能金币不足！！", Toast.LENGTH_SHORT);
                    getApple();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        CommonUtils.initAppFolder(mContext);

        if(!LibsChecker.checkVitamioLibs(this)) {
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        dbManager = new DatabaseManager(mContext);


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
        initData();
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

        int tvTitleId = UZResourcesIDFinder.getResIdID("video_title");
        if(tvTitleId > 0) {
            tvTitle = (TextView) findViewById(tvTitleId);
        } else {
            Log.e(TAG, "名为：video_battery_level的TextView不存在!请检查代码");
        }
        int ivAppleId = UZResourcesIDFinder.getResIdID("video_apple");
        if(ivAppleId > 0) {
            ivApple = (ImageView) findViewById(ivAppleId);
            ivApple.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ivApple.setClickable(false);
                    if (CommonUtils.isLogin()) {
                        postApple();
                    } else {
                        CommonUtils.toast(mContext, "您还没登录咧！！", Toast.LENGTH_SHORT);
                    }
                }
            });
        } else {
            Log.e(TAG, "名为：video_apple的ImageView不存在!请检查代码");
        }
        int ivAppleTipsId = UZResourcesIDFinder.getResIdID("video_apple_tips");
        if(ivAppleTipsId > 0) {
            ivAppleTips = (ImageView) findViewById(ivAppleTipsId);
        } else {
            Log.e(TAG, "名为：video_apple_tips的ImageView不存在!请检查代码");
        }
        int tvAppleNumId = UZResourcesIDFinder.getResIdID("apple_number");
        if(tvAppleNumId > 0) {
            tvAppleNum = (TextView) findViewById(tvAppleNumId);
        } else {
            Log.e(TAG, "名为：apple_number的TextView不存在!请检查代码");
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
                recordTime();
                try{
                    Thread.sleep(100);
                }catch(Exception e){
                    e.printStackTrace();
                }
                VideoPlayActivity.this.finish();
            }
        });
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
                recordTime();
                isComplete = true;
                VideoPlayActivity.this.finish();
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
    }

    private void initData() {
        userId = CommonUtils.getLoginUserId(mContext);
        userToken = CommonUtils.getLoginUserToken(mContext);
        resolution = CommonUtils.getResolution(mContext, 2);
    }

    private void handleIntent() {
        video = (Video) getIntent().getSerializableExtra("video");
        mode = getIntent().getIntExtra("mode", MODE_NET);

        mVideoView.setMode(mode);

        if(video == null) {
            CommonUtils.toast(mContext, "无视频信息", Toast.LENGTH_SHORT);
            VideoPlayActivity.this.finish();
            return;
        }

        tvTitle.setText(video.getTitle());

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
                final String format = ResolutionUtils.formatResolution(resolution);
                new Thread() {
                    @Override
                    public void run() {
                        NewVideoUrl videoUrl = AppClient.getNewYoukuUrl(mContext, userId, video.getCheckId(), format);
                        videoId = videoUrl.getVideoId();
                        Message msg = new Message();
                        msg.what = MSG_GET_NET_URL;
                        msg.obj = videoUrl;
                        mHandler.sendMessage(msg);
                    }
                }.start();
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        NewVideoUrl videoUrl = AppClient.getYoukuUrl(mContext, video.getId(), video.getCheckId(), userId, userToken);
                        videoId = videoUrl.getVideoId();
                        Message msg = new Message();
                        msg.what = MSG_GET_NET_URL;
                        msg.obj = videoUrl;
                        mHandler.sendMessage(msg);
                    }
                }.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "get exception when getNetVideoUrl, cause: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void getLocalVideoUrl() {
        //TODO
        //
    }

    private void getLiveVideoUrl() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Long id = video.getId();
                    if(id != null) {
                        NewVideoUrl videoUrl = AppClient.getLiveUrl(mContext, id.intValue());
                        videoId = videoUrl.getVideoId();
                        Message msg = new Message();
                        msg.what = MSG_GET_LIVE_URL;
                        msg.obj = videoUrl;
                        mHandler.sendMessage(msg);
                    } else {
                        CommonUtils.toast(mContext, "主播可能正在休息哦", Toast.LENGTH_SHORT);
                        VideoPlayActivity.this.finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "get exception when getLiveVideoUrl, cause: " + e.getMessage());
                }
            }
        }.start();
    }

    private void handleNetUrl(NewVideoUrl videoUrl) {
        List<UrlBean> urlBeans = videoUrl.getListUrl();
        if(urlBeans != null && urlBeans.size() > 0) {
            String url = urlBeans.get(urlBeans.size() - 1).getUrl();
            tmpResolutionName =  urlBeans.get(urlBeans.size() - 1).getShowName();
            tmpResolutionNumber = urlBeans.size() - 1;
            UrlBean tmpUrlBean = null;
            switch (resolution) {
                case 0:
                    tmpUrlBean = videoUrl.getUrlBean("flv");
                    break;
                case 1:
                    tmpUrlBean = videoUrl.getUrlBean("mp4");
                    break;
                case 2:
                    tmpUrlBean = videoUrl.getUrlBean("hd2");
                    break;
                case 3:
                    tmpUrlBean = videoUrl.getUrlBean("hd3");
                    break;
            }
            if (tmpUrlBean != null) {
                url = tmpUrlBean.getUrl();
                tmpResolutionName = tmpUrlBean.getShowName();
                for(int i = 0 ; i<urlBeans.size() ;i++){
                    if(tmpUrlBean.getShowName().equals(urlBeans.get(i).getShowName())){
                        tmpResolutionNumber = i;
                        break;
                    }
                }
            }
            playUrl = url;
            playUrls = videoUrl.getPlaylist();
        }

        setVideoUrl();
    }

    private void handleLiveUrl(NewVideoUrl videoUrl) {
        if(videoUrl != null) {
            playUrls = videoUrl.getPlaylist();
            setVideoUrl();
        } else {
            CommonUtils.toast(mContext, "主播可能正在休息哦", Toast.LENGTH_SHORT);
            VideoPlayActivity.this.finish();
        }
    }

    private void setVideoUrl() {
        if(playUrls != null && playUrls.length > 0){
            mVideoView.setVideoURI(playUrls, CommonUtils.getCacheSavePath());
        } else {
            if(playUrl != null && !"".equals(playUrl)) {
                Uri uri = Uri.parse(playUrl);
                mVideoView.setVideoURI(uri);
            } else {
                CommonUtils.toast(mContext, "无视频信息", Toast.LENGTH_SHORT);
                VideoPlayActivity.this.finish();
                return;
            }
        }
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

    /**
     * 记录播放历史
     */
    public void recordTime(){
        if(isComplete) return;
        try {
            Video video;
            if(mode == MODE_LOCAL || mode == MODE_NET){
                video = dbManager.queryRecordVideo(videoId, false);
                if (mController != null && mController.getMediaPlayer() != null) {
                    int recordTime;
                    if(mController.getMediaPlayer().getCurrentPosition() >= mController.getMediaPlayer().getDuration()) {
                        recordTime = Integer.MAX_VALUE;
                    } else {
                        recordTime = (int) (mController.getMediaPlayer().getCurrentPosition() / 1000);
                    }
                    video.setRecordTime(recordTime);
                }
            } else {
                video = dbManager.queryRecordVideo(videoId, true);
            }

            video.setLastTime(new Date());
            dbManager.updateRecordVideo(video);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "catch exception when record time, cause: " + e.getMessage());
        }
    }

    /**
     * 发送苹果
     */
    private void postApple(){
        new Thread() {
            @Override
            public void run() {
                try {
                    boolean isSuccess = AppClient.postApple(mContext, userId, userToken, videoId);
                    if (isSuccess) {
                        appleNumberHandler.sendEmptyMessage(APPLE_SEND_SUCCESS);
                    } else {
                        appleNumberHandler.sendEmptyMessage(APPLE_SEND_FAILED);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "catch exception when post apple, cause: " + e.getMessage());

                }
            }
        }.start();
    }

    /**
     * 获取苹果数量
     */
    private void getApple(){
        new Thread() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.what = APPLE_GET_NUMBER;
                    msg.obj = AppClient.getApple(mContext, videoId);
                    appleNumberHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "catch exception when get apple, cause: " + e.getMessage());
                }
            }
        }.start();
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
//        EventBus.getDefault().unregister(this);
    }
}
